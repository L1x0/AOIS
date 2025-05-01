package by.astakhau.formminimize;

import java.util.*;
import java.util.stream.Collectors;

public class GluingCNFCalc {
    private final String cnfFormula;

    public GluingCNFCalc(String cnfFormula) {
        this.cnfFormula = cnfFormula.replace(" ", "").replace("\t", "");
    }

    public String minimize() {
        List<Clause> originalClauses = parseClauses();
        if (originalClauses.isEmpty()) return "( )";

        Set<Clause> primeImplicants = findPrimeImplicants(originalClauses);
        Set<Clause> minimal = removeRedundantWithTable(originalClauses, primeImplicants);
        return formatResult(minimal);
    }

    private List<Clause> parseClauses() {
        return Arrays.stream(cnfFormula.split("\\)&\\("))
                .map(part -> part.replaceAll("[()]", ""))
                .map(Clause::new)
                .collect(Collectors.toList());
    }

    private Set<Clause> findPrimeImplicants(List<Clause> clauses) {
        Set<Clause> primes = new HashSet<>();
        List<Clause> current = new ArrayList<>(clauses);
        boolean changed;

        do {
            changed = false;
            Set<Clause> used = new HashSet<>();
            List<Clause> nextGen = new ArrayList<>();

            for (int i = 0; i < current.size(); i++) {
                Clause c1 = current.get(i);
                for (int j = i + 1; j < current.size(); j++) {
                    Clause c2 = current.get(j);
                    if (c1.canMerge(c2)) {
                        Clause merged = c1.merge(c2);
                        nextGen.add(merged);
                        used.add(c1);
                        used.add(c2);
                        changed = true;
                    }
                }
                if (!used.contains(c1)) {
                    primes.add(c1);
                }
            }

            current = nextGen;
        } while (changed);

        primes.addAll(current);
        return primes;
    }

    private Set<Clause> removeRedundantWithTable(List<Clause> original, Set<Clause> primes) {
        if (primes.isEmpty()) return new HashSet<>(original);

        Map<Clause, Set<Clause>> coverageTable = new LinkedHashMap<>();
        for (Clause prime : primes) {
            coverageTable.put(prime, new HashSet<>());
            for (Clause orig : original) {
                if (prime.covers(orig)) {
                    coverageTable.get(prime).add(orig);
                }
            }
        }

        printCoverageTable(original, coverageTable);
        return primes;
    }

    private void printCoverageTable(List<Clause> original, Map<Clause, Set<Clause>> coverageTable) {
        if (original.isEmpty() || coverageTable.isEmpty()) {
            System.out.println("No data to display.");
            return;
        }

        // Расчет ширины колонок
        int implicantWidth = coverageTable.keySet().stream()
                .mapToInt(c -> c.toString().length())
                .max()
                .orElse(20);

        List<Integer> columnWidths = original.stream()
                .map(c -> Math.max(c.toString().length(), 5))
                .collect(Collectors.toList());

        // Заголовок таблицы
        System.out.print("\n| Импликанты " + repeatChar(' ', Math.max(implicantWidth - 20, 0)) + "|");
        for (int i = 0; i < original.size(); i++) {
            System.out.print(formatCellCenter(original.get(i).toString(), columnWidths.get(i)));
        }
        System.out.println();

        // Разделительная линия
        System.out.print(repeatChar('-', Math.max(implicantWidth + 2, 0)));
        for (int width : columnWidths) {
            System.out.print(repeatChar('-', Math.max(width + 1, 0)));
        }
        System.out.println();

        // Тело таблицы
        for (Clause prime : coverageTable.keySet()) {
            System.out.print(formatCell(prime.toString(), implicantWidth));
            for (int i = 0; i < original.size(); i++) {
                Clause constit = original.get(i);
                String mark = coverageTable.get(prime).contains(constit) ? "X" : "";
                System.out.print(formatCellCenter(mark, columnWidths.get(i)));
            }
            System.out.println();
        }
        System.out.println();
    }

    private String formatCell(String content, int width) {
        return String.format("| %-" + width + "s", content);
    }

    private String formatCellCenter(String content, int width) {
        int totalPadding = Math.max(width - content.length(), 0);
        int leftPadding = totalPadding / 2;
        int rightPadding = totalPadding - leftPadding;
        String leftSpace = String.join("", Collections.nCopies(leftPadding, " "));
        String rightSpace = String.join("", Collections.nCopies(rightPadding, " "));
        return " " + leftSpace + content + rightSpace + " |";
    }

    private String repeatChar(char c, int count) {
        if (count <= 0) return "";
        return new String(new char[count]).replace('\0', c);
    }

    private String formatResult(Set<Clause> clauses) {
        return clauses.stream()
                .map(Clause::toString)
                .collect(Collectors.toList())
                .stream()
                .sorted()
                .collect(Collectors.joining(" & ", "(", ")"));
    }

    static class Clause {
        private final Map<Character, Boolean> literals = new LinkedHashMap<>();

        Clause(String expr) {
            for (String literal : expr.split("\\|")) {
                literal = literal.trim();
                if (literal.startsWith("!")) {
                    literals.put(literal.charAt(1), false);
                } else {
                    literals.put(literal.charAt(0), true);
                }
            }
        }

        Clause(Map<Character, Boolean> literals) {
            this.literals.putAll(literals);
        }

        boolean canMerge(Clause other) {
            int differences = 0;
            Set<Character> allVars = new HashSet<>(literals.keySet());
            allVars.addAll(other.literals.keySet());

            for (Character var : allVars) {
                Boolean v1 = literals.get(var);
                Boolean v2 = other.literals.get(var);
                if (!Objects.equals(v1, v2)) differences++;
            }

            return differences == 1;
        }

        Clause merge(Clause other) {
            Map<Character, Boolean> merged = new LinkedHashMap<>();
            Set<Character> allVars = new HashSet<>(literals.keySet());
            allVars.addAll(other.literals.keySet());

            for (Character var : allVars) {
                Boolean v1 = literals.get(var);
                Boolean v2 = other.literals.get(var);
                if (Objects.equals(v1, v2)) {
                    merged.put(var, v1);
                }
            }
            return new Clause(merged);
        }

        boolean covers(Clause other) {
            return other.literals.entrySet().stream()
                    .allMatch(e -> {
                        Boolean value = this.literals.get(e.getKey());
                        return value == null || value == e.getValue();
                    });
        }

        @Override
        public String toString() {
            return literals.entrySet().stream()
                    .map(e -> e.getValue() ? e.getKey().toString() : "!" + e.getKey())
                    .collect(Collectors.joining(" | ", "(", ")"));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Clause clause = (Clause) o;
            return literals.equals(clause.literals);
        }

        @Override
        public int hashCode() {
            return literals.hashCode();
        }
    }
}