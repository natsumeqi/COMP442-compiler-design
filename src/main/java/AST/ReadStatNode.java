package AST;

import visitors.Visitor;

public class ReadStatNode extends Node {
    public ReadStatNode() {
        super("");
    }

    public ReadStatNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        for (Node child : this.getChildren())
            child.accept(visitor);
        visitor.visit(this);
    }
}
