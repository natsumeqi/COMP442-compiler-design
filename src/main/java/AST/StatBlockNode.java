package AST;

import visitors.Visitor;

import java.util.List;

public class StatBlockNode extends Node {

    public StatBlockNode() {
        super("");
    }

    public StatBlockNode(Node p_parent) {
        super("", p_parent);
    }

    public StatBlockNode(List<Node> p_listOfStatOrVarDeclNodes) {
        super("");
        for (Node child : p_listOfStatOrVarDeclNodes)
            this.addChild(child);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
