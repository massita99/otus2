import lombok.Getter;

import java.util.Currency;

public enum BankNote {

    RUR1000(Currency.getInstance("RUR"), 1000),
    RUR500(Currency.getInstance("RUR"), 500),
    RUR100(Currency.getInstance("RUR"), 100),



    USD10(Currency.getInstance("USD"), 10),
    USD5(Currency.getInstance("USD"), 5),
    USD1(Currency.getInstance("USD"), 1);

    @Getter
    private Currency currency;

    @Getter
    private int value;

    BankNote(Currency currency, int value) {
        this.currency = currency;
        this.value = value;
    }

}
