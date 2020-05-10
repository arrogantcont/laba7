package server.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class ResponseSender {
    private static final Logger RESPONSE_LOGGER = Logger.getLogger(ResponseSender.class.getName());
    private final ExecutorService fixedThredPool = Executors.newFixedThreadPool(4);

    public void sendAnswer(String s, SocketChannel channel) {
        fixedThredPool.submit(() -> {
            try {
                send(s, channel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void send(String s, SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(s.getBytes());//массив байтов оборачиваем в
        // буффер (оболочка, позволяющая работать с массивами более удобно)
        RESPONSE_LOGGER.info("Отправка ответа клиенту");
        channel.write(buffer);
    }
}
