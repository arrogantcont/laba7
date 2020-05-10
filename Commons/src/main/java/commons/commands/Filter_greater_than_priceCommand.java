package commons.commands;

import commons.User;
import lombok.Data;

@Data
public class Filter_greater_than_priceCommand extends Command {
    int price;

    public Filter_greater_than_priceCommand(int price, User user) {
        super("filter_greater_than_price", user);
        this.price = price;
    }
}