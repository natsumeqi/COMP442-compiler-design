package AST;

import visitors.Visitor;

public class FuncOrVarNode extends Node {
    public FuncOrVarNode() {
        super("");
    }

    public FuncOrVarNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        for (Node child : this.getChildren())
            child.accept(visitor);
        visitor.visit(this);
    }
}
