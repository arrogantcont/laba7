package commons.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
/**
 * Координаты в билете
 */
public class Coordinates implements Serializable {
    private Integer x; //Максимальное значение поля: 851, Поле не может быть null
    private int y; //Максимальное значение поля: 621
}
