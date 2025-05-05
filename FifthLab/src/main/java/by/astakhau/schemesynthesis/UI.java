package by.astakhau.schemesynthesis;

public class UI {
    public static void doProgram() {
        Counter counter = new Counter();

        System.out.println("\nКНФ первого сигнала:");
        System.out.print(counter.getFirstSignalCNF().isEmpty()
                ? "всегда 1"
                : reviewOfResult(new GluingCNF(counter.getFirstSignalCNF()).minimize()));

        System.out.println("\nКНФ второго сигнала:");
        System.out.print(reviewOfResult(new GluingCNF(counter.getSecondSignalCNF()).minimize()));

        System.out.println("\nКНФ третьего сигнала:");
        System.out.print(reviewOfResult(new GluingCNF(counter.getThirdSignalCNF()).minimize()));
    }

    private static String reviewOfResult(String result) {
        result = result.replaceAll("a", "q2");
        result = result.replaceAll("b", "q1");
        result = result.replaceAll("c", "q0");
        result = result.replaceAll("d", "t2");
        result = result.replaceAll("e", "t1");
        result = result.replaceAll("f", "t0");

        return result;
    }
}
