import java.util.ArrayList;
import java.util.List;

public class GcUtil {

    public static void tryToAllocateAllAvailableMemory() {
        try {
            final List<Object[]> allocations = new ArrayList<>();
            int size;
            while ((size = (int) Runtime.getRuntime().freeMemory()) > 0) {
                Object[] part = new Object[Math.min(size, Integer.MAX_VALUE)];
                allocations.add(part);
            }
        } catch (OutOfMemoryError e) {
            System.out.println("catch expected exception: " + e.getMessage());
        }
        System.gc();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
