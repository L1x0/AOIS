package by.astakhau.formminimize;

import java.util.*;
import java.util.stream.Collectors;

public class GluingDNFCalc {
    private final String dnfFormula;

    public GluingDNFCalc(String dnfFormula) {
        this.dnfFormula = dnfFormula.replace(" ", "").replace("\t", "");
    }

    public String minimize() {
        List<Term> originalTerms = parseTerms();
        Set<Term> primeImplicants = findPrimeImplicants(originalTerms);
        Set<Term> minimal = removeRedundantWithTable(originalTerms, primeImplicants);
        return formatResult(minimal);
    }

    private List<Term> parseTerms() {
        return Arrays.stream(dnfFormula.split("\\)\\|\\("))
                .map(part -> part.replaceAll("[()]", ""))
                .map(Term::new)
                .collect(Collectors.toList());
    }

    private Set<Term> findPrimeImplicants(List<Term> terms) {
        Set<Term> primes = new HashSet<>();
        List<Term> current = new ArrayList<>(terms);
        boolean changed;

        do {
            changed = false;
            Set<Term> used = new HashSet<>();
            List<Term> nextGen = new ArrayList<>();

            for (int i = 0; i < current.size(); i++) {
                Term t1 = current.get(i);
                for (int j = i + 1; j < current.size(); j++) {
                    Term t2 = current.get(j);
                    if (t1.canMerge(t2)) {
                        Term merged = t1.merge(t2);
                        nextGen.add(merged);
                        used.add(t1);
                        used.add(t2);
                        changed = true;
                    }
                }
                if (!used.contains(t1)) {
                    primes.add(t1);
                }
            }

            current = nextGen;
        } while (changed);

        primes.addAll(current);
        return primes;
    }

    private Set<Term> removeRedundantWithTable(List<Term> original, Set<Term> primes) {
        if (primes.isEmpty()) return new HashSet<>(original);

        Map<Term, Set<Term>> coverageTable = new LinkedHashMap<>();
        for (Term prime : primes) {
            coverageTable.put(prime, new HashSet<>());
            for (Term orig : original) {
                if (prime.covers(orig)) {
                    coverageTable.get(prime).add(orig);
                }
            }
        }

        printCoverageTable(original, coverageTable);
        return primes;
    }

    private void printCoverageTable(List<Term> original, Map<Term, Set<Term>> coverageTable) {
        if (original.isEmpty() || coverageTable.isEmpty()) {
            System.out.println("No data to display.");
            return;
        }

        int implicantWidth = coverageTable.keySet().stream()
                .mapToInt(t -> t.toString().length())
                .max()
                .orElse(20);

        List<Integer> columnWidths = original.stream()
                .map(t -> Math.max(t.toString().length(), 5))
                .collect(Collectors.toList());

        System.out.print("\n| Импликанты " + repeatChar(' ', Math.max(implicantWidth - 18, 0)) + "|");
        for (int i = 0; i < original.size(); i++) {
            System.out.print(formatCellCenter(original.get(i).toString(), columnWidths.get(i)));
        }
        System.out.println();

        System.out.print(repeatChar('-', Math.max(implicantWidth + 2, 0)));
        for (int width : columnWidths) {
            System.out.print(repeatChar('-', Math.max(width + 1, 0)));
        }
        System.out.println();

        for (Term prime : coverageTable.keySet()) {
            System.out.print(formatCell(prime.toString(), implicantWidth));
            for (int i = 0; i < original.size(); i++) {
                Term term = original.get(i);
                String mark = coverageTable.get(prime).contains(term) ? "X" : "";
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

    private String formatResult(Set<Term> terms) {
        return terms.stream()
                .map(Term::toString)
                .sorted()
                .collect(Collectors.joining(" | ", "(", ")"));
    }

    static class Term {
        private final Map<Character, Boolean> literals = new LinkedHashMap<>();

        Term(String expr) {
            for (String literal : expr.split("&")) {
                literal = literal.trim();
                if (literal.startsWith("!")) {
                    literals.put(literal.charAt(1), false);
                } else {
                    literals.put(literal.charAt(0), true);
                }
            }
        }

        Term(Map<Character, Boolean> literals) {
            this.literals.putAll(literals);
        }

        boolean canMerge(Term other) {
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

        Term merge(Term other) {
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
            return new Term(merged);
        }

        boolean covers(Term other) {
            return this.literals.entrySet().stream()
                    .allMatch(e -> {
                        Boolean value = other.literals.get(e.getKey());
                        return value != null && value == e.getValue();
                    });
        }

        boolean isSubsetOf(Term other) {
            return other.literals.keySet().containsAll(this.literals.keySet()) &&
                    this.literals.entrySet().stream()
                            .allMatch(e -> Objects.equals(e.getValue(), other.literals.get(e.getKey())));
        }

        @Override
        public String toString() {
            return literals.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> e.getValue() ? e.getKey().toString() : "!" + e.getKey())
                    .collect(Collectors.joining(" & ", "(", ")"));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Term term = (Term) o;
            return literals.equals(term.literals);
        }

        @Override
        public int hashCode() {
            return literals.hashCode();
        }
    }
}