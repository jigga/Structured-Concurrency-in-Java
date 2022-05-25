import java.time.Duration;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jdk.incubator.concurrent.StructuredTaskScope;

public class StructuredTaskScopeHaystack {
    
    private static ThreadFactory factory = Thread.ofVirtual()
        .name("virtual-thread-", 1)
        .factory();
    private static Random random = new Random();
    private static long haystackWithNeedle = random.nextLong(9);

public static void main(String[] args) throws Exception {
    try(var scope = new StructuredTaskScope.ShutdownOnSuccess<Long>("Main", factory)) {
        Stream.iterate(0, i -> i + 1)
            .limit(10)
            .map(i -> scope.fork(findNeedleTask(i)))
            .collect(Collectors.toList());
        scope.join();
        System.out.println("Found needle in the haystack: " + scope.result());
    }
}

private static Callable<Long> findNeedleTask(long haystackId) {
    return () -> {
        try {
            Thread.sleep(Duration.ofSeconds(random.nextLong(4)));    
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " interrupted while looking for a needle in the haystack " + haystackId);
            throw e;
        }
        if (haystackId != haystackWithNeedle) {
            System.out.println("No needle in haystack " + haystackId);
            throw new RuntimeException("No needle in haystack " + haystackId);
        }
        return haystackId;
    };
}

}
