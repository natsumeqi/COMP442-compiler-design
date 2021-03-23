package AST;

import visitors.Visitor;

public class InlineIfNode extends Node {
    public InlineIfNode() {
        super("");
    }

    public InlineIfNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        for (Node child : this.getChildren())
            child.accept(visitor);
        visitor.visit(this);
    }
}
