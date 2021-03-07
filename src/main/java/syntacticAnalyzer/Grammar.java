package syntacticAnalyzer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.*;
import java.util.*;

public class Grammar {

    private ArrayList<String> terminal_list;
    private ArrayList<String> nonTerminal_list;
    private ArrayList<String> semantic_actions_list;
    private Map<String, Rule> rules;
    private Map<String, SemanticAction> semantic_actions;
    private Map<String, Rule> rules_attribute;
    private final Map<String, ArrayList<String>> follow_sets;
    private final Map<String, ArrayList<String>> first_sets;
    private Map<String, Map<String, String>> parsing_table;


    public Grammar() {
        terminal_list = new ArrayList<>();
        nonTerminal_list = new ArrayList<>();
        rules = new HashMap<>();
        follow_sets = new HashMap<>();
        first_sets = new HashMap<>();
        parsing_table = new HashMap<>();
    }


    public void createSymbolsProject() {
        terminal_list = (ArrayList<String>) Arrays.asList(",", "+", "-", "|", "[", "intLit", "]", "=", "class", "id", "{", "}", ";", "(", ")", "floatLit", "!", ":",
                "void", ".", "*", "/", "&", "inherits", "sr", "main", "eq", "geq", "gt", "leq", "lt", "neq", "if", "then", "else", "read", "return", "while", "write",
                "float", "integer", "private", "public", "func", "var", "break", "continue", "string", "qm", "stringLit");

        nonTerminal_list = (ArrayList<String>) Arrays.asList("START", "aParams", "aParamsTail", "addOp", "arithExpr", "arraySize", "assignOp", "assignStat", "classDecl",
                "expr", "fParams", "fParamsTail", "factor", "funcBody", "funcDecl", "funcDef", "funcHead", "functionCall", "idnest", "indice", "memberDecl", "multOp",
                "prog", "relExpr", "sign", "statBlock", "statement", "term", "type", "varDecl", "variable", "visibility");
    }


