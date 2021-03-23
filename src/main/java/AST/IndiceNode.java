package AST;

import visitors.Visitor;

public class IndiceNode extends Node {
    public IndiceNode() {
        super("");
    }

    public IndiceNode(Node parent) {
        super("", parent);
    }

    public void accept(Visitor visitor) {
        for (Node child : this.getChildren())
            child.accept(visitor);
        visitor.visit(this);
    }
}
