package AST;


import visitors.Visitor;

public class NumNode extends Node {

    public NumNode(String p_data, int line, String p_type) {
        super(p_data, line);
        m_type = p_type;
    }

    public NumNode(String p_data, Node p_parent) {
        super(p_data, p_parent);
    }

    public NumNode(String p_data, String p_type) {
        super(p_data, p_type);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
