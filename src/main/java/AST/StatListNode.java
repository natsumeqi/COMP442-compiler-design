package AST;

import visitors.Visitor;

public class StatListNode extends Node {

    public StatListNode(String p_data) {
        super(p_data);
    }

    public StatListNode(String p_data, Node p_parent) {
        super(p_data, p_parent);
    }

    public void accept(Visitor visitor) {
        for (Node child : this.getChildren())
            child.accept(visitor);
        visitor.visit(this);
    }
}
