package syntacticAnalyzer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.*;
import java.util.*;

public class Grammar {

    private ArrayList<String> terminalList;
    private ArrayList<String> nonTerminalList;
    private Map<String, Rule> rules;
    private Map<String, ArrayList<String>> followSets;
    private Map<String, ArrayList<String>> firstSets;
    private Map<String, Map<String, String>> parsingTable;


    public Grammar() {
        terminalList = new ArrayList<>();
        nonTerminalList = new ArrayList<>();
        rules = new HashMap<>();
        followSets = new HashMap<>();
        firstSets = new HashMap<>();
        parsingTable = new HashMap<>();
    }


    public void createSymbolsProject() {
        terminalList = (ArrayList<String>) Arrays.asList(",", "+", "-", "|", "[", "intLit", "]", "=", "class", "id", "{", "}", ";", "(", ")", "floatLit", "!", ":",
                "void", ".", "*", "/", "&", "inherits", "sr", "main", "eq", "geq", "gt", "leq", "lt", "neq", "if", "then", "else", "read", "return", "while", "write",
                "float", "integer", "private", "public", "func", "var", "break", "continue", "string", "qm", "stringLit");

        nonTerminalList = (ArrayList<String>) Arrays.asList("START", "aParams", "aParamsTail", "addOp", "arithExpr", "arraySize", "assignOp", "assignStat", "classDecl",
                "expr", "fParams", "fParamsTail", "factor", "funcBody", "funcDecl", "funcDef", "funcHead", "functionCall", "idnest", "indice", "memberDecl", "multOp",
                "prog", "relExpr", "sign", "statBlock", "statement", "term", "type", "varDecl", "variable", "visibility");
    }


    /**
     * Parsing HTML file to get first and follow sets using JSOUP
     */
    public void importFollowSets() {

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
                ArrayList<String> firstSet = new ArrayList<>();
                ArrayList<String> followSet = new ArrayList<>();
                firstSet.addAll(Arrays.asList(colVals.get(1).text().split(" ")));
                if (colVals.get(3).text().equals("yes")) {
                    firstSet.add("EPSILON");
                }
                followSet.addAll(Arrays.asList(colVals.get(2).text().split(" ")));
                String nonTerminal = colVals.get(0).text();
                nonTerminal = nonTerminal.substring(0, 1).toUpperCase() + nonTerminal.substring(1).toLowerCase();
                firstSets.put(nonTerminal, firstSet);
                followSets.put(nonTerminal, followSet);
            }
            // output first sets
            for (Map.Entry<String, ArrayList<String>> entry : firstSets.entrySet()) {
                StringBuilder firstSetPrint = new StringBuilder();
                for (int i = 0; i < entry.getValue().size() - 1; i++) {
                    String terminal = entry.getValue().get(i);
                    if (terminal.equals("EPSILON")) {
                        firstSetPrint.append(terminal);
                    } else {
                        firstSetPrint.append("'").append(terminal).append("', ");
                    }
                }
                String terminalLast = entry.getValue().get(entry.getValue().size() - 1);
                if (terminalLast.equals("EPSILON")) {
                    firstSetPrint.append(terminalLast);
                } else {
                    firstSetPrint.append("'").append(terminalLast).append("'");
                }
                System.out.println("FIRST(<" + entry.getKey() + ">)= [" + firstSetPrint + "]");
            }

