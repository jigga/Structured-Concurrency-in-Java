import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Report(List<String> data) {
    public Report merge(Report... reports) {
        return new Report(Stream.concat(Stream.of(this), Arrays.stream(reports))
            .map(Report::data)
            .flatMap(Collection::stream)
            .collect(Collectors.toList()));
    }

    public static Report create(Region region) {
        System.out.println(Thread.currentThread().getName() + " - " + region + " report generated.");
        return new Report(List.of(region.toString(), Thread.currentThread().getName()));
    }
}