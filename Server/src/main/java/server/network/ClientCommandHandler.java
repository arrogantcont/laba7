package server.network;

import commons.User;
import commons.commands.*;
import commons.model.Ticket;
import commons.model.TicketType;
import lombok.Data;
import server.DB.DataBaseConnection;
import server.lab6.Pair;
import server.lab6.TicketOffice;

import java.io.BufferedReader;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

@Data
public class ClientCommandHandler {
    private HashMap<String, String> users = new HashMap<>();
    private HashMap<String, ArrayDeque<String>> newHistory = new HashMap<>();
    private TicketOffice ticketOffice;
    private TicketType ticketType;
    private BufferedReader reader;
    private int nextTicketId = 1;
    private long nextEventId = 1;
    private boolean inputFromFile = false;
    private BufferedReader script;
    private Date dateOFCreation;
    private final DataBaseConnection dataBaseConnection = new DataBaseConnection();
    private static final Logger ClientCommandHandler_LOGGER = Logger.getLogger(ClientCommandHandler.class.getName());
    ResponseSender responseSender;

    public ClientCommandHandler(ResponseSender responseSender) throws SQLException {
        ticketOffice = new TicketOffice(dataBaseConnection);
        this.responseSender = responseSender;
        this.users = new HashMap<>();
        for (User user : dataBaseConnection.loadAllUsers()) {
            users.put(user.getUsername(), user.getPassword());
        }
        ticketOffice.setTickets(dataBaseConnection.loadAllTickets());
    }


    public void addCommandToHistory(Command command) {
        ArrayDeque<String> list = this.newHistory.get(command.getUser().getUsername());
        if (list == null) list = new ArrayDeque<>();
        list.add(command.getCommand());
        this.newHistory.put(command.getUser().getUsername(), list);
    }

    public void removeFirstCommand(String username) {
        this.newHistory.get(username).removeFirst();
    }

