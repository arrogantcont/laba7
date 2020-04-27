package commons.commands;

import commons.model.Ticket;
import lombok.Data;

@Data
public class Add_IF_MaxCommand extends Command {
    private Ticket ticket;

    public Add_IF_MaxCommand(Ticket ticket) {
        super("add_if_max");
        this.ticket = ticket;
    }
}
