import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import jdk.incubator.concurrent.StructuredTaskScope;

public class ReportGeneratorStructured implements ReportGenerator {
    
    private static ThreadFactory factory = Thread.ofVirtual()
            .name("virtual-thread-", 1)
            .factory();

    public Report generate(Region region) throws Exception {
        return switch(region) {
            case GLOBAL: {
                try(var scope = new StructuredTaskScope.ShutdownOnFailure("MainScope", factory)) {
                    var apac = scope.fork(() -> generate(Region.APAC));
                    var emea = scope.fork(() -> generate(Region.EMEA));
                    var nam = scope.fork(() -> generate(Region.NAM));
                    scope.join().throwIfFailed();
                    yield apac.resultNow().merge(emea.resultNow(), nam.resultNow());    
                }
            }
            default: yield delay(() -> Report.create(region));
        };
    }

    public static void main(String[] args) throws Exception {
        var thread = factory.newThread(() -> {
            try {
                System.out.println(new ReportGeneratorStructured().generate(Region.GLOBAL));    
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        TimeUnit.MILLISECONDS.sleep(500);
        thread.interrupt();
        thread.join();
        System.out.println("DONE");
    }

}