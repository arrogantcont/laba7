package commons.commands;

import lombok.Data;

@Data
public class Remove_By_IdCommand extends Command {
    private int id;

    public Remove_By_IdCommand(int id) {
        super("remove_by_id");
        this.id = id;
    }
}
