package AST;

import visitors.Visitor;

public class MethVarNode extends Node {
    public MethVarNode() {
        super("");
    }

    public MethVarNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
