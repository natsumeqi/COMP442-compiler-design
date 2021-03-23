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
        for (Node child : this.getChildren())
            child.accept(visitor);
        visitor.visit(this);
    }
}
