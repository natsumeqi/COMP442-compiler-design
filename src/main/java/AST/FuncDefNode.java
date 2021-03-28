package AST;

import visitors.Visitor;

public class FuncDefNode extends Node {

    public FuncDefNode() {
        super("");
    }

    public FuncDefNode(Node p_parent) {
        super("", p_parent);
    }

    public FuncDefNode(Node p_type, Node p_id, Node p_paramList, Node p_statBlock) {
        super("");
        this.addChild(p_type);
        this.addChild(p_id);
        this.addChild(p_paramList);
        this.addChild(p_statBlock);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}