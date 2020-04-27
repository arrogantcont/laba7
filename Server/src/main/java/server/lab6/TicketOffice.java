package server.lab6;

import server.io.TicketReader;
import server.io.TicketWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import commons.model.Ticket;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Iterator;

import static java.util.Arrays.asList;
import static java.util.Arrays.sort;

/**
 * Класс, работающий с коллекцей билетов
 */
public class TicketOffice {
    private ArrayDeque<Ticket> tickets = new ArrayDeque<>();
    private final GsonBuilder builder = new GsonBuilder();
    private final Gson gson = builder.create();
    private final String path;

    @SneakyThrows
    public TicketOffice(String path) {
        this.path = path;
        Ticket[] tickets = readTicketsFromFile();
        if (tickets == null) this.tickets = new ArrayDeque<>();
        else this.tickets = new ArrayDeque<>(asList(tickets));

    }

    /**
     * Метод, который возвращает пару чисел - текущие максимальные ticketId, eventId, нужен для генерации последующих Id
     *
     * @return пара чисел - текущие максимальные ticketId, eventId
     */
    public Pair<Integer, Long> getMaxs() {
        int maxTicketId = 0;
        long maxEventId = 0;
        if (tickets.size() > 0) {
            for (Ticket ticket : tickets) {
                if (ticket.getTicketId() > maxTicketId)
                    maxTicketId = ticket.getTicketId();
                if (ticket.getEvent() != null && ticket.getEvent().getEventId() > maxEventId)
                    maxEventId = ticket.getEvent().getEventId();
            }
        }
        return new Pair<>(maxTicketId, maxEventId);
    }

    @SneakyThrows
    public void save() {
        saveTicketsToFile(tickets);
    }

    private void saveTicketsToFile(ArrayDeque<Ticket> obj) throws IOException {
        TicketWriter ticketWriter = new TicketWriter(path);
        String s = gson.toJson(obj);
        ticketWriter.writeToFile(s);
    }

    public Ticket[] readTicketsFromFile() throws IOException {
        TicketReader ticketReader = new TicketReader(path);
        String s = ticketReader.readFromFile();
        return gson.fromJson(s, Ticket[].class);//мы записываем билеты как элты массива, поэтому поэтому обращаемся к ним аналогично
    }

    public String getAllTickets() {
        StringBuilder sb = new StringBuilder();
        sb.append("Все билеты: ");
        for (Ticket ticket : tickets) {
            sb.append(ticket.toString() + "\n");
        }
        return sb.toString();
    }

    public String addNewTicket(Ticket ticket) {
        tickets.add(ticket);
        return "Билет добавлен";
    }

    /**
     * Выводим на экран все билеты, цена которых больше заднной цены i
     *
     * @param price - заданная цена
     */
    public String printAllTicketsGTPrice(int price) {
        StringBuffer ticketsGTPrice = new StringBuffer(150);
        ticketsGTPrice.append("Билеты по фильтру: " + "\n");
        if (tickets.size() != 0) {
            for (Ticket ticket : tickets) {
                if (ticket.getPrice() > price) ticketsGTPrice.append(ticket).append("\n");
            }
            return ticketsGTPrice.toString();
        }
        return "Коллекция пуста";
    }

    public String remove_by_id(int id) {
        if (tickets.removeIf(ticket -> ticket.getTicketId() == id)) return "удалили успешно";
        else return "билета с таким id нет, попробуйте ещё раз";
    }

    /**
     * Метод проверяет, существет ли билет, который пользователь хочет обновить
     *
     * @param id введенный пользователем
     * @return true/false
     */
    public boolean checkId(int id) {
        boolean ticketPresent = false;
        for (Ticket ticket : tickets) {
            if (ticket.getTicketId() == id) {
                ticketPresent = true;
            }
        }
        return ticketPresent;
    }

    /**
     * Очищаем коллекцию
     */
    public void clear() {
        tickets.clear();
    }

    public Class<? extends ArrayDeque> info() {
        return tickets.getClass();
    }

    /**
     * @return размер коллекции
     */
    public int getSize() {
        return tickets.size();
    }

    public String printAllAscendingPrice() {
        StringBuffer allPrices = new StringBuffer(150);
        if (tickets.size() == 0) allPrices = allPrices.append("Билетов нет");
        else {
            int[] Prices = new int[tickets.size()];
            int counter = 0;
            for (Ticket ticket : tickets) {
                Prices[counter] = ticket.getPrice();
                counter++;
            }
            sort(Prices);
            for (int price : Prices) {
                allPrices.append(price).append("\n");
            }
        }
        return allPrices.toString();
    }

    public String printAllDescendingRefundable() {
        StringBuffer refunds = new StringBuffer(150);
        int trueCounter = 0;
        if (tickets.size() == 0) refunds.append("Коллекция пуста");
        else {
            for (Ticket ticket : tickets) {
                if (ticket.isRefundable()) trueCounter++;
            }
            for (int i = 1; i <= trueCounter; i++) refunds.append("true").append("\n");
            int falseCounter = tickets.size() - trueCounter;
            for (int i = 1; i <= falseCounter; i++) refunds.append("false").append("\n");
        }
        return refunds.toString();
    }

    public int getMaxPrice() {
        int maxPrice = 0;
        if (tickets.size() == 0) return maxPrice;
        else {
            for (Ticket ticket : tickets) {
                if (ticket.getPrice() > maxPrice) maxPrice = ticket.getPrice();
            }
            return maxPrice;
        }
    }

    public int getMinPrice() {
        int minPrice = 2147483647;
        if (tickets.size() == 0) return minPrice;
        for (Ticket ticket : tickets) {
            if (ticket.getPrice() < minPrice) minPrice = ticket.getPrice();
        }
        return minPrice;
    }

    public Date findCreationDate() {
        Date trueDate = null;
        for (Ticket ticket : tickets) {
            if (ticket.getTicketId() == 1) trueDate = ticket.getCreationDate();
        }
        return trueDate;
    }

    public void deleteOld(int ticketId) {
        Iterator<Ticket> ticketIterator = tickets.iterator();
        while (ticketIterator.hasNext()) {
            Ticket value = ticketIterator.next(); // должен быть вызван перед тем, как вызывается i.remove()
            if (value.getTicketId() == ticketId) {
                ticketIterator.remove(); // Элемент value был удалён из коллекции strings
                System.out.println("старый билет удалили успешно");
            }
        }
    }
}
