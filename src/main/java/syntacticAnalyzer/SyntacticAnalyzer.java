package syntacticAnalyzer;

import com.sun.javaws.IconUtil;
import lexicalAnalyzer.LexicalAnalyzer;
import lexicalAnalyzer.Token;

import java.util.*;
import java.util.stream.Collectors;

public class SyntacticAnalyzer {

    private Map<String, Map<String, String>> parsingTable;
    private LexicalAnalyzer lexicalAnalyzer;
    private Stack<String> stack;
    private Token lookaheadToken;
    private String lookaheadType;   // token type read
    private String lookahead;       // token read (represented by type)
    private HashSet<String> terminalSet;
    private HashSet<String> nonTerminalSet;
    private Map<String, Rule> rules;
    private Grammar grammar;
    private String derivation;


    public SyntacticAnalyzer() {
        lexicalAnalyzer = new LexicalAnalyzer();
        stack = new Stack<>();

        // import grammar
        grammar = new Grammar();
//        grammar.createSymbolsEx();
//        grammar.createRulesEx();
        grammar.importRules();
        rules = grammar.getRules();
//        grammar.createParsingTable();
//        grammar.importFollowSets();
        grammar.importParsingTable();
        parsingTable = grammar.getParsingTable();
        terminalSet = new HashSet<>(grammar.getTerminalList());
        nonTerminalSet = new HashSet<>(grammar.getNonTerminalList());

        // test grammar
//        terminalSet.forEach(System.out::println);
//        System.out.println(terminalSet.size());
//        nonTerminalSet.forEach(System.out::println);
//        System.out.println(nonTerminalSet.size());
//        rules.entrySet().stream().forEach(entry -> System.out.println(entry.getKey()));
//        rules.entrySet().stream().forEach(entry -> System.out.println(entry.getValue().toString()));
//        System.out.println(rules.size());
//        parsingTable.entrySet().stream().forEach(entry-> System.out.println(entry.getKey()+": "+entry.getValue().toString()));

//        Set<String> keysInRules = new HashSet<>(rules.keySet());
//        Set<String> keysInParsingTable = new HashSet<>();
//        parsingTable.entrySet().forEach(entry->keysInParsingTable.addAll( entry.getValue().values()));
        // Keys in A and not in B
//        Set<String> inANotB = new HashSet<String>(keysInRules);
//        inANotB.removeAll(keysInParsingTable);
//        Set<String> inANotB = new HashSet<String>(keysInParsingTable);
//        inANotB.removeAll(keysInRules);
//        inANotB.forEach(System.out::println);
//        System.out.println(inANotB.size());

//        Set<String> commonKeys = new HashSet<String>(keysInRules);
//        commonKeys.retainAll(keysInParsingTable);
//        commonKeys.forEach(System.out::println);
//        System.out.println(commonKeys.size());

    }


    public void parserSetup(String src_file_path) {
        lexicalAnalyzer.createTable();
        lexicalAnalyzer.IOFileSetup(src_file_path);
//        while (lexicalAnalyzer.nextToken() != null) {
//            if (lexicalAnalyzer.isFinished())
//                break;
//        }
    }

    public void parserIOFileClose() {
        lexicalAnalyzer.IOFileClose();
    }


