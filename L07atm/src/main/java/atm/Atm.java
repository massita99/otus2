package atm;

import common.BankNote;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class Atm implements MoneyExchangeable {

    private MoneyBoxChain mainMoneyBoxChain;

    public Atm(Set<MoneyBoxChain> moneyBoxChains) {
        if (moneyBoxChains == null || moneyBoxChains.isEmpty()) {
            throw new RuntimeException("Can't create atm without MoneyBoxes");
        }
        List<MoneyBoxChain> sortedMoneyBoxChains = moneyBoxChains
                .stream()
                .sorted()
                .collect(Collectors.toList());
        MoneyBoxChain previousBox = null;
        for (MoneyBoxChain box : sortedMoneyBoxChains) {
            if (mainMoneyBoxChain == null) {
                mainMoneyBoxChain = box;
            } else {
                previousBox.setNext(box);
            }
            previousBox = box;
        }
    }

    @Override
    public Map<BankNote, Integer> get(Currency currency, int value) {
        Map<BankNote, Integer> result = mainMoneyBoxChain.get(currency, value);

        Integer retrievedValue = result.entrySet()
                .stream().map(entry -> entry.getKey().getValue() * entry.getValue())
                .reduce(0, Integer::sum);
        //if get not right value (example% not enough money in all boxes)
        if (retrievedValue != value) {
            //Put it back
            this.put(result);
            throw new RuntimeException("Not enough money");
        }
        return result;
    }

    @Override
    public boolean put(Map<BankNote, Integer> moneyBundle) {
        return mainMoneyBoxChain.put(moneyBundle);
    }

    @Override
    public int getRemaindedValue(Currency currency) {
        return mainMoneyBoxChain.popAll(currency).entrySet()
                .stream().map(entry -> entry.getKey().getValue() * entry.getValue())
                .reduce(0, Integer::sum);
    }

    @Override
    public Memento getState() {
        return new AtmMemento(mainMoneyBoxChain.popAll());
    }

    @Override
    public void setState(Memento memento) {

        if (memento instanceof AtmMemento) {
            this.clean();
            this.put(((AtmMemento) memento).getSavedMoneyBundle());
        } else {
            throw new RuntimeException("Incorrect memento");
        }


    }

    private void clean() {
        mainMoneyBoxChain.cleanAll();
    }

    public class AtmMemento implements Memento {

        @Getter
        private Map<BankNote, Integer> savedMoneyBundle;

        AtmMemento(Map<BankNote, Integer> savedMoneyBundle) {
            this.savedMoneyBundle = new HashMap<>(savedMoneyBundle);

        }
    }

}
