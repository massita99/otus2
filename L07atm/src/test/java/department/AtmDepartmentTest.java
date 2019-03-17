package department;

import atm.MoneyBoxChain;
import atm.MoneyBoxChainImpl;
import atm.MoneyExchangeable;
import common.BankNote;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AtmDepartmentTest {

    Set<MoneyBoxChain> onlyDollarBoxes;
    Set<MoneyBoxChain> onlyRublesBoxes;
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

        onlyDollarBoxes = Stream.of(usd1Box, usd5Box, usd10Box)
                .collect(Collectors.toSet());
        onlyRublesBoxes = Stream.of(rur100Box, rur500Box, rur1000Box)
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

    @Test
    public void installNew() {
        ExchangeDepartment department = new AtmDepartment();
        MoneyExchangeable atm = department.installNew(onlyDollarBoxes, bunchOfDollarNotes);

        Assert.assertEquals(atm.getRemaindedValue(Currency.getInstance("USD")), 160);
    }

    @Test(expected = RuntimeException.class)
    public void installNewNotRightNotes() {
        ExchangeDepartment department = new AtmDepartment();
        MoneyExchangeable atm = department.installNew(onlyDollarBoxes, bunchOfRublesNotes);
    }

    @Test
    public void getRemaindedValue() {
        ExchangeDepartment department = new AtmDepartment();
        MoneyExchangeable atm1 = department.installNew(onlyDollarBoxes, bunchOfDollarNotes);
        MoneyExchangeable atm2 = department.installNew(onlyRublesBoxes, bunchOfRublesNotes);

        Assert.assertEquals(department.getRemaindedValue(Currency.getInstance("USD")), 160);
        Assert.assertEquals(department.getRemaindedValue(Currency.getInstance("RUR")), 16000);

    }

    @Test
    public void ResetState() {
        ExchangeDepartment department = new AtmDepartment();
        //Create two atm
        MoneyExchangeable atm1 = department.installNew(onlyDollarBoxes, bunchOfDollarNotes);
        MoneyExchangeable atm2 = department.installNew(onlyRublesBoxes, bunchOfRublesNotes);
        //Do some work
        atm1.put(new HashMap<>(Map.of(BankNote.USD10, 10)));
        atm2.put(new HashMap<>(Map.of(BankNote.RUR1000, 10)));
        //Check state
        Assert.assertEquals(department.getRemaindedValue(Currency.getInstance("USD")), 260);
        Assert.assertEquals(department.getRemaindedValue(Currency.getInstance("RUR")), 26000);
        //Reset state
        department.resetAll();
        //Check state
        Assert.assertEquals(department.getRemaindedValue(Currency.getInstance("USD")), 160);
        Assert.assertEquals(department.getRemaindedValue(Currency.getInstance("RUR")), 16000);


    }
}