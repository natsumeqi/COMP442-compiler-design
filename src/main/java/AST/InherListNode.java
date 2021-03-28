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
        visitor.visit(this);
    }

}
