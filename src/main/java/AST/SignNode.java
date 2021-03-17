package AST;

public class SignNode extends Node {

    public SignNode(String data, int line) {
        super(data, line);
    }

    public SignNode(Node p_parent) {
        super("", p_parent);
    }
}
