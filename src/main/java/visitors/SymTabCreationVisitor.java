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
                    this.m_errors.append("[6.2] No definition for declared member function: \t");
                    this.m_errors.append(class_entry.m_name).append(": ").append(funcDecl_entry.m_name).append("\r\n");
                }
            }
        }


        System.out.println(p_node.m_symTab);
        System.out.println(this.m_errors);


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
              SymTabEntry class_entry =  p_node.getParent().getParent().m_symTab.lookupName(class_name);
              if (class_entry.m_name!=null){
                  m_errors.append("[8.1] Multiple undeclared class: \t").
                          append(class_name).append("\r\n");
              }else{
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
     *
     * @param p_node of type
     * @return data of type
     */
    private String returnTypeDate(Node p_node) {
        if (p_node.getChildren().size() == 0) {
            return p_node.getData();
        } else {
            return p_node.getChildren().get(0).getData();
        }
    }

    /**
     * create function entry
     *
     * @param p_node of function declaration
     */
    public void visit(FuncDeclNode p_node) {
//        System.out.println("visiting funcDeclNode");
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
//            fParam_name = param.getChildren().get(1).getData();

            String fParam;
            StringBuilder dim_string = new StringBuilder();
            for (Node dim : param.getChildren().get(2).getChildren()) {
                // parameter dimension
                dim_string.append(dim.getData());
            }
            fParam = fParam_type + dim_string;
            fParam_list.add(fParam);
        }

        p_node.m_symTabEntry = new FuncEntry(func_type, func_name, fParam_list, func_visibility);


        // check if function header is matched with function declaration in class
//        if (!p_node.getChildren().get(0).getChildren().isEmpty()) {


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

        if (p_node.getParent().getClass().getSimpleName().equals("MethVarNode")) {
            p_node.m_symTabEntry = new VarEntry("local", var_type, var_id, dim_list);

            // check multiple declared identifier in function
            SymTabEntry var_entry =  p_node.m_symTab.lookupName(var_id);
            if(var_entry.m_name!=null){
                m_errors.append("[8.4] Multiple declared identifier in function: \t").append("'").
                        append(var_id).append("' in the function ").append(p_node.m_symTab.m_name).append("\r\n");
            }else{
                p_node.m_symTab.addEntry(p_node.m_symTabEntry);
            }

        } else {
            if (p_node.getParent().getClass().getSimpleName().equals("MembDeclNode")) {
                String var_visibility = p_node.getLm_sibling().m_data;
                p_node.m_symTabEntry = new VarEntry("data", var_type, var_id, var_visibility, dim_list);

                // check multiple declared identifier in class
                SymTabEntry var_entry =  p_node.m_symTab.lookupName(var_id);
                if(var_entry.m_name!=null){
                    m_errors.append("[8.3] Multiple declared identifier in class: \t").append("'").
                            append(var_id).append("' in the class ").append(p_node.m_symTab.m_name).append("\r\n");
                }else{
                    p_node.m_symTab.addEntry(p_node.m_symTabEntry);
                }
            }
        }
    }


    public void visit(FuncDefListNode p_node) {
//        System.out.println("Visiting FuncDefListNode");
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }


    public void visit(FuncDefNode p_node) {
        String func_type = returnTypeDate(p_node.getChildren().get(3));
        String func_name = p_node.getChildren().get(1).getData();
        String fParam_type, fParam_name;
        String func_scope = "";
        Vector<String> fParam_list = new Vector<>();
        SymTab local_table;

        // when it is a member function, add scope into the name
        if (!p_node.getChildren().get(0).getChildren().isEmpty()) {
            func_scope = p_node.getChildren().get(0).getChildren().get(0).getData();
//            System.out.println("func_scope: "+func_scope);
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
//            System.out.println("para var in function table");
            local_table.addEntry(new VarEntry("param", fParam_type, fParam_name, dim_list));
        }


        // add local variables into the table


        boolean matched = false;
        // for member function, check if function header is matched with function declaration in class
        if (!p_node.getChildren().get(0).getChildren().isEmpty()) {
            if (p_node.getParent().getParent() != null) {
                SymTabEntry class_entry = p_node.getParent().getParent().m_symTab.lookupName(func_scope);
                if (class_entry.m_subtable != null) {

                    SymTabEntry func_decl = class_entry.m_subtable.lookupName(func_name);
                    if (func_decl.m_name != null) {
//                        System.out.println(func_decl.m_name);
//                        System.out.println(func_name);
//                        System.out.println(func_decl.m_type);
//                        System.out.println(func_type);
                        if (func_decl.m_name.equals(func_name) && func_decl.m_type.equals(func_type)) {
//                            System.out.println("ici");
                            if (func_decl.getClass().getSimpleName().equals("FuncEntry")) {

                                if ( func_decl.m_fParam.toString().equals(fParam_list.toString())) {
                                    System.out.println("matched member function");
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

            p_node.m_symTabEntry = new FuncEntry(func_type, func_name, fParam_list, local_table);

            // check multiply declared classes
            if (p_node.getParent().getParent() != null) {
                SymTabEntry func_entry =  p_node.getParent().getParent().m_symTab.lookupName(func_name);
                if (func_entry.m_name!=null){

                    if(func_entry.m_type.equals(func_type) && func_entry.m_fParam.toString().equals(fParam_list.toString())) {

                        m_errors.append("[8.2] Multiple defined free function: \t").
                                append(func_name).append("\r\n");
                    }
                }else{
                    p_node.m_symTab.addEntry(p_node.m_symTabEntry);
                    p_node.m_symTab = local_table;

                }
            }
            matched = true;
        }


        if (!matched) {
            m_errors.append("[6.1] Definition provided for undeclared member function: \t").
                    append(func_scope).append(":").append(func_name).append("\r\n");
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
        p_node.m_symTabEntry = new FuncEntry("", "main", new Vector<>(), local_table);
        p_node.m_symTab.addEntry(p_node.m_symTabEntry);
        p_node.m_symTab = local_table;

        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
    }


}