package AST;

import visitors.Visitor;

public class FuncBodyNode extends Node {

    public FuncBodyNode() {
        super("");
    }

    public FuncBodyNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
