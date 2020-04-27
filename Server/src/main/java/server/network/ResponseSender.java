package server.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class ResponseSender {
    private static final Logger RESPONSE_LOGGER = Logger.getLogger(ResponseSender.class.getName());

    public void sendAnswer(String s, SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(s.getBytes());//массив байтов оборачиваем в
        // буффер (оболочка, позволяющая работать с массивами более удобно)
        RESPONSE_LOGGER.info("Отправка ответа клиенту");
        channel.write(buffer);
    }
}
