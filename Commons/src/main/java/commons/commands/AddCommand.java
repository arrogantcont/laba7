package commons.commands;

import commons.User;
import commons.model.Ticket;
import lombok.Data;

@Data
public class AddCommand extends Command {
    private Ticket ticket;

    public AddCommand(Ticket ticket, User user) {
        super("add", user);
        this.ticket = ticket;
    }
}
