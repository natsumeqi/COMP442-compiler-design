package AST;

import visitors.Visitor;

public class StringNode extends Node {

    public StringNode(String p_data, int line) {
        super(p_data, line);
        this.m_type = "string";
    }

    public StringNode(String p_data, Node p_parent) {
        super(p_data, p_parent);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
