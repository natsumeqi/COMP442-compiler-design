package AST;

import visitors.Visitor;

public class IfStatNode extends Node {

    public IfStatNode() {
        super("");
    }

    public IfStatNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
