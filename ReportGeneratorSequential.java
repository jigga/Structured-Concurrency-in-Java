public class ReportGeneratorSequential implements ReportGenerator {
    
    public Report generate(Region region) {
        return switch(region) {
            case GLOBAL: {
                var apac = generate(Region.APAC);
                var emea = generate(Region.EMEA);
                var nam = generate(Region.NAM);
                yield apac.merge(emea, nam);
            }
            default: yield delay(() -> Report.create(region));
        };
    }

    public static void main(String[] args) {
        System.out.println(new ReportGeneratorSequential().generate(Region.GLOBAL));
    }

}
