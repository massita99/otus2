package atm;

import common.BankNote;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoneyExchangableTest {

    Set<MoneyBoxChain> onlyDollarBoxes;
    Set<MoneyBoxChain> onlyRublesBoxes;
    Set<MoneyBoxChain> mixedCurrencyBoxes;
    Map<BankNote, Integer> bunchOfDollarNotes;
    Map<BankNote, Integer> bunchOfRublesNotes;

    @Before
    public void beforeEach() {
        MoneyBoxChain usd1Box = new MoneyBoxChainImpl(BankNote.USD1);
        MoneyBoxChain usd5Box = new MoneyBoxChainImpl(BankNote.USD5);
        MoneyBoxChain usd10Box = new MoneyBoxChainImpl(BankNote.USD10);

        MoneyBoxChain rur100Box = new MoneyBoxChainImpl(BankNote.RUR100);
        MoneyBoxChain rur500Box = new MoneyBoxChainImpl(BankNote.RUR500);
        MoneyBoxChain rur1000Box = new MoneyBoxChainImpl(BankNote.RUR1000);

        MoneyBoxChain usd1Box2 = new MoneyBoxChainImpl(BankNote.USD1);
        MoneyBoxChain usd5Box2 = new MoneyBoxChainImpl(BankNote.USD5);
        MoneyBoxChain usd10Box2 = new MoneyBoxChainImpl(BankNote.USD10);

        MoneyBoxChain rur100Box2 = new MoneyBoxChainImpl(BankNote.RUR100);
        MoneyBoxChain rur500Box2 = new MoneyBoxChainImpl(BankNote.RUR500);
        MoneyBoxChain rur1000Box2 = new MoneyBoxChainImpl(BankNote.RUR1000);

        onlyDollarBoxes = Stream.of(usd1Box, usd5Box, usd10Box)
                .collect(Collectors.toSet());
        onlyRublesBoxes = Stream.of(rur100Box, rur500Box, rur1000Box)
                .collect(Collectors.toSet());
        mixedCurrencyBoxes = Stream.of(usd1Box2, usd5Box2, usd10Box2, rur100Box2, rur500Box2, rur1000Box2)
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

    @Test
    public void stateSaveAndGet() {
        MoneyExchangeable atm = new Atm(mixedCurrencyBoxes);
        atm.put(bunchOfDollarNotes);
        //Save memento
        Memento memento = atm.getState();
        //Do some work
        atm.put(bunchOfRublesNotes);
        //Check work
        Assert.assertEquals(atm.getRemaindedValue(Currency.getInstance("RUR")), 16000);
        //Return memento
        atm.setState(memento);
        Assert.assertEquals(atm.getRemaindedValue(Currency.getInstance("USD")), 160);
        Assert.assertEquals(atm.getRemaindedValue(Currency.getInstance("RUR")), 0);
    }

}