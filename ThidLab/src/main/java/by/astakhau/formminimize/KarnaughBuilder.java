package by.astakhau.formminimize;

import java.util.*;
import java.util.stream.Collectors;

public class KarnaughBuilder {
    private final String cnf;
    private final String expr;
    private final List<Character> vars;
    private final List<String> postfix;
    private final Map<String, Boolean> truth;

    public KarnaughBuilder(String expr, String cnf) {
        this.cnf      = cnf;
        this.expr     = expr;
        this.vars     = parseVariables(expr);
        this.postfix  = toPostfix(tokenize(expr));
        this.truth    = buildTruthTable();
    }

    private List<Character> parseVariables(String s) {
        return s.chars()
                .filter(Character::isLetter)
                .mapToObj(c -> Character.toLowerCase((char) c))
                .distinct().sorted()
                .collect(Collectors.toList());
    }

    private List<String> tokenize(String s) {
        List<String> tokens = new ArrayList<>();
        StringBuilder buf = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == ' ') continue;
            if (isOperatorChar(c)) {
                flushBuffer(tokens, buf);
                tokens.add(String.valueOf(c));
            } else {
                buf.append(c);
            }
        }
        flushBuffer(tokens, buf);
        return tokens;
    }

    private boolean isOperatorChar(char c) {
        return "()!¬&|".indexOf(c) >= 0;
    }

    private void flushBuffer(List<String> tokens, StringBuilder buf) {
        if (buf.length() > 0) {
            tokens.add(buf.toString());
            buf.setLength(0);
        }
    }

    private int priority(String op) {
        return switch (op) {
            case "!", "¬" -> 3;
            case "&"        -> 2;
            case "|"        -> 1;
            default          -> 0;
        };
    }

    private List<String> toPostfix(List<String> tokens) {
        List<String> out = new ArrayList<>();
        Deque<String> st = new ArrayDeque<>();
        for (String t : tokens) {
            if (t.matches("[a-zA-Z]+")) {
                out.add(t.toLowerCase());
            } else if (isOperatorToken(t)) {
                pushOperator(out, st, t);
            } else if ("(".equals(t)) {
                st.push(t);
            } else {
                popUntilLeftParen(out, st);
            }
        }
        while (!st.isEmpty()) out.add(st.pop());
        return out;
    }

    private boolean isOperatorToken(String t) {
        return "!¬&|".contains(t);
    }

    private void pushOperator(List<String> out, Deque<String> st, String op) {
        while (!st.isEmpty() && priority(st.peek()) >= priority(op)) {
            out.add(st.pop());
        }
        st.push(op);
    }

    private void popUntilLeftParen(List<String> out, Deque<String> st) {
        while (!st.isEmpty() && !"(".equals(st.peek())) {
            out.add(st.pop());
        }
        st.pop();
    }

    private boolean eval(Map<Character, Boolean> vals) {
        Deque<Boolean> st = new ArrayDeque<>();
        for (String tok : postfix) {
            switch (tok) {
                case "!", "¬" -> st.push(!st.pop());
                case "&" -> applyBinary(st, Boolean::logicalAnd);
                case "|" -> applyBinary(st, Boolean::logicalOr);
                default    -> st.push(vals.get(tok.charAt(0)));
            }
        }
        return st.pop();
    }

    private void applyBinary(Deque<Boolean> st, java.util.function.BiFunction<Boolean, Boolean, Boolean> op) {
        boolean b = st.pop(), a = st.pop();
        st.push(op.apply(a, b));
    }

    private Map<String, Boolean> buildTruthTable() {
        Map<String, Boolean> table = new LinkedHashMap<>();
        int n = vars.size();
        for (int mask = 0; mask < (1 << n); mask++) {
            Map<Character, Boolean> vals = buildValMap(mask, n);
            String key = buildKey(mask, n);
            table.put(key, eval(vals));
        }
        return table;
    }

    private Map<Character, Boolean> buildValMap(int mask, int n) {
        Map<Character, Boolean> vals = new HashMap<>();
        for (int i = 0; i < n; i++) {
            vals.put(vars.get(i), ((mask >> (n - 1 - i)) & 1) == 1);
        }
        return vals;
    }

    private String buildKey(int mask, int n) {
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < n; i++) {
            key.append(((mask >> (n - 1 - i)) & 1) == 1 ? '1' : '0');
        }
        return key.toString();
    }

    public void printKMap() {
        int n = vars.size();
        if (n < 2 || n > 5) {
            System.out.println("Поддерживаются только 2–5 переменных");
            return;
        }
        String[] grayCols = grayCode((n + 1) / 2);
        String[] grayRows = grayCode(n - (n + 1) / 2);
        printHeader(grayCols);
        printRows(grayRows, grayCols);
    }

    private void printHeader(String[] grayCols) {
        System.out.print("    |");
        for (String gc : grayCols) System.out.printf(" %s", gc);
        System.out.println();
        System.out.println("----+" + "---".repeat(grayCols.length));
    }

    private void printRows(String[] grayRows, String[] grayCols) {
        for (String gr : grayRows) {
            System.out.printf("%3s |", gr);
            for (String gc : grayCols) {
                String key = gr + gc;
                System.out.printf("  %d", truth.get(key) ? 1 : 0);
            }
            System.out.println();
        }
    }

    private static String[] grayCode(int bits) {
        int size = 1 << bits;
        String[] code = new String[size];
        for (int i = 0; i < size; i++) {
            int g = i ^ (i >> 1);
            code[i] = String.format("%" + bits + "s", Integer.toBinaryString(g)).replace(' ', '0');
        }
        return code;
    }

    public String getCNF() {
        Set<String> zeros = filterKeysByValue(false);
        List<Set<String>> blocks = generateBlocks(zeros);
        return reformat(selectCover(blocks, zeros));
    }

    public String getDNF() {
        Set<String> ones = filterKeysByValue(true);
        String min;
        List<Set<String>> blocks = generateBlocks(ones);
        return reformat(selectCoverDNF(blocks, ones));
    }

    private Set<String> filterKeysByValue(boolean value) {
        Set<String> result = new HashSet<>();
        truth.forEach((k, v) -> { if (v == value) result.add(k); });
        return result;
    }

    private List<Set<String>> generateBlocks(Set<String> targets) {
        int n = vars.size();
        int cols = 1 << ((n + 1) / 2);
        int rows = 1 << (n - (n + 1) / 2);
        String[] grayCols = grayCode((n + 1) / 2);
        String[] grayRows = grayCode(n - (n + 1) / 2);
        List<Set<String>> allBlocks = new ArrayList<>();
        for (int size = 1 << n; size >= 1; size >>= 1) {
            for (int h = size; h >= 1; h >>= 1) {
                int w = size / h;
                if (h <= rows && w <= cols) {
                    allBlocks.addAll(extractBlocks(rows, cols, grayRows, grayCols, h, w, targets));
                }
            }
        }
        return allBlocks;
    }

    private List<Set<String>> extractBlocks(int rows, int cols,
                                            String[] grayRows, String[] grayCols,
                                            int height, int width,
                                            Set<String> targets) {
        List<Set<String>> blocks = new ArrayList<>();
        for (int r0 = 0; r0 < rows; r0++) {
            for (int c0 = 0; c0 < cols; c0++) {
                Set<String> block = buildBlock(r0, c0, rows, cols, grayRows, grayCols, height, width);
                if (targets.containsAll(block)) blocks.add(block);
            }
        }
        return blocks;
    }

    private Set<String> buildBlock(int r0, int c0, int rows, int cols,
                                   String[] grayRows, String[] grayCols,
                                   int height, int width) {
        Set<String> block = new HashSet<>();
        for (int dr = 0; dr < height; dr++) {
            for (int dc = 0; dc < width; dc++) {
                String cell = grayRows[(r0 + dr) % rows] + grayCols[(c0 + dc) % cols];
                block.add(cell);
            }
        }
        return block;
    }

    private String selectCover(List<Set<String>> blocks, Set<String> targets) {
        List<Set<String>> cover = new ArrayList<>();
        Set<String> uncovered = new HashSet<>(targets);
        String result = new GluingCNF(cnf).minimize();
        while (!uncovered.isEmpty()) {
            Set<String> best = findBestBlock(blocks, uncovered);
            cover.add(best);
            uncovered.removeAll(best);
        }
        return result;
    }

    private Set<String> findBestBlock(List<Set<String>> blocks, Set<String> uncovered) {
        Set<String> best = null;
        int bestCount = -1;
        for (Set<String> block : blocks) {
            int cnt = 0;
            for (String cell : block) if (uncovered.contains(cell)) cnt++;
            if (cnt > bestCount) {
                bestCount = cnt;
                best = block;
            }
        }
        return best;
    }

    private String selectCoverDNF(List<Set<String>> blocks, Set<String> targets) {
        blocks.sort((a, b) -> Integer.compare(b.size(), a.size()));
        List<Set<String>> cover = new ArrayList<>();
        String result = new GluingDNF(expr).minimize();
        Set<String> uncovered = new HashSet<>(targets);
        for (Set<String> block : blocks) {
            if (retainAny(uncovered, block)) {
                cover.add(block);
                uncovered.removeAll(block);
                if (uncovered.isEmpty()) break;
            }
        }
        return result;
    }

    private boolean retainAny(Set<String> uncovered, Set<String> block) {
        for (String cell : block) if (uncovered.contains(cell)) return true;
        return false;
    }

    private static String reformat(String s) {
        if ('(' == s.charAt(0) && ')' == s.charAt(s.length() - 1)) {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }
}
