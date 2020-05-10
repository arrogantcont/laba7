package commons.commands;

import commons.User;

public class UnknownCommand extends Command {

    public UnknownCommand(User user) {
        super("Unknown command", user);
    }
}
