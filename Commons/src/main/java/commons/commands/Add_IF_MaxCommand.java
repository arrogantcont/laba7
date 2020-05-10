package commons.commands;

import commons.User;
import commons.model.Ticket;
import lombok.Data;

@Data
public class Add_IF_MaxCommand extends Command {
    private Ticket ticket;

    public Add_IF_MaxCommand(Ticket ticket, User user) {
        super("add_if_max", user);
        this.ticket = ticket;
    }
}
