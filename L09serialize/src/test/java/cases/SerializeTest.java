package cases;

import com.massita.Serializer;
import exampleClasses.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SerializeTest {

    @DataProvider(name = "DecompositionData")

    public static Object[][] testData() {

        return new Object[][] {
                { new IntField(), "{\"intField\":0}" },
                { new ChildIntField(), "{\"childInt\":5,\"intField\":0}" },
                { new AllPrimitiveFields(), "{\"floatField\":1.1,\"longField\":100,\"doubleField\":5.5,\"charField\":c,\"booleanField\":true,\"byteField\":3,\"intField\":4,\"shortField\":1}" },
                { new OneStringField(), "{\"stringField\":\"test\"}" },
                { new ObjectField(), "{\"oneStringField\":{\"stringField\":\"test\"},\"intField\":null}" },
                { new ArrayField(), "{\"arrayField\":[1,2,3]}" },
                { new CollectionField(), "{\"listOfStringField\":[\"one\",\"two\"]}" },
                { new TransientField(), "{\"normalField\":0}" }
        };
    }

    @Test(dataProvider = "DecompositionData")
    public void test(Object objectToSerialize, String json) throws IllegalAccessException {

        Serializer serializer = new Serializer();

        String result = serializer.toJson(objectToSerialize).toString();

        Assert.assertEquals(result, json);
    }

}
