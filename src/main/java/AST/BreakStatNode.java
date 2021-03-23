package AST;

import visitors.Visitor;

public class BreakStatNode extends Node {
    public BreakStatNode() {
        super("");
    }

    public BreakStatNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        for (Node child : this.getChildren())
            child.accept(visitor);
        visitor.visit(this);
    }
}