    public void handleCommand(Command command, SocketChannel client) {
        new Thread(() -> {
            String handle = null;
            try {
                handle = handle(command);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            responseSender.sendAnswer(handle, client);
        }).start();
    }

    private String handle(Command command) throws SQLException {
        String stringCommand = command.getCommand();
        if (checkUser(command.getUser()) && !(stringCommand.equals("login") || stringCommand.equals("register"))) {
            if (!(stringCommand.equals("Unknown command"))) {
                if (newHistory.get(command.getUser().getUsername()) != null) {
                    if (newHistory.get(command.getUser().getUsername()).size() < 10) addCommandToHistory(command);
                    else {
                        removeFirstCommand(command.getUser().getUsername());
                        addCommandToHistory(command);
                    }
                } else addCommandToHistory(command);
            }
            switch (stringCommand) {
                case "help":
                    return ("Справка по командам: " + "\n" +
                            "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                            "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                            "add {element} : добавить новый элемент в коллекцию\n" +
                            "update id {element} : обновить значение элемента коллекции, id которого равен заданному\n" +
                            "remove_by_id id : удалить элемент из коллекции по его id\n" +
                            "clear : очистить коллекцию\n" +
                            "execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n" +
                            "exit : завершить программу (без сохранения в файл)\n" +
                            "add_if_max {element} : добавить новый элемент в коллекцию, если значение ЕГО ЦЕНЫ превышает значение наибольшего элемента этой коллекции\n" +
                            "add_if_min {element} : добавить новый элемент в коллекцию, если значение ЕГО ЦЕНЫ меньше, чем у наименьшего элемента этой коллекции\n" +
                            "history : вывести последние 10 команд (без их аргументов)\n" +
                            "filter_greater_than_price price : вывести элементы, значение поля price которых больше заданного\n" +
                            "print_field_ascending_price : вывести значения поля price всех элементов в порядке возрастания\n" +
                            "print_field_descending_refundable : вывести значения поля refundable всех элементов в порядке убывания");
                case "show":
                    return ticketOffice.getAllTickets();
                case "info":
                    return createInfo();
                case "clear":
                    ticketOffice.clear(command.getUser());
                    return "Ваша коллекция очищена";
                case "history":
                    return newHistory.get(command.getUser().getUsername()).toString();
                case "add":
                    ticketOffice.addNewTicket(((AddCommand) command).getTicket(), command.getUser());
                    return "Билет добавлен";
                case "add_if_max":
                    return addIfMax(((Add_IF_MaxCommand) command).getTicket(), command.getUser());
                case "add_if_min":
                    return addIfMin(((Add_IF_MinCommand) command).getTicket(), command.getUser());
                case "print_field_ascending_price":
                    return ticketOffice.printAllAscendingPrice();
                case "print_field_descending_refundable":
                    return ticketOffice.printAllDescendingRefundable();
                case "remove_by_id":
                    if (ticketOffice.checkUser(((Remove_By_IdCommand) command).getId(), command.getUser())) {
                        return ticketOffice.remove_by_id(((Remove_By_IdCommand) command).getId());
                    } else return "Вы не можете удалить чужой билет";
                case "filter_greater_than_price":
                    return ticketOffice.printAllTicketsGTPrice(((Filter_greater_than_priceCommand) command).getPrice());
                case "update":
                    if (ticketOffice.checkId(((UpdateCommand) command).getTicket().getTicketId())) {
                        if (ticketOffice.checkUser(((UpdateCommand) command).getTicket().getTicketId(), command.getUser())) {
                            ticketOffice.deleteOld(((UpdateCommand) command).getTicket().getTicketId());
                            return ticketOffice.updateTicket(((UpdateCommand) command).getTicket(), command.getUser());
                        } else return "Вы не можете обновить чужой билет";
                    } else return "билета с заданным id нет, попробуйте ещё раз";
                case "execute_script":
                    return "Начато выполнение скрипта";
                case "exit":
                    ClientCommandHandler_LOGGER.info("Подключение с клиентом разорвано");
                    return "Клиент вышел из программы";
                default:
                    return "Вы ввели неподдерживаемую команду";
            }
        } else if (stringCommand.equals("register")) {
            return registerUser(command.getUser());
        } else if (stringCommand.equals("login")) {
            if (users.containsKey(command.getUser().getUsername()) && users.get(command.getUser().getUsername()).equals(command.getUser().getPassword())) {
                return "success";
            } else return "fail";
        } else return "Получена команда от несуществующего пользователя";
    }

    private String registerUser(User user) throws SQLException {
        if (users.containsKey(user.getUsername())) return "prohibited username";
        else {
            dataBaseConnection.addUser(user);
            users.put(user.getUsername(), user.getPassword());
            return "success";
        }
    }

    private boolean checkUser(User user) {
        return users.containsKey(user.getUsername()) && users.get(user.getUsername()).equals(user.getPassword());
    }

    private Ticket ticketSetIds(Ticket ticket) {
        Pair<Integer, Long> maxs = ticketOffice.getMaxs();
        nextTicketId = maxs.getFirst() + 1;
        nextEventId = maxs.getSecond() + 1;
        ticket.setTicketId(nextTicketId);
        if (ticket.getEvent() != null) ticket.getEvent().setEventId(nextEventId);
        return ticket;
    }


    private String addIfMax(Ticket ticket, User user) throws SQLException {
        if (ticket.getPrice() > ticketOffice.getMaxPrice()) {
            return ticketOffice.addNewTicket(ticketSetIds(ticket), user);
        } else return "Билет не добавлен, условие не выполнено";
    }

    private String addIfMin(Ticket ticket, User user) throws SQLException {
        if (ticket.getPrice() < ticketOffice.getMinPrice()) {
            return ticketOffice.addNewTicket(ticketSetIds(ticket), user);
        } else return "Билет не добавлен, условие не выполнено";
    }

    private String createInfo() {
        dateOFCreation = ticketOffice.findCreationDate();
        String collectType = ("Тип коллекции: ArrayDequeue");
        String collectionSize = ("Количество элементов коллекции: " + ticketOffice.getSize());
        String creation;
        if (dateOFCreation == null || ticketOffice.getSize() == 0) {
            creation = "Коллекция ещё не создана";
        } else creation = ("Дата создания колекции: " + dateOFCreation);
        StringBuilder information = new StringBuilder(150);
        return (information.append(collectType).append("\n").append(collectionSize).append("\n").append(creation)).toString();
    }

}
