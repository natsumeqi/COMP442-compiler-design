package AST;

public class SignNode extends Node {

    public SignNode(String data) {
        super(data);
    }

    public SignNode(Node p_parent) {
        super("", p_parent);
    }
}
