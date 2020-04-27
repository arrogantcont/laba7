package commons.commands;

import commons.model.Ticket;
import lombok.Data;

@Data
public class UpdateCommand extends Command {
    Ticket ticket;

    public UpdateCommand(Ticket ticket) {
        super("update");
        this.ticket = ticket;
    }
}
