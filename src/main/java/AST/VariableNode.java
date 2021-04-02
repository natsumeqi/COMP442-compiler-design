package AST;

import visitors.Visitor;

public class VariableNode extends Node {
    public VariableNode() {
        super("");
    }

    public VariableNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
