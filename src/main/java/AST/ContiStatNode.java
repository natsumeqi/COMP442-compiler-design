package AST;

import visitors.Visitor;

public class ContiStatNode extends Node {

    public ContiStatNode() {
        super("");
    }

    public ContiStatNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        for (Node child : this.getChildren())
            child.accept(visitor);
        visitor.visit(this);
    }
}
