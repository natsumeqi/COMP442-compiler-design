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
        for (Node child : this.getChildren())
            child.accept(visitor);
        visitor.visit(this);
    }
}
