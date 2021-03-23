package AST;

import visitors.Visitor;

public class FactorNode extends Node {
    public FactorNode() {
        super("");
    }

    public FactorNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        for (Node child : this.getChildren())
            child.accept(visitor);
        visitor.visit(this);
    }
}
