package client;

import Exeptions.CommandException;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.util.NoSuchElementException;


public class Client {
    private static String host = "localhost";
    private static int port = 8080;

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            if (args.length > 0)
                port = Integer.parseInt(args[0]);
            if (args.length > 1)
                host = args[1];
            CommandHandler commandHandler = new CommandHandler(reader, port, host);
            commandHandler.getServerConnection().setCommandHandler(commandHandler);
            System.out.println("Введите команду");
            while (true) {
                try {
                    System.out.println(commandHandler.handleInput());
                } catch (CommandException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("Конец ввода");
        } catch (NumberFormatException e) {
            System.out.println("Неправильно задан порт");
        } catch (JsonSyntaxException e) {
            System.out.println("Ошибка парсинга файла");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Не введено имя файла");
        } catch (ConnectException e) {
            System.out.println("Сервер недоступен");
        } catch (IOException e) {
            System.out.println("Потеряно соединение с сервером");
        }
    }
}
