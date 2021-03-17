package syntacticAnalyzer;

import AST.Node;
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
//        grammar.getTerminal_list().forEach(System.out::println);
//        System.out.println(grammar.getTerminal_list().size());
//        grammar.getNonTerminal_list().forEach(System.out::println);
//        System.out.println(grammar.getNonTerminal_list().size());
//            grammar.getRules_attribute().forEach((key, value) -> System.out.println(key));
//        grammar.getRules_attribute().forEach((key, value) -> System.out.println(value.toString()));

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

//        System.out.println(grammar.getSymbol_map().get("rsqbr"));
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
            File outfile_AST = new File(file_path_temp + ".outast");
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
        writer_derivation.append(derivation).append("\r\n");

//        parsing_stack.push("E");
//        derivation = "E";
        skipCommentsRead();


        while (!parsing_stack.peek().equals("$")) {


            top_of_stack = parsing_stack.peek();
//            System.out.println("[parsing stack] top: " + top_of_stack);
//            System.out.println("lookahead : "+ lookahead);
//            System.out.println("terminal_suc: "+terminal_suc_token);
            if (grammar.getTerminal_list().contains(top_of_stack)) {
//                System.out.println("top is a terminal");
//                System.out.println("[parse]token type read: " + lookahead);
                terminal_suc_token = lookahead_token;
//                System.out.println("terminal_suc 2: "+terminal_suc_token);
                if (top_of_stack.equals(lookahead)) {
//                    System.out.println("token: "+lookaheadToken.toString());

                    parsing_stack.pop();
//                    System.out.println("[parse]lexeme: " + lookahead_token.getLexeme());
//                    System.out.println("[parse]index: " + index_terminal_derivation);
//                    System.out.println("1:" + derivation.substring(0, index_terminal_derivation));
//                    System.out.println("lookahead : " + lookahead);
//                    System.out.println("[parse][before der]" + derivation);

                    // process derivation
                    if (index_terminal_derivation < derivation.length()) {
                        String temp_derivation = derivation;

                        derivation = derivation.substring(0, index_terminal_derivation) +
                                derivation.substring(index_terminal_derivation).replaceFirst(lookahead, lookahead_token.getLexeme());
                        if (!temp_derivation.equals(derivation)) {
                            // store the index of the latest terminal in derivation
                            index_terminal_derivation += derivation.substring(index_terminal_derivation).indexOf(lookahead_token.getLexeme());
//                    System.out.println("[parse][der]" + derivation);
//                    System.out.println("[parse][der]index: " + index_terminal_derivation);
                            writer_derivation.append("=> ").append(derivation).append("\r\n");
                        }

                    }
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
//                    System.out.println(grammar.getSemantic_actions_list().contains(top_of_stack));
                    if (grammar.getSemantic_actions_list().contains(top_of_stack)) {
//                        System.out.println("[semantic action begins]");
                        semanticActionOnStack();
                    }
                }
            }
//            writer_derivation.append("=> ").append(derivation).append("\r\n");
//            System.out.println("=> " + derivation);
        }


        // print the node
        semantic_stack.peek().print();

        printToDot(semantic_stack.peek());

//        System.out.println(lookahead.equals("$"));
        if (!lookahead.equals("$") && !error) {
//            writer_err_report.append("")
            System.out.println("here");
            System.out.println("Syntax error at: " + lookahead_token.getLocation() + ";\t Unexpected: '" + lookahead + "'.");
            writer_err_report.append("Syntax error at line: ").append(String.valueOf(lookahead_token.getLocation())).append(";\t Unexpected: '").
                    append(lookahead).append("'\r\n");

        }

        // output error info
