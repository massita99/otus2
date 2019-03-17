package atm;

import common.BankNote;

import java.util.Currency;
import java.util.Map;

public interface MoneyBoxChain {

    /**
     * Try to store money in the current Box or delegate to next one
     * @param moneyBundle of {@link BankNote}
     * @return true if money was stored
     * or false if not
     */
    boolean put(Map<BankNote, Integer> moneyBundle);

    /**
     * Return {@link Map} of {@link BankNote} type with count of them
     * for value requested in params
     * If money not enough it return max that it can in requested Currency
     * @param currency of {@link BankNote}
     * @param value of requested Money
     * @return {@link BankNote} of requested value
     */
    Map<BankNote, Integer> get(Currency currency, int value);

    /**
     * Return {@link Map} of {@link BankNote} type of all stored in chain notes
     * @param currency of {@link BankNote}
     * @return {@link BankNote} of requested value
     */
    Map<BankNote, Integer> popAll(Currency currency);

    /**
     * Return {@link Map} of {@link BankNote} type of all stored in chain notes
     * @return {@link BankNote} of requested value
     */
    Map<BankNote, Integer> popAll();

    /**
     * clean current Box and all it chain
     */
    void cleanAll();

    /**
     * Set next atm.MoneyBoxChain if currnet can't handle all operations
     * @param nextBox
     */
    void setNext(MoneyBoxChain nextBox);
}
