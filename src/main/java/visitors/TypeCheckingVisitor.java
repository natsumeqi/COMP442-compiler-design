package visitors;

import AST.*;
import symbolTable.SymTabEntry;

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
    private HashSet<String> error_set = new HashSet<>();

    public TypeCheckingVisitor() {
    }

    public void visit(ProgNode p_node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : p_node.getChildren())
            child.accept(this);

        System.out.println("type checking error: \r\n" + this.m_errors);
    }

    public void visit(ClassListNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);
    }

    public void visit(ClassDeclNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);
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
    }


    public void visit(AssignStatNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);
    }

    public void visit(FuncCallStatNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);
    }

    public void visit(AddOpNode p_node) {
        System.out.println("visiting AddOp node: ");
        for (Node child : p_node.getChildren())
            child.accept(this);
        String left_operand_type = p_node.getChildren().get(0).getType();
        String right_operand_type = p_node.getChildren().get(1).getType();
        if (left_operand_type.equals(right_operand_type)) {
            p_node.setType(left_operand_type);
            System.out.println("AddOpNode type: " + left_operand_type);
        } else {
            p_node.setType("typeerror");
            String error_info = p_node.m_line + "AddOpNode";
            if (!error_set.contains(error_info)) {
                this.m_errors += "AddOpNode type error:  "
                        + p_node.getChildren().get(0).getData()
                        + "(" + p_node.getChildren().get(0).getType() + ")"
                        + " and "
                        + p_node.getChildren().get(1).getData()
                        + "(" + p_node.getChildren().get(1).getType() + ")"
                        + " in line: " + p_node.m_line
                        + "\n";
                error_set.add(p_node.m_line + "AddOpNode");
            }
        }
    }


    public void visit(MultOpNode p_node) {
        System.out.println("visiting MultOp node: " + p_node.m_line);
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        String left_operand_type = p_node.getChildren().get(0).getType();
        String right_operand_type = p_node.getChildren().get(1).getType();
        if (left_operand_type.equals(right_operand_type)) {
            p_node.setType(left_operand_type);
            System.out.println("MultOpNode type: " + left_operand_type);
        } else {
            p_node.setType("typeerror");
            // add only once in error set
            String error_info = p_node.m_line + "MultOpNode";
            if (!error_set.contains(error_info)) {
                this.m_errors += "MultOpNode type error:  "
                        + p_node.getChildren().get(0).getData()
                        + "(" + p_node.getChildren().get(0).getType() + ")"
                        + " and "
                        + p_node.getChildren().get(1).getData()
                        + "(" + p_node.getChildren().get(1).getType() + ")"
                        + " in line: " + p_node.m_line
                        + "\n";
                error_set.add(p_node.m_line + "MultOpNode");
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
            if(p_node.m_type.equals("typeerror")){
                this.m_errors += "Type error in expression:  ";
            }
        }
    }

    public void visit(TermNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        p_node.m_type = p_node.getChildren().get(0).getType();
        System.out.println("TermNode type: " + p_node.m_type);
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
    }

    public void visit(DataMemNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        p_node.m_type = p_node.getChildren().get(0).getType();
        p_node.m_data = p_node.getChildren().get(0).getData();
        System.out.println("DataMemNode type: " + p_node.m_type);
    }

    public void visit(TypeNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        if (p_node.getChildren().isEmpty()) {
            p_node.m_type = p_node.m_data;
        } else {
            p_node.m_type = p_node.getChildren().get(0).getType();
        }

    }

    public void visit(IdNode p_node) {
        // get type from symbol table
        // maybe has null todo
        System.out.println("IdNode symtab: " + p_node.m_symTab);
        System.out.println("IdNode data: " + p_node.m_data);

        // search variable in symbol table
        SymTabEntry entry = p_node.m_symTab.lookupName(p_node.m_data);
        if (entry.m_name != null) {
            p_node.m_type = entry.m_type;
            System.out.println("IdNode type: " + p_node.m_type + " for " + p_node.m_data);
        } else {
            // search variable in class symbol table


        }


    }

    public void visit(NumNode p_node) {

    }

    public void visit(DotNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        String var_class_type = p_node.getChildren().get(0).getType();
        String var_or_func_type;
        if (p_node.getChildren().get(1).getType() != null) {
            var_or_func_type = p_node.getChildren().get(1).getType();
        } else {

//            // goto global symbol table
//            SymTab global = p_node.m_symTab;
//            while(global.m_upperTable!=null){
//                global = global.m_upperTable;
//            }

            String func_name = p_node.getChildren().get(1).getData();   // get id from IdNode
            SymTabEntry class_entry = p_node.m_symTab.lookupName(var_class_type);
            if (class_entry.m_name != null) {
                SymTabEntry func_entry = class_entry.m_subtable.lookupName(func_name);
                if (func_entry.m_name != null) {
                    var_or_func_type = func_entry.m_type;
                    p_node.setType(var_or_func_type);
                }
            }

        }


//        if (var_class_type.equals(var_or_func_type))
//            p_node.setType(var_class_type);
//        else {
//            p_node.setType("typeerror");
//            this.m_errors += "DotNode type error:  "
//                    + p_node.getChildren().get(0).getData()
//                    + "(" + p_node.getChildren().get(0).getType() + ")"
//                    + " and "
//                    + p_node.getChildren().get(1).getData()
//                    + "(" + p_node.getChildren().get(1).getType() + ")"
//                    + "\n";
//        }
    }

    public void visit(FuncCallNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        // get the type of function call
        if (p_node.getChildren().get(0).m_sa_name.equals("IdNode") || p_node.getChildren().get(0).m_sa_name.equals("DataMemNode")) {
            String func_name = p_node.getChildren().get(0).getData();

            SymTabEntry func_entry = p_node.m_symTab.lookupName(func_name);
            if (func_entry.m_name != null) {
                p_node.setType(func_entry.m_type);
            }
        }

    }


}


