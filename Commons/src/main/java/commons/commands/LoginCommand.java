package commons.commands;

import commons.User;
import lombok.Data;

@Data
public class LoginCommand extends Command {
    public LoginCommand(User user) {
        super("login", user);
    }
}
