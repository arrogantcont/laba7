package commons.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
/**
 * Часть билета, отвечающая за раздел "Событие"
 */
public class Event implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long eventId; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private int ticketsCount; //Значение поля должно быть больше 0
    private EventType eventType; //Поле не может быть null
}
