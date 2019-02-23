import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BaseCacheEngineTest {


    private CacheEngine testEngine;

    @Before
    public void init() {
        testEngine = new SoftCacheEngineImpl(0, 0, false);
    }

    @Test
    public void baseTest() {

        //кладем 1
        testEngine.put(new MyElement(1,1));
        //Проверяем
        MyElement extractedElement = testEngine.get(1);
        Assert.assertEquals(extractedElement.getValue(), 1);
        Assert.assertEquals(testEngine.getMissCount(), 0);
        Assert.assertEquals(testEngine.getHitCount(), 1);
        //Преоверям нет ли чего-то лишнего
        MyElement missedElement = testEngine.get(5);
        Assert.assertNull(missedElement);
        Assert.assertEquals(testEngine.getMissCount(),1);
    }

    @Test
    public void testThatCacheCleanedWhenLowMemory() throws InterruptedException {
        //наполняем кэш
        int size = 1000;
        for (int i = 0; i < size; i++) {
            testEngine.put(new MyElement<>(i, "String: " + i));
        }
        //забиваем всю память
        GcUtil.tryToAllocateAllAvailableMemory();
        //Проверяем
        for (int i = 0; i < size; i++) {
            MyElement<Integer, String> element = testEngine.get(i);
        }

        Assert.assertEquals(size, testEngine.getMissCount());
    }

}