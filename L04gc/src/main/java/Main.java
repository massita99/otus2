import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

    /*
    -Xms512m
    -Xmx512m
    -XX:+UseG1GC
    500/400 30 sec and 130 110 6300/50 = 120ms/s in start 33 GC
    100/70 29 SEC 443 CYC ON 200: 5108/200 25 26 GC 3475 for GC
    100/90 84 SEC 1325 CYC ON 800: 21500/800 27 97 GC 11847 for GC
    -XX:+UseConcMarkSweepGC
    500/400 26 sec and 110 6400/50 = 120ms/s in start 10GC
    100/70 28 SEC 379 CYC ON 200: 5134/200 25 7 GC 892 for GC
    100/90 80 SEC 1134 CYC ON 200: 19500/800 24 35 GC 950 for GC
    -XX:+UseSerialGC
    500/400 very fast on statrt 32 sec on 126 cyles 3200/50 = 64ms/s at start 8GC
    100/70 42 SEC 427 CYC ON 200: 2867/200 15 6 GC 635 for GC
    100/90 122 SEC 1282 CYC ON 800: 12770/800 17 33 GC 7229 for GC
    -XX:+UseParallelGC
    500/400 30 sec 100 cycles 5365/50 = 107ms/s at start 14GC
    100/70 28 SEC 344 CYC ON 200: 6908/200 35 11 GC 3700 for GC
    100/90 80 SEC 1029 CYC ON 800: 33000/800 42 63 GC 20000 for GC
    */

public class Main {

    static int NUMBER_OF_CREATE_PER_CYCLE = 100_000;
    static int NUMBER_OF_DELETE_PER_CYCLE = 90_000;
    static double CHANCE_TO_DELETE_OLD_OBJECT = 0.1;

    private static final Logger logger
            = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        GcStatCollector.installGCMonitoring();
        LinkedList<Object> oomCollector = new LinkedList<>();
        int number_of_cycle = 0;
        long start_time = System.currentTimeMillis();

        while (true) {
            logger.info("Cycle No " + ++number_of_cycle + " start");
            for (int i = 0; i < NUMBER_OF_CREATE_PER_CYCLE; i++) {
                oomCollector.add(new Object());
            }
            for (int i = 0; i < NUMBER_OF_DELETE_PER_CYCLE; i++) {
                if (Math.random() > CHANCE_TO_DELETE_OLD_OBJECT) {
                    oomCollector.remove();
                }
                else {
                    oomCollector.removeFirst();
                }
            }
            long finish_time = System.currentTimeMillis();
            long work_time = finish_time - start_time;
            logger.info("Cycle No " + number_of_cycle + ". Summary time spended is " + work_time);
        }
    }
}
