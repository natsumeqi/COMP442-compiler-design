package AST;

import visitors.Visitor;

public class MembListNode extends Node {
    public MembListNode() {
        super("");
    }


    public MembListNode(Node parent) {
        super("", parent);

    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}