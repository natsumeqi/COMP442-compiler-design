package AST;

public class RelOpNode extends Node {
    public RelOpNode(String p_data, int line) {
        super(p_data, line);
    }

    public RelOpNode(String p_data, Node p_parent) {
        super(p_data, p_parent);
    }
}
