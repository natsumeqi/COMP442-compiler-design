package AST;

import visitors.Visitor;

public class SignNode extends Node {

    public SignNode(String data, int line) {
        super(data, line);
    }

    public SignNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
