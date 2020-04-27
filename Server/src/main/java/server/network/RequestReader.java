package server.network;

import commons.commands.Command;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Optional;
import java.util.logging.Logger;

public class RequestReader {
    private static final Logger READ_LOGGER = Logger.getLogger(RequestReader.class.getName());

    public RequestReader() {}

    public Optional<Command> readRequest(SocketChannel channel) throws IOException, ClassNotFoundException {//проверка на Null
        ByteBuffer readBuffer = ByteBuffer.allocate(102400);//выделяем буффер на 1Кб
        int num = channel.read(readBuffer);
        if (num > 0) {
            // Processing incoming data...
            ByteArrayInputStream inputStream = new ByteArrayInputStream(readBuffer.array());//массив байтов
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            Command command = (Command) objectInputStream.readObject();//считываем объект
            READ_LOGGER.info("Новый запрос от клиента: " + command);
            return Optional.of(command);
        } else if (num == -1) {
            // - 1 represents that the connection has been closed
            channel.close();
        }
        return Optional.empty();
    }
}
