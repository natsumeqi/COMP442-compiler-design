package AST;

import visitors.Visitor;

public class TermNode extends Node {
    public TermNode() {
        super("");
    }

    public TermNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