            // output follow sets
            for (Map.Entry<String, ArrayList<String>> entry : followSets.entrySet()) {
//                System.out.println(entry.getKey());
//                entry.getValue().forEach(System.out::println);
                StringBuilder followSetPrint = new StringBuilder();
                for (int i = 0; i < entry.getValue().size() - 1; i++) {
                    String terminal = entry.getValue().get(i);
                    followSetPrint.append("'").append(terminal).append("', ");
                }
                String terminalLast = entry.getValue().get(entry.getValue().size() - 1);
                followSetPrint.append("'").append(terminalLast).append("'");
//                System.out.println("FOLLOW(<"+ entry.getKey()+">)= [" + followSetPrint +"]");
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
            Document htmlDoc = Jsoup.parse(file_parsing_table, "UTF-8");
            ArrayList<String> downServers = new ArrayList<>();
            Element table = htmlDoc.select("table").get(1); //select the table.
            Elements rows = table.select("tr");
            Elements first = rows.get(0).select("th,td");       //header of the table

            List<String> headers = new ArrayList<String>();
//            System.out.println("headers");
            for (Element header : first) {
                headers.add(header.text());
                if(!header.text().isEmpty()){
                terminalList.add(header.text());}
//                System.out.println(header.text());
            }
            for (int i = 1; i < rows.size(); i++) {
                Element row = rows.get(i);
                Elements colVals = rows.get(i).select("th,td");
                Map<String, String> tableVals = new HashMap<>();
                int colCount = 1;
                for (int j = 1; j < colVals.size(); j++) {
                    String ruleID = colVals.get(j).text();
                    if (!ruleID.isEmpty()) {
                        ruleID = ruleID.replace(" → ", "_").replace(" ", "_").replace("&epsilon", "EPSILON");
//                        System.out.println(ruleID);
                        tableVals.put(headers.get(colCount++), ruleID);
                    } else {
                        tableVals.put(headers.get(colCount++), "error");
                    }
                }
                String nonTerminal = colVals.get(0).text();
                parsingTable.put(nonTerminal, tableVals);
                nonTerminalList.add(nonTerminal);
//                System.out.println("nonterminal: "+nonTerminal);
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
            String thisLine;
            while ((thisLine = reader.readLine()) != null) {
                if (!thisLine.isEmpty()) {
                    String[] ruleString = thisLine.split("->");
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // example symbols
    public void createSymbolsEx() {
        terminalList = (ArrayList<String>) Arrays.asList("0", "1", "(", ")", "+", "*", "$");
        nonTerminalList = (ArrayList<String>) Arrays.asList("E", "E'", "T", "T'", "F");
    }

    // example rules
    public void createRulesEx() {
        rules = new HashMap<>();
        rules.put("r1", new Rule("r1", "E", " T E' "));
        rules.put("r2", new Rule("r2", "E'", "+ T E' "));
        rules.put("r3", new Rule("r3", "E'", "EPSILON"));
        rules.put("r4", new Rule("r4", "T", " F T' "));
        rules.put("r5", new Rule("r5", "T'", " * F T' "));
        rules.put("r6", new Rule("r6", "T'", "EPSILON"));
        rules.put("r7", new Rule("r7", "F", " 0 "));
        rules.put("r8", new Rule("r8", "F", " 1 "));
        rules.put("r9", new Rule("r9", "F", " ( E ) "));
    }

    // example parsing table
    public void createParsingTable() {
        parsingTable = new HashMap<>();
        addToParsingRow("E", new String[]{"r1", "r1", "r1", "error", "error", "error", "error"});
        addToParsingRow("E'", new String[]{"error", "error", "error", "r3", "r2", "error", "r3"});
        addToParsingRow("T", new String[]{"r4", "r4", "r4", "error", "error", "error", "error"});
        addToParsingRow("T'", new String[]{"error", "error", "error", "r6", "r6", "r5", "r6"});
        addToParsingRow("F", new String[]{"r7", "r8", "r9", "error", "error", "error", "error"});
    }

    private void addToParsingRow(String nonTerminal, String[] rulesInRow) {
        Map<String, String> parsingTable_row = new HashMap<>(); // <terminal, rule>
        String[] terminalArray = terminalList.toArray(new String[0]);
        for (int i = 0; i < terminalArray.length; i++) {
            parsingTable_row.put(terminalArray[i], rulesInRow[i]);
        }
        parsingTable.put(nonTerminal, parsingTable_row);
    }


    public List<String> getTerminalList() {
        return terminalList;
    }

    public void setTerminalList(ArrayList<String> terminalList) {
        this.terminalList = terminalList;
    }

    public List<String> getNonTerminalList() {
        return nonTerminalList;
    }

    public void setNonTerminalList(ArrayList<String> nonTerminalList) {
        this.nonTerminalList = nonTerminalList;
    }

    public Map<String, Rule> getRules() {
        return rules;
    }

    public void setRules(Map<String, Rule> rules) {
        this.rules = rules;
    }

    public Map<String, Map<String, String>> getParsingTable() {
        return parsingTable;
    }

    public void setParsingTable(Map<String, Map<String, String>> parsingTable) {
        this.parsingTable = parsingTable;
    }
}
