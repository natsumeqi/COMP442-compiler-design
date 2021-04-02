package AST;

import visitors.Visitor;

public class FParamNode extends Node {

    public FParamNode() {
        super("");
    }


    public FParamNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
