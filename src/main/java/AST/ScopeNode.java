package AST;

import visitors.Visitor;

public class ScopeNode extends Node {

    public ScopeNode() {
        super("");
    }

    public ScopeNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
