package commons.commands;

import lombok.Data;

@Data
public class Filter_greater_than_priceCommand extends Command {
    int price;

    public Filter_greater_than_priceCommand(int price) {
        super("filter_greater_than_price");
        this.price = price;
    }
}