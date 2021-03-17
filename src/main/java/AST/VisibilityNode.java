package AST;

public class VisibilityNode extends Node {

    public VisibilityNode() {
        super("");
    }

    public VisibilityNode(String data, int line) {
        super(data, line);
    }

    public VisibilityNode(Node p_parent) {
        super("", p_parent);
    }

}
