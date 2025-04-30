package by.astakhau.formminimize;

import java.util.*;
import java.util.stream.Collectors;

public class GluingDNF {
    private final String dnfFormula;

    public GluingDNF(String dnfFormula) {
        this.dnfFormula = dnfFormula.replace(" ", "").replace("\t", "");
    }

    public String minimize() {
        List<Term> terms = parseTerms();
        Set<Term> primeImplicants = findPrimeImplicants(terms);
        Set<Term> minimal = removeRedundant(primeImplicants);
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

    private Set<Term> removeRedundant(Set<Term> terms) {
        Set<Term> minimal = new HashSet<>();
        List<Term> termList = new ArrayList<>(terms);

        for (int i = 0; i < termList.size(); i++) {
            Term current = termList.get(i);
            boolean isRedundant = false;

            for (int j = 0; j < termList.size(); j++) {
                if (i == j) continue;
                Term other = termList.get(j);
                if (current.isSubsetOf(other)) {
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

    private String formatResult(Set<Term> terms) {
        return terms.stream()
                .map(Term::toString)
                .collect(Collectors.joining(" | ", "(", ")"));
    }

    static class Term {
        private final Map<Character, Boolean> literals = new HashMap<>();

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
            return new Term(merged);
        }

        boolean isSubsetOf(Term other) {
            return this.literals.keySet().containsAll(other.literals.keySet()) &&
                    other.literals.entrySet().stream()
                            .allMatch(e -> Objects.equals(e.getValue(), this.literals.get(e.getKey())));
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
