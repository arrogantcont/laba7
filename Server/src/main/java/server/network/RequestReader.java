package server.network;

import commons.commands.Command;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.logging.Logger;

public class RequestReader {
    private static final Logger READ_LOGGER = Logger.getLogger(RequestReader.class.getName());
    ForkJoinPool pool = new ForkJoinPool(
            4, ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
    ClientCommandHandler clientCommandHandler;

    public RequestReader(ClientCommandHandler clientCommandHandler) {
        this.clientCommandHandler = clientCommandHandler;
    }

    public String encryptThisString(String input) {
        try {
// getInstance() method is called with algorithm SHA-224
            MessageDigest md = MessageDigest.getInstance("SHA-224");

// digest() method is called
// to calculate message digest of the input string
// returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

// Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

// Convert message digest into hex value
            String hashtext = no.toString(16);

// Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

// return the HashText
            return hashtext;
        }

// For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void clientService(SocketChannel channel, SelectionKey key) {
        pool.submit(new RecursiveAction() {
            //пример официант - переложить ответственность
            @Override
            protected void compute() {
                synchronized (channel) {
                    try {
                        ByteBuffer readBuffer = ByteBuffer.allocate(102400);//выделяем буффер на 1Кб
                        if (!channel.isOpen())
                            return;
                        int num = channel.read(readBuffer);
                        if (num > 0) {
                            // Processing incoming data...
                            ByteArrayInputStream inputStream = new ByteArrayInputStream(readBuffer.array());//массив байтов
                            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                            Command command = (Command) objectInputStream.readObject();//считываем объект
                            READ_LOGGER.info("Новый запрос от клиента: " + command);
                            command.getUser().setPassword(encryptThisString(command.getUser().getPassword()));
                            System.out.println(command.getUser().toString());
                            clientCommandHandler.handleCommand(command, channel);
                        } else if (num == -1) {
                            // - 1 represents that the connection has been closed
                            channel.close();
                        }
                    } catch (ClosedChannelException e) {
                        key.cancel();
                        System.out.println("Потеряно соединение с клиентом");
                    } catch (Exception e) {
                        key.cancel();
                    }

                }
            }
        });
    }


}
