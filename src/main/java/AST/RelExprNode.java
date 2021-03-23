package AST;

import visitors.Visitor;

public class RelExprNode extends Node {
    public RelExprNode() {
        super("");
    }

    public RelExprNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        for (Node child : this.getChildren())
            child.accept(visitor);
        visitor.visit(this);
    }
}
