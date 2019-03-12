
import java.util.*;
import java.util.stream.Collectors;

public class Atm implements MoneyExchangeable {

    public Atm(Set<MoneyBox> moneyBoxes) {
        if (moneyBoxes == null || moneyBoxes.isEmpty()) {
            throw new RuntimeException("Can't create atm without MoneyBoxes");
        }
        List<MoneyBox> sortedMoneyBoxes = moneyBoxes
                .stream()
                .sorted()
                .collect(Collectors.toList());
        MoneyBox previousBox = null;
        for (MoneyBox box : sortedMoneyBoxes) {
            if (mainMoneyBox == null) {
                mainMoneyBox = box;
            } else {
                previousBox.setNext(box);
            }
            previousBox = box;
        }
    }

    private MoneyBox mainMoneyBox;


    @Override
    public Map<BankNote, Integer> get(Currency currency, int value) {
        Map<BankNote, Integer> result = mainMoneyBox.get(currency, value);

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
        return mainMoneyBox.put(moneyBundle);
    }

    @Override
    public int getRemaindedValue(Currency currency) {
        return mainMoneyBox.getAll(currency).entrySet()
                .stream().map(entry -> entry.getKey().getValue() * entry.getValue())
                .reduce(0, Integer::sum);
    }
}
