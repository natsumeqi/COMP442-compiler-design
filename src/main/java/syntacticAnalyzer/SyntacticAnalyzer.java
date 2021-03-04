package syntacticAnalyzer;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import lexicalAnalyzer.LexicalAnalyzer;
import lexicalAnalyzer.Token;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class SyntacticAnalyzer {


    private final LexicalAnalyzer lexical_analyzer;
    private final Map<String, Map<String, String>> parsing_table;
    private final Map<String, Rule> rules;
    private final HashSet<String> terminal_set;
    private HashSet<String> nonTerminal_set;
    private final Map<String, ArrayList<String>> first_sets;
    private final Map<String, ArrayList<String>> follow_sets;
    private final Stack<String> stack;
    private Token lookahead_token;
    private String lookahead_type;   // token type read
    private String lookahead;       // token read (represented by type)
    private String derivation;
    private String top_of_stack;
    private PrintWriter writer_derivation;
    private PrintWriter writer_err_report;
    private PrintWriter writer_AST;


    public SyntacticAnalyzer() {
        lexical_analyzer = new LexicalAnalyzer();
        stack = new Stack<>();

        // import grammar
        Grammar grammar = new Grammar();
//        grammar.createSymbolsEx();
//        grammar.createRulesEx();
        grammar.importRules();

        rules = grammar.getRules();
//        grammar.createParsingTable();
//        grammar.importFollowSets();
        grammar.importParsingTable();
        parsing_table = grammar.getParsing_table();
        terminal_set = new HashSet<>(grammar.getTerminal_list());
        nonTerminal_set = new HashSet<>(grammar.getNonTerminal_list());
        grammar.importFirstFollowSets();
        first_sets = grammar.getFirst_sets();
        follow_sets = grammar.getFollow_sets();

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
//        follow_sets.entrySet().forEach(entry -> System.out.println(entry.getKey()));
//        first_sets.entrySet().forEach(entry -> System.out.println(entry.getKey()));
    }


    public void parserSetup(String src_file_path) {
        lexical_analyzer.createTable();
        lexical_analyzer.IOFileSetup(src_file_path);
    }

    public void parserIOFileSetup(String src_file_path) {
        try {
            String file_path_temp = src_file_path.substring(0, src_file_path.length() - 4);
            File outfile_derivation = new File(file_path_temp + ".outderivation");
//            File outfile_error = new File(file_path_temp + ".outsyntaxerrors");
            System.out.println("[Lexer] Writing to the file: " + outfile_derivation.getName());
//            System.out.println("[Lexer] Writing to the file: " + outfile_error.getName());
            writer_derivation = new PrintWriter(outfile_derivation);
//            writer_err_report = new PrintWriter(outfile_error);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parserIOFileClose() {
//        System.out.println("[Parser] Flushing & closing files. ");
        lexical_analyzer.IOFileClose();
//        writer_derivation.flush();
//        writer_derivation.close();
    }


    public boolean parse() {
        System.out.println("[Parser] Starting parsing...");
        boolean error = false;
        stack.push("$");
        stack.push("START");
//        stack.push("E");
//        derivation = "E";
        derivation = "START";

        skipCommentsRead();

        while (!stack.peek().equals("$")) {

            top_of_stack = stack.peek();
//            System.out.println("top of stack: " + top_of_stack);

            if (terminal_set.contains(top_of_stack)) {
//                System.out.println("top is a terminal");
//                System.out.println("token type read: " + lookahead);
                if (top_of_stack.equals(lookahead)) {
//                    System.out.println("token: "+lookaheadToken.toString());

                    stack.pop();
//                    System.out.println("lexeme: "+lookaheadToken.getLexeme());
                    derivation = derivation.replaceFirst(lookahead, lookahead_token.getLexeme());

                    skipCommentsRead();

                } else {
                    skipErrors();
                    error = true;
                }
            } else {
//                System.out.println("top is not a terminal");
                if (!lookupParsingTable(top_of_stack, lookahead).equals("error")) {
                    stack.pop();
                    inverseRHSMultiplePush(top_of_stack, lookupParsingTable(top_of_stack, lookahead));
                } else {
                    skipErrors();
                    error = true;
                }
            }
//            writer_derivation.append("=> ").append(derivation).append("\r\n");
//            System.out.println("=> " + derivation);
        }
        return lookahead.equals("$") && !error;
    }

    // ignore comments token
    private void skipCommentsRead() {
        do {
//            System.out.println("skip loop");
            lookahead_token = lexical_analyzer.nextToken();
//          System.out.println(lookahead_token);
//            System.out.println(lexical_analyzer.isFinished());
            if (lexical_analyzer.isFinished()) {
                lookahead_type = "$";
                lookahead = "$";
                break;
            } else {
                lookahead_type = lookahead_token.getType();
            }
        } while (lookahead_type.equals("blockcmt") || lookahead_type.equals("inlinecmt"));
        lookahead = toTerminalSymbols(lookahead_type);
    }

    private String lookupParsingTable(String top_of_stack, String lookahead) {
//        System.out.println("top of stack: " + top_of_stack);
//        System.out.println("lookahead: " + lookahead);
//        System.out.println(parsingTable.get(top_of_stack).get(lookahead));
        return parsing_table.get(top_of_stack).get(lookahead);
    }

    private void inverseRHSMultiplePush(String LHS, String rule) {
//        System.out.println("LHS: " + LHS);
//        System.out.println("rule: " + rule);
        String RHS_in_rule = rules.get(rule).getRule_RHS();
//        System.out.println("RHS: " + RHS_in_rule);
        if (!RHS_in_rule.equals("EPSILON")) {
            int index_LHS = derivation.indexOf(LHS);
//            System.out.println("index LHS : " + index_LHS);
//            System.out.println("length of LHS: " + LHS.length());
            derivation = derivation.replaceFirst(LHS.trim(), RHS_in_rule.trim());
//       derivation.replace(index_LHS, index_LHS+LHS.length()-1, RHS_in_rule);
//            System.out.println("=> " + derivation);
            String[] symbols = RHS_in_rule.split("\\s");
            if (symbols.length == 1) {
                stack.push(symbols[0]);
//                System.out.println("push to stack: " + symbols[0]);
            } else {
//           System.out.println("how many symbols : "+symbols.length);
                for (int i = symbols.length - 1; i >= 0; i--) {
                    if (!symbols[i].isEmpty() && !symbols[i].equals(" ")) {
                        stack.push(symbols[i]);
//                        System.out.println("push to stack: " + symbols[i]);
                    }
                }
            }
        } else {
            derivation = derivation.replaceFirst(LHS.trim(), "");
            derivation = derivation.replace("  ", " ");
//            System.out.println("=> " + derivation);
        }
    }

    /**
     * pop the stack if the next token is in the FOLLOW set of our current non-terminal on top of the stack.
     * scan tokens until we get one with which we can resume the parse
     */
    private void skipErrors() {

        System.out.println("syntax error at: " + lookahead_token.getLocation());
//        System.out.println("lookahead: " + lookahead);
//        System.out.println(follow_sets.get(top_of_stack));
//        System.out.println(top_of_stack);
        if (terminal_set.contains(top_of_stack)) {
            stack.pop();
        } else {
            if (!terminal_set.contains(top_of_stack)) {
                if (lookahead.equals("$") || follow_sets.get(top_of_stack).contains(lookahead)) {
                    stack.pop();
                } else {
                    while (!first_sets.get(top_of_stack).contains(lookahead) ||
                            (first_sets.get(top_of_stack).contains("EPSILON") && !follow_sets.get(top_of_stack).contains(lookahead))) {
                        skipCommentsRead();

                    }
                }
            }
        }
    }

    // match the token type to rules
    private String toTerminalSymbols(String lookahead_type) {
        switch (lookahead_type) {
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
                return lookahead_type;
        }
    }

    // for test example
    private String toTerminalSymbolsEx(String lookahead_type) {
        switch (lookahead_type) {
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
