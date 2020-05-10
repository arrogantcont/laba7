package commons.commands;

import commons.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@Data
@NoArgsConstructor
public abstract class Command implements Serializable {
    private String command;
    private User user;
}
