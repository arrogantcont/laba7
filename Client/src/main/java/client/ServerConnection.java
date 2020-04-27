package client;

import commons.commands.Command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ServerConnection {
    private final SocketChannel socketChannel;

    public ServerConnection(int port, String host) throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(host, port));
    }

    public String sendCommand(Command command) throws IOException {
        // Send requests
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(command);
        oos.flush();
        byte[] data = bos.toByteArray();
        ByteBuffer buffer = ByteBuffer.wrap(data);
        socketChannel.write(buffer);

        // Read response
        ByteBuffer readBuffer = ByteBuffer.allocate(102400);
        int num;
        if ((num = socketChannel.read(readBuffer)) > 0) {
            ((Buffer)readBuffer).flip();

            byte[] re = new byte[num];
            readBuffer.get(re);

            String result = new String(re, StandardCharsets.UTF_8);
            if (result.contains("Клиент вышел из программы")) {
                System.out.println("Выполнен выход из программы");
                System.exit(0);
            }
            return result;
        }
        return "Ответ не получен";
    }
}
