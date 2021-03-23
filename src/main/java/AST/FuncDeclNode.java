package AST;


import visitors.Visitor;

public class FuncDeclNode extends Node {
    public FuncDeclNode() {
        super("");
    }

    public FuncDeclNode(Node parent) {
        super("", parent);
    }

    public void accept(Visitor visitor) {
        for (Node child : this.getChildren())
            child.accept(visitor);
        visitor.visit(this);
    }
}