package AST;

import visitors.Visitor;

public class WriteStatNode extends Node {
    public WriteStatNode() {
        super("");
    }

    public WriteStatNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
