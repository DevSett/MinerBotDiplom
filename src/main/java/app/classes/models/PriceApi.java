package app.classes.models;

import lombok.Getter;
import lombok.Setter;

public class PriceApi {
    @Getter
    @Setter
    private String ask;

    @Getter
    @Setter
    private String bid;

    public PriceApi(String ask, String bid) {
        this.ask = ask;
        this.bid = bid;
    }
}
