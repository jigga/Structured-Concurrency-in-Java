import java.time.Duration;
import java.util.Random;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jdk.incubator.concurrent.StructuredTaskScope;

public class StructuredTaskScopeInterrupt {
    
    private static ThreadFactory factory = Thread.ofVirtual()
        .name("virtual-thread-", 1)
        .factory();
    private static Random random = new Random();

    public static void main(String[] args) throws Exception {
        var thread = factory.newThread(() -> {
            try(var scope = new StructuredTaskScope<Object>("Main", factory)) {
                Stream.iterate(0, i -> i + 1).limit(10)
                    .map(i -> scope.fork(() -> {
                        try {
                            Thread.sleep(Duration.ofSeconds(random.nextLong(3)));
                        } catch (InterruptedException e) {
                            System.out.println(Thread.currentThread().getName() + " interrupted while executing task " + i);
                        }
                        System.out.println("Task " + i + " completed");
                        return null;
                    }))
                    .collect(Collectors.toList());
                scope.join();
            } catch(InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " interrupted while waiting for subtasks to finish");
            }
        });
        thread.start();
        Thread.sleep(Duration.ofSeconds(1));
        thread.interrupt();
        thread.join();
        System.out.println("DONE");
    }

}
