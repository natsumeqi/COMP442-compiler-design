package visitors;

import AST.*;
import symbolTable.*;

import java.util.ArrayList;
import java.util.Vector;

public class ComputeMemSizeVisitor extends Visitor {


    public Integer m_tempVarNum = 0;


    public String getNewTempVarName() {
        m_tempVarNum++;
        return "t" + m_tempVarNum;
    }

    public int sizeOfEntry(Node p_node) {
        int size = 0;
        if (p_node.m_symTabEntry.m_type.equals("integer")) {
            size = 4;
        } else {
            if (p_node.m_symTabEntry.m_type.equals("float")) {
                size = 8;
            } else {
                SymTabEntry class_entry = p_node.m_symTab.lookupName(p_node.m_symTabEntry.m_type);
                if (class_entry.m_name != null) {
                    size = -class_entry.m_subtable.m_size;
                }
            }
        }
        // if it is an array, multiply by all dimension sizes
        VarEntry ve = (VarEntry) p_node.m_symTabEntry;
        if (ve.m_dims != null) {
            if (!ve.m_dims.isEmpty())
                for (String dim : ve.m_dims) {
                    // todo test []
                    String dim_string = dim.substring(1, dim.length() - 1);
                    if (!dim_string.isEmpty() && !dim_string.equals("0")) {
                        int dim_int = Integer.parseInt(dim_string);
                        size *= dim_int;
                    }
                }
        }
        return size;
    }

