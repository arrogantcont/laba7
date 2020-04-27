package server.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Класс, отвечающий за считывание билетов из файла.
 */
public class TicketReader {
    private final FileReader reader;

    /**
     * @param s
     * @throws FileNotFoundException
     */
    public TicketReader(String s) throws FileNotFoundException {
        File file = new File(s);
        reader = new FileReader(file);
    }

    public String readFromFile() throws IOException {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (i != -1) {
            i = reader.read();
            builder.append((char) i);
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();

    }

}
