package commons.commands;

import commons.User;

public class ClearCommand extends Command {
    public ClearCommand(User user) {
        super("clear", user);
    }
}
