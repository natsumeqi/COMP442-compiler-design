package AST;

import visitors.Visitor;

public class FParamListNode extends Node {


    public FParamListNode() {
        super("");
    }

    public FParamListNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
