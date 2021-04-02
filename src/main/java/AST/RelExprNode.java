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
        visitor.visit(this);
    }
}