//        System.out.println(lookahead_token);

        return lookahead.equals("$") && !error;
    }


    // output to a dot.file for displaying the AST node
    public void printToDot(Node node) {

        writer_AST.append("digraph AST {").append("\r\n");
        writer_AST.append("node [shape=record];\r\n");
        writer_AST.append("node [fontname=Sans];charset=\"UTF-8\" splines=true splines=spline rankdir =LR\r\n");

        printNodetoDot(node);

        writer_AST.append("}");

    }

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
        if(node.getM_line()!=0){
            node_name_data = node_name_data + " | "+ node.getM_line();
        }
        String node_id = String.valueOf(node.m_nodeId);
        if (children.isEmpty()) {

            writer_AST.append(node_id);
            writer_AST.append(";\r\n");
            writer_AST.append(node_id).append("[label=\"").append(node_name_data).append("\"]\r\n");
        } else {

            for (Node child : children) {
                writer_AST.append(node_id).append(" -> ");
                printNodetoDot(child);
            }
            writer_AST.append(node_id).append("[label=\"").append(node_name_data).append("\"];\r\n");
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
//        System.out.println("[semantic stack] action begin: " + semantic_action.toString());
            String node_type = right_sem_act.substring(right_sem_act.indexOf("(") + 1, right_sem_act.indexOf(")"));
            String node_lexeme = terminal_suc_token.getLexeme();
            int node_line = terminal_suc_token.getLocation();
//            System.out.println(node_type);
//            System.out.println(node_lexeme);
            System.out.println("[makeNode branch] token going to be made as a leaf: " + terminal_suc_token.toString() + " " + node_type);
            node_to_push = nodeFactory.makeNode(node_type, node_lexeme, node_line);

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
                int node_line;
                if (terminal_suc_token != null) {
                    node_line = terminal_suc_token.getLocation();
                }else{
                    node_line = lookahead_token.getLocation();
                }

//                System.out.println("op: " + op);

                // recorde the list of children
                ArrayList<Node> para_nodes = new ArrayList<>();
                Node opNode_backup = null;

                // variable number of children
                if (parameters[parameters.length - 1].trim().equals("n")) {
                    assert semantic_stack.peek() != null;
                    if (!semantic_stack.isEmpty()) {
                        node_on_top = semantic_stack.peek();
                        //                    System.out.println("[node on the stack top]" + node_on_top.m_sa_name);
//                    System.out.println("para[2]" + parameters[parameters.length - 2].trim());
//                    int num_kids = Integer.parseInt(parameters[parameters.length-1].trim().substring(parameters[parameters.length-1].trim().indexOf("n")+1));
                        int num_kids = parameters.length - 2;
//                    System.out.println("num_kids: " + num_kids);
                        ArrayList<String> string_kids = new ArrayList<>();

                        for (int i = 1; i <= num_kids; i++) {
                            string_kids.add(parameters[i].trim());
//                        System.out.println(parameters[i].trim());
                        }
//                    System.out.println("size of list: " + string_kids.size());
                        String name_node_on_top = node_on_top.m_sa_name;
//                    System.out.println(name_node_on_top);
                        while (ifTheKidsInMakeFamily(string_kids, name_node_on_top)) {
                            Node node_to_pop = semantic_stack.pop();
//                        System.out.println("[!!!node to pop]: " + node_to_pop.m_sa_name);
                            para_nodes.add(node_to_pop);
                            if (!semantic_stack.isEmpty()) {
                                node_on_top = semantic_stack.peek();
                                name_node_on_top = node_on_top.m_sa_name;
//                            System.out.println("[node on top]: " + node_on_top.m_sa_name);
                            } else {
                                break;
                            }
                        }
                    }

                } else {

                    // any one of kids can make a family
                    if (parameters[parameters.length - 1].trim().equals("any")) {

                        // only 2 kids can be interchangeable
                        if (parameters[parameters.length - 2].trim().equals("2")) {
                            node_on_top = semantic_stack.peek();
//                    System.out.println("[node on the stack top]" + node_on_top.m_sa_name);
//                    System.out.println("para[2]" + parameters[parameters.length - 2].trim());
//                    int num_kids = Integer.parseInt(parameters[parameters.length-1].trim().substring(parameters[parameters.length-1].trim().indexOf("n")+1));

                            // real kid
                            int index_kid = parameters.length - 3;
//                            System.out.println("num_kids: " + num_kids);
                            ArrayList<String> string_kids = new ArrayList<>();

//                            System.out.println(parameters[index_kid].trim());
//                            System.out.println(parameters[index_kid - 1].trim());

                            string_kids.add(parameters[index_kid].trim());
                            string_kids.add(parameters[index_kid - 1].trim());


                            String name_node_on_top = node_on_top.m_sa_name;
//                            System.out.println("[any 2]name_node_on_top: " + name_node_on_top);
                            if (ifTheKidsInMakeFamily(string_kids, name_node_on_top)) {
                                Node node_to_pop = semantic_stack.pop();
//                                System.out.println("[!!!node to pop]: " + node_to_pop.m_sa_name);
                                para_nodes.add(node_to_pop);
                            }

                            for (int i = parameters.length - 5; i > 0; i--) {
                                if (!semantic_stack.isEmpty()) {
                                    node_on_top = semantic_stack.peek();
//                                    System.out.println("[any 2 branch][node on the stack top]" + node_on_top.m_sa_name);
//                                    System.out.println("[any 2 branch]parameter: " + parameters[i]);
                                    if (parameters[i].trim().equals(node_on_top.m_sa_name)) {
                                        Node node_to_pop = semantic_stack.pop();
//                                    System.out.println("[node to pop] " + node_to_pop.m_sa_name);
                                        para_nodes.add(node_to_pop);
                                    }
                                }
                            }


                        } else {
                            // any one of kids can make a family (general case)
                            node_on_top = semantic_stack.peek();
//                    System.out.println("[node on the stack top]" + node_on_top.m_sa_name);
//                    System.out.println("para[2]" + parameters[parameters.length - 2].trim());
//                    int num_kids = Integer.parseInt(parameters[parameters.length-1].trim().substring(parameters[parameters.length-1].trim().indexOf("n")+1));
                            int num_kids = parameters.length - 2;
//                            System.out.println("num_kids: " + num_kids);
                            ArrayList<String> string_kids = new ArrayList<>();

                            for (int i = 1; i <= num_kids; i++) {
                                string_kids.add(parameters[i].trim());
//                                System.out.println(parameters[i].trim());
                            }
                            String name_node_on_top = node_on_top.m_sa_name;
//                            System.out.println(name_node_on_top);
                            if (ifTheKidsInMakeFamily(string_kids, name_node_on_top)) {
                                Node node_to_pop = semantic_stack.pop();
//                                System.out.println("[!!!node to pop]: " + node_to_pop.m_sa_name);
                                para_nodes.add(node_to_pop);
                            }
                        }

                    } else {

                        // first create a parent node, then reuse the node to make a family
                        if (parameters[parameters.length - 1].trim().equals("reuse")) {
                            Arrays.stream(parameters).forEach(System.out::println);
                            String op_name = parameters[0];
                            System.out.println("size of parameters: " + parameters.length);
                            System.out.println("op_name: " + op_name);
                            for (int i = parameters.length - 2; i >= 0; i--) {
                                System.out.println(i);
                                if (!semantic_stack.isEmpty()) {
                                    node_on_top = semantic_stack.peek();
                                    System.out.println("[reuse][node on the stack top]" + node_on_top.m_sa_name);
                                    System.out.println("[reuse]parameter: " + parameters[i]);
                                    if (parameters[i].trim().equals(node_on_top.m_sa_name)) {
                                        System.out.println(parameters[i].trim());
                                        // reserve the node for opNode
                                        if (node_on_top.m_sa_name.equals(op_name)) {
                                            System.out.println("original node on top : " + node_on_top);
                                            opNode_backup = semantic_stack.pop();
                                        } else {

                                            Node node_to_pop = semantic_stack.pop();
                                            System.out.println("[node to pop] " + node_to_pop.m_sa_name);
                                            para_nodes.add(node_to_pop);

                                        }

                                    }
                                }
                            }


                        } else {

                            // fixed number of children
                            for (int i = parameters.length - 1; i > 0; i--) {
                                if (!semantic_stack.isEmpty()) {
                                    node_on_top = semantic_stack.peek();
//                                    System.out.println("[fixed number branch][node on the stack top]" + node_on_top.m_sa_name);
//                                    System.out.println("[fixed number branch]parameter: " + parameters[i]);
                                    if (parameters[i].trim().equals(node_on_top.m_sa_name)) {
                                        Node node_to_pop = semantic_stack.pop();
//                                    System.out.println("[node to pop] " + node_to_pop.m_sa_name);
                                        para_nodes.add(node_to_pop);
                                    }
                                }
                            }


                        }


                    }


                }


                Node opNode;

                // keep the arraylist consistent with the following call of makeFamily
                if (parameters[parameters.length - 1].trim().equals("reuse")) {
//                    System.out.println("reuse previous node -i");
//                    System.out.println("opNode_backup: "+ opNode_backup);
//                    System.out.println("[opNode(last)]: " + para_nodes.get(para_nodes.size() - 1));
                    opNode = opNode_backup;

                } else {
//                    System.out.println("op:" + op);
                    opNode = nodeFactory.makeNode(op, op, node_line);
//                    System.out.println("[opNode]" + opNode);
                }


                System.out.println("[opNode]" + opNode);
                System.out.println("para_nodes: " + para_nodes.size());
//                System.out.println("[para_nodes][0]" + para_nodes.get(0));
//                System.out.println("[para_nodes][1]" + para_nodes.get(1));


                node_to_push = makeFamily(opNode, para_nodes);
//                node_to_push.print();


                semantic_stack.push(node_to_push);

                // migrate makeFamily node
                Node temp = semantic_stack.pop();
                temp.setName(left_sem_act);
                semantic_stack.push(temp);
//                System.out.println("[MMM----]" + right_sem_act + " migration to: " + left_sem_act);
            } else {// migrate operation

//                System.out.println("[semantic stack]node of top :" + node_on_top.m_sa_name);
//                System.out.println("[semantic stack]right sem act : " + right_sem_act);

                if (node_on_top != null) {
                    if (node_on_top.m_sa_name.equals(right_sem_act)) {
                        Node temp = semantic_stack.pop();
                        temp.setName(left_sem_act);
                        semantic_stack.push(temp);
//                        System.out.println("[MMM----]" + right_sem_act + " migration to: " + left_sem_act);
//                        temp.print();
                    } else {
//                        System.out.println("node on top is null!!!");
                    }
                }
            }
        }

    }


    // check if the node on the top of stack will be made a family
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
            case 7:
                return kids.get(0).equals(name_node) || kids.get(1).equals(name_node) ||
                        kids.get(2).equals(name_node) || kids.get(3).equals(name_node) ||
                        kids.get(4).equals(name_node) || kids.get(5).equals(name_node) ||
                        kids.get(6).equals(name_node);
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


    // generates a family with n children under a parent op
    private Node makeFamily(Node opNode, ArrayList<Node> kids) {
        if (!kids.isEmpty()) {
            Node first_kids = kids.get(kids.size() - 1);
//         System.out.println("[kids][first]"+ first_in_kids.toString());
            for (int i = kids.size() - 1; i >= 1; i--) {
                first_kids = first_kids.makeSiblings(kids.get(i - 1));

            }
//            System.out.println("[after make siblings]" + first_kids.toString());
            return opNode.adoptChildren(first_kids);
        } else
            return opNode;
    }


    private String lookupParsingTable(String top_of_stack, String lookahead) {
        return grammar.getParsing_table().get(top_of_stack).get(lookahead);
    }


    private void inverseRHSMultiplePush(String LHS, String rule) {
//        System.out.println("LHS: " + LHS);
        System.out.println("rule: " + rule);
//        System.out.println(grammar.getRules_attribute().get(rule));
        String RHS_in_rule = grammar.getRules_attribute().get(rule).getRule_RHS().trim();
//        System.out.println(RHS_in_rule);
        if (!RHS_in_rule.equals("EPSILON")) {
//            int index_LHS = derivation.indexOf(LHS);
//            System.out.println("index LHS : " + index_LHS);
//            System.out.println("length of LHS: " + LHS.length());

            // generate derivation
            String RHS_to_replace = RHS_in_rule;
            if (RHS_in_rule.contains("EPSILON")) {
                RHS_to_replace = RHS_to_replace.replace("EPSILON", "");
            }

//            System.out.println("LHS: " + LHS);
//            System.out.println("RHS_to_replace: " + RHS_to_replace);
            derivation = derivation.replaceFirst(LHS.trim(), RHS_to_replace.trim());
            derivation = derivation.replaceAll(" sa-\\d\\d", "");
            writer_derivation.append("=> ").append(derivation).append("\r\n");

//            System.out.println(" [inverse]: " + derivation);


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
            writer_derivation.append("=> ").append(derivation).append("\r\n");

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

        if (lookahead_token != null) {
//            System.out.println(lookahead);
            String unexpected = grammar.getSymbol_map().get(lookahead) == null ? lookahead : grammar.getSymbol_map().get(lookahead);
//            System.out.println(unexpected);
            System.out.println("Syntax error at: " + lookahead_token.getLocation());
            writer_err_report.append("Syntax error at line: ").append(String.valueOf(lookahead_token.getLocation())).append(";\t Unexpected: '").
                    append(unexpected).append("';\t Expected: '").append(expected).append("'\r\n");

//        System.out.println("lookahead: " + lookahead);
//        System.out.println("top of stack: " + top_of_stack);
//        System.out.println(terminal_suc_token.getLexeme());
//        System.out.println(first_sets.get(top_of_stack));
//        System.out.println(follow_sets.get(top_of_stack));


            if (grammar.getTerminal_list().contains(top_of_stack)) {
                parsing_stack.pop();
            } else {
                if (!grammar.getTerminal_list().contains(top_of_stack)) {
                    if (lookahead.equals("$") || follow_sets.get(top_of_stack).contains(lookahead)) {

                        parsing_stack.pop();
                    } else {
                        while (!first_sets.get(top_of_stack).contains(lookahead) ||
                                ((first_sets.get(top_of_stack).contains("EPSILON") && !follow_sets.get(top_of_stack).contains(lookahead)))) {
                            skipCommentsRead();
//                        System.out.println("[2 branch]loook ahead: "+lookahead);
                            if (lookahead.equals("$")) {
                                break;
                            }
                        }
                    }
                }
            }
        } else {

            System.out.println("Syntax error at: " + terminal_suc_token.getLocation() + "; Unexpected: " + lookahead);
            System.out.println("The program has syntax error(s).");
            writer_err_report.append("Syntax error at line: ").append(String.valueOf(terminal_suc_token.getLocation())).append(";\t Unexpected: '").
                    append(lookahead).append("'\r\n");
            writer_err_report.flush();
            writer_err_report.close();
            System.exit(0);
        }
    }


    // ignore comments token
    private void skipCommentsRead() {
        do {
//            System.out.println("skip loop");
            lookahead_token = lexical_analyzer.nextToken();
//            System.out.println("[skip read method ]lookahead_token: " + lookahead_token);
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
