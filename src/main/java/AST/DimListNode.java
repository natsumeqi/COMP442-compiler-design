package AST;

import visitors.Visitor;

import java.util.List;


public class DimListNode extends Node {

    public DimListNode() {
        super("");
    }


    public DimListNode(Node p_parent) {
        super("", p_parent);
    }

    public DimListNode(List<Node> p_listOfDimNodes) {
        super("");
        for (Node child : p_listOfDimNodes)
            this.addChild(child);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
