package commons.commands;

import commons.User;

public class HistoryCommand extends Command {
    public HistoryCommand(User user) {
        super("history", user);
    }
}
