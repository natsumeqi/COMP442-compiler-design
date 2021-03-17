package AST;

public class StringNode extends Node {

    public StringNode(String p_data, int line) {
        super(p_data, line);
    }

    public StringNode(String p_data, Node p_parent) {
        super(p_data, p_parent);
    }

}
