package AST;

import visitors.Visitor;

public class FuncCallStatNode extends Node {
    public FuncCallStatNode() {
        super("");
    }

    public FuncCallStatNode(Node p_parent) {
        super("", p_parent);
    }


    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
