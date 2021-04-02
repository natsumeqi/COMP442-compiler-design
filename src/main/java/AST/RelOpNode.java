package AST;

import visitors.Visitor;

public class RelOpNode extends Node {
    public RelOpNode(String p_data, int line) {
        super(p_data, line);
    }

    public RelOpNode(String p_data, Node p_parent) {
        super(p_data, p_parent);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
