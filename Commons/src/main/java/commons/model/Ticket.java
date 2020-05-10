package commons.model;

import commons.User;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
/**
 * Сам класс билета
 */
public class Ticket implements Comparable<Ticket>, Serializable {
    private static int nextid;
    private int ticketId; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.util.Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private int price; //Значение поля должно быть больше 0
    private String comment; //Поле не может быть null
    private boolean refundable;
    private TicketType type; //Поле может быть null
    private Event event; //Поле может быть null
    private User user;

    @Override
    public int compareTo(Ticket ticket) {
        return price - ticket.price;
    }

}
