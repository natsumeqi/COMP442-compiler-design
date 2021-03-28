package syntacticAnalyzer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Grammar {

    private ArrayList<String> terminal_list;
    private ArrayList<String> nonTerminal_list;
    private final ArrayList<String> semantic_actions_list;
    private Map<String, String> symbol_map;

    private Map<String, Rule> rules;                            // first step without attribute
    private Map<String, SemanticAction> semantic_actions;
    private final Map<String, Rule> rules_attribute;

    private Map<String, ArrayList<String>> follow_sets;
    private Map<String, ArrayList<String>> first_sets;
    private Map<String, Map<String, String>> parsing_table;


    public Grammar() {
//        terminal_list = new ArrayList<>();
        nonTerminal_list = new ArrayList<>();
        semantic_actions_list = new ArrayList<>();
        rules_attribute = new HashMap<>();
        follow_sets = new HashMap<>();
        first_sets = new HashMap<>();
        parsing_table = new HashMap<>();
    }


    public void createSymbols() {

        symbol_map = Stream.of(new String[][]{{"dot", "."}, {"semi", ";"}, {"rpar", ")"}, {"lpar", "("},
                {"rcurbr", "}"}, {"lcurbr", "{"}, {"minus", "-"}, {"plus", "+"}, {"geq", ">="},
                {"leq", "<="}, {"gt", ">"}, {"lt", "<"}, {"neq", "<>"}, {"eq", "=="}, {"comma", ","},
                {"div", "/"}, {"mult", "*"}, {"rsqbr", "]"}, {"lsqbr", "["}, {"colon", ":"}, {"qm", "?"},
                {"not", "!"}, {"sr", "::"}, {"assign", "="}, {"or", "|"}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));


        terminal_list = new ArrayList<>(Arrays.asList("$", "private", "public", "id", "dot", "semi", "string", "float", "integer", "continue",
                "break", "rpar", "lpar", "return", "write", "read", "while", "else", "then", "if", "rcurbr",
                "lcurbr", "minus", "plus", "geq", "leq", "gt", "lt", "neq", "eq", "main", "comma", "and", "div", "mult", "var",
                "intnum", "inherits", "rsqbr", "lsqbr", "colon", "func", "void", "qm", "not", "stringlit", "floatnum", "sr", "class", "assign", "or"));


        nonTerminal_list = new ArrayList<>(Arrays.asList(
                "START", "ADDOP", "APARAMS", "APARAMSTAIL", "ARITHEXPR", "ARITHEXPRTAIL", "ARRAYSIZEREPT",
                "ASSIGNOP", "ASSIGNSTATTAIL", "CLASSDECL", "CLASSDECLBODY", "CLASSMETHOD", "EXPR", "EXPRTAIL",
                "FACTOR", "FPARAMS", "FPARAMSTAIL", "FUNCBODY", "FUNCDECL", "FUNCDECLTAIL", "FUNCDEF",
                "FUNCHEAD", "FUNCORASSIGNSTAT", "FUNCORASSIGNSTATIDNEST", "FUNCORASSIGNSTATIDNESTFUNCTAIL",
                "FUNCORASSIGNSTATIDNESTVARTAIL", "FUNCORVAR", "FUNCORVARIDNEST", "FUNCORVARIDNESTTAIL",
                "FUNCSTATTAIL", "FUNCSTATTAILIDNEST", "FUNCTION", "INDICEREP", "INHERIT", "INTNUM",
                "MEMBERDECL", "METHODBODYVAR", "MULTOP", "NESTEDID", "PROG", "RELOP", "SIGN",
                "STATBLOCK", "STATEMENT", "STATEMENTLIST", "TERM", "TERMTAIL", "TYPE", "VARDECL",
                "VARDECLREP", "VARIABLE", "VARIABLEIDNEST", "VARIABLEIDNESTTAIL", "VISIBILITY"));
    }


    public void generateGrammarProject() {
        createSymbols();
        importRules();
        importSemanticActions();
        importFromJsonFiles();
//        importParsingTable();
//        importFirstFollowSets();
//        writeToFileTwoSets();
    }

    private void importSemanticActions() {


        semantic_actions = new HashMap<>();
        semantic_actions.put("sa-01", new SemanticAction("sa-01", "ClassList_s", "makeFamily(ClassList, ClassDecl_s, n)"));
        semantic_actions.put("sa-02", new SemanticAction("sa-02", "MainBlock_s", "makeFamily(MainBlock, FuncBody_s)"));
        semantic_actions.put("sa-03", new SemanticAction("sa-03", "Prog_s", "makeFamily(Prog, ClassList_s, FuncDefList_s, MainBlock_s)"));
        semantic_actions.put("sa-04", new SemanticAction("sa-04", "ClassDecl_s", "makeFamily(ClassDecl, Id_i, InherList_s, MembList_s)"));
        semantic_actions.put("sa-05", new SemanticAction("sa-05", "FuncDefList_s", "makeFamily(FuncDefList, FuncDef_s, n)"));
        semantic_actions.put("sa-06", new SemanticAction("sa-06", "Id_s", "makeNode(Id)"));
        semantic_actions.put("sa-07", new SemanticAction("sa-07", "InherList_s", "makeFamily(InherList, Id_s, n)"));
        semantic_actions.put("sa-08", new SemanticAction("sa-08", "MembList_s", "makeFamily(MembList, MembDecl_s, n)"));
        semantic_actions.put("sa-09", new SemanticAction("sa-09", "Id_i", "makeNode(Id)"));
        semantic_actions.put("sa-10", new SemanticAction("sa-10", "FuncDecl_s", "makeFamily(FuncDecl, Id_s, FParamList_s, Type_s)"));
        semantic_actions.put("sa-11", new SemanticAction("sa-11", "VarDecl_s", "makeFamily(VarDecl, Type_s, Id_s, DimList_s)"));
        semantic_actions.put("sa-12", new SemanticAction("sa-12", "FParamList_s", "makeFamily(FParamList, FParam_s, n)"));
        semantic_actions.put("sa-13", new SemanticAction("sa-13", "Type_s", "makeNode(Type)"));
        semantic_actions.put("sa-14", new SemanticAction("sa-14", "DimList_s", "makeFamily(DimList, Dim_s, n)"));
        semantic_actions.put("sa-15", new SemanticAction("sa-15", "Dim_s", "makeNode(Dim)"));
        semantic_actions.put("sa-16", new SemanticAction("sa-16", "FParam_s", "makeFamily(FParam, Type_s, Id_s, DimList_s)"));
        semantic_actions.put("sa-17", new SemanticAction("sa-17", "FuncDef_s", "makeFamily(FuncDef, Scope_s, Id_s, FParamList_s, Type_s, FuncBody_s)"));
        semantic_actions.put("sa-18", new SemanticAction("sa-18", "FuncBody_s", "makeFamily(FuncBody, MethVar_s, StatBlock_s)"));
        semantic_actions.put("sa-19", new SemanticAction("sa-19", "Scope_s", "makeFamily(Scope_s, Scope_s, Id_s, reuse)"));
        semantic_actions.put("sa-20", new SemanticAction("sa-20", "MethVar_s", "makeFamily(MethVar, VarDecl_s, n)"));
        semantic_actions.put("sa-21", new SemanticAction("sa-21", "StatBlock_s", "makeFamily(StatBlock, VarDecl_s, IfStat_s, WhileStat_s, ReadStat_s, WriteStat_s, ReturnStat_s, BreakStat_s, ContiStat_s, FuncCallStat_s, AssignStat_s, n)"));
        semantic_actions.put("sa-22", new SemanticAction("sa-22", "IfStat_s", "makeFamily(IfStat, Expr_s, StatBlock_s, StatBlock_s)"));
        semantic_actions.put("sa-23", new SemanticAction("sa-23", "WhileStat_s", "makeFamily(WhileStat, Expr_s, StatBlock_s)"));
        semantic_actions.put("sa-24", new SemanticAction("sa-24", "ReadStat_s", "makeFamily(ReadStat, Variable_s)"));
        semantic_actions.put("sa-25", new SemanticAction("sa-25", "WriteStat_s", "makeFamily(WriteStat, Expr_s)"));
        semantic_actions.put("sa-26", new SemanticAction("sa-26", "ReturnStat_s", "makeFamily(ReturnStat, Expr_s)"));
        semantic_actions.put("sa-27", new SemanticAction("sa-27", "BreakStat_s", "makeNode(BreakStat)"));
        semantic_actions.put("sa-28", new SemanticAction("sa-28", "ContiStat_s", "makeNode(ContiStat)"));
//        semantic_actions.put("sa-29", new SemanticAction("sa-29", "FuncCallStat_s", "makeFamily(FuncCallStat, FuncCall_s)"));
        semantic_actions.put("sa-30", new SemanticAction("sa-30", "Expr_s", "makeFamily(Expr, ArithExpr_s, RelExpr_s, any)"));
        semantic_actions.put("sa-31", new SemanticAction("sa-31", "Variable_s", "makeFamily(Variable, DataMem_s, n)"));
        semantic_actions.put("sa-32", new SemanticAction("sa-32", "AssignStat_s", "makeFamily(AssignStat, Variable_s, Expr_s)"));
        semantic_actions.put("sa-33", new SemanticAction("sa-33", "ArithExpr_s", "makeFamily(ArithExpr, Term_s, AddOp_s, any)"));
        semantic_actions.put("sa-34", new SemanticAction("sa-34", "RelExpr_s", "makeFamily(RelExpr, Expr_s, RelOp_s, Expr_s)"));
        semantic_actions.put("sa-35", new SemanticAction("sa-35", "Term_s", "makeFamily(Term, Factor_s, MultOp_s, any)"));
        semantic_actions.put("sa-36", new SemanticAction("sa-36", "Factor_s", "makeFamily(Factor, Num_s, String_s, FuncOrVar_s, Expr_s, Not_s, Sign_s, InlineIf_s, any)"));
        semantic_actions.put("sa-37", new SemanticAction("sa-37", "AddOp_s", "makeFamily(AddOp_i, ArithExpr_s, AddOp_i, Term_s, reuse)"));
        semantic_actions.put("sa-38", new SemanticAction("sa-38", "MultOp_s", "makeFamily(MultOp_i, Term_s, MultOp_i, Factor_s, reuse)"));
        semantic_actions.put("sa-39", new SemanticAction("sa-39", "Num_s", "makeNode(Num)"));
        semantic_actions.put("sa-40", new SemanticAction("sa-40", "String_s", "makeNode(String)"));
        semantic_actions.put("sa-41", new SemanticAction("sa-41", "Not_s", "makeFamily(Not, Factor_s)"));
        semantic_actions.put("sa-42", new SemanticAction("sa-42", "Sign_s", "makeFamily(Sign_i, Factor_s, reuse)"));
        semantic_actions.put("sa-43", new SemanticAction("sa-43", "InlineIf_s", "makeFamily(InlineIf, Expr_s, Expr_s, Expr_s)"));
        semantic_actions.put("sa-44", new SemanticAction("sa-44", "FuncOrVar_s", "makeFamily(FuncOrVar, DataMem_s, FuncCall_s, n)"));
        semantic_actions.put("sa-45", new SemanticAction("sa-45", "RelOp_s", "makeNode(RelOp)"));
        semantic_actions.put("sa-46", new SemanticAction("sa-46", "Sign_i", "makeNode(Sign)"));
        semantic_actions.put("sa-47", new SemanticAction("sa-47", "AddOp_i", "makeNode(AddOp)"));
        semantic_actions.put("sa-48", new SemanticAction("sa-48", "MultOp_i", "makeNode(MultOp)"));
        semantic_actions.put("sa-49", new SemanticAction("sa-49", "FuncCall_s", "makeFamily(FuncCall, Id_s, AParams_s)"));
        semantic_actions.put("sa-50", new SemanticAction("sa-50", "AParams_s", "makeFamily(AParams, Expr_s, n)"));
        semantic_actions.put("sa-51", new SemanticAction("sa-51", "Indice_s", "makeFamily(Indice, Expr_s, n)"));
        semantic_actions.put("sa-52", new SemanticAction("sa-52", "DataMem_s", "makeFamily(DataMem, Id_s, Indice_s)"));
        semantic_actions.put("sa-53", new SemanticAction("sa-53", "Type_s", "makeFamily(Type, Id_s)"));
        semantic_actions.put("sa-54", new SemanticAction("sa-54", "MembDecl_s", "makeFamily(MembDecl, Visibility_s, VarDecl_s, FuncDecl_s, 2, any)"));
        semantic_actions.put("sa-55", new SemanticAction("sa-55", "Visibility_s", "makeNode(Visibility)"));
        semantic_actions.put("sa-56", new SemanticAction("sa-56", "Visibility_s", "makeNode(VisibilityDefault)"));
        semantic_actions.put("sa-57", new SemanticAction("sa-57", "InherList_s", "makeNode(InherList)"));
        semantic_actions.put("sa-58", new SemanticAction("sa-58", "Dim_s", "makeNode(DimNull)"));
        semantic_actions.put("sa-59", new SemanticAction("sa-59", "Scope_s", "makeNode(Scope)"));
//        semantic_actions.put("sa-61", new SemanticAction("sa-61", "Scope_s", "Scope_i"));

        semantic_actions.put("sa-60", new SemanticAction("sa-60", "FuncCallStat_s", "makeFamily(FuncCallStat, FuncCall_s, DataMem_s, n)"));


        // initialize semantic actions list
        for (Map.Entry<String, SemanticAction> pair : semantic_actions.entrySet()) {
            semantic_actions_list.add(pair.getKey());
        }

    }


    /**
     * Parsing HTML file to get first and follow sets using JSOUP
     */
    public void importFirstFollowSets() {

        String file_name = "/exclude/firstFollowSets.html";
        InputStream in = getClass().getResourceAsStream(file_name);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        Document html_doc = Jsoup.parse(reader.lines().collect(Collectors.joining()), "UTF-8");

        Element table = html_doc.select("table").get(2); //select table.
        Elements rows = table.select("tr");
        Elements first = rows.get(0).select("th,td");       //header of the table

//        List<String> headers = new ArrayList<String>();
//        for (Element header : first) {
//            headers.add(header.text());
//        }

        // parsing the table in HTML file
        for (int i = 1; i < rows.size(); i++) {
            Elements colVals = rows.get(i).select("th,td");
            ArrayList<String> first_set = new ArrayList<>(Arrays.asList(colVals.get(1).text().split(" ")));
            if (colVals.get(3).text().equals("yes")) {
                first_set.add("EPSILON");
            }
            ArrayList<String> follow_set = new ArrayList<>(Arrays.asList(colVals.get(2).text().split(" ")));
            String non_terminal = colVals.get(0).text();
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
        }

        // output follow sets
        for (Map.Entry<String, ArrayList<String>> entry : follow_sets.entrySet()) {
            StringBuilder follow_set_print = new StringBuilder();
            for (int i = 0; i < entry.getValue().size() - 1; i++) {
                String terminal = entry.getValue().get(i);
                follow_set_print.append("'").append(terminal).append("', ");
            }
            String terminalLast = entry.getValue().get(entry.getValue().size() - 1);
            follow_set_print.append("'").append(terminalLast).append("'");
//                System.out.println("FOLLOW(<"+ entry.getKey()+">)= [" + follow_set_print +"]");
        }
    }


    /**
     * write map object into JSON file
     */
    private void writeToJSONFiles() {

        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File("./src/main/resources/firstSets.json"), first_sets);
            mapper.writeValue(new File("./src/main/resources/followSets.json"), follow_sets);
            mapper.writeValue(new File("./src/main/resources/parsingTable.json"), parsing_table);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * import FIRST and FOLLOW sets, parsing table from JSON files
     */
    private void importFromJsonFiles() {

        try {
            ObjectMapper mapper = new ObjectMapper();
            String file_name = "/firstSets.json";
            System.out.println("[Grammar] Importing FIRST sets from file: " + file_name);
            InputStream in = getClass().getResourceAsStream(file_name);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            first_sets = mapper.readValue(reader, new TypeReference<Map<String, ArrayList<String>>>() {
            });

            file_name = "/followSets.json";
            System.out.println("[Grammar] Importing FOLLOW sets from file: " + file_name);
            in = getClass().getResourceAsStream(file_name);
            reader = new BufferedReader(new InputStreamReader(in));
            follow_sets = mapper.readValue(reader, new TypeReference<Map<String, ArrayList<String>>>() {
            });

            file_name = "/parsingTable.json";
            System.out.println("[Grammar] Importing parsing table from file: " + file_name);
            in = getClass().getResourceAsStream(file_name);
            reader = new BufferedReader(new InputStreamReader(in));
            parsing_table = mapper.readValue(reader, new TypeReference<Map<String, Map<String, String>>>() {
            });

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Parsing HTML file to get parsing table using JSOUP; also terminal list and nonTerminal list
     */
    public void importParsingTable() {

        String file_name = "/exclude/parsingTable.html";
        System.out.println("[Grammar] Importing parsing table from file: " + file_name);
        InputStream in = getClass().getResourceAsStream(file_name);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        Document html_doc = Jsoup.parse(reader.lines().collect(Collectors.joining()), "UTF-8");
        Element table = html_doc.select("table").get(1); //select the table.
        Elements rows = table.select("tr");
        Elements first = rows.get(0).select("th,td");       //header of the table

        List<String> headers = new ArrayList<String>();
        for (Element header : first) {
            headers.add(header.text());
            if (!header.text().isEmpty()) {
                terminal_list.add(header.text());
            }
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
                    table_vals.put(headers.get(col_count++), ruleID);
                } else {
                    table_vals.put(headers.get(col_count++), "error");
                }
            }
            String non_terminal = col_vals.get(0).text();
            parsing_table.put(non_terminal, table_vals);
            nonTerminal_list.add(non_terminal);
        }
    }


    /**
     * import rules from final LL1 grammar file
     */
    public void importRules() {

        String file_name = "/LL1Attribute.grm";
        System.out.println("[Grammar] Reading the grammar rules from file: " + file_name);

        try {
            InputStream in = getClass().getResourceAsStream(file_name);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String this_line;
            while ((this_line = reader.readLine()) != null) {
                if (!this_line.isEmpty()) {
                    String[] ruleString = this_line.split("->");
                    Rule rule = new Rule();
                    rule.setRule_LHS(ruleString[0].trim());
                    if (ruleString[1].startsWith("  . ")) {
                        ruleString[1] = ruleString[1].replace("  .", "EPSILON").trim();
                        rule.setRule_RHS(ruleString[1]);
                    } else {
                        rule.setRule_RHS(ruleString[1].substring(0, ruleString[1].lastIndexOf(".") - 1));
                    }

                    // match rule ID with the ones in parsing table
                    String ruleId = rule.getRule_LHS() + "_" + rule.getRule_RHS().trim().replaceAll(" ", "_");
                    ruleId = ruleId.replaceAll("_sa-\\d*", "");
                    rule.setRule_id(ruleId);
                    rules_attribute.put(rule.getRule_id(), rule);
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    public void generateGrammarEx() {
//        createSymbolsEx();
//        createRulesWithAttributeEx();
//        createParsingTableEx();
//        createSemanticActionsEx();
//    }
//
//
//    // example symbols
//    public void createSymbolsEx() {
//        terminal_list = new ArrayList<>(Arrays.asList("id", "(", ")", "plus", "mult", "$"));
//        nonTerminal_list = new ArrayList<>(Arrays.asList("E", "E'", "T", "T'", "F"));
//        semantic_actions_list = new ArrayList<>(Arrays.asList("sa_A", "sa_B", "sa_C", "sa_D", "sa_E", "sa_F", "sa_G", "sa_H", "sa_I", "sa_J", "sa_K", "sa_L"));
//    }
//
//    // example rules
//    public void createRulesEx() {
//        rules = new HashMap<>();
//        rules.put("r1", new Rule("r1", "E", " T E' "));
//        rules.put("r2", new Rule("r2", "E'", " plus T E' "));
//        rules.put("r3", new Rule("r3", "E'", "EPSILON"));
//        rules.put("r4", new Rule("r4", "T", " F T' "));
//        rules.put("r5", new Rule("r5", "T'", " mult F T' "));
//        rules.put("r6", new Rule("r6", "T'", "EPSILON"));
//        rules.put("r7", new Rule("r7", "F", " id "));
//        rules.put("r8", new Rule("r8", "F", " ( E ) "));
//    }
//
//    // example parsing table
//    public void createParsingTableEx() {
//        parsing_table = new HashMap<>();
//        addToParsingRow("E", new String[]{"r1", "r1", "error", "error", "error", "error"});
//        addToParsingRow("E'", new String[]{"error", "error", "r3", "r2", "error", "r3"});
//        addToParsingRow("T", new String[]{"r4", "r4", "error", "error", "error", "error"});
//        addToParsingRow("T'", new String[]{"error", "error", "r6", "r6", "r5", "r6"});
//        addToParsingRow("F", new String[]{"r7", "r8", "error", "error", "error", "error"});
//    }
//
//    public void createSemanticActionsEx() {
//        semantic_actions = new HashMap<>();
//        semantic_actions.put("sa_A", new SemanticAction("sa_A", "E'_i", "T_s"));
//        semantic_actions.put("sa_B", new SemanticAction("sa_B", "E_s", "E'_s"));
//        semantic_actions.put("sa_C", new SemanticAction("sa_C", "E'_i", "makeFamily(plus, E'_i, T_s)"));
//        semantic_actions.put("sa_D", new SemanticAction("sa_D", "E'_s", "E'_s"));
//        semantic_actions.put("sa_E", new SemanticAction("sa_E", "E'_s", "E'_i"));
//        semantic_actions.put("sa_F", new SemanticAction("sa_F", "T'_i", "F_s"));
//        semantic_actions.put("sa_G", new SemanticAction("sa_G", "T_s", "T'_s"));
//        semantic_actions.put("sa_H", new SemanticAction("sa_H", "T'_i", "makeFamily(mult,T'_i,F_s)"));
//        semantic_actions.put("sa_I", new SemanticAction("sa_I", "T'_s", "T'_s"));
//        semantic_actions.put("sa_J", new SemanticAction("sa_J", "T'_s", "T'_i"));
//        semantic_actions.put("sa_K", new SemanticAction("sa_K", "F_s", "makeNode(id)"));
//        semantic_actions.put("sa_L", new SemanticAction("sa_L", "F_s", "E_s"));
//    }
//
//    public void createRulesWithAttributeEx() {
//        rules_attribute = new HashMap<>();
//        rules_attribute.put("r1", new Rule("r1", "E", " T sa_A E' sa_B "));
//        rules_attribute.put("r2", new Rule("r2", "E'", " plus T sa_C E' sa_D "));
//        rules_attribute.put("r3", new Rule("r3", "E'", "EPSILON sa_E"));
//        rules_attribute.put("r4", new Rule("r4", "T", " F sa_F T' sa_G"));
//        rules_attribute.put("r5", new Rule("r5", "T'", " mult F sa_H T' sa_I "));
//        rules_attribute.put("r6", new Rule("r6", "T'", "EPSILON sa_J"));
//        rules_attribute.put("r7", new Rule("r7", "F", " id sa_K"));
//        rules_attribute.put("r8", new Rule("r8", "F", " ( E ) sa_L"));
//    }

//    private void addToParsingRow(String nonTerminal, String[] rulesInRow) {
//        Map<String, String> parsingTable_row = new HashMap<>(); // <terminal, rule>
//        String[] terminalArray = terminal_list.toArray(new String[0]);
//        for (int i = 0; i < terminalArray.length; i++) {
//            parsingTable_row.put(terminalArray[i], rulesInRow[i]);
//        }
//        parsing_table.put(nonTerminal, parsingTable_row);
//    }

    public ArrayList<String> getTerminal_list() {
        return terminal_list;
    }


    public ArrayList<String> getNonTerminal_list() {
        return nonTerminal_list;
    }


//    public Map<String, Rule> getRules() {
//        return rules;
//    }


    public Map<String, Map<String, String>> getParsing_table() {
        return parsing_table;
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


    public Map<String, SemanticAction> getSemantic_actions() {
        return semantic_actions;
    }


    public Map<String, Rule> getRules_attribute() {
        return rules_attribute;
    }


    public Map<String, String> getSymbol_map() {
        return symbol_map;
    }


}
