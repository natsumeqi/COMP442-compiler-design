package AST;

import visitors.Visitor;

public class ExprNode extends Node {

    public ExprNode() {
        super("");
    }

    public ExprNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        for (Node child : this.getChildren())
            child.accept(visitor);
        visitor.visit(this);
    }
}
