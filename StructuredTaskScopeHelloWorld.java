import java.time.Duration;
import java.util.concurrent.Callable;

import jdk.incubator.concurrent.StructuredTaskScope;

public class StructuredTaskScopeHelloWorld {
    
    public static void main(String[] args) throws Exception {
        var factory = Thread.ofVirtual()
            .name("virtual-thread-", 1)
            .factory();
        try(var scope = new StructuredTaskScope<String>("MainScope", factory)) {
            var minute = Duration.ofSeconds(3);
            var hello = scope.fork(delay(minute, () -> "Hello"));
            var world = scope.fork(delay(minute, () -> "World"));
            scope.join();
            var greeting = String.format(
                "%s, %s!",
                hello.resultNow(),
                world.resultNow()
            );
            System.out.println(greeting);
        }
    }

    static <T> Callable<T> delay(Duration duration, Callable<T> delegate) {
        return () -> {
            Thread.sleep(duration);
            return delegate.call();
        };
    }

}
