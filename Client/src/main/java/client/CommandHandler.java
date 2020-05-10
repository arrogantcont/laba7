package client;

import Exeptions.CommandException;
import commons.User;
import commons.commands.*;
import commons.model.*;
import lombok.Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

@Data
public class CommandHandler {
    ServerConnection serverConnection;
    public BufferedReader reader;
    private boolean inputFromFile = false;
    private BufferedReader script;
    private boolean isMultiLineCommand = false;
    private boolean isAuthorised = false;
    private User user;

    public CommandHandler(BufferedReader reader, int port, String host) throws IOException {
        this.reader = reader;
        serverConnection = new ServerConnection(port, host);
    }


    public Command handleCommand(String command) throws IOException, CommandException {
        String[] commandParts = command.split(" ");
        switch (commandParts[0]) {
            case "history":
                return new HistoryCommand(user);
            case "clear":
                return new ClearCommand(user);
            case "help":
                return new HelpCommand(user);
            case "info":
                return new InfoCommand(user);
            case "show":
                return new ShowCommand(user);
            case "add":
                return readTicket();
            case "exit":
                return new ExitCommand(user);
            case "add_if_max":
                return addIfMax();
            case "add_if_min":
                return addIfMin();
            case "print_field_ascending_price":
                return new Print_field_ascending_priceCommand(user);
            case "print_field_descending_refundable":
                return new Print_field_descending_refundableCommand(user);
            case "remove_by_id":
                try {
                    if ((commandParts.length == 2) && (Integer.parseInt(commandParts[1]) > 0)) {
                        return new Remove_By_IdCommand(Integer.parseInt(commandParts[1]), user);
                    }
                } catch (NumberFormatException e) {
                    throw new CommandException("Введите команду в формате: update *id билета (тип данных int), который хотите обновить*");
                }
            case "filter_greater_than_price":
                try {
                    if ((commandParts.length == 2) && (Integer.parseInt(commandParts[1]) > 0)) {
                        return new Filter_greater_than_priceCommand(Integer.parseInt(commandParts[1]), user);
                    }
                } catch (NumberFormatException e) {
                    throw new CommandException("Введите команду в формате: filter_greater_than_price *price билета (тип данных int)*");
                }
            case "update":
                try {
                    if (Integer.parseInt(commandParts[1]) > 0) {
                        Ticket updatedTicket = readTicket().getTicket();
                        updatedTicket.setTicketId(Integer.parseInt(commandParts[1]));
                        return new UpdateCommand(updatedTicket, user);
                    }
                } catch (IndexOutOfBoundsException e) {
                    throw new CommandException("Не хватает данных для команды");
                } catch (NumberFormatException e) {
                    throw new CommandException("Введите команду в формате: update *id билета (тип данных int)*");
                }
            case "execute_script":
                if (inputFromFile) {
                    throw new CommandException("Нельзя вызывать execute_script при выполнении другого скрипта");
                }
                StringBuilder path = new StringBuilder();

                for (int i = 1; i < commandParts.length; i++)
                    path.append(commandParts[i]);
                try {
                    script = new BufferedReader(new FileReader(new File(path.toString())));
                    inputFromFile = true;
                } catch (Exception e) {
                    throw new CommandException("Доступа к файлу нет, либо вы не указали файл со скриптом");
                }
                return new Execute_scriptCommand(user);
            case "login":
                return new LoginCommand(login());
            case "register":
                return new RegisterCommand(register());

        }
        return new UnknownCommand(user);
    }

    private User login() throws IOException, CommandException {
        String username = null;
        while (username == null) {
            System.out.println("Имя пользователя (не должно быть пустым)");
            username = readLine();
        }
        String pass = null;
        while (pass == null) {
            System.out.println("Пароль (поле не должно быть пустым)");
            pass = readLine();
        }
        return new User(username, pass);
    }

    private Add_IF_MinCommand addIfMin() throws IOException, CommandException {
        Ticket retTick = readTicket().getTicket();
        return new Add_IF_MinCommand(retTick, user);
    }

    private Add_IF_MaxCommand addIfMax() throws IOException, CommandException {
        Ticket retTick = readTicket().getTicket();
        return new Add_IF_MaxCommand(retTick, user);
    }

