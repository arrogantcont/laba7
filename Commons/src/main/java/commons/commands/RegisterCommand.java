package commons.commands;

import commons.User;
import lombok.Data;

@Data
public class RegisterCommand extends Command {
    public RegisterCommand(User user) {
        super("register", user);
    }
}
