package AST;

import visitors.Visitor;

public class MultOpNode extends Node {

    public MultOpNode(String p_data, int line) {
        super(p_data, line);
    }

    public MultOpNode(String p_data, Node p_parent) {
        super(p_data, p_parent);
    }


    public MultOpNode(String p_data, Node p_leftChild, Node p_rightChild) {
        super(p_data);
        this.addChild(p_leftChild);
        this.addChild(p_rightChild);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
