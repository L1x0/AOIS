package by.astakhau.schemesynthesis;

import java.util.*;
import java.util.stream.Collectors;

public class GluingCNF {
    private final String cnfFormula;

    public GluingCNF(String cnfFormula) {
        this.cnfFormula = cnfFormula.replace(" ", "").replace("\t", "");
    }

    public String minimize() {
        List<Clause> clauses = parseClauses();
        Set<Clause> primeImplicants = findPrimeImplicants(clauses);
        Set<Clause> minimal = removeRedundant(primeImplicants);
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

    private Set<Clause> removeRedundant(Set<Clause> clauses) {
        Set<Clause> minimal = new HashSet<>();
        List<Clause> clauseList = new ArrayList<>(clauses);

        for (int i = 0; i < clauseList.size(); i++) {
            Clause current = clauseList.get(i);
            boolean isRedundant = false;

            for (int j = 0; j < clauseList.size(); j++) {
                if (i == j) continue;
                Clause other = clauseList.get(j);
                if (other.isSubsetOf(current)) {
                    isRedundant = true;
                    break;
                }
            }

            if (!isRedundant) {
                minimal.add(current);
            }
        }

        return minimal;
    }

    private String formatResult(Set<Clause> clauses) {
        return clauses.stream()
                .map(Clause::toString)
                .collect(Collectors.joining(" & ", "(", ")"));
    }

    static class Clause {
        private final Map<Character, Boolean> literals = new HashMap<>();

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
            Map<Character, Boolean> merged = new HashMap<>();
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

        boolean isSubsetOf(Clause other) {
            return other.literals.keySet().containsAll(this.literals.keySet()) &&
                    this.literals.entrySet().stream()
                            .allMatch(e -> Objects.equals(e.getValue(), other.literals.get(e.getKey())));
        }

        @Override
        public String toString() {
            return literals.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
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