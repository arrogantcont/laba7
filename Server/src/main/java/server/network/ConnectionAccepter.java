package server.network;

import lombok.Data;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

@Data
public class ConnectionAccepter {
    private Selector selector;

    public ConnectionAccepter(Selector selector) {
        this.selector = selector;
    }

    public void acceptConnection(SocketChannel socketChannel) throws IOException {
        //получили новый канал, регаем в селекторе новогго клиента
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }
}
