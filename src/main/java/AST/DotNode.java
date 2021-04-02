package AST;

import visitors.Visitor;

public class DotNode extends Node {
    public DotNode() {
        super("");
    }

    public DotNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
