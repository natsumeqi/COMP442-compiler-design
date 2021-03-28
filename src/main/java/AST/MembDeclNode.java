package AST;

import visitors.Visitor;

public class MembDeclNode extends Node {
    public MembDeclNode() {
        super("");
    }

    public MembDeclNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
