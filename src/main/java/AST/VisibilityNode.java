package AST;

import visitors.Visitor;

public class VisibilityNode extends Node {

    public VisibilityNode() {
        super("");
    }

    public VisibilityNode(String data, int line) {
        super(data, line);
    }

    public VisibilityNode(Node p_parent) {
        super("", p_parent);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
