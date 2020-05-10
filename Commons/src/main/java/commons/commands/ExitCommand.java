package commons.commands;

import commons.User;

public class ExitCommand extends Command {
    public ExitCommand(User user) {
        super("exit", user);
    }
}
