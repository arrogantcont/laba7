package commons.commands;

import commons.User;
import commons.model.Ticket;
import lombok.Data;

@Data
public class UpdateCommand extends Command {
    Ticket ticket;

    public UpdateCommand(Ticket ticket, User user) {
        super("update", user);
        this.ticket = ticket;
    }
}
