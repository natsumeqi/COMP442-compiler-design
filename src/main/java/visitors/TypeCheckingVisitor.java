package visitors;

import AST.*;
import symbolTable.SymTabEntry;


/**
 * Template from astvisitor
 * Visitor to compute the type of subexpressions, assignment statements and return statements.
 * <p>
 * This applies only to nodes that are part of expressions and assignment statements i.e.
 * AddOpNode, MultOpNode, and AssignStatp_node.
 */
public class TypeCheckingVisitor extends Visitor {

    public String m_errors;

    public TypeCheckingVisitor() {
    }

    public void visit(ProgNode p_node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : p_node.getChildren())
            child.accept(this);
    }


    public void visit(AddOpNode p_node) {
        for (Node child : p_node.getChildren())
            child.accept(this);
        String leftOperandType = p_node.getChildren().get(0).getType();
        String rightOperandType = p_node.getChildren().get(1).getType();
        if (leftOperandType.equals(rightOperandType))
            p_node.setType(leftOperandType);
        else {
            p_node.setType("typeerror");
            this.m_errors += "AddOpNode type error:  "
                    + p_node.getChildren().get(0).getData()
                    + "(" + p_node.getChildren().get(0).getType() + ")"
                    + " and "
                    + p_node.getChildren().get(1).getData()
                    + "(" + p_node.getChildren().get(1).getType() + ")"
                    + "\n";
        }
    }


    public void visit(MultOpNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(ArithExprNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(TermNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(FactorNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(FuncOrVarNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
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
    }

    public void visit(IdNode p_node) {
        // get type from symbol table
        // maybe has null todo
        SymTabEntry entry = p_node.m_symTab.lookupName(p_node.m_data);
        if(entry.m_name!=null){
            p_node.m_type = entry.m_type;
        }
    }

    public void visit(NumNode p_node) {

    }

}


