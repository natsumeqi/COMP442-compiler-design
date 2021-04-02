package AST;

import visitors.Visitor;

public class AParamsNode extends Node {


    public AParamsNode() {
        super("");
    }

    public AParamsNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
