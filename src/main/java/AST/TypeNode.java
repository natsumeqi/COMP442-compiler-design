package AST;

import visitors.Visitor;

public class TypeNode extends Node {

    public TypeNode(String p_data) {
        super(p_data);
    }

    public TypeNode(String p_data, int line) {
        super(p_data, line);
    }

    public TypeNode(String p_data, Node p_parent) {
        super(p_data, p_parent);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
