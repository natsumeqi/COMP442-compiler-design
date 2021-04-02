package syntacticAnalyzer;

import AST.Node;
import lexicalAnalyzer.LexicalAnalyzer;
import lexicalAnalyzer.Token;

import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;

public class SyntacticAnalyzer {

    // import from other classes
    private final LexicalAnalyzer lexical_analyzer;
    private final NodeFactory nodeFactory;
    private final Grammar grammar;

    // two stacks
    private final Stack<String> parsing_stack;
    private final Stack<Node> semantic_stack;

    // variables
    private Token lookahead_token;
    private Token terminal_suc_token;       // store the terminal token that has been parsed
    private String lookahead;               // token read (represented by type)
    private String derivation;              // record Derivation output
    private int index_terminal_derivation;  // keep updated index of the terminal symbol in derivation
    private String top_of_stack;
    private Node progNode;

    // output to files
    private PrintWriter writer_derivation;
    private PrintWriter writer_err_report;
    private PrintWriter writer_DOT;
    private PrintStream writer_AST;


    /**
     * constructor
     */
    public SyntacticAnalyzer() {
        lexical_analyzer = new LexicalAnalyzer();
        parsing_stack = new Stack<>();
        semantic_stack = new Stack<>();
        nodeFactory = new NodeFactory();
        grammar = new Grammar();
//        grammar.generateGrammarEx();
        grammar.generateGrammarProject();

    }


    /**
     * lexical analyzer setup
     *
     * @param src_file_path input file
     */
    public void parserLexicalSetup(String src_file_path) {

        lexical_analyzer.createTable();
        lexical_analyzer.IOFileSetup(src_file_path);
    }


