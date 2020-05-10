package commons.commands;

import commons.User;
import commons.model.Ticket;
import lombok.Data;

@Data
public class Add_IF_MinCommand extends Command {
    private Ticket ticket;

    public Add_IF_MinCommand(Ticket ticket, User user) {
        super("add_if_min", user);
        this.ticket = ticket;
    }
}
