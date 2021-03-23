package AST;

import visitors.Visitor;

public class WhileStatNode extends Node {
    public WhileStatNode() {
        super("");
    }

    public WhileStatNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        for (Node child : this.getChildren())
            child.accept(visitor);
        visitor.visit(this);
    }
}
