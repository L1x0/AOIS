package by.astakhau.formminimize;

import by.astakhau.formminimize.nfbuild.Forms;

import java.util.*;
import java.util.stream.Collectors;

public class KarnaughBuilder {
    private static final int MIN_VARS = 2;
    private static final int MAX_VARS = 5;
    private static final String OPERATORS = "!¬&|";
    private static final String NOT_SYMBOLS = "!¬";
    private static final String AND_SYMBOL = "&";
    private static final String OR_SYMBOL = "|";
    private static final String LEFT_PAREN = "(";
    private static final String RIGHT_PAREN = ")";
    private static final String LETTER_REGEX = "[a-zA-Z]+";

    private final String expr;

    private final List<Character> vars;
    private final Forms expression;
    private final List<String> postfix;
    private final Map<String, Boolean> truth;

    public KarnaughBuilder(Forms expression) {
        this.expr    = expression.getPCNF();
        this.vars    = parseVariables(expression.getPCNF());
        this.postfix = toPostfix(tokenize(expression.getPCNF()));
        this.truth   = buildTruthTable();
        this.expression = expression;
    }

    private List<Character> parseVariables(String s) {
        return s.chars()
                .filter(Character::isLetter)
                .mapToObj(c -> Character.toLowerCase((char)c))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private List<String> tokenize(String s) {
        List<String> tokens = new ArrayList<>();
        StringBuilder buf = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isWhitespace(c)) continue;
            if (OPERATORS.indexOf(c) >= 0 || c == '(' || c == ')') {
                addBufferToken(tokens, buf);
                tokens.add(String.valueOf(c));
            } else {
                buf.append(c);
            }
        }
        addBufferToken(tokens, buf);
        return tokens;
    }

    private void addBufferToken(List<String> tokens, StringBuilder buf) {
        if (buf.length() > 0) {
            tokens.add(buf.toString());
            buf.setLength(0);
        }
    }

    private int getPriority(String op) {
        return switch (op) {
            case "!", "¬" -> 3;
            case "&"       -> 2;
            case "|"       -> 1;
            default          -> 0;
        };
    }

    private List<String> toPostfix(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();
        for (String token : tokens) {
            if (token.matches(LETTER_REGEX)) {
                output.add(token.toLowerCase());
            } else if (NOT_SYMBOLS.contains(token) || AND_SYMBOL.equals(token) || OR_SYMBOL.equals(token)) {
                processOperator(token, output, stack);
            } else if (LEFT_PAREN.equals(token)) {
                stack.push(token);
            } else if (RIGHT_PAREN.equals(token)) {
                popUntilLeftParen(output, stack);
            }
        }
        while (!stack.isEmpty()) output.add(stack.pop());
        return output;
    }

    private void processOperator(String token, List<String> output, Deque<String> stack) {
        while (!stack.isEmpty() && getPriority(stack.peek()) >= getPriority(token)) {
            output.add(stack.pop());
        }
        stack.push(token);
    }

    private void popUntilLeftParen(List<String> output, Deque<String> stack) {
        while (!LEFT_PAREN.equals(stack.peek())) output.add(stack.pop());
        stack.pop();
    }

    private boolean eval(Map<Character, Boolean> vals) {
        Deque<Boolean> stack = new ArrayDeque<>();
        for (String tok : postfix) {
            switch (tok) {
                case "!", "¬" -> stack.push(!stack.pop());
                case "&"       -> applyBinaryOp(stack, Boolean::logicalAnd);
                case "|"       -> applyBinaryOp(stack, Boolean::logicalOr);
                default         -> stack.push(vals.get(tok.charAt(0)));
            }
        }
        return stack.pop();
    }

    private void applyBinaryOp(Deque<Boolean> stack, java.util.function.BiFunction<Boolean, Boolean, Boolean> op) {
        boolean b = stack.pop(), a = stack.pop();
        stack.push(op.apply(a, b));
    }

    private Map<String, Boolean> buildTruthTable() {
        int n = vars.size();
        Map<String, Boolean> table = new LinkedHashMap<>();
        for (int mask = 0; mask < (1 << n); mask++) {
            Map<Character, Boolean> assign = new HashMap<>();
            StringBuilder key = new StringBuilder();
            for (int i = 0; i < n; i++) {
                boolean bit = ((mask >> (n - 1 - i)) & 1) == 1;
                assign.put(vars.get(i), bit);
                key.append(bit ? '1' : '0');
            }
            table.put(key.toString(), eval(assign));
        }
        return table;
    }

    public void printKMap() {
        int n = vars.size();
        if (n < MIN_VARS || n > MAX_VARS) {
            System.out.println("Поддерживаются только 2–5 переменных");
            return;
        }
        int bitCols = (n + 1) / 2;
        int bitRows = n - bitCols;
        String[] grayCols = grayCode(bitCols);
        String[] grayRows = grayCode(bitRows);

        printHeader(grayCols);
        for (String rowCode : grayRows) printRow(rowCode, grayCols);
    }

    private void printHeader(String[] cols) {
        System.out.print("    |");
        for (String c : cols) System.out.printf(" %s", c);
        System.out.println();
        System.out.println("----+" + "---".repeat(cols.length));
    }

    private void printRow(String rowCode, String[] cols) {
        System.out.printf("%3s |", rowCode);
        for (String col : cols) {
            String key = rowCode + col;
            System.out.printf("  %d", truth.get(key) ? 1 : 0);
        }
        System.out.println();
    }

    public String getCNF() { return minimize(false); }
    public String getDNF() { return minimize(true);  }

    private String minimize(boolean dnf) {
        int n = vars.size();
        int bitCols = (n + 1) / 2;
        int bitRows = n - bitCols;
        String[] grayCols = grayCode(bitCols);
        String[] grayRows = grayCode(bitRows);

        Set<String> targets = truth.entrySet().stream()
                .filter(e -> e.getValue() == dnf)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        List<Set<String>> blocks = generateAllBlocks(targets, grayRows, grayCols);
        List<Set<String>> cover  = coverBlocks(blocks, targets);

        return  !dnf ? blockToConjunct(blocks.get(0)) : blockToDisjunct(blocks.get(0));
    }

    private List<Set<String>> generateAllBlocks(Set<String> targets, String[] rows, String[] cols) {
        int n = vars.size();
        int rowsCount = rows.length;
        int colsCount = cols.length;
        List<Set<String>> blocks = new ArrayList<>();

        for (var sizePair : getSizePairs(n, rowsCount, colsCount)) {
            int height = sizePair[0];
            int width  = sizePair[1];
            blocks.addAll(generateBlocksForSize(height, width, rows, cols, targets));
        }
        return blocks;
    }

    private List<int[]> getSizePairs(int n, int rowsCount, int colsCount) {
        List<int[]> pairs = new ArrayList<>();
        int maxSize = 1 << n;
        for (int size = maxSize; size >= 1; size >>= 1) {
            for (int height = size; height >= 1; height >>= 1) {
                int width = size / height;
                if (height <= rowsCount && width <= colsCount) {
                    pairs.add(new int[]{height, width});
                }
            }
        }
        return pairs;
    }

    private List<Set<String>> generateBlocksForSize(int height, int width,
                                                    String[] rows, String[] cols, Set<String> targets) {
        List<Set<String>> blocks = new ArrayList<>();
        int rowsCount = rows.length;
        int colsCount = cols.length;
        for (int r0 = 0; r0 < rowsCount; r0++) {
            for (int c0 = 0; c0 < colsCount; c0++) {
                Set<String> block = buildBlock(r0, c0, height, width, rows, cols);
                if (targets.containsAll(block)) {
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    private Set<String> buildBlock(int r0, int c0, int height, int width, String[] rows, String[] cols) {
        Set<String> block = new HashSet<>();
        for (int dr = 0; dr < height; dr++) {
            for (int dc = 0; dc < width; dc++) {
                String cell = rows[(r0 + dr) % rows.length] + cols[(c0 + dc) % cols.length];
                block.add(cell);
            }
        }
        return block;
    }

    private List<Set<String>> coverBlocks(List<Set<String>> all, Set<String> targets) {
        List<Set<String>> cover = new ArrayList<>();
        Set<String> remaining = new HashSet<>(targets);
        while (!remaining.isEmpty()) {
            Set<String> best = findBestBlock(all, remaining);
            cover.add(best);
            remaining.removeAll(best);
        }
        return cover;
    }

    private Set<String> findBestBlock(List<Set<String>> blocks, Set<String> remaining) {
        Set<String> best = null;
        int bestCount = -1;
        for (Set<String> block : blocks) {
            int count = (int) block.stream().filter(remaining::contains).count();
            if (count > bestCount) { bestCount = count; best = block; }
        }
        return best;
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

    private String blockToDisjunct(Set<String> block) { return buildClause(block, false); }
    private String blockToConjunct(Set<String> block) { return buildClause(block, true);  }

    private String buildClause(Set<String> block, boolean conjunct) {
        Map<Integer, Character> fixed = findFixedBits(block);
        List<String> lits = new ArrayList<>();
        GluingDNF dnf  = new GluingDNF(expression.getPDNF());
        GluingCNF cnf = new GluingCNF(expression.getPCNF());
        for (var e : fixed.entrySet()) {
            char var = vars.get(e.getKey());
            char bit = e.getValue();
            boolean neg = (bit == (conjunct ? '0' : '1'));
            String prefix = neg ? String.valueOf(NOT_SYMBOLS.charAt(0)) : "";
            lits.add(prefix + var);
        }
        String sep = conjunct ? " & " : " | ";
        return conjunct ? reformat(cnf.minimize()) : reformat(dnf.minimize());
    }

    private Map<Integer, Character> findFixedBits(Set<String> block) {
        Map<Integer, Character> fixed = new LinkedHashMap<>();
        String sample = block.iterator().next();
        for (int i = 0; i < vars.size(); i++) {
            char bit = sample.charAt(i);
            int finalI = i;
            if (block.stream().allMatch(s -> s.charAt(finalI) == bit)) fixed.put(i, bit);
        }
        return fixed;
    }

    private static String reformat(String s) {
        if ('(' == s.charAt(0) && ')' == s.charAt(s.length() - 1)) {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }
}