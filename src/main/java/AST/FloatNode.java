package AST;

import visitors.Visitor;

public class FloatNode extends Node {

    public FloatNode(String p_data, int line) {
        super(p_data, line);
        this.m_type = "float";
    }

    public FloatNode(String p_data, Node p_parent) {
        super(p_data, p_parent);
    }

    public FloatNode(String p_data, String p_type) {
        super(p_data, p_type);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}