package department;

import atm.MoneyBoxChain;
import atm.MoneyExchangeable;
import common.BankNote;

import java.util.Currency;
import java.util.Map;
import java.util.Set;

public interface ExchangeDepartment {
    /**
     * Create and return new {@link MoneyExchangeable} with specified {@link MoneyBoxChain} and initial set of {@link BankNote}
     * @param boxes - set of {@link MoneyBoxChain}es, that will be installed in new {@link MoneyExchangeable}
     * @param initialMoneyBundle - initial set of {@link BankNote} that will put in new one
     * @return new {@link MoneyExchangeable}
     * @throws RuntimeException if initialMoneyBundle can't be stored in boxes
     */
    MoneyExchangeable installNew(Set<MoneyBoxChain> boxes, Map<BankNote, Integer> initialMoneyBundle) throws RuntimeException;

    /**
     * return map with all value with requested Currency that contains in all {@link MoneyExchangeable} created by current department
     */
    int getRemaindedValue(Currency currency);

    /**
     * reset all {@link MoneyExchangeable} to state they were created(return initialMoneyBundle in each one)
     */
    void resetAll();

}