    public boolean parse() {
        boolean error = false;
        stack.push("$");
        stack.push("START");
//        stack.push("E");
        derivation = "E";
//        System.out.println(stack.peek());
        if (lexicalAnalyzer.isFinished()) {
            lookahead = "$";
        } else {

            do {
                lookaheadToken = lexicalAnalyzer.nextToken();
                lookaheadType = lexicalAnalyzer.nextToken().getType();
            } while (lookaheadType.equals("blockcmt") || lookaheadType.equals("inlinecmt"));
            lookahead = toTerminalSymbols(lookaheadType);
        }


        while (!stack.peek().equals("$")) {

            String topOfStack = stack.peek();
            System.out.println("top of stack: " + topOfStack);

            if (terminalSet.contains(topOfStack)) {
                System.out.println("top is a terminal");
                System.out.println("token type read: " + lookahead);
                if (topOfStack.equals(lookahead)) {
                    System.out.println("lookahead 1: " + lookahead);

                    stack.pop();
                    do {

                        lookaheadToken = lexicalAnalyzer.nextToken();
//                        System.out.println(lookaheadToken);
                        if (lexicalAnalyzer.isFinished()) {
                            lookaheadType = "$";
                            lookahead = "$";
                            break;

                        } else {
                            lookaheadType = lookaheadToken.getType();
                        }
                    } while (lookaheadType.equals("blockcmt") || lookaheadType.equals("inlinecmt"));
                    lookahead = toTerminalSymbols(lookaheadType);
                    System.out.println("lookahead 2: " + lookahead);
                } else {
                    skipErrors();
                    error = true;
                }
            } else {
                System.out.println("top is not a terminal");
                if (!lookupParsingTable(topOfStack, lookahead).equals("error")) {
                    stack.pop();
                    inverseRHSMultiplePush(topOfStack, lookupParsingTable(topOfStack, lookahead));
                } else {
                    skipErrors();
                    error = true;
                }
            }
        }
        return lookahead.equals("$") && !error;
    }

    private String lookupParsingTable(String topOfStack, String lookahead) {
//        System.out.println("top of stack: " + topOfStack);
//        System.out.println("lookahead: " + lookahead);
//        System.out.println(parsingTable.get(topOfStack).get(lookahead));
        return parsingTable.get(topOfStack).get(lookahead);
    }

    private void inverseRHSMultiplePush(String LHS, String rule) {
        System.out.println(rule);
        String RHS_in_rule = rules.get(rule).getRule_RHS();
        if (!RHS_in_rule.equals("EPSILON")) {
            int index_LHS = derivation.indexOf(LHS);
            System.out.println("index LHS : " + index_LHS);
            System.out.println("length of LHS: " + LHS.length());
            derivation = derivation.replaceFirst(LHS.trim(), RHS_in_rule.trim());
//       derivation.replace(index_LHS, index_LHS+LHS.length()-1, RHS_in_rule);
            System.out.println("=> " + derivation);
            String[] symbols = RHS_in_rule.split("\\s");
            if (symbols.length == 1) {
                stack.push(symbols[0]);
                System.out.println("push to stack: " + symbols[0]);
            } else {
//           System.out.println("how many symbols : "+symbols.length);
                for (int i = symbols.length - 1; i >= 0; i--) {
                    if (!symbols[i].isEmpty() && !symbols[i].equals(" ")) {
                        stack.push(symbols[i]);
                        System.out.println("push to stack: " + symbols[i]);
                    }
                }
            }
        } else {
            derivation = derivation.replaceFirst(LHS.trim(), "");
            derivation = derivation.replace("  ", " ");
            System.out.println("=> " + derivation);
        }
    }


    private void skipErrors() {
    }

    // match the token type to rules
    private String toTerminalSymbols(String lookaheadType) {
        switch (lookaheadType) {
            case "openpar":
                return "lpar";
            case "closepar":
                return "rpar";
            case "opensqbr":
                return "lsqbr";
            case "closesqbr":
                return "rsqbr";
            case "opencubr":
                return "lcurbr";
            case "closecubr":
                return "rcurbr";
            case "noteq":
                return "neq";
            case "qmark":
                return "qm";
            case "coloncolon":
                return "sr";
            default:
                return lookaheadType;
        }
    }

    // for test example
    private String toTerminalSymbolsEx(String lookaheadType) {
        switch (lookaheadType) {
            case "id":
                return "0";
            case "intnum":
                return "1";
            case "openpar":
                return "(";
            case "closepar":
                return ")";
            case "mult":
                return "*";
            case "plus":
                return "+";
            default:
                return null;
        }
    }


}
