import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ReportGeneratorConcurrent implements ReportGenerator {
    
private final ExecutorService executor = Executors.newCachedThreadPool();

public Report generate(Region region) throws Exception {
    return switch(region) {
        case GLOBAL: {
            var apac = executor.submit(() -> generate(Region.APAC));
            var emea = executor.submit(() -> generate(Region.EMEA));
            var nam = executor.submit(() -> generate(Region.NAM));
            yield apac.get().merge(emea.get(), nam.get());
        }
        default: yield delay(() -> Report.create(region));
    };
}

    public static void main(String[] args) throws Exception {
        // System.out.println(new ReportGeneratorConcurrent().generate(Region.GLOBAL));

        var thread = new Thread(() -> {
            try {
                System.out.println(new ReportGeneratorConcurrent().generate(Region.GLOBAL));    
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