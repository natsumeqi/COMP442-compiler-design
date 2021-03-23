package AST;

import visitors.Visitor;

public class InherListNode extends Node {

    public InherListNode() {
        super("");
    }

    public InherListNode(Node parent) {
        super("", parent);
    }

    public void accept(Visitor visitor) {
        for (Node child : this.getChildren())
            child.accept(visitor);
        visitor.visit(this);
    }

}
