package syntacticAnalyzer;

import AST.Node;
import com.sun.xml.internal.rngom.digested.DValuePattern;
import lexicalAnalyzer.LexicalAnalyzer;
import lexicalAnalyzer.Token;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class SyntacticAnalyzer {


    private final LexicalAnalyzer lexical_analyzer;

    private final Stack<String> parsing_stack;
    private final Stack<Node> semantic_stack;

    private Token lookahead_token;
    private Token terminal_suc_token; // store the terminal token that has been parsed
    private String lookahead_type;   // token type read
    private String lookahead;       // token read (represented by type)
    private String derivation;
    private int index_terminal_derivation;
    private String top_of_stack;

    private PrintWriter writer_derivation;
    private PrintWriter writer_err_report;
    private PrintWriter writer_AST;
    private PrintWriter writer_test;

    private final NodeFactory nodeFactory;
    private final Grammar grammar;


    public SyntacticAnalyzer() {
        lexical_analyzer = new LexicalAnalyzer();
        parsing_stack = new Stack<>();
        semantic_stack = new Stack<>();
        nodeFactory = new NodeFactory();

        // import grammar
        grammar = new Grammar();
//        grammar.generateGrammarEx();
        grammar.generateGrammarProject();


//        Grammar grammar = new Grammar();
//        grammar.importRules();
//        rules = grammar.getRules();
//        grammar.importParsingTable();
//        parsing_table = grammar.getParsing_table();
//        terminal_set = new HashSet<>(grammar.getTerminal_list());
//        nonTerminal_set = new HashSet<>(grammar.getNonTerminal_list());
//        grammar.importFirstFollowSets();
//        first_sets = grammar.getFirst_sets();
//        follow_sets = grammar.getFollow_sets();

        // test grammar
        grammar.getTerminal_list().forEach(System.out::println);
//        System.out.println(grammar.getTerminal_list().size());
//        grammar.getNonTerminal_list().forEach(System.out::println);
//        System.out.println(grammar.getNonTerminal_list().size());
//            grammar.getRules_attribute().forEach((key, value) -> System.out.println(key));
//            grammar.getRules_attribute().forEach((key, value)-> System.out.println(value.toString()));

//        System.out.println(grammar.getRules_attribute().size());
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
            File outfile_error = new File(file_path_temp + ".outsyntaxerrors");
            File outfile_AST = new File(file_path_temp + ".dot");
//            File outfile_test = new File(file_path_temp + ".outtest");
            System.out.println("[Lexer] Writing to the file: " + outfile_derivation.getName());
            System.out.println("[Lexer] Writing to the file: " + outfile_error.getName());
            System.out.println("[Lexer] Writing to the file: " + outfile_AST.getName());
            writer_derivation = new PrintWriter(outfile_derivation);
            writer_err_report = new PrintWriter(outfile_error);
            writer_AST = new PrintWriter(outfile_AST);
//            writer_test = new PrintWriter(outfile_test);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parserIOFileClose() {
//        System.out.println("[Parser] Flushing & closing files. ");
        lexical_analyzer.IOFileClose();
        writer_derivation.flush();
        writer_derivation.close();
        writer_err_report.flush();
        writer_err_report.close();
        writer_AST.flush();
        writer_AST.close();
//        writer_test.flush();
//        writer_test.close();
    }


    public boolean parse() {
        System.out.println("[Parser] Starting parsing...");
        boolean error = false;
        parsing_stack.push("$");
        parsing_stack.push("START");
        derivation = "START";


//        parsing_stack.push("E");
//        derivation = "E";
        skipCommentsRead();


        while (!parsing_stack.peek().equals("$")) {


            top_of_stack = parsing_stack.peek();
            System.out.println("[parsing stack] top: " + top_of_stack);
            System.out.println("lookahead : "+ lookahead);
            System.out.println("terminal_suc: "+terminal_suc_token);
            if (grammar.getTerminal_list().contains(top_of_stack)) {
                System.out.println("top is a terminal");
                System.out.println("[parse]token type read: " + lookahead);
                terminal_suc_token = lookahead_token;
                System.out.println("terminal_suc 2: "+terminal_suc_token);
                if (top_of_stack.equals(lookahead)) {
//                    System.out.println("token: "+lookaheadToken.toString());

                    parsing_stack.pop();
//                    System.out.println("[parse]lexeme: " + lookahead_token.getLexeme());
//                    System.out.println("[parse]index: " + index_terminal_derivation);
//                    System.out.println("1:" + derivation.substring(0, index_terminal_derivation));
//                    System.out.println("lookahead : " + lookahead);
                    derivation = derivation.substring(0, index_terminal_derivation) +
                            derivation.substring(index_terminal_derivation).replaceFirst(lookahead, lookahead_token.getLexeme());

                    derivation = derivation.replaceAll(" sa-\\w", "");
                    index_terminal_derivation = derivation.indexOf(lookahead_token.getLexeme());
//                    System.out.println("[parse][der]" + derivation);
//                    System.out.println("[parse][der]index: " + index_terminal_derivation);
                    skipCommentsRead();

                } else {
                    skipErrors();
                    error = true;
                }
            } else {
//                System.out.println("top is not a terminal");
//                System.out.println(lookahead);
                if (grammar.getNonTerminal_list().contains(top_of_stack)) {
//            System.out.println("[non terminal branch]lookahead: " + lookahead_token);

                    if (!lookupParsingTable(top_of_stack, lookahead).equals("error")) {
                        parsing_stack.pop();
                        inverseRHSMultiplePush(top_of_stack, lookupParsingTable(top_of_stack, lookahead));
                    } else {
                        skipErrors();
                        error = true;
                    }


                    // semantic action branch
                } else {
//                    System.out.println("top_of_stack: " + top_of_stack);
//                    grammar.getSemantic_actions_list().forEach(System.out::println);
                    System.out.println(grammar.getSemantic_actions_list().contains(top_of_stack));
                    if (grammar.getSemantic_actions_list().contains(top_of_stack)) {
//                        System.out.println("[semantic action begins]");
                        semanticActionOnStack();
                    }
                }
            }
            writer_derivation.append("=> ").append(derivation).append("\r\n");
//            System.out.println("=> " + derivation);
        }


        // print the node
        semantic_stack.peek().print();

        printToDot(semantic_stack.peek());

        return lookahead.equals("$") && !error;
    }


    // output to a dot.file for displaying the AST node
    public void printToDot(Node node) {

        writer_AST.append("graph AST {").append("\r\n");

        printNodetoDot(node);

        writer_AST.append("}");

    }

    private void printNodetoDot(Node node) {

        List<Node> children = node.getChildren();
        String name_id = node.m_sa_name + node.m_nodeId;
        if (children.isEmpty()) {
            writer_AST.append(name_id);
            writer_AST.append(";\r\n");
        } else {

            for (Node child : children) {
                writer_AST.append(name_id).append(" -- ");
                printNodetoDot(child);
            }
        }

    }


    // private methodes

    /**
     * when the top of parsing stack is a semantic action, do the following (create nodes or migrate nodes)
     */
    private void semanticActionOnStack() {
        SemanticAction semantic_action = grammar.getSemantic_actions().get(top_of_stack);
        parsing_stack.pop();
        String left_sem_act = semantic_action.getSem_act_LHS();
        String right_sem_act = semantic_action.getSem_act_RHS();
        System.out.println("[semantic stack] action begin: " + semantic_action.toString());
        Node node_to_push;
        Node node_on_top = null;
        if (!semantic_stack.empty()) {
            node_on_top = semantic_stack.peek();
        }

        if (right_sem_act.contains("makeNode(")) {

            String node_type = right_sem_act.substring(right_sem_act.indexOf("(") + 1, right_sem_act.indexOf(")"));
            String node_lexeme = terminal_suc_token.getLexeme();
            System.out.println(node_type);
            System.out.println(node_lexeme);
            System.out.println("[makeNode branch] token going to be made as a leaf: " + terminal_suc_token.toString() + " " + node_type);
            node_to_push = nodeFactory.makeNode(node_type, node_lexeme);

            assert node_to_push != null;
            node_to_push.setName(left_sem_act);
//            System.out.println("[semantic stack] node to push: " + node_to_push.toString());

            semantic_stack.push(node_to_push);
//            System.out.println("node of top :" + semantic_stack.peek().m_sa_name);
        } else {
            if (right_sem_act.contains("makeFamily(")) {
                System.out.println("[make family branch] begin: " + right_sem_act);

                String parameter = right_sem_act.substring(right_sem_act.indexOf("makeFamily(") + 11, right_sem_act.indexOf(")"));
                String[] parameters = parameter.split(",");
                String op = parameters[0];
                System.out.println("op: " + op);

                // recorde the list of children
                ArrayList<Node> para_nodes = new ArrayList<Node>();


                // variable number of children
                // 2 variables
                if (parameters[parameters.length - 1].trim().equals("n2")) {
                    node_on_top = semantic_stack.peek();
                    System.out.println("[node on the stack top]" + node_on_top.m_sa_name);
                    System.out.println("para[2]" + parameters[parameters.length - 2].trim());

                    String para1 = parameters[parameters.length - 2].trim();
                    String para2 = parameters[parameters.length - 3].trim();

                    while (para1.equals(node_on_top.m_sa_name) || para2.equals(node_on_top.m_sa_name)) {
                        Node node_to_pop = semantic_stack.pop();
                        System.out.println("[node to pop]: " + node_to_pop.m_sa_name);
                        para_nodes.add(node_to_pop);
                        if (!semantic_stack.isEmpty()) {
                            node_on_top = semantic_stack.peek();
                            System.out.println("[node on top]: " + node_on_top.m_sa_name);
                        } else {
                            break;
                        }
                    }
                } else {

                    // 1 variable
                    if (parameters[parameters.length - 1].trim().equals("n")) {
                        node_on_top = semantic_stack.peek();
                        System.out.println("[node on the stack top]" + node_on_top.m_sa_name);
                        System.out.println("para[2]" + parameters[parameters.length - 2].trim());
                        while (parameters[parameters.length - 2].trim().equals(node_on_top.m_sa_name)) {

                            Node node_to_pop = semantic_stack.pop();
                            System.out.println("[node to pop]: " + node_to_pop.m_sa_name);
                            para_nodes.add(node_to_pop);
                            if (!semantic_stack.isEmpty()) {
                                node_on_top = semantic_stack.peek();
                                System.out.println("[node on top]: " + node_on_top.m_sa_name);
                            } else {
                                break;
                            }
                        }
                    } else {

                        // fixed number of children
                        for (int i = parameters.length - 1; i > 0; i--) {
                            if (!semantic_stack.isEmpty()) {
                                node_on_top = semantic_stack.peek();
//                    System.out.println("[node on the stack top]" + node_on_top.m_sa_name);
//                    System.out.println("parameter: " + parameters[i]);
                                if (parameters[i].trim().equals(node_on_top.m_sa_name)) {
                                    Node node_to_pop = semantic_stack.pop();
                                    System.out.println("[node to pop] " + node_to_pop.m_sa_name);
                                    para_nodes.add(node_to_pop);
                                }
                            }
                        }
                    }
                }

                Node opNode = nodeFactory.makeNode(op, op);


                System.out.println("[opNode]" + opNode);
//                System.out.println(para_nodes.size());
//                System.out.println("[para_nodes][0]" + para_nodes.get(0));
//                System.out.println("[para_nodes][1]" + para_nodes.get(1));


                    node_to_push = makeFamily(opNode, para_nodes);

//                System.out.println("[after make family] node to push " + node_to_push.toString());
//                node_to_push.print();
//                System.out.println("[after make family] opnode" + opNode.toString());
//                opNode.print();
                semantic_stack.push(node_to_push);

                // migrate makeFamily node
                Node temp = semantic_stack.pop();
                temp.setName(left_sem_act);
                semantic_stack.push(temp);
//                System.out.println("[MMM----]" + right_sem_act + " migration to: " + left_sem_act);


            } else {// migrate operation

//                System.out.println("[semantic stack]node of top :" + node_on_top.m_sa_name);
//                System.out.println("[semantic stack]right sem act : " + right_sem_act);
                if (node_on_top.m_sa_name.equals(right_sem_act)) {
                    Node temp = semantic_stack.pop();
                    temp.setName(left_sem_act);
                    semantic_stack.push(temp);
                    System.out.println("[MMM----]" + right_sem_act + " migration to: " + left_sem_act);
//                        temp.print();

                }
            }
        }
    }


    // generates a family with n children under a parent op
    private Node makeFamily(Node opNode, ArrayList<Node> kids) {
        if(!kids.isEmpty()) {
//        System.out.println("[node][make family][opNode]: " + opNode.toString());
//        System.out.println("size: " + kids.size());
            Node first_kids = kids.get(kids.size() - 1);
//         System.out.println("[kids][first]"+ first_in_kids.toString());
            for (int i = kids.size() - 1; i >= 1; i--) {
//            System.out.println("[kids]"+ kids.get(i).toString());
//            Node temp = kids.get(i);
                first_kids = first_kids.makeSiblings(kids.get(i - 1));

//            System.out.println("[after make siblings][opnode]" + opNode.toString());
//            System.out.println("[after make siblings] [0]" + kids.get(0).toString());
//            System.out.println("[after make siblings] [1]" + kids.get(1).toString());

            }
//            System.out.println("[after make siblings]" + first_kids.toString());
            return opNode.adoptChildren(first_kids);
//        opNode.addChild(kids.get(0));
//         System.out.println("prepare return make family");
        }else
            return opNode;
    }


    private String lookupParsingTable(String top_of_stack, String lookahead) {
//        System.out.println("top of stack: " + top_of_stack);
//        System.out.println("lookahead: " + lookahead);
//        System.out.println(parsingTable.get(top_of_stack).get(lookahead));
        return grammar.getParsing_table().get(top_of_stack).get(lookahead);
    }


    private void inverseRHSMultiplePush(String LHS, String rule) {
//        System.out.println("LHS: " + LHS);
//        System.out.println("rule: " + rule);
        System.out.println(grammar.getRules_attribute().get(rule));
        String RHS_in_rule = grammar.getRules_attribute().get(rule).getRule_RHS().trim();
//        System.out.println(RHS_in_rule);
        if (!RHS_in_rule.equals("EPSILON")) {
            int index_LHS = derivation.indexOf(LHS);
//            System.out.println("index LHS : " + index_LHS);
//            System.out.println("length of LHS: " + LHS.length());

            // generate derivation
            String RHS_to_replace = RHS_in_rule;
            if (RHS_in_rule.contains("EPSILON")) {
                RHS_to_replace = RHS_to_replace.replace("EPSILON", "");
            }
            derivation = derivation.replaceFirst(LHS.trim(), RHS_to_replace.trim());
            derivation = derivation.replaceAll(" sa-\\w", "");


//       derivation.replace(index_LHS, index_LHS+LHS.length()-1, RHS_in_rule);
//            System.out.println("=> " + derivation);
            String[] symbols = RHS_in_rule.split("\\s");
            if (symbols.length == 1) {
                parsing_stack.push(symbols[0]);
//                System.out.println("push to stack: " + symbols[0]);
            } else {
//                System.out.println("how many symbols : " + symbols.length);
                for (int i = symbols.length - 1; i >= 0; i--) {
                    if (!symbols[i].isEmpty() && !symbols[i].equals(" ")) {
                        if (!symbols[i].equals("EPSILON")) {
                            parsing_stack.push(symbols[i]);
//                            System.out.println("push to stack: " + symbols[i]);
                        }
                    }
                }
            }
        } else {
            derivation = derivation.replaceFirst(LHS.trim(), "");
            derivation = derivation.replace("  ", " ");
            derivation = derivation.replace(" EPSILON", "");
//            System.out.println("=> " + derivation);
        }
    }

    /**
     * pop the stack if the next token is in the FOLLOW set of our current non-terminal on top of the stack.
     * scan tokens until we get one with which we can resume the parse
     */
    private void skipErrors() {
        Map<String, ArrayList<String>> first_sets = grammar.getFirst_sets();
        Map<String, ArrayList<String>> follow_sets = grammar.getFollow_sets();

        // get the original lexeme
        String expected = grammar.getSymbol_map().get(top_of_stack) == null ? top_of_stack : grammar.getSymbol_map().get(top_of_stack);

        // output error info
        System.out.println("Syntax error at: " + lookahead_token.getLocation());
        writer_err_report.append("Syntax error at: ").append(String.valueOf(lookahead_token.getLocation())).append("; Unexpected: \"").
                append(lookahead).append("\";  Expected: \"").append(expected).append("\".");
        System.out.println("lookahead: " + lookahead);

        System.out.println("top of stack: " + top_of_stack);
//        System.out.println(terminal_suc_token.getLexeme());
        System.out.println(first_sets.get(top_of_stack));
        System.out.println(follow_sets.get(top_of_stack));


        if (grammar.getTerminal_list().contains(top_of_stack)) {
            parsing_stack.pop();
        } else {
            if (!grammar.getTerminal_list().contains(top_of_stack)) {
                if (lookahead.equals("$") || follow_sets.get(top_of_stack).contains(lookahead)) {

                    parsing_stack.pop();
                } else {
                    while (!first_sets.get(top_of_stack).contains(lookahead) &&
                            ((first_sets.get(top_of_stack).contains("EPSILON") && !follow_sets.get(top_of_stack).contains(lookahead)))) {
                        skipCommentsRead();
//                        System.out.println("loook ahead: "+lookahead);
                    }
                }
            }
        }
    }


    // ignore comments token
    private void skipCommentsRead() {
        do {
//            System.out.println("skip loop");
            lookahead_token = lexical_analyzer.nextToken();
            System.out.println("[skip read method ]lookahead_token: " + lookahead_token);
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

            case "intnum":
                return "1";
            case "openpar":
                return "(";
            case "closepar":
                return ")";
//            case "mult":
//                return "mult";
//            case "plus":
//                return "plus";
            default:
                return lookahead_type;
        }
    }


}
