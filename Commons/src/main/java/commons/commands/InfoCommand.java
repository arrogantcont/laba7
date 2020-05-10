package commons.commands;

import commons.User;

public class InfoCommand extends Command {
    public InfoCommand(User user) {
        super("info", user);
    }
}
