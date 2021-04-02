package AST;

import visitors.Visitor;

public class ReturnStatNode extends Node {

    public ReturnStatNode() {
        super("");
    }


    public ReturnStatNode(Node p_child) {
        super("");
        this.addChild(p_child);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
