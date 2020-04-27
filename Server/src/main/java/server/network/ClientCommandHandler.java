package server.network;

import commons.commands.*;
import commons.model.Ticket;
import commons.model.TicketType;
import lombok.Data;
import server.lab6.Pair;
import server.lab6.TicketOffice;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

@Data
public class ClientCommandHandler {
    private HashMap<SocketChannel, ArrayDeque<String>> userHistory = new HashMap<>();
    private ArrayDeque<Ticket> tickets = new ArrayDeque<>();
    private TicketOffice ticketOffice;
    private TicketType ticketType;
    private BufferedReader reader;
    private int nextTicketId = 1;
    private long nextEventId = 1;
    private boolean inputFromFile = false;
    private BufferedReader script;
    private Date dateOFCreation;
    private static final Logger ClientCommandHandler_LOGGER = Logger.getLogger(ClientCommandHandler.class.getName());
    private SocketChannel client;
    HashMap<SocketChannel, ArrayList<Command>> history = new HashMap<>();

    public ClientCommandHandler(String path) {
        ticketOffice = new TicketOffice(path);
    }


    public void addCommandToHistory(String command) {
        ArrayList list = this.history.get(this.client);
        if (list == null) list = new ArrayList();
        list.add(command);
        this.history.put(this.client, list);
    }

    public void removeFirstCommand() {
        this.history.get(this.client).remove(0);
    }


    public String handleCommand(Command command, SocketChannel client) throws IOException {
        String stringCommand = command.getCommand();
        this.client = client;
        if (!(stringCommand.equals("Unknown command"))) {
            if (this.history.get(this.client) != null) {
                if (this.history.get(this.client).size() < 10) this.addCommandToHistory(stringCommand);
                else {
                    this.removeFirstCommand();
                    this.addCommandToHistory(stringCommand);
                }
            } else this.addCommandToHistory(stringCommand);
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
                ticketOffice.clear();
                return "Коллекция очищена";
            case "history":
                return history.get(client).toString();
            case "add":
                return ticketOffice.addNewTicket(ticketSetIds(((AddCommand) command).getTicket()));
            case "add_if_max":
                return addIfMax(((Add_IF_MaxCommand) command).getTicket());
            case "add_if_min":
                return addIfMin(((Add_IF_MinCommand) command).getTicket());
            case "print_field_ascending_price":
                return ticketOffice.printAllAscendingPrice();
            case "print_field_descending_refundable":
                return ticketOffice.printAllDescendingRefundable();
            case "remove_by_id":
                return ticketOffice.remove_by_id(((Remove_By_IdCommand) command).getId());
            case "filter_greater_than_price":
                return ticketOffice.printAllTicketsGTPrice(((Filter_greater_than_priceCommand) command).getPrice());
            case "update":
                if (ticketOffice.checkId(((UpdateCommand) command).getTicket().getTicketId())) {
                    ticketOffice.deleteOld(((UpdateCommand) command).getTicket().getTicketId());
                    return ticketOffice.addNewTicket(ticketUpdateEventId(((UpdateCommand) command).getTicket()));
                } else return "билета с заданным id нет, попробуйте ещё раз";
            case "execute_script":
                return "Начато выполнение скрипта";
            case "exit":
                ClientCommandHandler_LOGGER.info("Подключение с клиентом разорвано");
                return "Клиент вышел из программы";
            default:
                return "Вы ввели неподдерживаемую команду";
        }
    }

    private Ticket ticketSetIds(Ticket ticket) {
        Pair<Integer, Long> maxs = ticketOffice.getMaxs();
        nextTicketId = maxs.getFirst() + 1;
        nextEventId = maxs.getSecond() + 1;
        ticket.setTicketId(nextTicketId);
        if (ticket.getEvent() != null) ticket.getEvent().setEventId(nextEventId);
        return ticket;
    }

    private Ticket ticketUpdateEventId(Ticket ticket) {
        Pair<Integer, Long> maxs1 = ticketOffice.getMaxs();
        nextEventId = maxs1.getSecond() + 1;
        if (ticket.getEvent() != null) ticket.getEvent().setEventId(nextEventId);
        return ticket;
    }


    private String addIfMax(Ticket ticket) {
        if (ticket.getPrice() > ticketOffice.getMaxPrice()) {
            return ticketOffice.addNewTicket(ticketSetIds(ticket));
        } else return "Билет не добавлен, условие не выполнено";
    }

    private String addIfMin(Ticket ticket) {
        if (ticket.getPrice() < ticketOffice.getMinPrice()) {
            return ticketOffice.addNewTicket(ticketSetIds(ticket));
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
