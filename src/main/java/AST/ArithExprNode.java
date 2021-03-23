package AST;

import visitors.Visitor;

public class ArithExprNode extends Node {

    public ArithExprNode() {
        super("");
    }

    public ArithExprNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        for (Node child : this.getChildren())
            child.accept(visitor);
        visitor.visit(this);
    }

}
