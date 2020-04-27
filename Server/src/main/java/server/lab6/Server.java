package server.lab6;

import commons.commands.UnknownCommand;
import server.io.SystemInPipe;
import server.network.ClientCommandHandler;
import server.network.ConnectionAccepter;
import server.network.RequestReader;
import server.network.ResponseSender;
import commons.commands.Command;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Main класс программы
 */
public class Server {
    private static int port = 8080;
    private static String hostname = "localhost";
    private static ClientCommandHandler clientCommandHandler;
    private final Selector selector;//доступ к кналом
    private final ServerSocketChannel server;//главый канал, генерит каналы для клиентов
    private final ConnectionAccepter connectionAccepter;
    private final RequestReader requestReader;
    private final ResponseSender responseSender;
    private static final Logger SERVER_LOGGER = Logger.getLogger(Server.class.getName());
    private SocketChannel client;


    public Server(int port, String hostname) throws IOException {
        selector = Selector.open();
        server = ServerSocketChannel.open();
        server.socket().bind(new InetSocketAddress(hostname,port));//указываем порт, на котором будет приниматься подключение
        server.configureBlocking(false);//не блокирующий режим, вызов метода Read не блокирует программу
        server.register(selector, SelectionKey.OP_ACCEPT);//регаем в селекторе(если много каналов, он позволяет работать с каналами - пример жд пути) наш канал-сервер
        connectionAccepter = new ConnectionAccepter(selector);
        requestReader = new RequestReader();
        responseSender = new ResponseSender();
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        try {
            String path = args[0];
            if (args.length > 1) {
                port = Integer.parseInt(args[1]);
            }
            if (args.length > 2) {
                hostname = args[2];
            }
            Server serverObject = new Server(port, hostname);
            SystemInPipe systemInPipe = new SystemInPipe();
            clientCommandHandler = new ClientCommandHandler(path);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Завершение работы сервера...");
                clientCommandHandler.getTicketOffice().save();
                System.out.println("Коллекция сохранена");
            }));
            SelectableChannel stdinChannel = systemInPipe.getStdinChannel();//подключить поток ввода к селектору в след строке
            stdinChannel.register(serverObject.selector, SelectionKey.OP_READ);//OP_reaD - КАНАЛ на чтение
            SERVER_LOGGER.info("Logger Name: " + SERVER_LOGGER.getName());
            systemInPipe.start();
            SERVER_LOGGER.info("Сервер запущен");
            serverObject.startServer();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Не введено имя файла");
        } catch (NumberFormatException e) {
            System.err.println("Неправильно задан порт");
        } catch (SocketException e) {
            System.err.println("Неправильно задан хост");
        }
    }

    public void startServer() throws IOException, ClassNotFoundException {
        while (true) {
            int readyChannels = selector.select();//ждёт, когда будет готов канал, возвращает кол-во готовых к работе каналов
            if (readyChannels == 0) {
                continue;
            }
            Set<SelectionKey> readyKeys = selector.selectedKeys();//список каналов, которые готовы
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    // There are accepted new connections to the server
                    SocketChannel socketChannel = server.accept();
                    connectionAccepter.acceptConnection(socketChannel);
//                     ArrayDeque<String> history = new ArrayDeque<>();
//                    userHistory.put(socketChannel, history);
                    SERVER_LOGGER.info("Новое подключение");
                    // A new connection does not mean that the channel has data.
                    // Here, register this new Socket Channel with Selector, listen for OP_READ events, and wait for data
                } else if (key.isReadable()) {
                    if (key.channel() instanceof SocketChannel) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        this.client = socketChannel;
                        try {
                            Optional<Command> command = requestReader.readRequest(socketChannel);
                            if (command.isPresent()) {
                                String s = clientCommandHandler.handleCommand(command.get(), client);
                                responseSender.sendAnswer(s, socketChannel);
                            }
                        } catch (IOException e) {
                            socketChannel.close();
                        }
                    } else {
                        ReadableByteChannel channel = (ReadableByteChannel) key.channel();
                        ByteBuffer readBuffer = ByteBuffer.allocate(102400);//выделяем буффер на 1Кб
                        int num = channel.read(readBuffer);
                        String command;
                        if (num > 0) {
                            // Processing incoming data...
                            command = new String(readBuffer.array());
                            System.out.println("Server command:" + command);
                            switch (command.trim()) {
                                case "save":
                                    clientCommandHandler.getTicketOffice().save();
                                    System.out.println("Коллекция сохранена");
                                    break;
                                case "stop":
                                    server.close();
                                    System.exit(0);
                                    break;
                                default:
                                    System.out.println("Вы ввели неподдерживаюмую команду");
                            }
                        } else if (num == -1) {
                            // - 1 represents that the connection has been closed
                            SERVER_LOGGER.info("Связь с клиентом потеряна");
                            channel.close();
                        }
                    }
                    //requestReader
                    // Data Readable
                    // The Socket Channel that monitors OP_READ events is registered in the above if branch
                }
            }
        }
    }
}
