package visitors;

import AST.*;
import symbolTable.*;

import java.util.ArrayList;
import java.util.Vector;

public class SymTabCreationVisitor extends Visitor {

    /**
     * Template from astvisitor
     * Visitor to create symbol tables and their entries.
     * <p>
     * This concerns only nodes that either:
     * <p>
     * (1) represent identifier declarations/definitions, in which case they need to assemble
     * a symbol table record to be inserted in a symbol table. These are:  VarDeclNode, ClassDeclNode
     * and FuncDeclNode.
     * <p>
     * (2) represent a scope, in which case they need to create a new symbol table, and then
     * insert the symbol table entries they get from their children. These are:  ProgNode, ClassDeclNode,
     * FuncDefNode and MainBlockNode.
     */


    public StringBuilder m_errors = new StringBuilder();

    public SymTabCreationVisitor() {
    }

    public void visit(ProgNode p_node) {
        p_node.m_symTab = new SymTab(0, "global", null);

        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : p_node.getChildren()) {
            //make all children use this scopes' symbol table
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }


        // check in the global table after the visit
        ArrayList<SymTabEntry> class_entries = p_node.m_symTab.lookupKind("class");
        for (SymTabEntry class_entry : class_entries) {

            // check if a member function that is declared but not defined
            ArrayList<SymTabEntry> funcDecl_entries = class_entry.m_subtable.lookupKind("function");
            for (SymTabEntry funcDecl_entry : funcDecl_entries) {
                if (funcDecl_entry.m_subtable == null) {
                    this.m_errors.append("[6.2][semantic error][line:"+((FuncEntry)funcDecl_entry).m_line+"] No definition for declared member function: \t");
                    this.m_errors.append(class_entry.m_name).append(": ").append(funcDecl_entry.m_name).append("\r\n");
                }
            }
        }

//        System.out.println(p_node.m_symTab);
//        System.out.println(this.m_errors);

    }

    public void visit(ClassListNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    /**
     * build symbol table for the class
     *
     * @param p_node of class Declaration
     */
    public void visit(ClassDeclNode p_node) {
        // get the class name from node 0
        String class_name = p_node.getChildren().get(0).getData();
        // create a new table with the class name
        SymTab local_table = new SymTab(1, class_name, p_node.m_symTab);
        // create the symbol table entry for the class
        p_node.m_symTabEntry = new ClassEntry(class_name, local_table);

        // check multiply declared classes
        if (p_node.getParent().getParent() != null) {
            SymTabEntry class_entry = p_node.getParent().getParent().m_symTab.lookupName(class_name);
            if (class_entry.m_name != null) {
                m_errors.append("[8.1][semantic error][line:"+p_node.m_line+"] Multiple undeclared class: \t'").
                        append(class_name).append("'\r\n");
                // still create table for multiple classes, but not add to global table
                String class_name_duplicate = class_name + "_duplicate";
                local_table = new SymTab(1, class_name_duplicate, p_node.m_symTab);
                p_node.m_symTabEntry = new ClassEntry(class_name_duplicate, local_table);
//                p_node.m_symTab.addEntry(p_node.m_symTabEntry);
                p_node.m_symTab = local_table;

            } else {
                p_node.m_symTab.addEntry(p_node.m_symTabEntry);
                p_node.m_symTab = local_table;
            }
        }


        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node member : p_node.getChildren()) {
            member.m_symTab = p_node.m_symTab;
            member.accept(this);
        }


    }

    public void visit(InherListNode p_node) {
        if (p_node.getChildren().isEmpty()) {
            VarEntry varEntry = new VarEntry("inherit", "", "none", null);
            p_node.m_symTab.addEntry(varEntry);
        } else {
            for (Node child : p_node.getChildren()) {
                String var_id = child.getData();
                VarEntry varEntry = new VarEntry("inherit", "", var_id, null);
                p_node.m_symTab.addEntry(varEntry);
                p_node.m_symTab.addInherit(p_node.m_symTab.m_upperTable.lookupName(var_id).m_subtable);
            }
        }
    }

    public void visit(MembListNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(MembDeclNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }


    /**
     * type node has two different cases
     * @param p_node of type
     * @return data of type
     */
    private String returnTypeDate(Node p_node) {
        if (p_node.isLeaf()) {
            return p_node.getData();
        } else {
            return p_node.getChildren().get(0).getData().toUpperCase();
        }
    }

    /**
     * create function entry
     *
     * @param p_node of function declaration
     */
    public void visit(FuncDeclNode p_node) {
        String func_visibility = "";
        if (p_node.getLm_sibling() != null) {
            func_visibility = p_node.getLm_sibling().getData();
        }
        String func_name = p_node.getChildren().get(0).getData();
        String func_type = returnTypeDate(p_node.getChildren().get(2));
        String fParam_type;


        Vector<String> fParam_list = new Vector<>();
        for (Node param : p_node.getChildren().get(1).getChildren()) {
            fParam_type = param.getChildren().get(0).getData();

            String fParam;
            StringBuilder dim_string = new StringBuilder();
            for (Node dim : param.getChildren().get(2).getChildren()) {
                // parameter dimension
                dim_string.append(dim.getData());
            }
            fParam = fParam_type + dim_string;
            fParam_list.add(fParam);
        }

        p_node.m_symTabEntry = new FuncEntry(func_type, func_name, fParam_list, func_visibility, p_node.m_line);


        SymTabEntry func_entry = p_node.m_symTab.lookupNameInOneTable(func_name);
        if (func_entry.m_name != null) {
            // check overloading member functions
            if (!func_entry.m_fParam.equals(fParam_list) || !func_entry.m_type.equals(func_type)) {
                String class_name = p_node.m_symTab.m_name;
                m_errors.append("[9.2][semantic warning][line:"+p_node.m_line+"] Overloaded member function: \t'").
                        append(func_name).append("' in the class: ").append(class_name).append("\r\n");
            } else {
                // check multiple identifier of functions
                m_errors.append("[8.3][semantic error][line:"+p_node.m_line+"] Multiple declared identifier in class: \t").append("'").
                        append(func_name).append("' in the class ").append(p_node.m_symTab.m_name).append("\r\n");
            }
        }


        // check overridden inherited member function
        SymTabEntry func_entry_inherited = p_node.m_symTab.lookupFunctionInInherited(func_name);
        if (func_entry_inherited.m_name != null) {

            // check overloading member functions
            if (!func_entry_inherited.m_fParam.equals(fParam_list) || !func_entry_inherited.m_type.equals(func_type)) {
                String class_name = p_node.m_symTab.m_name;
                m_errors.append("[9.2][semantic warning][line:"+p_node.m_line+"] Overloaded member function: \t'").
                        append(func_name).append("' in the class: ").append(class_name).append("\r\n");
            } else {
                // check overridden function
                m_errors.append("[9.3][semantic warning][line:"+p_node.m_line+"] Overridden inherited member function: \t").append("'").
                        append(func_name).append("' in the class ").append(p_node.m_symTab.m_name).append("\r\n");
            }
        }


        p_node.m_symTab.addEntry(p_node.m_symTabEntry);
    }


    public void visit(VarDeclNode p_node) {

        // aggregate information from the subtree
        // get the type from the first child node and aggregate here
        String var_type = returnTypeDate(p_node.getChildren().get(0));
        // get the id from the second child node and aggregate here
        String var_id = p_node.getChildren().get(1).getData();
        // loop over the list of dimension nodes and aggregate here
        Vector<String> dim_list = new Vector<>();
        for (Node dim : p_node.getChildren().get(2).getChildren()) {
            String dim_val = dim.getData();
            dim_list.add(dim_val);
        }
        // create the symbol table entry for this variable
        // it will be picked-up by another node above later

        // variable in function
        if (p_node.getParent().getClass().getSimpleName().equals("MethVarNode")) {
            p_node.m_symTabEntry = new VarEntry("local", var_type, var_id, dim_list);

            // check multiple declared identifier in function
            SymTabEntry var_entry = p_node.m_symTab.lookupName(var_id);
            if (var_entry.m_name != null) {
                m_errors.append("[8.4][semantic error][line:"+p_node.m_line+"] Multiple declared identifier in function: \t").append("'").
                        append(var_id).append("' in the function ").append(p_node.m_symTab.m_name).append("\r\n");
            } else {
                p_node.m_symTab.addEntry(p_node.m_symTabEntry);
            }

        } else {    // data member in class
            if (p_node.getParent().getClass().getSimpleName().equals("MembDeclNode")) {
                String var_visibility = p_node.getLm_sibling().m_data;
                p_node.m_symTabEntry = new VarEntry("data", var_type, var_id, var_visibility, dim_list);

                // check multiple declared identifier in class
                SymTabEntry var_entry = p_node.m_symTab.lookupName(var_id);
                if (var_entry.m_name != null) {
                    m_errors.append("[8.3][semantic error][line:"+p_node.m_line+"] Multiple declared identifier in class: \t").append("'").
                            append(var_id).append("' in the class ").append(p_node.m_symTab.m_name).append("\r\n");
                } else {

                    // check shadowed inherited member

                    if (p_node.m_symTab.lookupInInherited(var_id)) {
                        m_errors.append("[8.5][semantic warning][line:"+p_node.m_line+"] shadowed inherited data member: \t").append("'").
                                append(var_id).append("' in the class ").append(p_node.m_symTab.m_name).append("\r\n");
                    }


                    p_node.m_symTab.addEntry(p_node.m_symTabEntry);
                }
            }
        }
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }


    public void visit(FuncDefListNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }


    public void visit(FuncDefNode p_node) {
        String func_type = returnTypeDate(p_node.getChildren().get(3));
        String func_name = p_node.getChildren().get(1).getData();
        String unmatch_line = String.valueOf(p_node.getChildren().get(1).m_line);
        String fParam_type, fParam_name;
        String func_scope = "";
        Vector<String> fParam_list = new Vector<>();
        SymTab local_table;

        // when it is a member function, add scope into the name
        if (!p_node.getChildren().get(0).isLeaf()) {
            func_scope = p_node.getChildren().get(0).getChildren().get(0).getData();
            local_table = new SymTab(2, func_scope + "::" + func_name, p_node.m_symTab);
        } else {
            local_table = new SymTab(1, func_name, p_node.m_symTab);
        }

        // add parameter as VarEntry into the table
        for (Node param : p_node.getChildren().get(2).getChildren()) {
            fParam_type = param.getChildren().get(0).getData();
            fParam_name = param.getChildren().get(1).getData();
            String fParam;
            StringBuilder dim_string = new StringBuilder();
            Vector<String> dim_list = new Vector<>();
            for (Node dim : param.getChildren().get(2).getChildren()) {
                dim_string.append(dim.getData());
                dim_list.add(dim.getData());
            }

            fParam = fParam_type + dim_string;
            fParam_list.add(fParam);
            local_table.addEntry(new VarEntry("param", fParam_type, fParam_name, dim_list));
        }


        // add local variables into the table
        boolean matched = false;
        // for member function, check if function header is matched with function declaration in class
        if (!p_node.getChildren().get(0).isLeaf()) {
            if (p_node.getParent().getParent() != null) {
                SymTabEntry class_entry = p_node.getParent().getParent().m_symTab.lookupName(func_scope);
                if (class_entry.m_subtable != null) {
                    SymTabEntry func_decl = class_entry.m_subtable.lookupName(func_name);
                    if (func_decl.m_name != null) {
                        if (func_decl.m_name.equals(func_name) && func_decl.m_type.equals(func_type)) {
                            if (func_decl.getClass().getSimpleName().equals("FuncEntry")) {
                                if (func_decl.m_fParam.toString().equals(fParam_list.toString())) {
                                    // make class table be the upper table of member function table
                                    local_table.m_upperTable = class_entry.m_subtable;
                                    func_decl.m_subtable = local_table;
                                    p_node.m_symTab = local_table;
                                    matched = true;
                                }
                            }
                        }
                    }
                }
            }
        } else {    // for free functions

            p_node.m_symTabEntry = new FuncEntry(func_type, func_name, fParam_list, local_table, p_node.m_line);

            // check multiply declared classes
            if (p_node.getParent().getParent() != null) {

                // go to global table to check
                SymTabEntry func_entry = p_node.getParent().getParent().m_symTab.lookupName(func_name);
                if (func_entry.m_name != null) {
                    // ignore  func_entry.m_type.equals(func_type)
                    if (func_entry.m_fParam.toString().equals(fParam_list.toString())) {
                        m_errors.append("[8.2][semantic error][line:"+p_node.m_line+"] Multiple defined free function: \t'").
                                append(func_name).append("'\r\n");
                    } else {
                        // overloading free function
                        m_errors.append("[9.1][semantic warning][line:"+p_node.m_line+"] Overloaded free function: \t'").
                                append(func_name).append("'\r\n");
                        p_node.m_symTab.addEntry(p_node.m_symTabEntry);
                        p_node.m_symTab = local_table;
                    }

                } else {
                    p_node.m_symTab.addEntry(p_node.m_symTabEntry);
                    p_node.m_symTab = local_table;
                }
            }
            matched = true;
        }


        if (!matched) {
            m_errors.append("[6.1][semantic error][line:"+unmatch_line+"] Definition provided for undeclared member function: \t").
                    append(func_scope).append(":").append(func_name).append("\r\n");

            // make this unmatched member function still exist in the symbol table
            if (!p_node.getChildren().get(0).isLeaf()) {
                if (p_node.getParent().getParent() != null) {
                    SymTabEntry class_entry = p_node.getParent().getParent().m_symTab.lookupName(func_scope);
                    // make class table be the upper table of member function table
                    local_table.m_upperTable = class_entry.m_subtable;
                    // create a new funcDecl entry
                    p_node.m_symTabEntry = new FuncEntry(func_type, func_name, fParam_list, "", p_node.m_line);
                    p_node.m_symTabEntry.m_subtable = local_table;
                    if (class_entry.m_subtable != null) {
                        class_entry.m_subtable.addEntry(p_node.m_symTabEntry);

                    }
                    p_node.m_symTab = local_table;
                }
            }
        }


        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }


    }

    public void visit(FuncBodyNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(MethVarNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(MainBlockNode p_node) {
        SymTab local_table = new SymTab(1, "main", p_node.m_symTab);

        p_node.m_symTabEntry = new FuncEntry("", "main", new Vector<>(), local_table, p_node.m_line);
        p_node.m_symTab.addEntry(p_node.m_symTabEntry);
        p_node.m_symTab = local_table;

        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }

    }

    public void visit(DotNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(AddOpNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(MultOpNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(ArithExprNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(ExprNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(TermNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(FactorNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(FuncOrVarNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(DataMemNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(IdNode p_node) {

    }


    public void visit(ScopeNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(FParamListNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(FParamNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(DimListNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }


    public void visit(StatBlockNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }


    public void visit(IfStatNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(WhileStatNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(ReadStatNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }


    public void visit(WriteStatNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(ReturnStatNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(FuncCallStatNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(VariableNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(AssignStatNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(TypeNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(FuncCallNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }


    public void visit(AParamsNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(BreakStatNode p_node) {
    }

    public void visit(ContiStatNode p_node) {
    }

    public void visit(DimNode p_node) {
    }

    public void visit(FloatNode p_node) {
    }

    public void visit(IndiceNode p_node) {
    }

    public void visit(InlineIfNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(NotNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    ;

    public void visit(NumNode p_node) {
    }

    ;

    public void visit(RelExprNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }

    public void visit(RelOpNode p_node) {
    }

    public void visit(SignNode p_node) {
    }

    public void visit(StringNode p_node) {
    }

    public void visit(VisibilityNode p_node) {
    }

}
