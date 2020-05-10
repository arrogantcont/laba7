package commons.commands;

import commons.User;
import lombok.Data;

@Data
public class Remove_By_IdCommand extends Command {
    private int id;

    public Remove_By_IdCommand(int id, User user) {
        super("remove_by_id", user);
        this.id = id;
    }
}
