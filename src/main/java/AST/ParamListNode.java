package AST;

import visitors.Visitor;

import java.util.List;


public class ParamListNode extends Node {

    public ParamListNode() {
        super("");
    }

    public ParamListNode(Node p_parent) {
        super("", p_parent);
    }

    public ParamListNode(List<Node> p_listOfParamNodes) {
        super("");
        for (Node child : p_listOfParamNodes)
            this.addChild(child);
    }

    public void accept(Visitor visitor) {
        for (Node child : this.getChildren())
            child.accept(visitor);
        visitor.visit(this);
    }
}
