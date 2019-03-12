import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoneyExchangableTest {

    Set<MoneyBox> onlyDollarBoxes;
    Set<MoneyBox> onlyRublesBoxes;
    Set<MoneyBox> mixedCurrencyBoxes;
    Map<BankNote, Integer> bunchOfDollarNotes;
    Map<BankNote, Integer> bunchOfRublesNotes;

    @Before
    public void beforeEach() {
        MoneyBox usd1Box = new MoneyBoxImpl(BankNote.USD1);
        MoneyBox usd5Box = new MoneyBoxImpl(BankNote.USD5);
        MoneyBox usd10Box = new MoneyBoxImpl(BankNote.USD10);

        MoneyBox rur100Box = new MoneyBoxImpl(BankNote.RUR100);
        MoneyBox rur500Box = new MoneyBoxImpl(BankNote.RUR500);
        MoneyBox rur1000Box = new MoneyBoxImpl(BankNote.RUR1000);

        onlyDollarBoxes = Stream.of(usd1Box, usd5Box, usd10Box)
                .collect(Collectors.toSet());
        onlyRublesBoxes = Stream.of(rur100Box, rur500Box, rur1000Box)
                .collect(Collectors.toSet());
        mixedCurrencyBoxes = Stream.of(usd1Box, usd5Box, usd10Box, rur100Box, rur500Box, rur1000Box)
                .collect(Collectors.toSet());

        bunchOfDollarNotes = new HashMap<>() {{
            put(BankNote.USD1, 10);
            put(BankNote.USD5, 10);
            put(BankNote.USD10, 10);
        }};

        bunchOfRublesNotes = new HashMap<>() {{
            put(BankNote.RUR100, 10);
            put(BankNote.RUR500, 10);
            put(BankNote.RUR1000, 10);
        }};


    }

    @Test(expected = RuntimeException.class)
    public void createAtmWithoutBoxes(){
        MoneyExchangeable atm = new Atm(null);
    }

    @Test(expected = RuntimeException.class)
    public void createAtmWithoutBoxes2(){
        MoneyExchangeable atm = new Atm(Collections.emptySet());
    }

    @Test
    public void putMoney() {
        MoneyExchangeable atm = new Atm(mixedCurrencyBoxes);

        Assert.assertTrue(atm.put(bunchOfDollarNotes));
        Assert.assertTrue(atm.put(bunchOfRublesNotes));


    }

    @Test
    public void getMoneySimple() {
        MoneyExchangeable atm = new Atm(mixedCurrencyBoxes);
        atm.put(bunchOfDollarNotes);

        Map<BankNote, Integer> expectedResult = new HashMap<>() {{
            put(BankNote.USD10, 10);
        }};

        Assert.assertEquals(atm.get(Currency.getInstance("USD"), 100), expectedResult);

    }

    @Test
    public void getMoneyMixed() {
        MoneyExchangeable atm = new Atm(mixedCurrencyBoxes);
        atm.put(bunchOfDollarNotes);
        atm.put(bunchOfRublesNotes);

        Map<BankNote, Integer> expectedResult = new HashMap<>() {{
            put(BankNote.USD10, 9);
            put(BankNote.USD5, 1);
            put(BankNote.USD1, 4);
        }};

        Assert.assertEquals(atm.get(Currency.getInstance("USD"), 99), expectedResult);

    }

    @Test(expected = RuntimeException.class)
    public void getTooManyMoneys() {
        MoneyExchangeable atm = new Atm(mixedCurrencyBoxes);
        atm.put(bunchOfDollarNotes);
        atm.put(bunchOfRublesNotes);

        atm.get(Currency.getInstance("USD"), 200);

    }

    @Test
    public void getAllMoneyMixed() {
        MoneyExchangeable atm = new Atm(mixedCurrencyBoxes);
        atm.put(bunchOfDollarNotes);
        atm.put(bunchOfRublesNotes);

        Assert.assertEquals(atm.getRemaindedValue(Currency.getInstance("USD")), 160);
        Assert.assertEquals(atm.getRemaindedValue(Currency.getInstance("RUR")), 16000);


    }



}