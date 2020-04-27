package server.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Класс, отвечающих за запись билетов в файл
 */
public class TicketWriter {
    private BufferedWriter writer;

    public TicketWriter(String path) throws IOException {
        File file = new File(path);
        writer = new BufferedWriter(new FileWriter(file, false));
    }

    public void writeToFile(String s) throws IOException {
        writer.write(s);
        writer.flush();
    }
}
