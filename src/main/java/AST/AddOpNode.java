package AST;


import visitors.Visitor;

public class AddOpNode extends Node {

    public AddOpNode(String p_data, int line) {
        super(p_data, line);
    }

    public AddOpNode(String p_data, Node p_parent) {
        super(p_data, p_parent);
    }

    public AddOpNode(String p_data, String p_type) {
        super(p_data, p_type);
    }

    public AddOpNode(String p_data, Node p_leftChild, Node p_rightChild) {
        super(p_data);
        this.addChild(p_leftChild);
        this.addChild(p_rightChild);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}