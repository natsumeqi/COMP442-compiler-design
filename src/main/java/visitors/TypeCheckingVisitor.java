package visitors;

import AST.*;
import symbolTable.SymTabEntry;

import java.util.ArrayList;
import java.util.HashSet;


/**
 * Template from astvisitor
 * Visitor to compute the type of subexpressions, assignment statements and return statements.
 * <p>
 * This applies only to nodes that are part of expressions and assignment statements i.e.
 * AddOpNode, MultOpNode, and AssignStatp_node.
 */
public class TypeCheckingVisitor extends Visitor {

    public String m_errors = "";
    private final HashSet<Integer> error_set = new HashSet<>();

    public TypeCheckingVisitor() {
    }

    public void visit(ProgNode p_node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : p_node.getChildren())
            child.accept(this);

//        System.out.println("type checking error: \r\n" + this.m_errors);
    }

    public void visit(ClassListNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);
    }

    public void visit(ClassDeclNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);


        // check circular class dependencies through inheritance
        String class_name = p_node.m_symTab.m_name;
        if (p_node.m_symTab.checkCircular(class_name)) {
            this.m_errors += "[14.1][semantic error][line:" + p_node.m_line + "] Circular class dependency:  '" + class_name + "'\n";
        }


    }


    public void visit(FuncDefListNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);
    }

    public void visit(FuncDefNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);
    }

    public void visit(ScopeNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);
    }

    public void visit(FParamListNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);


    }

    public void visit(FParamNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);
    }

    public void visit(DimListNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);
        if (p_node.getChildren().size() >= 2) {
            for (int i = 1; i < p_node.getChildren().size(); i++) {
                if (p_node.getChildren().get(i).m_data.equals("[]")) {
                    this.m_errors += "[13.4][semantic error][line:" + p_node.m_line + "] Array dimension unknown:  '" + p_node.m_subtreeString + "'\n";
                }
            }
        }
    }

    public void visit(MethVarNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);
    }

    public void visit(FuncBodyNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);
    }

    public void visit(StatBlockNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);
    }

    public void visit(VarDeclNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);
    }

    public void visit(IfStatNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);
    }

    public void visit(WhileStatNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);
    }

    public void visit(ReadStatNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);
    }


    public void visit(WriteStatNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);
    }

    public void visit(ReturnStatNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);
        String return_stat_type = p_node.getChildren().get(0).getType();
        String func_name = p_node.m_symTab.m_name;
        String lookup_func_name = func_name.substring(func_name.indexOf("::") + 2);
        String func_return_type = p_node.m_symTab.lookupName(lookup_func_name).m_type;
        if (return_stat_type != null) {
            if (return_stat_type.equals(func_return_type)) {
                p_node.setType(return_stat_type);
            } else {
                p_node.setType("typeerror");
                this.m_errors += "[10.3][semantic error][line:" + p_node.m_line + "] Type error in return statement:  "
                        + p_node.getChildren().get(0).getSubtreeString()
                        + "(" + p_node.getChildren().get(0).getType() + ")"
                        + " and "
                        + func_name
                        + "(" + func_return_type + ")"
                        + "\n";
            }
        }
    }


    public void visit(AssignStatNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);

        String left_operand_type = p_node.getChildren().get(0).getType();
        String right_operand_type = p_node.getChildren().get(1).getType();
        if (left_operand_type != null) {
            if (left_operand_type.equals(right_operand_type)) {
                p_node.setType(left_operand_type);
            } else {
                p_node.setType("typeerror");
                // add only once in error set
//            String error_info = p_node.m_line + "MultOpNode";
                if (!error_set.contains(p_node.m_line)) {
                    this.m_errors += "[10.2][semantic error][line:" + p_node.m_line + "] Type error in assignment statement:  "
                            + p_node.getChildren().get(0).getSubtreeString()
                            + "(" + p_node.getChildren().get(0).getType() + ")"
                            + " and "
                            + p_node.getChildren().get(1).getSubtreeString()
                            + "(" + p_node.getChildren().get(1).getType() + ")"
                            + "\n";
                    error_set.add(p_node.m_line);
                }
            }
        }
    }

    public void visit(FuncCallStatNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);
    }

    public void visit(AddOpNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);
        String left_operand_type = p_node.getChildren().get(0).getType();
        String right_operand_type = p_node.getChildren().get(1).getType();
