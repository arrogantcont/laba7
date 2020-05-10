package commons.commands;

import commons.User;

public class HelpCommand extends Command {
    public HelpCommand(User user) {
        super("help", user);
    }
}