    private AddCommand readTicket() throws IOException, CommandException, NullPointerException {
        System.out.println("Приступаем к заполнению данных билета" + "\n" + "Вводите данные без пробелов в начале или на конце " + "\n" + "имя билета");
        String name = "";
        while (name.trim().equals("") || name.equals(null)) {
            System.out.println("имя билета не должно быть пустым");
            name = readLine();
        }
        System.out.println("координаты");
        System.out.println("x");
        int x = 0;
        boolean xUnsuccessful = true;
        while (xUnsuccessful) {
            try {
                x = Integer.parseInt(readLine());
                if (x <= 851) xUnsuccessful = false;
                else System.out.println("Максимальное значение поля: 851");
            } catch (NumberFormatException e) {
                System.out.println("должно быть int");
            }
        }
        System.out.println("y");
        int y = 0;
        String userY;
        boolean yUnsuccessful = true;
        while (yUnsuccessful) {
            try {
                userY = readLine();
                if (userY.equals("")) {
                    y = 0;
                    System.out.println("y = 0");
                    yUnsuccessful = false;
                } else {
                    y = Integer.parseInt(userY);
                    if (y <= 621) yUnsuccessful = false;
                    else System.out.println("Максимальное значение поля: 621");
                }
            } catch (NumberFormatException e) {
                System.out.println("должно быть int");
            }
        }
        System.out.println("price");
        int price = 0;
        boolean priceUnsuccessful = true;
        while (priceUnsuccessful) {
            try {
                price = Integer.parseInt(readLine());
                if (price > 0) priceUnsuccessful = false;
                else System.out.println("Бесплатный сыр только в мышеловке, цена должна быть больше 0");
            } catch (NumberFormatException e) {
                System.out.println("должно быть int");
            }
        }
        System.out.println("коммент");
        String comment = "";
        while (comment.trim().equals("")) {
            System.out.println("комментарий к билету не должен быть пустым");
            comment = readLine();
        }
        System.out.println("возврат" + "\n" + "Если Вы хотите добавть опцию возврата, то введите *true*" + "\n" + "иначе оставьте поле пустым или введите любой символ");
        Boolean refundable = Boolean.parseBoolean(readLine());
        System.out.println("тип билета");
        TicketType[] allTicketTypes = TicketType.values();
        for (TicketType ticketType : allTicketTypes) {
            System.out.println(ticketType);
        }
        String stringTicketType;
        TicketType ticketType = null;
        boolean typeUnsuccessful = true;
        while (typeUnsuccessful) {
            try {
                stringTicketType = readLine();
                if (stringTicketType.equals("")) {
                    ticketType = null;
                    typeUnsuccessful = false;
                } else {
                    ticketType = TicketType.valueOf(stringTicketType);
                    typeUnsuccessful = false;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Либо выберите предложенный тип, либо оставьте поле пустым");
            }
        }
        System.out.println("Хотите ли Вы добавить событие? " + "\n" + "Чтобы добавить событие, введите *да*" + "\n" + "Если не хотите добавлять событие, оставьте поле пустым или введите любой символ");
        String userEvent = readLine();
        Event event = null;

        if (userEvent.equalsIgnoreCase("Да")) {

            System.out.println("имя события");
            String eventName = "";
            while (eventName.trim().equals("")) {
                System.out.println("имя события не должно быть пустым");
                eventName = readLine();
            }
            System.out.println("число билетов");
            int ticketCount = 0;
            boolean countUnsuccessful = true;
            while (countUnsuccessful) {
                try {
                    ticketCount = Integer.parseInt(readLine());
                    if (ticketCount > 0) countUnsuccessful = false;
                    else System.out.println("Число билетов должно быть больше 0");
                } catch (NumberFormatException e) {
                    System.out.println("должно быть int");
                }
            }
            System.out.println("выберите eventType");
            EventType[] allEventsTypes = EventType.values();
            for (EventType eventType : allEventsTypes) {
                System.out.println(eventType);
            }
            EventType eventType = null;
            boolean successful = false;
            while (!successful) {
                try {
                    String userEventType = readLine();
                    eventType = EventType.valueOf(userEventType);
                    successful = true;
                } catch (IllegalArgumentException e) {
                    System.out.println("Выберите один из преддложенных eventType");
                }

            }
            event = new Event((long) 0, eventName, ticketCount, eventType);
        }

        Coordinates coordinates = new Coordinates(x, y);
        Ticket ticket = Ticket.builder()
                .comment(comment)
                .coordinates(coordinates)
                .creationDate(new Date())
                .event(event)
                .ticketId(0)
                .name(name)
                .price(price)
                .refundable(refundable)
                .type(ticketType)
                .user(user)
                .build();
        isMultiLineCommand = false;
        return new AddCommand(ticket, user);

    }


    public String handleInput() throws IOException, CommandException {
        String s = readLine();
        Command command = handleCommand(s);
        if ((command.getCommand().equals("login") || command.getCommand().equals("register")) && !isAuthorised)
            return serverConnection.sendCommand(command);
        else if (isAuthorised) return serverConnection.sendCommand(command);
        else return "Вы не авторизованы";

    }


    private String readLine() throws IOException, CommandException {
        if (inputFromFile) {
            String s = script.readLine();
            if (s == null) {
                if (isMultiLineCommand) {
                    isMultiLineCommand = false;
                    throw new CommandException("Не хватает данных для корректного завершения команды скрипта");
                }
                inputFromFile = false;
                return reader.readLine();
            } else {
                String[] commandParts = s.split(" ");
                if (commandParts[0].equals("add") || commandParts[0].equals("add_if_max") || commandParts[0].equals("add_if_min") || commandParts[0].equals("update")) {
                    isMultiLineCommand = true;
                }
                System.out.println(s);
                return s;
            }
        } else return reader.readLine();
    }

    private User register() throws IOException, CommandException {
        String username = null;
        while (username == null) {
            System.out.println("Имя пользователя (поле не должно быть пустым)");
            username = readLine();
        }
        boolean invalidPassword = true;
        String pass1 = null;
        String pass2;
        while (invalidPassword) {
            System.out.println("Пароль (поле не должно быть пустым)");
            pass1 = readLine();
            System.out.println("Введите пароль повторно");
            pass2 = readLine();
            if (pass1.equals(pass2) && !(pass1 == null)) invalidPassword = false;
            else System.out.println("Введенные пароли не совпадают");
        }
        return new User(username, pass1);
    }
}
