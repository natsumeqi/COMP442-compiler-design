package AST;

import visitors.Visitor;

public class NotNode extends Node {
    public NotNode() {
        super("");
    }

    public NotNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