    /**
     * Parsing HTML file to get first and follow sets using JSOUP
     */
    public void importFirstFollowSets() {

        try {
            File file_parsing_table = new File("./src/main/resources/first_follow_sets.html");
            Document htmlDoc = Jsoup.parse(file_parsing_table, "UTF-8");

            Element table = htmlDoc.select("table").get(2); //select table.
            Elements rows = table.select("tr");
            Elements first = rows.get(0).select("th,td");       //header of the table

            List<String> headers = new ArrayList<String>();
//            System.out.println("headers");
            for (Element header : first) {
                headers.add(header.text());
            }

            // parsing the table in HTML file
            for (int i = 1; i < rows.size(); i++) {
                Elements colVals = rows.get(i).select("th,td");
                ArrayList<String> first_set = new ArrayList<>();
                ArrayList<String> follow_set = new ArrayList<>();
                first_set.addAll(Arrays.asList(colVals.get(1).text().split(" ")));
                if (colVals.get(3).text().equals("yes")) {
                    first_set.add("EPSILON");
                }
                follow_set.addAll(Arrays.asList(colVals.get(2).text().split(" ")));
                String non_terminal = colVals.get(0).text();
//                non_terminal = non_terminal.substring(0, 1).toUpperCase() + non_terminal.substring(1).toLowerCase();
                first_sets.put(non_terminal, first_set);
                follow_sets.put(non_terminal, follow_set);
            }

            // output first sets
            for (Map.Entry<String, ArrayList<String>> entry : first_sets.entrySet()) {
                StringBuilder first_set_print = new StringBuilder();
                for (int i = 0; i < entry.getValue().size() - 1; i++) {
                    String terminal = entry.getValue().get(i);
                    if (terminal.equals("EPSILON")) {
                        first_set_print.append(terminal);
                    } else {
                        first_set_print.append("'").append(terminal).append("', ");
                    }
                }
                String terminal_last = entry.getValue().get(entry.getValue().size() - 1);
                if (terminal_last.equals("EPSILON")) {
                    first_set_print.append(terminal_last);
                } else {
                    first_set_print.append("'").append(terminal_last).append("'");
                }
//                System.out.println("FIRST(<" + entry.getKey() + ">)= [" + first_set_print + "]");
            }

            // output follow sets
            for (Map.Entry<String, ArrayList<String>> entry : follow_sets.entrySet()) {
//                System.out.println(entry.getKey());
//                entry.getValue().forEach(System.out::println);
                StringBuilder follow_set_print = new StringBuilder();
                for (int i = 0; i < entry.getValue().size() - 1; i++) {
                    String terminal = entry.getValue().get(i);
                    follow_set_print.append("'").append(terminal).append("', ");
                }
                String terminalLast = entry.getValue().get(entry.getValue().size() - 1);
                follow_set_print.append("'").append(terminalLast).append("'");
//                System.out.println("FOLLOW(<"+ entry.getKey()+">)= [" + follow_set_print +"]");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parsing HTML file to get parsing table using JSOUP; also terminal list and nonTerminal list
     */
    public void importParsingTable() {

        try {
            File file_parsing_table = new File("./src/main/resources/parsing_table.html");
            System.out.println("[Grammar] Importing parsing table from file: " + file_parsing_table.getName());
            Document html_doc = Jsoup.parse(file_parsing_table, "UTF-8");
            Element table = html_doc.select("table").get(1); //select the table.
            Elements rows = table.select("tr");
            Elements first = rows.get(0).select("th,td");       //header of the table

            List<String> headers = new ArrayList<String>();
//            System.out.println("headers");
            for (Element header : first) {
                headers.add(header.text());
                if (!header.text().isEmpty()) {
                    terminal_list.add(header.text());
                }
//                System.out.println(header.text());
            }
            for (int i = 1; i < rows.size(); i++) {
                Element row = rows.get(i);
                Elements col_vals = rows.get(i).select("th,td");
                Map<String, String> table_vals = new HashMap<>();
                int col_count = 1;
                for (int j = 1; j < col_vals.size(); j++) {
                    String ruleID = col_vals.get(j).text();
                    if (!ruleID.isEmpty()) {
                        ruleID = ruleID.replace(" → ", "_").replace(" ", "_").replace("&epsilon", "EPSILON");
//                        System.out.println(ruleID);
                        table_vals.put(headers.get(col_count++), ruleID);
                    } else {
                        table_vals.put(headers.get(col_count++), "error");
                    }
                }
                String non_terminal = col_vals.get(0).text();
                parsing_table.put(non_terminal, table_vals);
                nonTerminal_list.add(non_terminal);
//                System.out.println("nonterminal: "+non_terminal);
//                System.out.println("rule: "+tableVal.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * import rules from final LL1 grammar file
     */
    public void importRules() {

        BufferedReader reader;
        String rules_file_path = "./src/main/resources/LL1.paquetFinal.grm.ucalgary";
        File file_read = new File(rules_file_path);
        System.out.println("[Grammar] Reading the grammar rules from file: " + file_read.getName());
        try {
            reader = new BufferedReader(new FileReader(rules_file_path));
            String this_line;
            while ((this_line = reader.readLine()) != null) {
                if (!this_line.isEmpty()) {
                    String[] ruleString = this_line.split("->");
                    Rule rule = new Rule();
                    rule.setRule_LHS(ruleString[0].trim());
                    if (ruleString[1].equals("  . ")) {
                        rule.setRule_RHS("EPSILON");
                    } else {
                        rule.setRule_RHS(ruleString[1].substring(0, ruleString[1].lastIndexOf(".") - 1));
                    }
                    String ruleId = rule.getRule_LHS() + "_" + rule.getRule_RHS().trim().replaceAll(" ", "_");
                    rule.setRule_id(ruleId);
//                    System.out.println(rule.toString());
                    rules.put(rule.getRule_id(), rule);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // example symbols
    public void createSymbolsEx() {
        terminal_list =  new ArrayList<> (Arrays.asList("id", "(", ")", "plus", "mult", "$"));
        nonTerminal_list =  new ArrayList<> (Arrays.asList("E", "E'", "T", "T'", "F"));
        semantic_actions_list = new ArrayList<>(Arrays.asList("sa_A", "sa_B", "sa_C","sa_D","sa_E","sa_F","sa_G","sa_H","sa_I","sa_J","sa_K","sa_L"));
    }

    // example rules
    public void createRulesEx() {
        rules = new HashMap<>();
        rules.put("r1", new Rule("r1", "E", " T E' "));
        rules.put("r2", new Rule("r2", "E'", " plus T E' "));
        rules.put("r3", new Rule("r3", "E'", "EPSILON"));
        rules.put("r4", new Rule("r4", "T", " F T' "));
        rules.put("r5", new Rule("r5", "T'", " mult F T' "));
        rules.put("r6", new Rule("r6", "T'", "EPSILON"));
        rules.put("r7", new Rule("r7", "F", " id "));
        rules.put("r8", new Rule("r8", "F", " ( E ) "));
    }

    // example parsing table
    public void createParsingTableEx() {
        parsing_table = new HashMap<>();
        addToParsingRow("E", new String[]{"r1", "r1", "error", "error", "error", "error"});
        addToParsingRow("E'", new String[]{"error", "error", "r3", "r2", "error", "r3"});
        addToParsingRow("T", new String[]{"r4", "r4", "error", "error", "error", "error"});
        addToParsingRow("T'", new String[]{"error", "error", "r6", "r6", "r5", "r6"});
        addToParsingRow("F", new String[]{"r7", "r8", "error", "error", "error", "error"});
    }

    public void createSemanticActions(){
        semantic_actions = new HashMap<>();
        semantic_actions.put("sa_A", new SemanticAction("sa_A", "E'_i", "T_s"));
        semantic_actions.put("sa_B", new SemanticAction("sa_B", "E_s", "E'_s"));
        semantic_actions.put("sa_C", new SemanticAction("sa_C", "E'_i", "makeFamily(plus, E'_i, T_s)"));
        semantic_actions.put("sa_D", new SemanticAction("sa_D", "E'_s","E'_s"));
        semantic_actions.put("sa_E", new SemanticAction("sa_E", "E'_s","E'_i"));
        semantic_actions.put("sa_F", new SemanticAction("sa_F", "T'_i","F_s"));
        semantic_actions.put("sa_G", new SemanticAction("sa_G","T_s","T'_s"  ));
        semantic_actions.put("sa_H", new SemanticAction("sa_H", "T'_i","makeFamily(mult,T'_i,F_s)"));
        semantic_actions.put("sa_I", new SemanticAction("sa_I", "T'_s", "T'_s"));
        semantic_actions.put("sa_J", new SemanticAction("sa_J", "T'_s", "T'_i"));
        semantic_actions.put("sa_K", new SemanticAction("sa_K", "F_s","makeNode(id)"));
        semantic_actions.put("sa_L", new SemanticAction("sa_L", "F_s", "E_s"));
    }

    public void createRulesWithAttributeEx(){
        rules_attribute = new HashMap<>();
        rules_attribute.put("r1", new Rule("r1", "E", " T sa_A E' sa_B "));
        rules_attribute.put("r2", new Rule("r2", "E'", " plus T sa_C E' sa_D "));
        rules_attribute.put("r3", new Rule("r3", "E'", "EPSILON sa_E"));
        rules_attribute.put("r4", new Rule("r4", "T", " F sa_F T' sa_G"));
        rules_attribute.put("r5", new Rule("r5", "T'", " mult F sa_H T' sa_I "));
        rules_attribute.put("r6", new Rule("r6", "T'", "EPSILON sa_J"));
        rules_attribute.put("r7", new Rule("r7", "F", " id sa_K"));
        rules_attribute.put("r8", new Rule("r8", "F", " ( E ) sa_L"));
    }

    private void addToParsingRow(String nonTerminal, String[] rulesInRow) {
        Map<String, String> parsingTable_row = new HashMap<>(); // <terminal, rule>
        String[] terminalArray = terminal_list.toArray(new String[0]);
        for (int i = 0; i < terminalArray.length; i++) {
            parsingTable_row.put(terminalArray[i], rulesInRow[i]);
        }
        parsing_table.put(nonTerminal, parsingTable_row);
    }


    public List<String> getTerminal_list() {
        return terminal_list;
    }

    public void setTerminal_list(ArrayList<String> terminal_list) {
        this.terminal_list = terminal_list;
    }

    public List<String> getNonTerminal_list() {
        return nonTerminal_list;
    }

    public void setNonTerminal_list(ArrayList<String> nonTerminal_list) {
        this.nonTerminal_list = nonTerminal_list;
    }

    public Map<String, Rule> getRules() {
        return rules;
    }

    public void setRules(Map<String, Rule> rules) {
        this.rules = rules;
    }

    public Map<String, Map<String, String>> getParsing_table() {
        return parsing_table;
    }

    public void setParsing_table(Map<String, Map<String, String>> parsing_table) {
        this.parsing_table = parsing_table;
    }

    public Map<String, ArrayList<String>> getFollow_sets() {
        return follow_sets;
    }

    public Map<String, ArrayList<String>> getFirst_sets() {
        return first_sets;
    }

    public ArrayList<String> getSemantic_actions_list() {
        return semantic_actions_list;
    }

    public void setSemantic_actions_list(ArrayList<String> semantic_actions_list) {
        this.semantic_actions_list = semantic_actions_list;
    }

    public Map<String, SemanticAction> getSemantic_actions() {
        return semantic_actions;
    }

    public void setSemantic_actions(Map<String, SemanticAction> semantic_actions) {
        this.semantic_actions = semantic_actions;
    }

    public Map<String, Rule> getRules_attribute() {
        return rules_attribute;
    }

    public void setRules_attribute(Map<String, Rule> rules_attribute) {
        this.rules_attribute = rules_attribute;
    }
}
