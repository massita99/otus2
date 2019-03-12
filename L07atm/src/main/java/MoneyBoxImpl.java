import lombok.Getter;
import lombok.NonNull;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

public class MoneyBoxImpl implements MoneyBox, Comparable<MoneyBoxImpl> {

    @Getter
    private int count = 0;

    @Getter
    private BankNote banknote;

    private MoneyBox nextBox;

    public MoneyBoxImpl(BankNote banknote) {
        this.banknote = banknote;
    }

    private void put(int count) {
        this.count += count;
    }


    @Override
    public boolean put(@NonNull Map<BankNote, Integer> moneyBundle) {
        Integer notesOfThisType = moneyBundle.get(banknote);
        if (notesOfThisType != null) {
            put(notesOfThisType);
            moneyBundle.remove(banknote);
        }
        if (moneyBundle.isEmpty()) {
            return true;
        }
        if (nextBox != null) {
            return nextBox.put(moneyBundle);
        }
        return false;
    }

    @Override
    public Map<BankNote, Integer> get(Currency currency, int value) throws RuntimeException {
        int thisBankNoteCountToResult = 0;
        Map<BankNote, Integer> result = new HashMap<>();
        if (this.banknote.getCurrency().equals(currency)) {
            //While there are banknote in the current box and remained requested value more than 1 note
            while (value >= banknote.getValue() && count > 0) {
                value -= banknote.getValue();
                count--;
                thisBankNoteCountToResult++;
            }
            if (thisBankNoteCountToResult > 0) {
                result.put(banknote, thisBankNoteCountToResult);
            }
        }
        // If not all requested money returned try next box
        if (value > 0 && nextBox != null) {

            Map<BankNote, Integer> nextBoxResult = nextBox.get(currency, value);
            nextBoxResult.forEach((k, v) -> result.merge(k, v, (v1, v2) -> v1 + v2));

        }
        return result;
    }

    @Override
    public Map<BankNote, Integer> getAll(Currency currency) {
        Map<BankNote, Integer> result = new HashMap<>();
        if (this.banknote.getCurrency().equals(currency) && count > 0) {
            result.put(banknote, count);
        }
        if (nextBox != null) {
            Map<BankNote, Integer> nextBoxResult = nextBox.getAll(currency);
            nextBoxResult.forEach((k, v) -> result.merge(k, v, (v1, v2) -> v1 + v2));
        }
        return result;

    }

    @Override
    public void setNext(MoneyBox nextBox) {
        this.nextBox = nextBox;
    }

    @Override
    public int compareTo(MoneyBoxImpl o) {
        return this.banknote.compareTo(o.getBanknote());
    }
}
