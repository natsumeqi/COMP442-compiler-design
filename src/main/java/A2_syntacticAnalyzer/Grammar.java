package A2_syntacticAnalyzer;

//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;


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
        createSemanticActions();
//        importFromJsonFiles();
//        importParsingTable();
//        importFirstFollowSets();
//        writeToFileTwoSets();
        createFirstSets();
        createFollowSets();
        createParsingTable();
//        printStatements();
    }

    private void createSemanticActions() {
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
        semantic_actions.put("sa-29", new SemanticAction("sa-29", "Null_s", "makeNode(Null)"));   // as a EPSILON
        semantic_actions.put("sa-30", new SemanticAction("sa-30", "Expr_s", "makeFamily(Expr, ArithExpr_s, RelExpr_s, any)"));
        semantic_actions.put("sa-31", new SemanticAction("sa-31", "Variable_s", "makeFamily(Variable, DataMem_s, Dot_s, any)"));
        semantic_actions.put("sa-32", new SemanticAction("sa-32", "AssignStat_s", "makeFamily(AssignStat, Variable_s, Expr_s)"));
        semantic_actions.put("sa-33", new SemanticAction("sa-33", "ArithExpr_s", "makeFamily(ArithExpr, Term_s, AddOp_s, any)"));
        semantic_actions.put("sa-34", new SemanticAction("sa-34", "RelExpr_s", "makeFamily(RelExpr, Expr_s, RelOp_s, Expr_s)"));
        semantic_actions.put("sa-35", new SemanticAction("sa-35", "Term_s", "makeFamily(Term, Factor_s, MultOp_s, any)"));
        semantic_actions.put("sa-36", new SemanticAction("sa-36", "Factor_s", "makeFamily(Factor, Num_s, Float_s, String_s, FuncOrVar_s, Expr_s, Not_s, Sign_s, InlineIf_s, any)"));
        semantic_actions.put("sa-37", new SemanticAction("sa-37", "AddOp_s", "makeFamily(AddOp_i, ArithExpr_s, AddOp_i, Term_s, reuse)"));
        semantic_actions.put("sa-38", new SemanticAction("sa-38", "MultOp_s", "makeFamily(MultOp_i, Term_s, MultOp_i, Factor_s, reuse)"));
        semantic_actions.put("sa-39", new SemanticAction("sa-39", "Num_s", "makeNode(Num)"));
        semantic_actions.put("sa-40", new SemanticAction("sa-40", "String_s", "makeNode(String)"));
        semantic_actions.put("sa-41", new SemanticAction("sa-41", "Not_s", "makeFamily(Not, Factor_s)"));
        semantic_actions.put("sa-42", new SemanticAction("sa-42", "Sign_s", "makeFamily(Sign_i, Factor_s, reuse)"));
        semantic_actions.put("sa-43", new SemanticAction("sa-43", "InlineIf_s", "makeFamily(InlineIf, Expr_s, Expr_s, Expr_s)"));
        semantic_actions.put("sa-44", new SemanticAction("sa-44", "FuncOrVar_s", "makeFamily(FuncOrVar, DataMem_s, Dot_s, FuncCall_s, any)"));
        semantic_actions.put("sa-45", new SemanticAction("sa-45", "RelOp_s", "makeNode(RelOp)"));
        semantic_actions.put("sa-46", new SemanticAction("sa-46", "Sign_i", "makeNode(Sign)"));
        semantic_actions.put("sa-47", new SemanticAction("sa-47", "AddOp_i", "makeNode(AddOp)"));
        semantic_actions.put("sa-48", new SemanticAction("sa-48", "MultOp_i", "makeNode(MultOp)"));
        semantic_actions.put("sa-49", new SemanticAction("sa-49", "FuncCall_s", "makeFamily(FuncCall, Id_s, Dot_s, AParams_s, first2, any)"));
        semantic_actions.put("sa-50", new SemanticAction("sa-50", "AParams_s", "makeFamily(AParams, Null_s, Expr_s, n)"));
        semantic_actions.put("sa-51", new SemanticAction("sa-51", "Indice_s", "makeFamily(Indice, Expr_s, n)"));
        semantic_actions.put("sa-52", new SemanticAction("sa-52", "DataMem_s", "makeFamily(DataMem, Id_s, Indice_s)"));
        semantic_actions.put("sa-53", new SemanticAction("sa-53", "Type_s", "makeFamily(Type, Id_s)"));
        semantic_actions.put("sa-54", new SemanticAction("sa-54", "MembDecl_s", "makeFamily(MembDecl, Visibility_s, VarDecl_s, FuncDecl_s, 2, any)"));
        semantic_actions.put("sa-55", new SemanticAction("sa-55", "Visibility_s", "makeNode(Visibility)"));
        semantic_actions.put("sa-56", new SemanticAction("sa-56", "Visibility_s", "makeNode(VisibilityDefault)"));
        semantic_actions.put("sa-57", new SemanticAction("sa-57", "InherList_s", "makeNode(InherList)"));
        semantic_actions.put("sa-58", new SemanticAction("sa-58", "Dim_s", "makeNode(DimNull)"));
        semantic_actions.put("sa-59", new SemanticAction("sa-59", "Scope_s", "makeNode(Scope)"));
        semantic_actions.put("sa-60", new SemanticAction("sa-60", "FuncCallStat_s", "makeFamily(FuncCallStat, FuncCall_s, n)"));
        semantic_actions.put("sa-61", new SemanticAction("sa-61", "Dot_s", "makeFamily(Dot, DataMem_s, Dot_s, DataMem_s, 2, any)"));
        semantic_actions.put("sa-62", new SemanticAction("sa-62", "Dot_s", "makeFamily(Dot, DataMem_s, Id_s, keepOrSkip)"));    // if not DataMem, skip; if DataMem, combine
        semantic_actions.put("sa-63", new SemanticAction("sa-63", "Dot_s", "makeFamily(Dot, DataMem_s, Dot_s, DataMem_s, keepOrSkip, first2, any)"));   // maybe a dot or just a dataMem special case
        semantic_actions.put("sa-64", new SemanticAction("sa-64", "FuncCall_s", "makeFamily(FuncCall, Dot_s, DataMem_s, FuncCall_s, keepOrSkip, first2, any)"));    // if not DataMem, just skip; if DataMem, combine
        semantic_actions.put("sa-65", new SemanticAction("sa-65", "Dot_s", "makeFamily(Dot, FuncCall_s, DataMem_s, keepOrSkip)"));
        semantic_actions.put("sa-66", new SemanticAction("sa-66", "Float_s", "makeNode(Float)"));


        // initialize semantic actions list
        for (Map.Entry<String, SemanticAction> pair : semantic_actions.entrySet()) {
            semantic_actions_list.add(pair.getKey());
        }

    }


    /**
     * Parsing HTML file to get first and follow sets using JSOUP
     */
    public void importFirstFollowSets() {

//        String file_name = "/exclude/firstFollowSets.html";
//        InputStream in = getClass().getResourceAsStream(file_name);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//        Document html_doc = Jsoup.parse(reader.lines().collect(Collectors.joining()), "UTF-8");
//
//        Element table = html_doc.select("table").get(2); //select table.
//        Elements rows = table.select("tr");
//        Elements first = rows.get(0).select("th,td");       //header of the table
//
////        List<String> headers = new ArrayList<String>();
////        for (Element header : first) {
////            headers.add(header.text());
////        }
//
//        // parsing the table in HTML file
//        for (int i = 1; i < rows.size(); i++) {
//            Elements colVals = rows.get(i).select("th,td");
//            ArrayList<String> first_set = new ArrayList<>(Arrays.asList(colVals.get(1).text().split(" ")));
//            if (colVals.get(3).text().equals("yes")) {
//                first_set.add("EPSILON");
//            }
//            ArrayList<String> follow_set = new ArrayList<>(Arrays.asList(colVals.get(2).text().split(" ")));
//            String non_terminal = colVals.get(0).text();
//            first_sets.put(non_terminal, first_set);
//            follow_sets.put(non_terminal, follow_set);
//        }
//
//        // output first sets
//        for (Map.Entry<String, ArrayList<String>> entry : first_sets.entrySet()) {
//            StringBuilder first_set_print = new StringBuilder();
//            for (int i = 0; i < entry.getValue().size() - 1; i++) {
//                String terminal = entry.getValue().get(i);
//                if (terminal.equals("EPSILON")) {
//                    first_set_print.append(terminal);
//                } else {
//                    first_set_print.append("'").append(terminal).append("', ");
//                }
//            }
//            String terminal_last = entry.getValue().get(entry.getValue().size() - 1);
//            if (terminal_last.equals("EPSILON")) {
//                first_set_print.append(terminal_last);
//            } else {
//                first_set_print.append("'").append(terminal_last).append("'");
//            }
//        }
//
//        // output follow sets
//        for (Map.Entry<String, ArrayList<String>> entry : follow_sets.entrySet()) {
//            StringBuilder follow_set_print = new StringBuilder();
//            for (int i = 0; i < entry.getValue().size() - 1; i++) {
//                String terminal = entry.getValue().get(i);
//                follow_set_print.append("'").append(terminal).append("', ");
//            }
//            String terminalLast = entry.getValue().get(entry.getValue().size() - 1);
//            follow_set_print.append("'").append(terminalLast).append("'");
////                System.out.println("FOLLOW(<"+ entry.getKey()+">)= [" + follow_set_print +"]");
//        }
    }


    /**
     * write map object into JSON file
     */
    private void writeToJSONFiles() {

//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            mapper.writeValue(new File("./src/main/resources/firstSets.json"), first_sets);
//            mapper.writeValue(new File("./src/main/resources/followSets.json"), follow_sets);
//            mapper.writeValue(new File("./src/main/resources/parsingTable.json"), parsing_table);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    /**
     * import FIRST and FOLLOW sets, parsing table from JSON files
     */
    private void importFromJsonFiles() {

//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            String file_name = "/exclude/firstSets.json";
//            System.out.println("[Grammar] Importing FIRST sets from file: " + file_name);
//            InputStream in = getClass().getResourceAsStream(file_name);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//            first_sets = mapper.readValue(reader, new TypeReference<Map<String, ArrayList<String>>>() {
//            });
//
//            file_name = "/exclude/followSets.json";
//            System.out.println("[Grammar] Importing FOLLOW sets from file: " + file_name);
//            in = getClass().getResourceAsStream(file_name);
//            reader = new BufferedReader(new InputStreamReader(in));
//            follow_sets = mapper.readValue(reader, new TypeReference<Map<String, ArrayList<String>>>() {
//            });
//
//            file_name = "/exclude/parsingTable.json";
//            System.out.println("[Grammar] Importing parsing table from file: " + file_name);
//            in = getClass().getResourceAsStream(file_name);
//            reader = new BufferedReader(new InputStreamReader(in));
//            parsing_table = mapper.readValue(reader, new TypeReference<Map<String, Map<String, String>>>() {
//            });
//
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void printStatements() {
        for (Map.Entry<String, ArrayList<String>> entry : follow_sets.entrySet()) {
            StringBuilder first_set = new StringBuilder();
            for (String first : entry.getValue()) {
                first_set.append("\"").append(first).append("\", ");
            }
            String first_set_string = first_set.substring(0, first_set.length() - 2);
            System.out.println("follow_sets.put(\"" + entry.getKey() + "\", new ArrayList<>(Arrays.asList(" + first_set_string + ")));");
        }
        for (Map.Entry<String, Map<String, String>> entry : parsing_table.entrySet()) {
            StringBuilder table_entry_map_builder = new StringBuilder();
            for (Map.Entry<String, String> pair : entry.getValue().entrySet()) {
                table_entry_map_builder.append("{\"").append(pair.getKey()).append("\", \"").append(pair.getValue()).append("\"}, ");
            }
            String table_entry_map = table_entry_map_builder.substring(0, table_entry_map_builder.length() - 2);
            System.out.println("table_entry_table = Stream.of(new String[][]{" + table_entry_map + "}).collect(Collectors.toMap(data->data[0], data->data[1]));");
            System.out.println("parsing_table.put(\"" + entry.getKey() + "\", table_entry_table);");
        }
    }


    private void createFirstSets() {
        first_sets.put("FUNCDECL", new ArrayList<>(Collections.singletonList("func")));
        first_sets.put("STATEMENT", new ArrayList<>(Arrays.asList("if", "while", "read", "write", "return", "break", "continue", "id")));
        first_sets.put("ASSIGNSTATTAIL", new ArrayList<>(Collections.singletonList("assign")));
        first_sets.put("INHERIT", new ArrayList<>(Arrays.asList("inherits", "EPSILON")));
        first_sets.put("FUNCORVARIDNESTTAIL", new ArrayList<>(Arrays.asList("dot", "EPSILON")));
        first_sets.put("ASSIGNOP", new ArrayList<>(Collections.singletonList("assign")));
        first_sets.put("FUNCORASSIGNSTATIDNESTVARTAIL", new ArrayList<>(Arrays.asList("dot", "assign")));
        first_sets.put("CLASSDECL", new ArrayList<>(Arrays.asList("class", "EPSILON")));
        first_sets.put("FUNCBODY", new ArrayList<>(Collections.singletonList("lcurbr")));
        first_sets.put("ADDOP", new ArrayList<>(Arrays.asList("plus", "minus", "or")));
        first_sets.put("FPARAMSTAIL", new ArrayList<>(Arrays.asList("comma", "EPSILON")));
        first_sets.put("FUNCORASSIGNSTATIDNEST", new ArrayList<>(Arrays.asList("lpar", "lsqbr", "dot", "assign")));
        first_sets.put("INDICEREP", new ArrayList<>(Arrays.asList("lsqbr", "EPSILON")));
        first_sets.put("SIGN", new ArrayList<>(Arrays.asList("plus", "minus")));
        first_sets.put("TYPE", new ArrayList<>(Arrays.asList("integer", "float", "string", "id")));
        first_sets.put("MULTOP", new ArrayList<>(Arrays.asList("mult", "div", "and")));
        first_sets.put("VARIABLEIDNESTTAIL", new ArrayList<>(Arrays.asList("dot", "EPSILON")));
        first_sets.put("FUNCTION", new ArrayList<>(Collections.singletonList("func")));
        first_sets.put("CLASSMETHOD", new ArrayList<>(Arrays.asList("sr", "EPSILON")));
        first_sets.put("VISIBILITY", new ArrayList<>(Arrays.asList("public", "private", "EPSILON")));
        first_sets.put("INTNUM", new ArrayList<>(Arrays.asList("intnum", "EPSILON")));
        first_sets.put("ARITHEXPR", new ArrayList<>(Arrays.asList("intnum", "floatnum", "stringlit", "lpar", "not", "qm", "id", "plus", "minus")));
        first_sets.put("APARAMS", new ArrayList<>(Arrays.asList("intnum", "floatnum", "stringlit", "lpar", "not", "qm", "id", "plus", "minus", "EPSILON")));
        first_sets.put("FUNCORASSIGNSTATIDNESTFUNCTAIL", new ArrayList<>(Arrays.asList("dot", "EPSILON")));
        first_sets.put("CLASSDECLBODY", new ArrayList<>(Arrays.asList("public", "private", "func", "integer", "float", "string", "id", "EPSILON")));
        first_sets.put("STATBLOCK", new ArrayList<>(Arrays.asList("lcurbr", "if", "while", "read", "write", "return", "break", "continue", "id", "EPSILON")));
        first_sets.put("ARRAYSIZEREPT", new ArrayList<>(Arrays.asList("lsqbr", "EPSILON")));
        first_sets.put("RELOP", new ArrayList<>(Arrays.asList("eq", "neq", "lt", "gt", "leq", "geq")));
        first_sets.put("MEMBERDECL", new ArrayList<>(Arrays.asList("func", "integer", "float", "string", "id")));
        first_sets.put("EXPRTAIL", new ArrayList<>(Arrays.asList("eq", "neq", "lt", "gt", "leq", "geq", "EPSILON")));
        first_sets.put("VARDECLREP", new ArrayList<>(Arrays.asList("integer", "float", "string", "id", "EPSILON")));
        first_sets.put("FUNCHEAD", new ArrayList<>(Collections.singletonList("func")));
        first_sets.put("FUNCDEF", new ArrayList<>(Arrays.asList("func", "EPSILON")));
        first_sets.put("TERM", new ArrayList<>(Arrays.asList("intnum", "floatnum", "stringlit", "lpar", "not", "qm", "id", "plus", "minus")));
        first_sets.put("APARAMSTAIL", new ArrayList<>(Arrays.asList("comma", "EPSILON")));
        first_sets.put("ARITHEXPRTAIL", new ArrayList<>(Arrays.asList("plus", "minus", "or", "EPSILON")));
        first_sets.put("EXPR", new ArrayList<>(Arrays.asList("intnum", "floatnum", "stringlit", "lpar", "not", "qm", "id", "plus", "minus")));
        first_sets.put("VARIABLE", new ArrayList<>(Collections.singletonList("id")));
        first_sets.put("VARDECL", new ArrayList<>(Arrays.asList("integer", "float", "string", "id")));
        first_sets.put("METHODBODYVAR", new ArrayList<>(Arrays.asList("var", "EPSILON")));
        first_sets.put("NESTEDID", new ArrayList<>(Arrays.asList("comma", "EPSILON")));
        first_sets.put("FUNCSTATTAIL", new ArrayList<>(Arrays.asList("dot", "lpar", "lsqbr")));
        first_sets.put("VARIABLEIDNEST", new ArrayList<>(Arrays.asList("lsqbr", "dot", "EPSILON")));
        first_sets.put("FUNCSTATTAILIDNEST", new ArrayList<>(Arrays.asList("dot", "EPSILON")));
        first_sets.put("FACTOR", new ArrayList<>(Arrays.asList("intnum", "floatnum", "stringlit", "lpar", "not", "qm", "id", "plus", "minus")));
        first_sets.put("FUNCDECLTAIL", new ArrayList<>(Arrays.asList("void", "integer", "float", "string", "id")));
        first_sets.put("STATEMENTLIST", new ArrayList<>(Arrays.asList("if", "while", "read", "write", "return", "break", "continue", "id", "EPSILON")));
        first_sets.put("FUNCORASSIGNSTAT", new ArrayList<>(Collections.singletonList("id")));
        first_sets.put("FUNCORVARIDNEST", new ArrayList<>(Arrays.asList("lpar", "lsqbr", "dot", "EPSILON")));
        first_sets.put("FUNCORVAR", new ArrayList<>(Collections.singletonList("id")));
        first_sets.put("START", new ArrayList<>(Arrays.asList("main", "class", "func")));
        first_sets.put("FPARAMS", new ArrayList<>(Arrays.asList("integer", "float", "string", "id", "EPSILON")));
        first_sets.put("TERMTAIL", new ArrayList<>(Arrays.asList("mult", "div", "and", "EPSILON")));
        first_sets.put("PROG", new ArrayList<>(Arrays.asList("main", "class", "func")));
    }

    private void createFollowSets() {
        follow_sets.put("FUNCDECL", new ArrayList<>(Arrays.asList("public", "private", "func", "integer", "float", "string", "id", "rcurbr")));
        follow_sets.put("STATEMENT", new ArrayList<>(Arrays.asList("if", "while", "read", "write", "return", "break", "continue", "id", "else", "semi", "rcurbr")));
        follow_sets.put("ASSIGNSTATTAIL", new ArrayList<>(Collections.singletonList("semi")));
        follow_sets.put("INHERIT", new ArrayList<>(Collections.singletonList("lcurbr")));
        follow_sets.put("FUNCORVARIDNESTTAIL", new ArrayList<>(Arrays.asList("mult", "div", "and", "semi", "eq", "neq", "lt", "gt", "leq", "geq", "plus", "minus", "or", "comma", "colon", "rsqbr", "rpar")));
        follow_sets.put("ASSIGNOP", new ArrayList<>(Arrays.asList("intnum", "floatnum", "stringlit", "lpar", "not", "qm", "id", "plus", "minus")));
        follow_sets.put("FUNCORASSIGNSTATIDNESTVARTAIL", new ArrayList<>(Collections.singletonList("semi")));
        follow_sets.put("CLASSDECL", new ArrayList<>(Arrays.asList("func", "main")));
        follow_sets.put("FUNCBODY", new ArrayList<>(Arrays.asList("main", "func")));
        follow_sets.put("ADDOP", new ArrayList<>(Arrays.asList("intnum", "floatnum", "stringlit", "lpar", "not", "qm", "id", "plus", "minus")));
        follow_sets.put("FPARAMSTAIL", new ArrayList<>(Collections.singletonList("rpar")));
        follow_sets.put("FUNCORASSIGNSTATIDNEST", new ArrayList<>(Collections.singletonList("semi")));
        follow_sets.put("INDICEREP", new ArrayList<>(Arrays.asList("mult", "div", "and", "semi", "assign", "dot", "eq", "neq", "lt", "gt", "leq", "geq", "plus", "minus", "or", "comma", "colon", "rsqbr", "rpar")));
        follow_sets.put("SIGN", new ArrayList<>(Arrays.asList("intnum", "floatnum", "stringlit", "lpar", "not", "qm", "id", "plus", "minus")));
        follow_sets.put("TYPE", new ArrayList<>(Arrays.asList("lcurbr", "semi", "id")));
        follow_sets.put("MULTOP", new ArrayList<>(Arrays.asList("intnum", "floatnum", "stringlit", "lpar", "not", "qm", "id", "plus", "minus")));
        follow_sets.put("VARIABLEIDNESTTAIL", new ArrayList<>(Collections.singletonList("rpar")));
        follow_sets.put("FUNCTION", new ArrayList<>(Arrays.asList("main", "func")));
        follow_sets.put("CLASSMETHOD", new ArrayList<>(Collections.singletonList("lpar")));
        follow_sets.put("VISIBILITY", new ArrayList<>(Arrays.asList("func", "integer", "float", "string", "id")));
        follow_sets.put("INTNUM", new ArrayList<>(Collections.singletonList("rsqbr")));
        follow_sets.put("ARITHEXPR", new ArrayList<>(Arrays.asList("semi", "eq", "neq", "lt", "gt", "leq", "geq", "comma", "colon", "rsqbr", "rpar")));
        follow_sets.put("APARAMS", new ArrayList<>(Collections.singletonList("rpar")));
        follow_sets.put("FUNCORASSIGNSTATIDNESTFUNCTAIL", new ArrayList<>(Collections.singletonList("semi")));
        follow_sets.put("CLASSDECLBODY", new ArrayList<>(Collections.singletonList("rcurbr")));
        follow_sets.put("STATBLOCK", new ArrayList<>(Arrays.asList("else", "semi")));
        follow_sets.put("ARRAYSIZEREPT", new ArrayList<>(Arrays.asList("rpar", "comma", "semi")));
        follow_sets.put("RELOP", new ArrayList<>(Arrays.asList("intnum", "floatnum", "stringlit", "lpar", "not", "qm", "id", "plus", "minus")));
        follow_sets.put("MEMBERDECL", new ArrayList<>(Arrays.asList("public", "private", "func", "integer", "float", "string", "id", "rcurbr")));
        follow_sets.put("EXPRTAIL", new ArrayList<>(Arrays.asList("semi", "comma", "colon", "rsqbr", "rpar")));
        follow_sets.put("VARDECLREP", new ArrayList<>(Collections.singletonList("rcurbr")));
        follow_sets.put("FUNCHEAD", new ArrayList<>(Collections.singletonList("lcurbr")));
        follow_sets.put("FUNCDEF", new ArrayList<>(Collections.singletonList("main")));
        follow_sets.put("TERM", new ArrayList<>(Arrays.asList("semi", "eq", "neq", "lt", "gt", "leq", "geq", "plus", "minus", "or", "comma", "colon", "rsqbr", "rpar")));
        follow_sets.put("APARAMSTAIL", new ArrayList<>(Collections.singletonList("rpar")));
        follow_sets.put("ARITHEXPRTAIL", new ArrayList<>(Arrays.asList("semi", "eq", "neq", "lt", "gt", "leq", "geq", "comma", "colon", "rsqbr", "rpar")));
        follow_sets.put("EXPR", new ArrayList<>(Arrays.asList("semi", "comma", "colon", "rsqbr", "rpar")));
        follow_sets.put("VARIABLE", new ArrayList<>(Collections.singletonList("rpar")));
        follow_sets.put("VARDECL", new ArrayList<>(Arrays.asList("public", "private", "func", "integer", "float", "string", "id", "rcurbr")));
        follow_sets.put("METHODBODYVAR", new ArrayList<>(Arrays.asList("if", "while", "read", "write", "return", "break", "continue", "id", "rcurbr")));
        follow_sets.put("NESTEDID", new ArrayList<>(Collections.singletonList("lcurbr")));
        follow_sets.put("FUNCSTATTAIL", new ArrayList<>(Collections.singletonList("semi")));
        follow_sets.put("VARIABLEIDNEST", new ArrayList<>(Collections.singletonList("rpar")));
        follow_sets.put("FUNCSTATTAILIDNEST", new ArrayList<>(Collections.singletonList("semi")));
        follow_sets.put("FACTOR", new ArrayList<>(Arrays.asList("mult", "div", "and", "semi", "eq", "neq", "lt", "gt", "leq", "geq", "plus", "minus", "or", "comma", "colon", "rsqbr", "rpar")));
        follow_sets.put("FUNCDECLTAIL", new ArrayList<>(Arrays.asList("lcurbr", "semi")));
        follow_sets.put("STATEMENTLIST", new ArrayList<>(Collections.singletonList("rcurbr")));
        follow_sets.put("FUNCORASSIGNSTAT", new ArrayList<>(Collections.singletonList("semi")));
        follow_sets.put("FUNCORVARIDNEST", new ArrayList<>(Arrays.asList("mult", "div", "and", "semi", "eq", "neq", "lt", "gt", "leq", "geq", "plus", "minus", "or", "comma", "colon", "rsqbr", "rpar")));
        follow_sets.put("FUNCORVAR", new ArrayList<>(Arrays.asList("mult", "div", "and", "semi", "eq", "neq", "lt", "gt", "leq", "geq", "plus", "minus", "or", "comma", "colon", "rsqbr", "rpar")));
        follow_sets.put("START", new ArrayList<>(Collections.singletonList("∅")));
        follow_sets.put("FPARAMS", new ArrayList<>(Collections.singletonList("rpar")));
        follow_sets.put("TERMTAIL", new ArrayList<>(Arrays.asList("semi", "eq", "neq", "lt", "gt", "leq", "geq", "plus", "minus", "or", "comma", "colon", "rsqbr", "rpar")));
        follow_sets.put("PROG", new ArrayList<>(Collections.singletonList("∅")));
    }

    private void createParsingTable() {

        Map<String, String> table_entry_table = new HashMap<>();
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "FUNCDECL_func_id_lpar_FPARAMS_rpar_colon_FUNCDECLTAIL_semi"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("FUNCDECL", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "STATEMENT_while_lpar_EXPR_rpar_STATBLOCK_semi"}, {"div", "error"}, {"continue", "STATEMENT_continue_semi"}, {"else", "error"}, {"leq", "error"}, {"id", "STATEMENT_FUNCORASSIGNSTAT_semi"}, {"neq", "error"}, {"qm", "error"}, {"write", "STATEMENT_write_lpar_EXPR_rpar_semi"}, {"if", "STATEMENT_if_lpar_EXPR_rpar_then_STATBLOCK_else_STATBLOCK_semi"}, {"read", "STATEMENT_read_lpar_VARIABLE_rpar_semi"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "STATEMENT_break_semi"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "STATEMENT_return_lpar_EXPR_rpar_semi"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("STATEMENT", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "ASSIGNSTATTAIL_ASSIGNOP_EXPR"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("ASSIGNSTATTAIL", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "INHERIT_EPSILON"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "INHERIT_inherits_id_NESTEDID"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("INHERIT", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "FUNCORVARIDNESTTAIL_EPSILON"}, {"lpar", "error"}, {"lt", "FUNCORVARIDNESTTAIL_EPSILON"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "FUNCORVARIDNESTTAIL_EPSILON"}, {"continue", "error"}, {"else", "error"}, {"leq", "FUNCORVARIDNESTTAIL_EPSILON"}, {"id", "error"}, {"neq", "FUNCORVARIDNESTTAIL_EPSILON"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "FUNCORVARIDNESTTAIL_EPSILON"}, {"plus", "FUNCORVARIDNESTTAIL_EPSILON"}, {"lsqbr", "error"}, {"minus", "FUNCORVARIDNESTTAIL_EPSILON"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "FUNCORVARIDNESTTAIL_dot_id_FUNCORVARIDNEST"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "FUNCORVARIDNESTTAIL_EPSILON"}, {"not", "error"}, {"public", "error"}, {"and", "FUNCORVARIDNESTTAIL_EPSILON"}, {"rpar", "FUNCORVARIDNESTTAIL_EPSILON"}, {"semi", "FUNCORVARIDNESTTAIL_EPSILON"}, {"rsqbr", "FUNCORVARIDNESTTAIL_EPSILON"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "FUNCORVARIDNESTTAIL_EPSILON"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "FUNCORVARIDNESTTAIL_EPSILON"}, {"comma", "FUNCORVARIDNESTTAIL_EPSILON"}, {"func", "error"}, {"colon", "FUNCORVARIDNESTTAIL_EPSILON"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("FUNCORVARIDNESTTAIL", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "ASSIGNOP_assign"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("ASSIGNOP", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "CLASSDECL_EPSILON"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "CLASSDECL_class_id_INHERIT_lcurbr_CLASSDECLBODY_rcurbr_semi_CLASSDECL"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "CLASSDECL_EPSILON"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("CLASSDECL", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "FUNCORASSIGNSTATIDNESTVARTAIL_dot_id_FUNCORASSIGNSTATIDNEST"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "FUNCORASSIGNSTATIDNESTVARTAIL_ASSIGNSTATTAIL"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("FUNCORASSIGNSTATIDNESTVARTAIL", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "FUNCBODY_lcurbr_METHODBODYVAR_STATEMENTLIST_rcurbr"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("FUNCBODY", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "ADDOP_plus"}, {"lsqbr", "error"}, {"minus", "ADDOP_minus"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "ADDOP_or"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("ADDOP", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "FPARAMSTAIL_EPSILON"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "FPARAMSTAIL_comma_TYPE_id_ARRAYSIZEREPT_FPARAMSTAIL"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("FPARAMSTAIL", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "FUNCORASSIGNSTATIDNEST_lpar_APARAMS_rpar_FUNCORASSIGNSTATIDNESTFUNCTAIL"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "FUNCORASSIGNSTATIDNEST_INDICEREP_FUNCORASSIGNSTATIDNESTVARTAIL"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "FUNCORASSIGNSTATIDNEST_INDICEREP_FUNCORASSIGNSTATIDNESTVARTAIL"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "FUNCORASSIGNSTATIDNEST_INDICEREP_FUNCORASSIGNSTATIDNESTVARTAIL"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("FUNCORASSIGNSTATIDNEST", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "INDICEREP_EPSILON"}, {"lpar", "error"}, {"lt", "INDICEREP_EPSILON"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "INDICEREP_EPSILON"}, {"continue", "error"}, {"else", "error"}, {"leq", "INDICEREP_EPSILON"}, {"id", "error"}, {"neq", "INDICEREP_EPSILON"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "INDICEREP_EPSILON"}, {"plus", "INDICEREP_EPSILON"}, {"lsqbr", "INDICEREP_lsqbr_EXPR_rsqbr_INDICEREP"}, {"minus", "INDICEREP_EPSILON"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "INDICEREP_EPSILON"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "INDICEREP_EPSILON"}, {"not", "error"}, {"public", "error"}, {"and", "INDICEREP_EPSILON"}, {"rpar", "INDICEREP_EPSILON"}, {"semi", "INDICEREP_EPSILON"}, {"rsqbr", "INDICEREP_EPSILON"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "INDICEREP_EPSILON"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "INDICEREP_EPSILON"}, {"comma", "INDICEREP_EPSILON"}, {"func", "error"}, {"colon", "INDICEREP_EPSILON"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "INDICEREP_EPSILON"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("INDICEREP", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "SIGN_plus"}, {"lsqbr", "error"}, {"minus", "SIGN_minus"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("SIGN", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "TYPE_integer"}, {"float", "TYPE_float"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "TYPE_id"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "TYPE_string"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("TYPE", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "MULTOP_mult"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "MULTOP_div"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "MULTOP_and"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("MULTOP", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "VARIABLEIDNESTTAIL_dot_id_VARIABLEIDNEST"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "VARIABLEIDNESTTAIL_EPSILON"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("VARIABLEIDNESTTAIL", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "FUNCTION_FUNCHEAD_FUNCBODY"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("FUNCTION", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "CLASSMETHOD_EPSILON"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "CLASSMETHOD_sr_id"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("CLASSMETHOD", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "VISIBILITY_EPSILON"}, {"float", "VISIBILITY_EPSILON"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "VISIBILITY_EPSILON"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "VISIBILITY_private"}, {"string", "VISIBILITY_EPSILON"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "VISIBILITY_public"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "VISIBILITY_EPSILON"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("VISIBILITY", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "INTNUM_intnum"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "INTNUM_EPSILON"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("INTNUM", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "ARITHEXPR_TERM_ARITHEXPRTAIL"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "ARITHEXPR_TERM_ARITHEXPRTAIL"}, {"neq", "error"}, {"qm", "ARITHEXPR_TERM_ARITHEXPRTAIL"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "ARITHEXPR_TERM_ARITHEXPRTAIL"}, {"lsqbr", "error"}, {"minus", "ARITHEXPR_TERM_ARITHEXPRTAIL"}, {"private", "error"}, {"string", "error"}, {"intnum", "ARITHEXPR_TERM_ARITHEXPRTAIL"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "ARITHEXPR_TERM_ARITHEXPRTAIL"}, {"geq", "error"}, {"not", "ARITHEXPR_TERM_ARITHEXPRTAIL"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "ARITHEXPR_TERM_ARITHEXPRTAIL"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("ARITHEXPR", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "APARAMS_EXPR_APARAMSTAIL"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "APARAMS_EXPR_APARAMSTAIL"}, {"neq", "error"}, {"qm", "APARAMS_EXPR_APARAMSTAIL"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "APARAMS_EXPR_APARAMSTAIL"}, {"lsqbr", "error"}, {"minus", "APARAMS_EXPR_APARAMSTAIL"}, {"private", "error"}, {"string", "error"}, {"intnum", "APARAMS_EXPR_APARAMSTAIL"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "APARAMS_EXPR_APARAMSTAIL"}, {"geq", "error"}, {"not", "APARAMS_EXPR_APARAMSTAIL"}, {"public", "error"}, {"and", "error"}, {"rpar", "APARAMS_EPSILON"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "APARAMS_EXPR_APARAMSTAIL"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("APARAMS", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "FUNCORASSIGNSTATIDNESTFUNCTAIL_dot_id_FUNCSTATTAIL"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "FUNCORASSIGNSTATIDNESTFUNCTAIL_EPSILON"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("FUNCORASSIGNSTATIDNESTFUNCTAIL", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "ARRAYSIZEREPT_lsqbr_INTNUM_rsqbr_ARRAYSIZEREPT"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "ARRAYSIZEREPT_EPSILON"}, {"semi", "ARRAYSIZEREPT_EPSILON"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "ARRAYSIZEREPT_EPSILON"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("ARRAYSIZEREPT", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "CLASSDECLBODY_VISIBILITY_MEMBERDECL_CLASSDECLBODY"}, {"float", "CLASSDECLBODY_VISIBILITY_MEMBERDECL_CLASSDECLBODY"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "CLASSDECLBODY_VISIBILITY_MEMBERDECL_CLASSDECLBODY"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "CLASSDECLBODY_VISIBILITY_MEMBERDECL_CLASSDECLBODY"}, {"string", "CLASSDECLBODY_VISIBILITY_MEMBERDECL_CLASSDECLBODY"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "CLASSDECLBODY_VISIBILITY_MEMBERDECL_CLASSDECLBODY"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "CLASSDECLBODY_EPSILON"}, {"gt", "error"}, {"comma", "error"}, {"func", "CLASSDECLBODY_VISIBILITY_MEMBERDECL_CLASSDECLBODY"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("CLASSDECLBODY", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "STATBLOCK_STATEMENT"}, {"div", "error"}, {"continue", "STATBLOCK_STATEMENT"}, {"else", "STATBLOCK_EPSILON"}, {"leq", "error"}, {"id", "STATBLOCK_STATEMENT"}, {"neq", "error"}, {"qm", "error"}, {"write", "STATBLOCK_STATEMENT"}, {"if", "STATBLOCK_STATEMENT"}, {"read", "STATBLOCK_STATEMENT"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "STATBLOCK_lcurbr_STATEMENTLIST_rcurbr"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "STATBLOCK_EPSILON"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "STATBLOCK_STATEMENT"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "STATBLOCK_STATEMENT"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("STATBLOCK", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "RELOP_lt"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "RELOP_leq"}, {"id", "error"}, {"neq", "RELOP_neq"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "RELOP_eq"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "RELOP_geq"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "RELOP_gt"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("RELOP", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "MEMBERDECL_VARDECL"}, {"float", "MEMBERDECL_VARDECL"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "MEMBERDECL_VARDECL"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "MEMBERDECL_VARDECL"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "MEMBERDECL_FUNCDECL"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("MEMBERDECL", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "EXPRTAIL_RELOP_ARITHEXPR"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "EXPRTAIL_RELOP_ARITHEXPR"}, {"id", "error"}, {"neq", "EXPRTAIL_RELOP_ARITHEXPR"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "EXPRTAIL_RELOP_ARITHEXPR"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "EXPRTAIL_RELOP_ARITHEXPR"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "EXPRTAIL_EPSILON"}, {"semi", "EXPRTAIL_EPSILON"}, {"rsqbr", "EXPRTAIL_EPSILON"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "EXPRTAIL_RELOP_ARITHEXPR"}, {"comma", "EXPRTAIL_EPSILON"}, {"func", "error"}, {"colon", "EXPRTAIL_EPSILON"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("EXPRTAIL", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "VARDECLREP_VARDECL_VARDECLREP"}, {"float", "VARDECLREP_VARDECL_VARDECLREP"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "VARDECLREP_VARDECL_VARDECLREP"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "VARDECLREP_VARDECL_VARDECLREP"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "VARDECLREP_EPSILON"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("VARDECLREP", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "FUNCDEF_EPSILON"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "FUNCDEF_FUNCTION_FUNCDEF"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("FUNCDEF", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "FUNCHEAD_func_id_CLASSMETHOD_lpar_FPARAMS_rpar_colon_FUNCDECLTAIL"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("FUNCHEAD", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "TERM_FACTOR_TERMTAIL"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "TERM_FACTOR_TERMTAIL"}, {"neq", "error"}, {"qm", "TERM_FACTOR_TERMTAIL"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "TERM_FACTOR_TERMTAIL"}, {"lsqbr", "error"}, {"minus", "TERM_FACTOR_TERMTAIL"}, {"private", "error"}, {"string", "error"}, {"intnum", "TERM_FACTOR_TERMTAIL"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "TERM_FACTOR_TERMTAIL"}, {"geq", "error"}, {"not", "TERM_FACTOR_TERMTAIL"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "TERM_FACTOR_TERMTAIL"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("TERM", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "APARAMSTAIL_EPSILON"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "APARAMSTAIL_comma_EXPR_APARAMSTAIL"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("APARAMSTAIL", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "ARITHEXPRTAIL_EPSILON"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "ARITHEXPRTAIL_EPSILON"}, {"id", "error"}, {"neq", "ARITHEXPRTAIL_EPSILON"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "ARITHEXPRTAIL_EPSILON"}, {"plus", "ARITHEXPRTAIL_ADDOP_TERM_ARITHEXPRTAIL"}, {"lsqbr", "error"}, {"minus", "ARITHEXPRTAIL_ADDOP_TERM_ARITHEXPRTAIL"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "ARITHEXPRTAIL_EPSILON"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "ARITHEXPRTAIL_EPSILON"}, {"semi", "ARITHEXPRTAIL_EPSILON"}, {"rsqbr", "ARITHEXPRTAIL_EPSILON"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "ARITHEXPRTAIL_ADDOP_TERM_ARITHEXPRTAIL"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "ARITHEXPRTAIL_EPSILON"}, {"comma", "ARITHEXPRTAIL_EPSILON"}, {"func", "error"}, {"colon", "ARITHEXPRTAIL_EPSILON"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("ARITHEXPRTAIL", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "EXPR_ARITHEXPR_EXPRTAIL"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "EXPR_ARITHEXPR_EXPRTAIL"}, {"neq", "error"}, {"qm", "EXPR_ARITHEXPR_EXPRTAIL"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "EXPR_ARITHEXPR_EXPRTAIL"}, {"lsqbr", "error"}, {"minus", "EXPR_ARITHEXPR_EXPRTAIL"}, {"private", "error"}, {"string", "error"}, {"intnum", "EXPR_ARITHEXPR_EXPRTAIL"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "EXPR_ARITHEXPR_EXPRTAIL"}, {"geq", "error"}, {"not", "EXPR_ARITHEXPR_EXPRTAIL"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "EXPR_ARITHEXPR_EXPRTAIL"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("EXPR", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "VARIABLE_id_VARIABLEIDNEST"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("VARIABLE", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "VARDECL_TYPE_id_ARRAYSIZEREPT_semi"}, {"float", "VARDECL_TYPE_id_ARRAYSIZEREPT_semi"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "VARDECL_TYPE_id_ARRAYSIZEREPT_semi"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "VARDECL_TYPE_id_ARRAYSIZEREPT_semi"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("VARDECL", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "METHODBODYVAR_EPSILON"}, {"div", "error"}, {"continue", "METHODBODYVAR_EPSILON"}, {"else", "error"}, {"leq", "error"}, {"id", "METHODBODYVAR_EPSILON"}, {"neq", "error"}, {"qm", "error"}, {"write", "METHODBODYVAR_EPSILON"}, {"if", "METHODBODYVAR_EPSILON"}, {"read", "METHODBODYVAR_EPSILON"}, {"void", "error"}, {"$", "error"}, {"var", "METHODBODYVAR_var_lcurbr_VARDECLREP_rcurbr"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "METHODBODYVAR_EPSILON"}, {"rcurbr", "METHODBODYVAR_EPSILON"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "METHODBODYVAR_EPSILON"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("METHODBODYVAR", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "NESTEDID_EPSILON"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "NESTEDID_comma_id_NESTEDID"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("NESTEDID", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "FUNCSTATTAIL_lpar_APARAMS_rpar_FUNCSTATTAILIDNEST"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "FUNCSTATTAIL_INDICEREP_dot_id_FUNCSTATTAIL"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "FUNCSTATTAIL_INDICEREP_dot_id_FUNCSTATTAIL"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("FUNCSTATTAIL", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "VARIABLEIDNEST_INDICEREP_VARIABLEIDNESTTAIL"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "VARIABLEIDNEST_INDICEREP_VARIABLEIDNESTTAIL"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "VARIABLEIDNEST_INDICEREP_VARIABLEIDNESTTAIL"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("VARIABLEIDNEST", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "FACTOR_lpar_EXPR_rpar"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "FACTOR_FUNCORVAR"}, {"neq", "error"}, {"qm", "FACTOR_qm_lsqbr_EXPR_colon_EXPR_colon_EXPR_rsqbr"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "FACTOR_SIGN_FACTOR"}, {"lsqbr", "error"}, {"minus", "FACTOR_SIGN_FACTOR"}, {"private", "error"}, {"string", "error"}, {"intnum", "FACTOR_intnum"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "FACTOR_stringlit"}, {"geq", "error"}, {"not", "FACTOR_not_FACTOR"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "FACTOR_floatnum"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("FACTOR", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "FUNCSTATTAILIDNEST_dot_id_FUNCSTATTAIL"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "FUNCSTATTAILIDNEST_EPSILON"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("FUNCSTATTAILIDNEST", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "FUNCDECLTAIL_TYPE"}, {"float", "FUNCDECLTAIL_TYPE"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "FUNCDECLTAIL_TYPE"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "FUNCDECLTAIL_void"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "FUNCDECLTAIL_TYPE"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("FUNCDECLTAIL", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "STATEMENTLIST_STATEMENT_STATEMENTLIST"}, {"div", "error"}, {"continue", "STATEMENTLIST_STATEMENT_STATEMENTLIST"}, {"else", "error"}, {"leq", "error"}, {"id", "STATEMENTLIST_STATEMENT_STATEMENTLIST"}, {"neq", "error"}, {"qm", "error"}, {"write", "STATEMENTLIST_STATEMENT_STATEMENTLIST"}, {"if", "STATEMENTLIST_STATEMENT_STATEMENTLIST"}, {"read", "STATEMENTLIST_STATEMENT_STATEMENTLIST"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "STATEMENTLIST_STATEMENT_STATEMENTLIST"}, {"rcurbr", "STATEMENTLIST_EPSILON"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "STATEMENTLIST_STATEMENT_STATEMENTLIST"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("STATEMENTLIST", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "FUNCORASSIGNSTAT_id_FUNCORASSIGNSTATIDNEST"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("FUNCORASSIGNSTAT", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "FUNCORVARIDNEST_INDICEREP_FUNCORVARIDNESTTAIL"}, {"lpar", "FUNCORVARIDNEST_lpar_APARAMS_rpar_FUNCORVARIDNESTTAIL"}, {"lt", "FUNCORVARIDNEST_INDICEREP_FUNCORVARIDNESTTAIL"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "FUNCORVARIDNEST_INDICEREP_FUNCORVARIDNESTTAIL"}, {"continue", "error"}, {"else", "error"}, {"leq", "FUNCORVARIDNEST_INDICEREP_FUNCORVARIDNESTTAIL"}, {"id", "error"}, {"neq", "FUNCORVARIDNEST_INDICEREP_FUNCORVARIDNESTTAIL"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "FUNCORVARIDNEST_INDICEREP_FUNCORVARIDNESTTAIL"}, {"plus", "FUNCORVARIDNEST_INDICEREP_FUNCORVARIDNESTTAIL"}, {"lsqbr", "FUNCORVARIDNEST_INDICEREP_FUNCORVARIDNESTTAIL"}, {"minus", "FUNCORVARIDNEST_INDICEREP_FUNCORVARIDNESTTAIL"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "FUNCORVARIDNEST_INDICEREP_FUNCORVARIDNESTTAIL"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "FUNCORVARIDNEST_INDICEREP_FUNCORVARIDNESTTAIL"}, {"not", "error"}, {"public", "error"}, {"and", "FUNCORVARIDNEST_INDICEREP_FUNCORVARIDNESTTAIL"}, {"rpar", "FUNCORVARIDNEST_INDICEREP_FUNCORVARIDNESTTAIL"}, {"semi", "FUNCORVARIDNEST_INDICEREP_FUNCORVARIDNESTTAIL"}, {"rsqbr", "FUNCORVARIDNEST_INDICEREP_FUNCORVARIDNESTTAIL"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "FUNCORVARIDNEST_INDICEREP_FUNCORVARIDNESTTAIL"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "FUNCORVARIDNEST_INDICEREP_FUNCORVARIDNESTTAIL"}, {"comma", "FUNCORVARIDNEST_INDICEREP_FUNCORVARIDNESTTAIL"}, {"func", "error"}, {"colon", "FUNCORVARIDNEST_INDICEREP_FUNCORVARIDNESTTAIL"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("FUNCORVARIDNEST", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "FUNCORVAR_id_FUNCORVARIDNEST"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("FUNCORVAR", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "START_PROG"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "START_PROG"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "START_PROG"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("START", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "error"}, {"integer", "FPARAMS_TYPE_id_ARRAYSIZEREPT_FPARAMSTAIL"}, {"float", "FPARAMS_TYPE_id_ARRAYSIZEREPT_FPARAMSTAIL"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "FPARAMS_TYPE_id_ARRAYSIZEREPT_FPARAMSTAIL"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "FPARAMS_TYPE_id_ARRAYSIZEREPT_FPARAMSTAIL"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "FPARAMS_EPSILON"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "error"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("FPARAMS", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "TERMTAIL_MULTOP_FACTOR_TERMTAIL"}, {"lpar", "error"}, {"lt", "TERMTAIL_EPSILON"}, {"main", "error"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "TERMTAIL_MULTOP_FACTOR_TERMTAIL"}, {"continue", "error"}, {"else", "error"}, {"leq", "TERMTAIL_EPSILON"}, {"id", "error"}, {"neq", "TERMTAIL_EPSILON"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "TERMTAIL_EPSILON"}, {"plus", "TERMTAIL_EPSILON"}, {"lsqbr", "error"}, {"minus", "TERMTAIL_EPSILON"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "TERMTAIL_EPSILON"}, {"not", "error"}, {"public", "error"}, {"and", "TERMTAIL_MULTOP_FACTOR_TERMTAIL"}, {"rpar", "TERMTAIL_EPSILON"}, {"semi", "TERMTAIL_EPSILON"}, {"rsqbr", "TERMTAIL_EPSILON"}, {"inherits", "error"}, {"class", "error"}, {"sr", "error"}, {"or", "TERMTAIL_EPSILON"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "TERMTAIL_EPSILON"}, {"comma", "TERMTAIL_EPSILON"}, {"func", "error"}, {"colon", "TERMTAIL_EPSILON"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("TERMTAIL", table_entry_table);
        table_entry_table = Stream.of(new String[][]{{"mult", "error"}, {"lpar", "error"}, {"lt", "error"}, {"main", "PROG_CLASSDECL_FUNCDEF_main_FUNCBODY"}, {"integer", "error"}, {"float", "error"}, {"while", "error"}, {"div", "error"}, {"continue", "error"}, {"else", "error"}, {"leq", "error"}, {"id", "error"}, {"neq", "error"}, {"qm", "error"}, {"write", "error"}, {"if", "error"}, {"read", "error"}, {"void", "error"}, {"$", "error"}, {"var", "error"}, {"then", "error"}, {"eq", "error"}, {"plus", "error"}, {"lsqbr", "error"}, {"minus", "error"}, {"private", "error"}, {"string", "error"}, {"intnum", "error"}, {"dot", "error"}, {"lcurbr", "error"}, {"stringlit", "error"}, {"geq", "error"}, {"not", "error"}, {"public", "error"}, {"and", "error"}, {"rpar", "error"}, {"semi", "error"}, {"rsqbr", "error"}, {"inherits", "error"}, {"class", "PROG_CLASSDECL_FUNCDEF_main_FUNCBODY"}, {"sr", "error"}, {"or", "error"}, {"break", "error"}, {"rcurbr", "error"}, {"gt", "error"}, {"comma", "error"}, {"func", "PROG_CLASSDECL_FUNCDEF_main_FUNCBODY"}, {"colon", "error"}, {"floatnum", "error"}, {"return", "error"}, {"assign", "error"}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        parsing_table.put("PROG", table_entry_table);
    }


    /**
     * Parsing HTML file to get parsing table using JSOUP; also terminal list and nonTerminal list
     */
    public void importParsingTable() {

//        String file_name = "/exclude/parsingTable.html";
//        System.out.println("[Grammar] Importing parsing table from file: " + file_name);
//        InputStream in = getClass().getResourceAsStream(file_name);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//        Document html_doc = Jsoup.parse(reader.lines().collect(Collectors.joining()), "UTF-8");
//        Element table = html_doc.select("table").get(1); //select the table.
//        Elements rows = table.select("tr");
//        Elements first = rows.get(0).select("th,td");       //header of the table
//
//        List<String> headers = new ArrayList<String>();
//        for (Element header : first) {
//            headers.add(header.text());
//            if (!header.text().isEmpty()) {
//                terminal_list.add(header.text());
//            }
//        }
//        for (int i = 1; i < rows.size(); i++) {
//            Element row = rows.get(i);
//            Elements col_vals = rows.get(i).select("th,td");
//            Map<String, String> table_vals = new HashMap<>();
//            int col_count = 1;
//            for (int j = 1; j < col_vals.size(); j++) {
//                String ruleID = col_vals.get(j).text();
//                if (!ruleID.isEmpty()) {
//                    ruleID = ruleID.replace(" → ", "_").replace(" ", "_").replace("&epsilon", "EPSILON");
//                    table_vals.put(headers.get(col_count++), ruleID);
//                } else {
//                    table_vals.put(headers.get(col_count++), "error");
//                }
//            }
//            String non_terminal = col_vals.get(0).text();
//            parsing_table.put(non_terminal, table_vals);
//            nonTerminal_list.add(non_terminal);
//        }
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
