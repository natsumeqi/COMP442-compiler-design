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
        visitor.visit(this);
    }
}