    public int sizeOfTypeNode(Node p_node) {
        int size = 0;
        if (p_node.m_type != null) {
            if (p_node.m_type.equals("integer")) {
                size = 4;
            } else {
                if (p_node.m_type.equals("float")) {
                    size = 8;
                } else {
                    SymTabEntry class_entry = p_node.m_symTab.lookupName(p_node.m_symTabEntry.m_type);
                    if (class_entry.m_name != null) {
                        size = -class_entry.m_subtable.m_size;
                    }
                }
            }
        }
        return size;
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

        // compute total size and offsets along the way
        for (SymTabEntry entry : p_node.m_symTab.m_symList) {
            entry.m_offset = p_node.m_symTab.m_size - entry.m_size;
            p_node.m_symTab.m_size -= entry.m_size;
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

                // still create table for multiple classes, but not add to global table
                String class_name_duplicate = class_name + "_duplicate";
                local_table = new SymTab(1, class_name_duplicate, p_node.m_symTab);
                p_node.m_symTabEntry = new ClassEntry(class_name_duplicate, local_table);
//                p_node.m_symTab.addEntry(p_node.m_symTabEntry);

            } else {
                p_node.m_symTab.addEntry(p_node.m_symTabEntry);
            }
            p_node.m_symTab = local_table;
        }


        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node member : p_node.getChildren()) {
            member.m_symTab = p_node.m_symTab;
            member.accept(this);
        }

        // computes size and offsets
        for (SymTabEntry entry : p_node.m_symTab.m_symList) {
            entry.m_offset = p_node.m_symTab.m_size - entry.m_size;
            p_node.m_symTab.m_size -= entry.m_size;
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


    public void visit(FuncDeclNode p_node) {
        String func_visibility = "";
        if (p_node.getLm_sibling() != null) {
            func_visibility = p_node.getLm_sibling().getData();
        }
        String func_name = p_node.getChildren().get(0).getData();
        String func_type = p_node.getChildren().get(2).getType();
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
        p_node.m_symTab.addEntry(p_node.m_symTabEntry);
    }


    public void visit(VarDeclNode p_node) {

        // aggregate information from the subtree
        // get the type from the first child node and aggregate here
        String var_type = p_node.getChildren().get(0).getType();
//        System.out.println(var_type);
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
            if (var_entry.m_name == null) {
                p_node.m_symTabEntry.m_size = this.sizeOfEntry(p_node);
                p_node.m_symTab.addEntry(p_node.m_symTabEntry);
            }

        } else {    // data member in class
            if (p_node.getParent().getClass().getSimpleName().equals("MembDeclNode")) {
                String var_visibility = p_node.getLm_sibling().m_data;
                p_node.m_symTabEntry = new VarEntry("data", var_type, var_id, var_visibility, dim_list);

                // check multiple declared identifier in class
                SymTabEntry var_entry = p_node.m_symTab.lookupName(var_id);
                if (var_entry.m_name == null) {
                    p_node.m_symTabEntry.m_size = this.sizeOfEntry(p_node);
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
        String func_type = p_node.getChildren().get(3).getType();
        String func_name = p_node.getChildren().get(1).getData();
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
//            fParam_type = returnTypeDate(param.getChildren().get(0));
            fParam_type = param.getChildren().get(0).getType();
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
            VarEntry param_entry = new VarEntry("param", fParam_type, fParam_name, dim_list);
//            System.out.println(param.m_type);
            param_entry.m_size = this.sizeOfTypeNode(param);
            local_table.addEntry(param_entry);
        }


        // add local variables into the table
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

                                }
                            }
                        }
                    }
                }
            }
        } else {    // for free functions
//            System.out.println(func_name);
            p_node.m_symTabEntry = new FuncEntry(func_type, func_name, fParam_list, local_table, p_node.m_line);

            // check multiply declared classes
            if (p_node.getParent().getParent() != null) {

                // go to global table to check
                SymTabEntry func_entry = p_node.getParent().getParent().m_symTab.lookupName(func_name);
                if (func_entry.m_name != null) {
                    // ignore  func_entry.m_type.equals(func_type)
                    p_node.m_symTab.addEntry(p_node.m_symTabEntry);
                    p_node.m_symTab = local_table;
                } else {
                    p_node.m_symTab.addEntry(p_node.m_symTabEntry);
                    p_node.m_symTab = local_table;
                }
            }
        }


        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }

        // compute total size and offsets along the way
        // this should be node on all nodes that represent
        // a scope and contain their own table
        // stack frame contains the return value at the bottom of the stack
        p_node.m_symTab.m_size = -(this.sizeOfTypeNode(p_node));
        //then is the return addess is stored on the stack frame
        p_node.m_symTab.m_size -= 4;
        for (SymTabEntry entry : p_node.m_symTab.m_symList) {
            entry.m_offset = p_node.m_symTab.m_size - entry.m_size;
            p_node.m_symTab.m_size -= entry.m_size;
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

        // compute size and offsets
        for (SymTabEntry entry : p_node.m_symTab.m_symList) {
            entry.m_offset = p_node.m_symTab.m_size - entry.m_size;
            p_node.m_symTab.m_size -= entry.m_size;
        }

    }

    public void visit(DotNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
//        if (p_node.m_type != null) {
//            p_node.m_moonVarName = this.getNewTempVarName();
//            p_node.m_symTabEntry = new VarEntry("tempvar", p_node.getType(), p_node.m_moonVarName, null);
//            p_node.m_symTabEntry.m_size = this.sizeOfEntry(p_node);
//            p_node.m_symTab.addEntry(p_node.m_symTabEntry);
//        }
        p_node.m_moonVarName = p_node.getChildren().get(0).m_moonVarName;  // todo
    }

    public void visit(AddOpNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
//        System.out.println("[compute addop node] "+p_node.m_type);
//        System.out.println("[compute addop node child:] "+p_node.getChildren().get(0).m_type);
//        System.out.println("[compute addop node child:] "+p_node.getChildren().get(1).m_type);
        if (p_node.m_type != null) {
            p_node.m_moonVarName = this.getNewTempVarName();
            p_node.m_symTabEntry = new VarEntry("tempvar", p_node.getType(), p_node.m_moonVarName, null);
            p_node.m_symTabEntry.m_size = this.sizeOfEntry(p_node);
            p_node.m_symTab.addEntry(p_node.m_symTabEntry);
        }
    }

    public void visit(MultOpNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
        if (p_node.m_type != null) {
            p_node.m_moonVarName = this.getNewTempVarName();
            p_node.m_symTabEntry = new VarEntry("tempvar", p_node.getType(), p_node.m_moonVarName, null);
            p_node.m_symTabEntry.m_size = this.sizeOfEntry(p_node);
            p_node.m_symTab.addEntry(p_node.m_symTabEntry);
        }
    }

    public void visit(FloatNode p_node) {
        p_node.m_moonVarName = this.getNewTempVarName();
        p_node.m_symTabEntry = new VarEntry("litval", p_node.getType(), p_node.m_moonVarName, null);
        p_node.m_symTabEntry.m_size = this.sizeOfEntry(p_node);
        p_node.m_symTab.addEntry(p_node.m_symTabEntry);
    }


    public void visit(NumNode p_node) {
//        System.out.println(p_node.m_moonVarName);
        p_node.m_moonVarName = this.getNewTempVarName();
        p_node.m_symTabEntry = new VarEntry("litval_" + p_node.m_data, p_node.getType(), p_node.m_moonVarName, null);// todo
//        p_node.m_symTabEntry = new VarEntry("litval", p_node.getType(), p_node.m_moonVarName + "_" + p_node.m_data, null);// todo
        p_node.m_symTabEntry.m_size = this.sizeOfEntry(p_node);
        p_node.m_symTab.addEntry(p_node.m_symTabEntry);

    }

    public void visit(FuncCallStatNode p_node) {
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
        p_node.m_moonVarName = p_node.getChildren().get(0).m_moonVarName;
    }

    public void visit(ExprNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
        p_node.m_moonVarName = p_node.getChildren().get(0).m_moonVarName;
    }

    public void visit(TermNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
        p_node.m_moonVarName = p_node.getChildren().get(0).m_moonVarName;
    }

    public void visit(FactorNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
        p_node.m_moonVarName = p_node.getChildren().get(0).m_moonVarName;
    }

    public void visit(FuncOrVarNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
        p_node.m_moonVarName = p_node.getChildren().get(0).m_moonVarName;
    }

    public void visit(DataMemNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
        p_node.m_moonVarName = p_node.getChildren().get(0).m_moonVarName;
    }

    public void visit(IdNode p_node) {
        p_node.m_moonVarName = p_node.m_data;
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
        p_node.m_moonVarName = p_node.getChildren().get(0).m_moonVarName;
    }

    public void visit(WhileStatNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
        p_node.m_moonVarName = p_node.getChildren().get(0).m_moonVarName;
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


    public void visit(VariableNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
        p_node.m_moonVarName = p_node.getChildren().get(0).m_moonVarName;
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
        System.out.println("func call type: " + p_node.m_type);
        if (p_node.m_type != null) {
            if (!p_node.m_type.equals("void")) {
                p_node.m_moonVarName = this.getNewTempVarName();
                p_node.m_symTabEntry = new VarEntry("retval", p_node.getType(), p_node.m_moonVarName, null);
                p_node.m_symTabEntry.m_size = this.sizeOfEntry(p_node);
                p_node.m_symTab.addEntry(p_node.m_symTabEntry);
            }
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


    public void visit(IndiceNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
//        System.out.println("indice type : "+p_node.getParent().m_type);
        if (!p_node.isLeaf()) {
            if (p_node.m_type != null) {
                p_node.m_moonVarName = this.getNewTempVarName();
                p_node.m_symTabEntry = new VarEntry("tempvar", p_node.getParent().m_type, p_node.m_moonVarName, null);
                p_node.m_symTabEntry.m_size = this.sizeOfEntry(p_node);
                p_node.m_symTab.addEntry(p_node.m_symTabEntry);
            }
        }
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
        p_node.m_moonVarName = p_node.getChildren().get(0).m_moonVarName;
    }


    public void visit(RelExprNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.m_symTab = p_node.m_symTab;
            child.accept(this);
        }
        if (p_node.m_type != null) {
            p_node.m_moonVarName = this.getNewTempVarName();
            p_node.m_symTabEntry = new VarEntry("tempvar", p_node.getType(), p_node.m_moonVarName, null);
            p_node.m_symTabEntry.m_size = this.sizeOfEntry(p_node);
            p_node.m_symTab.addEntry(p_node.m_symTabEntry);
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
