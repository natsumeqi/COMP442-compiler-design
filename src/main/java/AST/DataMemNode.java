package AST;

import visitors.Visitor;

public class DataMemNode extends Node {
    public DataMemNode() {
        super("");
    }

    public DataMemNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        for (Node child : this.getChildren())
            child.accept(visitor);
        visitor.visit(this);
    }
}
