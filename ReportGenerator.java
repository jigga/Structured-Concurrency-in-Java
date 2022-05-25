import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface ReportGenerator {

    Report generate(Region region) throws Exception;

    default <R> R delay(Supplier<R> supplier) {
        try {
            Thread.sleep(new Random().nextLong(3000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return supplier.get();
    }

    default <T> List<T> merge(List<T>... lists) {
        return Arrays.stream(lists)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    default Report report(Region region, long delay) {
        long startTime = System.nanoTime();
        if (delay > 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // ... the code being measured ...    
        long elapsedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        System.out.println(Thread.currentThread().getName() + " - " + region + " report generated in " + elapsedTime + "ms.");
        return new Report(List.of(region.toString() + "," + Thread.currentThread().getName()));
    }

}