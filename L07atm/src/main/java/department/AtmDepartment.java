package department;

import atm.*;
import common.BankNote;

import java.util.*;

public class AtmDepartment implements ExchangeDepartment {

    private Map<MoneyExchangeable, Memento> atmsWithInitialState = new HashMap<>();

    @Override
    public MoneyExchangeable installNew(Set<MoneyBoxChain> boxes, Map<BankNote, Integer> initialMoneyBundle) throws RuntimeException {
        MoneyExchangeable atm = new Atm(boxes);
        if (atm.put(initialMoneyBundle)) {
            atmsWithInitialState.put(atm, atm.getState());
            return atm;
        } else {
            throw new RuntimeException("Can't create atm. InitialMoneyBundle can't fit in boxes");
        }
    }

    @Override
    public int getRemaindedValue(Currency currency) {
        return atmsWithInitialState.keySet().stream()
                .map(atm -> atm.getRemaindedValue(currency))
                .reduce(0, Integer::sum);
    }

    @Override
    public void resetAll() {
        atmsWithInitialState.forEach(Statable::setState);
    }
}
