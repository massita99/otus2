package atm;

import common.BankNote;

import java.util.Currency;
import java.util.Map;

public interface MoneyExchangeable extends Statable {

    /**
     * Return {@link Map} of {@link BankNote} type with count of them
     * for value requested in params
     * @param currency of {@link BankNote}
     * @param value of requested Money
     * @return {@link BankNote} of requested value
     * @throws RuntimeException if not enough Money of requested Currency
     */
    Map<BankNote, Integer> get(Currency currency, int value) throws RuntimeException;

    /**
     * Try to store moneyBundle in current moneyExchanger
     * @param moneyBundle of {@link BankNote}
     * @return true if money was store
     * or false if not
     */
    boolean put(Map<BankNote, Integer> moneyBundle);

    /**
     * Return remainded value of requested Currency money in current moneyExchanger
     * @return value of Money
     */
    int getRemaindedValue(Currency currency);

}
