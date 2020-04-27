package commons.commands;

import commons.model.Ticket;
import lombok.Data;

@Data
public class AddCommand extends Command {
    private Ticket ticket;

    public AddCommand(Ticket ticket) {
        super("add");
        this.ticket = ticket;
    }
}