    /**
     * input output files setup
     *
     * @param src_file_path input file
     */
    public void parserIOFileSetup(String src_file_path) {
        try {
            String file_path_temp = src_file_path.substring(0, src_file_path.length() - 4);
            File outfile_derivation = new File(file_path_temp + ".outderivation");
            File outfile_error = new File(file_path_temp + ".outsyntaxerrors");
            File outfile_DOT = new File(file_path_temp + ".dot");
            File outfile_AST = new File(file_path_temp + ".outast");
            System.out.println("[Lexer] Writing to the file: " + outfile_derivation.getName());
            System.out.println("[Lexer] Writing to the file: " + outfile_error.getName());
            System.out.println("[Lexer] Writing to the file: " + outfile_DOT.getName());
            System.out.println("[Lexer] Writing to the file: " + outfile_AST.getName());
            writer_derivation = new PrintWriter(outfile_derivation);
            writer_err_report = new PrintWriter(outfile_error);
            writer_DOT = new PrintWriter(outfile_DOT);
            writer_AST = new PrintStream(outfile_AST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * close files
     */
    public void parserIOFileClose() {
        System.out.println("[Parser] Flushing & closing files. ");
        lexical_analyzer.IOFileClose();
        writer_derivation.flush();
        writer_derivation.close();
        writer_err_report.flush();
        writer_err_report.close();
        writer_DOT.flush();
        writer_DOT.close();
        writer_AST.flush();
        writer_AST.close();
    }


    public Node getProgNode() {
        return progNode;
    }


    /**
     * parsing function
     *
     * @return false when syntactic error exists
     */
    public boolean parse() {
        System.out.println("[Parser] Starting parsing...");
        boolean error = false;
//        parsing_stack.push("E");
//        derivation = "E";
        parsing_stack.push("$");
        parsing_stack.push("START");
        derivation = "START";
        writer_derivation.append(derivation).append("\r\n");

        skipCommentsRead();


        while (!parsing_stack.peek().equals("$")) {
            top_of_stack = parsing_stack.peek();

            // the top of parsing stack is a terminal symbol
            if (grammar.getTerminal_list().contains(top_of_stack)) {
                terminal_suc_token = lookahead_token;

                // when matching a terminal symbol
                if (top_of_stack.equals(lookahead)) {
                    parsing_stack.pop();

                    // process derivation
                    if (index_terminal_derivation < derivation.length()) {
                        String temp_derivation = derivation;
                        derivation = derivation.substring(0, index_terminal_derivation) + derivation.substring(index_terminal_derivation).replaceFirst(lookahead, lookahead_token.getLexeme());
                        if (!temp_derivation.equals(derivation)) {
                            // store the index of the latest terminal in derivation
                            index_terminal_derivation += derivation.substring(index_terminal_derivation).indexOf(lookahead_token.getLexeme());
                            writer_derivation.append("=> ").append(derivation).append("\r\n");
                        }
                    }

                    // read next valid token
                    skipCommentsRead();

                } else {    // when match fails
                    skipErrors();
                    error = true;
                }
            } else {

                // when the top of parsing stack is a non terminal symbol
                if (grammar.getNonTerminal_list().contains(top_of_stack)) {

                    // looking up parsing table
                    if (!lookupParsingTable(top_of_stack, lookahead).equals("error")) {
                        parsing_stack.pop();
                        inverseRHSMultiplePush(top_of_stack, lookupParsingTable(top_of_stack, lookahead));
                    } else {
                        skipErrors();
                        error = true;
                    }

                    // when the top of parsing stack is a semantic action
                } else {
                    if (grammar.getSemantic_actions_list().contains(top_of_stack)) {
                        semanticActionOnStack();
                    }
                }
            }
        }


        // print the node to console and file
//        semantic_stack.peek().print();

        PrintStream console = System.out;
        System.setOut(writer_AST);
        semantic_stack.peek().print();
        System.setOut(console);

        printToDot(semantic_stack.peek());
        progNode = semantic_stack.peek();


        if (!lookahead.equals("$") && !error) {
            System.out.println("Syntax error at: " + lookahead_token.getLocation() + ";\t Unexpected: '" + lookahead + "'.");
            writer_err_report.append("Syntax error at line: ").append(String.valueOf(lookahead_token.getLocation())).append(";\t Unexpected: '").
                    append(lookahead).append("'\r\n");
        }

        return lookahead.equals("$") && !error;
    }


    /**
     * output to a dot.file for displaying the AST node
     *
     * @param node AST node
     */
    public void printToDot(Node node) {

        writer_DOT.append("digraph AST {").append("\r\n");
        writer_DOT.append("node [shape=record];\r\n");
        writer_DOT.append("node [fontname=Sans];charset=\"UTF-8\" splines=true splines=spline rankdir =LR\r\n");
        printNodetoDot(node);
        writer_DOT.append("}");

    }


    /**
     * recursively print node for dot language display
     *
     * @param node AST node
     */
    private void printNodetoDot(Node node) {

        List<Node> children = node.getChildren();
        String node_name_data = node.m_sa_name.substring(0, node.m_sa_name.indexOf("_"));
        if (node.getData() != null && !node.getData().isEmpty()) {
            String data_for_dot = node.getData();
            switch (data_for_dot) {
                case ">":
                    data_for_dot = "gt";
                    break;
                case "<":
                    data_for_dot = "lt";
                    break;
                case ">=":
                    data_for_dot = "geq";
                    break;
                case "<=":
                    data_for_dot = "leq";
                    break;
                default:
            }
            node_name_data = node_name_data + " | " + data_for_dot;
        }
        if (node.getM_line() != 0) {
            node_name_data = node_name_data + " | " + node.getM_line();
        }
        String node_id = String.valueOf(node.m_nodeId);
        if (children.isEmpty()) {

            writer_DOT.append(node_id);
            writer_DOT.append(";\r\n");
            writer_DOT.append(node_id).append("[label=\"").append(node_name_data).append("\"]\r\n");
        } else {

            for (Node child : children) {
                writer_DOT.append(node_id).append(" -> ");
                printNodetoDot(child);
            }
            writer_DOT.append(node_id).append("[label=\"").append(node_name_data).append("\"];\r\n");
        }

    }


    /**
     * when the top of parsing stack is a semantic action, do the following (create nodes or migrate nodes)
     */
    private void semanticActionOnStack() {

        boolean skip = false;
//        boolean skipAll = false;

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

        // leaf node
        if (right_sem_act.contains("makeNode(")) {
            String type = right_sem_act.substring(right_sem_act.indexOf("(") + 1, right_sem_act.indexOf(")"));
            String node_lexeme = terminal_suc_token.getLexeme();
            int node_line = terminal_suc_token.getLocation();

            System.out.println("[makeNode branch] token going to be made as a leaf: " + terminal_suc_token.toString() + " " + type);
            node_to_push = nodeFactory.makeNode(type, node_lexeme, node_line);

            assert node_to_push != null;
            node_to_push.setName(left_sem_act);

            semantic_stack.push(node_to_push);
        } else {

            // generate AST subtree
            if (right_sem_act.contains("makeFamily(")) {
                System.out.println("[make family branch] begin: " + right_sem_act);
                String parameter = right_sem_act.substring(right_sem_act.indexOf("makeFamily(") + 11, right_sem_act.indexOf(")"));
                String[] parameters = parameter.split(",");
                String op = parameters[0];
                int node_line;
                String token_type;
                if (terminal_suc_token != null) {
                    node_line = terminal_suc_token.getLocation();
                    token_type = terminal_suc_token.getType();
                } else {
                    node_line = lookahead_token.getLocation();
                    token_type = lookahead_token.getType();
                }


                // record the list of children
                ArrayList<Node> para_nodes = new ArrayList<>();
                Node opNode_backup = null;

                // variable number of children, any type can be a kid. Ex: makeFamily(FuncOrVar, DataMem_s, FuncCall_s, n)
                if (parameters[parameters.length - 1].trim().equals("n")) {

                    // for the case makeFamily(AParams, Null_s, Expr_s, n)
                    if (parameters[1].trim().equals("Null_s")) {
                        assert semantic_stack.peek() != null;
                        if (!semantic_stack.isEmpty()) {
                            node_on_top = semantic_stack.peek();
                            int num_kids = parameters.length - 2;
                            ArrayList<String> string_kids = new ArrayList<>();
                            for (int i = 2; i <= num_kids; i++) {
                                string_kids.add(parameters[i].trim());
                            }
                            String name_node_on_top = node_on_top.m_sa_name;
                            while (ifTheKidsInMakeFamily(string_kids, name_node_on_top)) {
                                Node node_to_pop = semantic_stack.pop();
                                para_nodes.add(node_to_pop);
                                if (!semantic_stack.isEmpty()) {
                                    node_on_top = semantic_stack.peek();
                                    name_node_on_top = node_on_top.m_sa_name;
                                } else {
                                    break;
                                }
                            }
                            if(node_on_top.m_sa_name.equals("Null_s")){
                                semantic_stack.pop();
                            }

                        }


                    } else {


                        assert semantic_stack.peek() != null;
                        if (!semantic_stack.isEmpty()) {
                            node_on_top = semantic_stack.peek();
                            int num_kids = parameters.length - 2;
                            ArrayList<String> string_kids = new ArrayList<>();
                            for (int i = 1; i <= num_kids; i++) {
                                string_kids.add(parameters[i].trim());
                            }
                            String name_node_on_top = node_on_top.m_sa_name;
                            while (ifTheKidsInMakeFamily(string_kids, name_node_on_top)) {
                                Node node_to_pop = semantic_stack.pop();
                                para_nodes.add(node_to_pop);
                                if (!semantic_stack.isEmpty()) {
                                    node_on_top = semantic_stack.peek();
                                    name_node_on_top = node_on_top.m_sa_name;
                                } else {
                                    break;
                                }
                            }
                        }
                    }

                } else {

                    // for special case makeFamily(Dot, DataMem_s, DataMem_s, keepOrSkip)
                    if (parameters[parameters.length - 1].trim().equals("keepOrSkip")) {


                        if (!semantic_stack.isEmpty()) {
                            node_on_top = semantic_stack.peek();
                            if (parameters[2].trim().equals(node_on_top.m_sa_name)) {
                                Node node_to_pop = semantic_stack.pop();
                                para_nodes.add(node_to_pop);
                            }
                        }


                        if (!semantic_stack.isEmpty()) {
                            node_on_top = semantic_stack.peek();
                            if (parameters[1].trim().equals(node_on_top.m_sa_name)) {
                                Node node_to_pop = semantic_stack.pop();
                                para_nodes.add(node_to_pop);
                            } else {
//                                if (parameters[parameters.length - 2].trim().equals("skipAll")) {
//                                    skipAll = true;
//                                } else {
                                skip = true;
//
//                                }
                            }
                        }
                        System.out.println("[keepOrskip] para_nodes size : " + para_nodes.size());

                    } else {


                        // any one of kids can make a family (one type is enough)
                        if (parameters[parameters.length - 1].trim().equals("any")) {

                            // only the first 2 kids can be interchangeable. Special case for makeFamily(FuncCall, Id_s, Dot_s, AParams_s, first2, any).
                            if (parameters[parameters.length - 2].trim().equals("first2")) {

                                System.out.println("[first2] node on top: " + node_on_top);


                                if (!semantic_stack.isEmpty()) {
                                    node_on_top = semantic_stack.peek();
                                    if (parameters[3].trim().equals(node_on_top.m_sa_name)) {
                                        Node node_to_pop = semantic_stack.pop();
                                        para_nodes.add(node_to_pop);
                                    }
                                }


                                node_on_top = semantic_stack.peek();
                                System.out.println("[first2] para1 " + parameters[1].trim());
                                // real kid first 2 (any one is enough)
                                ArrayList<String> string_kids = new ArrayList<>();
                                string_kids.add(parameters[1].trim());
                                string_kids.add(parameters[2].trim());
                                String name_node_on_top = node_on_top.m_sa_name;
                                System.out.println("[first2] name_node_on_top: " + name_node_on_top);
                                if (ifTheKidsInMakeFamily(string_kids, name_node_on_top)) {
                                    System.out.println("[first2] add to para_nodes: ");
                                    Node node_to_pop = semantic_stack.pop();
                                    para_nodes.add(node_to_pop);
                                }

//---------------------------------------------------------special case for makeFamily(Dot, DataMem_s, Dot_s, DataMem_s, keepOrSkip, first2, any)-------------------------------------
                                if (parameters[parameters.length - 3].trim().equals("keepOrSkip")) {
                                    if (para_nodes.size() == 1) {
                                        skip = true;
                                    }
                                }

//---------------------------------------------------------special case -----------------------------------
                                System.out.println("[first2] para size " + para_nodes.size());

                            } else {


                                // only 2 kids can be interchangeable. Special case for makeFamily(MembDecl, Visibility_s, VarDecl_s, FuncDecl_s, 2, any).
                                if (parameters[parameters.length - 2].trim().equals("2")) {
                                    node_on_top = semantic_stack.peek();

                                    // real kid
                                    int index_kid = parameters.length - 3;
                                    ArrayList<String> string_kids = new ArrayList<>();
                                    string_kids.add(parameters[index_kid].trim());
                                    string_kids.add(parameters[index_kid - 1].trim());
                                    String name_node_on_top = node_on_top.m_sa_name;
                                    if (ifTheKidsInMakeFamily(string_kids, name_node_on_top)) {
                                        Node node_to_pop = semantic_stack.pop();
                                        para_nodes.add(node_to_pop);
                                    }

                                    for (int i = parameters.length - 5; i > 0; i--) {
                                        if (!semantic_stack.isEmpty()) {
                                            node_on_top = semantic_stack.peek();
                                            if (parameters[i].trim().equals(node_on_top.m_sa_name)) {
                                                Node node_to_pop = semantic_stack.pop();
                                                para_nodes.add(node_to_pop);
                                            }
                                        }
                                    }

                                } else {

                                    // any one of kids can make a family (general case). Ex: makeFamily(ArithExpr, Term_s, AddOp_s, any)
                                    node_on_top = semantic_stack.peek();
                                    int num_kids = parameters.length - 2;
                                    ArrayList<String> string_kids = new ArrayList<>();
                                    for (int i = 1; i <= num_kids; i++) {
                                        string_kids.add(parameters[i].trim());
                                    }
                                    String name_node_on_top = node_on_top.m_sa_name;
                                    if (ifTheKidsInMakeFamily(string_kids, name_node_on_top)) {
                                        Node node_to_pop = semantic_stack.pop();
                                        para_nodes.add(node_to_pop);
                                    }
                                }
                            }
                        } else {

                            // first create a parent node, then reuse the node to make a family. Ex: makeFamily(Sign_i, Factor_s, reuse)
                            if (parameters[parameters.length - 1].trim().equals("reuse")) {
                                String op_name = parameters[0];
                                for (int i = parameters.length - 2; i >= 0; i--) {
                                    if (!semantic_stack.isEmpty()) {
                                        node_on_top = semantic_stack.peek();
                                        if (parameters[i].trim().equals(node_on_top.m_sa_name)) {

                                            // reserve the node for opNode
                                            if (node_on_top.m_sa_name.equals(op_name)) {
                                                opNode_backup = semantic_stack.pop();
                                            } else {
                                                Node node_to_pop = semantic_stack.pop();
                                                para_nodes.add(node_to_pop);
                                            }
                                        }
                                    }
                                }


                            } else {

                                // fixed number of children. Ex: makeFamily(Prog, ClassList_s, FuncDefList_s, MainBlock_s)
                                for (int i = parameters.length - 1; i > 0; i--) {
                                    if (!semantic_stack.isEmpty()) {
                                        node_on_top = semantic_stack.peek();
                                        if (parameters[i].trim().equals(node_on_top.m_sa_name)) {
                                            Node node_to_pop = semantic_stack.pop();
                                            para_nodes.add(node_to_pop);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                Node opNode;

                if (skip) {

                    if (!para_nodes.isEmpty()) {
                        System.out.println("after make family: skip node to push " + para_nodes.get(0));
                        semantic_stack.push(para_nodes.get(0));
                    }

                } else {
//                    if (!skipAll) {

                    // keep the arraylist consistent with the following call of makeFamily
                    if (parameters[parameters.length - 1].trim().equals("reuse")) {
                        opNode = opNode_backup;
                    } else {
                        opNode = nodeFactory.makeNode(op, op, node_line);
                    }

                    node_to_push = makeFamily(opNode, para_nodes);
//                    node_to_push.print();
                    System.out.println("after make family: node to push " + node_to_push);
                    semantic_stack.push(node_to_push);

                    // migrate makeFamily node
                    Node temp = semantic_stack.pop();
                    temp.setName(left_sem_act);
                    semantic_stack.push(temp);
                }
//                }
            } else {

                // migrate operation
                if (node_on_top != null) {
                    if (node_on_top.m_sa_name.equals(right_sem_act)) {
                        Node temp = semantic_stack.pop();
                        temp.setName(left_sem_act);
                        semantic_stack.push(temp);
                    }
                }
            }
        }
    }


    /**
     * check if the node on the top of stack will be made a family
     *
     * @param kids      parameter names potential to be a kid
     * @param name_node the name of the node on the top of the stack
     * @return true when two matched
     */
    private boolean ifTheKidsInMakeFamily(ArrayList<String> kids, String name_node) {
        int n = kids.size();
        switch (n) {
            case 1:
                return kids.get(0).equals(name_node);
            case 2:
                return kids.get(0).equals(name_node) || kids.get(1).equals(name_node);
            case 3:
                return kids.get(0).equals(name_node) || kids.get(1).equals(name_node) ||
                        kids.get(2).equals(name_node);
            case 6:
                return kids.get(0).equals(name_node) || kids.get(1).equals(name_node) ||
                        kids.get(2).equals(name_node) || kids.get(3).equals(name_node) ||
                        kids.get(4).equals(name_node) || kids.get(5).equals(name_node);
            case 8:
                return kids.get(0).equals(name_node) || kids.get(1).equals(name_node) ||
                        kids.get(2).equals(name_node) || kids.get(3).equals(name_node) ||
                        kids.get(4).equals(name_node) || kids.get(5).equals(name_node) ||
                        kids.get(6).equals(name_node) || kids.get(7).equals(name_node);
            case 10:
                return kids.get(0).equals(name_node) || kids.get(1).equals(name_node) ||
                        kids.get(2).equals(name_node) || kids.get(3).equals(name_node) ||
                        kids.get(4).equals(name_node) || kids.get(5).equals(name_node) ||
                        kids.get(6).equals(name_node) || kids.get(7).equals(name_node) ||
                        kids.get(8).equals(name_node) || kids.get(9).equals(name_node);
            default:
                System.err.println("NO matched kids in makeFamily()");
                return false;
        }
    }


    /**
     * generates a family with n children under a parent op
     *
     * @param opNode parent node
     * @param kids   children nodes
     * @return patent node with children
     */
    private Node makeFamily(Node opNode, ArrayList<Node> kids) {
        if (!kids.isEmpty()) {
            Node first_kids = kids.get(kids.size() - 1);
            for (int i = kids.size() - 1; i >= 1; i--) {
                first_kids = first_kids.makeSiblings(kids.get(i - 1));
            }
            return opNode.adoptChildren(first_kids);
        } else
            return opNode;
    }


    /**
     * looking up result of the parsing table
     *
     * @param top_of_stack non terminal symbols on the top of stack
     * @param lookahead    token
     * @return result in the parsing table
     */
    private String lookupParsingTable(String top_of_stack, String lookahead) {
        return grammar.getParsing_table().get(top_of_stack).get(lookahead);
    }


    /**
     * inverse push into the parsing stack
     *
     * @param LHS  non terminal symbols
     * @param rule grammar in the parsing table
     */
    private void inverseRHSMultiplePush(String LHS, String rule) {
        System.out.println("rule: " + rule);
        String RHS_in_rule = grammar.getRules_attribute().get(rule).getRule_RHS().trim();
        if (!RHS_in_rule.equals("EPSILON")) {

            // generate derivation
            String RHS_to_replace = RHS_in_rule;
            if (RHS_in_rule.contains("EPSILON")) {
                RHS_to_replace = RHS_to_replace.replace("EPSILON", "");
            }
            derivation = derivation.replaceFirst(LHS.trim(), RHS_to_replace.trim());
            derivation = derivation.replaceAll(" sa-\\d\\d", "");
            writer_derivation.append("=> ").append(derivation).append("\r\n");

            // push into the stack
            String[] symbols = RHS_in_rule.split("\\s");
            if (symbols.length == 1) {
                parsing_stack.push(symbols[0]);
            } else {
                for (int i = symbols.length - 1; i >= 0; i--) {
                    if (!symbols[i].isEmpty() && !symbols[i].equals(" ")) {
                        if (!symbols[i].equals("EPSILON")) {
                            parsing_stack.push(symbols[i]);
                        }
                    }
                }
            }
        } else {

            // a EPSILON rule
            derivation = derivation.replaceFirst(LHS.trim(), "");
            derivation = derivation.replace("  ", " ");
            derivation = derivation.replace(" EPSILON", "");
            writer_derivation.append("=> ").append(derivation).append("\r\n");

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
        if (lookahead_token != null) {
            String unexpected = grammar.getSymbol_map().get(lookahead) == null ? lookahead : grammar.getSymbol_map().get(lookahead);

            // top of stack is a terminal symbol
            if (grammar.getTerminal_list().contains(top_of_stack)) {
                parsing_stack.pop();

                System.out.println("Syntax error at: " + lookahead_token.getLocation());
                writer_err_report.append("Syntax error at line: ").append(String.valueOf(lookahead_token.getLocation())).
                        append("\t Missing expected symbol: '").append(expected).append("'\t Unexpected: '").append(unexpected).append("'\r\n");
            } else {

                //  pop the stack if the next token is in the FOLLOW set of our current non terminal on top of the stack
                if (lookahead.equals("$") || follow_sets.get(top_of_stack).contains(lookahead)) {
                    System.out.println("Syntax error at: " + lookahead_token.getLocation());
                    writer_err_report.append("Syntax error at line: ").append(String.valueOf(lookahead_token.getLocation())).append("\t Skip parsing : '").
                            append(unexpected).append("'\t Expected: '").append(expected).append("'\r\n");
                    parsing_stack.pop();
                } else {

                    // scan tokens until we get one with which we can resume the parse
                    System.out.println("Syntax error at: " + lookahead_token.getLocation());
                    writer_err_report.append("Syntax error at line: ").append(String.valueOf(lookahead_token.getLocation())).append("\t Unexpected: '").
                            append(unexpected).append("'\t Expected: '").append(expected).append("'\r\n");
                    while (!first_sets.get(top_of_stack).contains(lookahead) ||
                            ((first_sets.get(top_of_stack).contains("EPSILON") && !follow_sets.get(top_of_stack).contains(lookahead)))) {
                        skipCommentsRead();
                        if (lookahead.equals("$")) {
                            break;
                        }
                    }
                }
            }
        } else {
            System.out.println("Syntax error at the end of the file. \tUnexpected: " + lookahead);
            System.out.println("The program has syntax error(s).");
            writer_err_report.append("Syntax error at the end of the file.").append("\t Unexpected: '").
                    append(lookahead).append("'\r\n");
            writer_err_report.flush();
            writer_err_report.close();
            System.exit(0);
        }
    }


    /**
     * ignore comments token
     */
    private void skipCommentsRead() {
        // token type read
        String lookahead_type;
        do {
            lookahead_token = lexical_analyzer.nextToken();
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


    /**
     * match the token type to the forms of rules defined
     *
     * @param lookahead_type type of the token
     * @return corresponding to grammar rules
     */
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
            default:
                return lookahead_type;
        }
    }


}
