package commons.commands;

import commons.User;

public class Execute_scriptCommand extends Command {
    public Execute_scriptCommand(User user) {
        super("execute_script", user);
    }
}
