package commons.commands;

import commons.User;

public class ShowCommand extends Command {
    public ShowCommand(User user) {
        super("show", user);
    }
}