//        System.out.println("add: "+left_operand_type);
//        System.out.println("add: "+right_operand_type);
        if (left_operand_type != null) {
            if (left_operand_type.equals(right_operand_type)) {
                p_node.setType(left_operand_type);
            } else {
                p_node.setType("typeerror");
                if (!error_set.contains(p_node.m_line)) {
                    this.m_errors += "[10.1][semantic error][line:" + p_node.m_line + "] Type error in AddOpNode:  "
                            + p_node.getChildren().get(0).getData()
                            + "(" + p_node.getChildren().get(0).getType() + ")"
                            + " and "
                            + p_node.getChildren().get(1).getData()
                            + "(" + p_node.getChildren().get(1).getType() + ")"
                            + "\n";
                    error_set.add(p_node.m_line);

                }
            }
        }
    }


    public void visit(MultOpNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        String left_operand_type = p_node.getChildren().get(0).getType();
        String right_operand_type = p_node.getChildren().get(1).getType();
        if (left_operand_type != null) {
            if (left_operand_type.equals(right_operand_type)) {
                p_node.setType(left_operand_type);
            } else {
                p_node.setType("typeerror");
                // add only once in error set
                if (!error_set.contains(p_node.m_line)) {
                    this.m_errors += "[10.1][semantic error][line:" + p_node.m_line + "] Type error in MultOpNode:  "
                            + p_node.getChildren().get(0).getData()
                            + "(" + p_node.getChildren().get(0).getType() + ")"
                            + " and "
                            + p_node.getChildren().get(1).getData()
                            + "(" + p_node.getChildren().get(1).getType() + ")"
                            + "\n";
                    error_set.add(p_node.m_line);
                }
            }
        }
    }

    public void visit(ArithExprNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        p_node.m_type = p_node.getChildren().get(0).getType();
    }

    public void visit(ExprNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        p_node.m_type = p_node.getChildren().get(0).getType();
        if (p_node.m_type != null) {
            if (p_node.m_type.equals("typeerror")) {
                if (!error_set.contains(p_node.m_line)) {
                    this.m_errors += "[10.1][semantic error][line:" + p_node.m_line + "] Type error in expression: '" + p_node.m_subtreeString + "'\n";
//                    error_set.add("ExprNode" + p_node.m_subtreeString);
                    error_set.add(p_node.m_line);
                }
            }
        }
    }

    public void visit(TermNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        p_node.m_type = p_node.getChildren().get(0).getType();
    }

    public void visit(FactorNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        p_node.m_type = p_node.getChildren().get(0).getType();
    }

    public void visit(InlineIfNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        String else_expr_type = p_node.getChildren().get(1).getType();
        String then_expr_type = p_node.getChildren().get(2).getType();
        if (else_expr_type != null) {
            if (else_expr_type.equals(then_expr_type))
                p_node.setType(else_expr_type);
            else {
                p_node.setType("typeerror");
                this.m_errors += "InlineIfNode type error:  "
                        + p_node.getChildren().get(1).getData()
                        + "(" + p_node.getChildren().get(1).getType() + ")"
                        + " and "
                        + p_node.getChildren().get(2).getData()
                        + "(" + p_node.getChildren().get(2).getType() + ")"
                        + "\n";
            }
        }
    }

    public void visit(SignNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        p_node.m_type = p_node.getChildren().get(0).getType();
    }

    public void visit(NotNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        p_node.m_type = p_node.getChildren().get(0).getType();
    }

    public void visit(FuncOrVarNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        p_node.m_type = p_node.getChildren().get(0).getType();
    }

    public void visit(VariableNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        p_node.m_type = p_node.getChildren().get(0).getType();

        // check array dimensions when the first child is DataMemNode
        if (p_node.getChildren().get(0).m_sa_name.equals("DataMem_s")) {
            Node dataMem_temp = p_node.getChildren().get(0);
            int size_dimList = dataMem_temp.getChildren().get(1).getChildren().size();
            SymTabEntry var_entry = p_node.m_symTab.lookupName(dataMem_temp.m_data);
            if (var_entry.m_name != null) {
                if (size_dimList != var_entry.m_dims.size()) {
                    this.m_errors += "[13.1][semantic error][line:" + p_node.m_line + "] Use of array with wrong number of dimensions:  '" + p_node.m_subtreeString + "'\n";

                }
            }
        }


    }

    public void visit(DataMemNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        p_node.m_type = p_node.getChildren().get(0).getType();
        p_node.m_data = p_node.getChildren().get(0).getData();
//        System.out.println("datamem: "+p_node.m_type);
    }

    public void visit(TypeNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        // primitive type
        if (p_node.isLeaf()) {
            p_node.m_type = p_node.m_data;
        } else {
            // class type
            p_node.m_type = p_node.getChildren().get(0).getType();

            // check if the type is undeclared
            if (p_node.m_type == null) {
                String undeclared_type = p_node.getChildren().get(0).getData();

                // check if uppercase is good
                SymTabEntry class_entry = p_node.m_symTab.lookupName(undeclared_type.toUpperCase());
                if (class_entry.m_name != null) {
                    p_node.m_type = class_entry.m_name;
                } else {
                    this.m_errors += "[11.5][semantic error][line:" + p_node.m_line + "] Use of undeclared class:  '" + undeclared_type + "'\n";
                }
            }
        }

    }

    public void visit(IdNode p_node) {

//         search variable in symbol table; for cases in Dot, the type is not right, will check in the Dot node
        if (p_node.m_symTab != null) {
//            System.out.println("p_node.m_data:"+p_node.m_data);
            SymTabEntry entry = p_node.m_symTab.lookupName(p_node.m_data);
            if (entry.m_name != null) {
//                System.out.println("entry.m_name: "+entry.m_name+" entry.type: "+ entry.m_type);
                p_node.m_type = entry.m_type;
            } else {


                if (p_node.getParent().m_sa_name.equals("DataMem_s") && p_node.getParent().getParent().m_sa_name.equals("FuncOrVar_s")) {

                    this.m_errors += "[11.1][semantic error][line:" + p_node.m_line + "] Undeclared local variable:  '" + p_node.m_data + "'\n";
                    error_set.add(p_node.m_line);
                }

            }
        }
    }


    public void visit(DotNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        String var_class_type = p_node.getChildren().get(0).getType() == null ? null : p_node.getChildren().get(0).getType().toUpperCase();
        String var_or_func_type;


        // get from nodes rather than IdNode
        String var_func_name = p_node.getChildren().get(1).m_subtreeString;
        SymTabEntry class_entry = p_node.m_symTab.lookupName(var_class_type);


        if (class_entry.m_name != null) {
            SymTabEntry func_var_entry = class_entry.m_subtable.lookupNameInOneTable(var_func_name);
            if (func_var_entry.m_name != null) {
                var_or_func_type = func_var_entry.m_type;
                p_node.setType(var_or_func_type);

                // check function call
                if (p_node.getR_sibling() != null) {
                    if (p_node.getR_sibling().m_sa_name.equals("AParams_s")) {
                        // add aParam into type
                        var_or_func_type = func_var_entry.m_fParam + ":" + func_var_entry.m_type;
                        p_node.setType(var_or_func_type);
                    }

                } else {// check variable array dimensions  test
                    if (p_node.getChildren().get(1).m_sa_name.equals("DataMem_s")) {
                        Node dataMem_temp = p_node.getChildren().get(1);
                        int size_dimList = dataMem_temp.getChildren().get(1).getChildren().size();
                        SymTabEntry var_entry = p_node.m_symTab.lookupName(dataMem_temp.m_data);
                        if (var_entry.m_name != null) {
                            if (size_dimList != var_entry.m_dims.size()) {
                                this.m_errors += "[13.1][semantic error][line:" + p_node.m_line + "] Use of array with wrong number of dimensions:  '" + p_node.m_subtreeString + "'\n";

                            }
                        }
                    }

                }


            } // function or variable is not declared in the class


        } else {// "." operator used on non-class type

            if (!error_set.contains(p_node.m_line)) {
                this.m_errors += "[15.1][semantic error][line:" + p_node.m_line + "] '.' operator used on non-class type:  '" + p_node.m_subtreeString + "'\n";
                error_set.add(p_node.m_line);
            }
        }

        // semantic error
        if (p_node.m_type == null) {
            // for [15.1]
            if (p_node.getR_sibling() != null) {
                if (p_node.getR_sibling().m_sa_name.equals("AParams_s")) {            // undeclared member function
                    if (!error_set.contains(p_node.m_line)) {
                        this.m_errors += "[11.3][semantic error][line:" + p_node.m_line + "] Undeclared member function:  '" + var_func_name + "'\n";
                        error_set.add(p_node.m_line);
                    }
                }

                // undeclared data member
            } else {
                if (!error_set.contains(p_node.m_line)) {
                    this.m_errors += "[11.2][semantic error][line:" + p_node.m_line + "] Undeclared data member:  '" + p_node.m_subtreeString + "'\n";
                    error_set.add(p_node.m_line);
                }
            }
        }
    }


    public void visit(FuncCallNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }


        // get the type of function call
        String func_name;

        if (p_node.getChildren().get(0).m_sa_name.equals("Id_s")) {
            // free function
            func_name = p_node.getChildren().get(0).getData();
            if (p_node.m_symTab.lookupName(func_name).m_name == null) {
                if (!error_set.contains(p_node.m_line)) {
                    this.m_errors += "[11.4][semantic error][line:" + p_node.m_line + "] Undeclared free function:  '" + p_node.m_subtreeString + "'\n";
                    error_set.add(p_node.m_line);
                }
            } else {

                p_node.m_type = p_node.getChildren().get(0).getType();

                // check type and number of parameters in function calls
                String funcCall_name = p_node.getChildren().get(0).m_subtreeString;
                String func_paras_type = p_node.getChildren().get(1).m_type;

                String func_paras_ids = p_node.getChildren().get(1).m_subtreeString;
                func_paras_ids = func_paras_ids.substring(1, func_paras_ids.length() - 1).replace(" ", "");

                ArrayList<SymTabEntry> decl_func_entries = p_node.m_symTab.lookupFunction(funcCall_name);
                String[] func_paras_type_list = func_paras_type.split(",");
                String[] func_paras_ids_list = func_paras_ids.split(",");
                int size_paras = func_paras_type_list.length;
                boolean paras_size_match = false;


                for (SymTabEntry decl_func_entry : decl_func_entries) {
                    if (decl_func_entry.m_fParam.size() == size_paras) {
                        paras_size_match = true;
                        for (int i = 0; i < size_paras; i++) {
                            // first check type
                            String type_para;
                            if (decl_func_entry.m_fParam.get(i).contains("[")) {
                                type_para = decl_func_entry.m_fParam.get(i).substring(0, decl_func_entry.m_fParam.get(i).indexOf("["));
                            } else {
                                type_para = decl_func_entry.m_fParam.get(i);
                            }

                            if (!type_para.equals(func_paras_type_list[i])) {
                                if (!error_set.contains(p_node.m_line)) {
                                    this.m_errors += "[12.2][semantic error][line:" + p_node.m_line + "] Function call with wrong type of parameters:  '" + p_node.m_subtreeString + "'\n";
                                    error_set.add(p_node.m_line);
                                    break;
                                }
                            } else {
                                // then check dimensionality
                                int size_dims = decl_func_entry.m_fParam.get(i).length() - decl_func_entry.m_fParam.get(i).replace("[", "").length();

                                // find variable declaration of parameter id
                                SymTabEntry var_entry = p_node.m_symTab.lookupName(func_paras_ids_list[i]);
                                if (var_entry.m_name != null) {
                                    if (var_entry.m_dims.size() != size_dims) {
                                        if (!error_set.contains(p_node.m_line)) {
                                            this.m_errors += "[13.3][semantic error][line:" + p_node.m_line + "] Array parameter using wrong number of dimensions:  '" + p_node.m_subtreeString + "'\n";
                                            error_set.add(p_node.m_line);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (!paras_size_match) {
                    if (!error_set.contains(p_node.m_line)) {
                        this.m_errors += "[12.1][semantic error][line:" + p_node.m_line + "] Function call with wrong number of parameters:  '" + p_node.m_subtreeString + "'\n";
                        error_set.add(p_node.m_line);
                    }
                }
            }
        } else {
            if (p_node.getChildren().get(0).m_sa_name.equals("DataMem_s")) {


            } else {
                if (p_node.getChildren().get(0).m_sa_name.equals("Dot_s")) {

                    p_node.m_type = p_node.getChildren().get(0).getType();

                    // check type and number of parameters in function calls
                    String funcCall_name = p_node.getChildren().get(0).m_subtreeString;
                    String func_paras_type = p_node.getChildren().get(1).m_type;

                    String func_paras_ids = p_node.getChildren().get(1).m_subtreeString;
                    func_paras_ids = func_paras_ids.substring(1, func_paras_ids.length() - 1).replace(" ", "");

                    String[] func_paras_type_list = func_paras_type.split(",");
                    String[] func_paras_ids_list = func_paras_ids.split(",");
                    int size_paras = func_paras_type_list.length;
                    boolean paras_size_match_dot = false;
                    if (p_node.m_type != null) {
                        String[] decl_func_type_list = p_node.m_type.substring(1, p_node.m_type.indexOf("]")).replace(" ", "").split(",");

                        if (decl_func_type_list.length == size_paras) {
                            paras_size_match_dot = true;
                            for (int i = 0; i < size_paras; i++) {
                                // first check type
                                String decl_type_para = decl_func_type_list[i];

                                if (!decl_type_para.equals(func_paras_type_list[i])) {
                                    if (!error_set.contains(p_node.m_line)) {
                                        this.m_errors += "[12.2][semantic error][line:" + p_node.m_line + "] Function call with wrong type of parameters:  '" + p_node.m_subtreeString + "'\n";
                                        error_set.add(p_node.m_line);
                                        break;
                                    }
                                } else {
                                    // then check dimensionality
                                    int size_dims = decl_type_para.length() - decl_type_para.replace("[", "").length();

                                    // find variable declaration of parameter id
                                    SymTabEntry var_entry = p_node.m_symTab.lookupName(func_paras_ids_list[i]);
                                    if (var_entry.m_name != null) {
                                        if (var_entry.m_dims.size() != size_dims) {
                                            if (!error_set.contains(p_node.m_line)) {
                                                this.m_errors += "[13.3][semantic error][line:" + p_node.m_line + "] Array parameter using wrong number of dimensions:  '" + p_node.m_subtreeString + "'\n";
                                                error_set.add(p_node.m_line);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (!paras_size_match_dot) {
                            if (!error_set.contains(p_node.m_line)) {
                                this.m_errors += "[12.1][semantic error][line:" + p_node.m_line + "] Function call with wrong number of parameters:  '" + p_node.m_subtreeString + "'\n";
                                error_set.add(p_node.m_line);
                            }
                        }
                    }
                    // restore the type of function call for following checking, i.e. assignment statement
                    if (p_node.m_type != null) {
                        p_node.m_type = p_node.m_type.substring(p_node.m_type.indexOf(":") + 1);
                    }
                }
            }
        }
    }


    public void visit(AParamsNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        p_node.m_type = "";
        boolean first = true;
        for (Node child : p_node.getChildren()) {
            if (first)
                p_node.m_type += child.m_type;
            else
                p_node.m_type += "," + child.m_type;
            first = false;
        }
        p_node.m_type += "";
    }


    public void visit(MainBlockNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(IndiceNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        for (Node child : p_node.getChildren()) {
            if (child.m_type != null) {
                if (!child.m_type.equals("integer")) {
                    this.m_errors += "[13.2][semantic error][line:" + p_node.m_line + "] Array index is not an integer:  '" + child.m_subtreeString + "'\n";
                }
            }
        }
        p_node.m_type = "[" + p_node.getChildren().size() + "]";
    }

    public void visit(RelExprNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        String left_operand_type = p_node.getChildren().get(0).getType();
        String right_operand_type = p_node.getChildren().get(2).getType();
        if (left_operand_type != null) {
            if (left_operand_type.equals(right_operand_type)) {
                p_node.setType(left_operand_type);
            } else {
                p_node.setType("typeerror");
                // add only once in error set
                if (!error_set.contains(p_node.m_line)) {
                    this.m_errors += "[10.1][semantic error][line:" + p_node.m_line + "] Type error in RelOpNode:  "
                            + p_node.getChildren().get(0).getData()
                            + "(" + p_node.getChildren().get(0).getType() + ")"
                            + " and "
                            + p_node.getChildren().get(1).getData()
                            + "(" + p_node.getChildren().get(1).getType() + ")"
                            + "\n";
                    error_set.add(p_node.m_line);
                }
            }
        }
    }


}


