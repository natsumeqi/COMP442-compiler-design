package AST;

import symbolTable.SymTab;
import symbolTable.SymTabEntry;
import visitors.Visitor;

import java.util.ArrayList;
import java.util.List;


public abstract class Node {

    // Original data members of the class
    // that are not related to the introduction of
    // the visitors
    public String m_sa_name = null;
    public int m_line;

    private List<Node> m_children = new ArrayList<>();
    private Node m_parent = null;
    public String m_data = null;
    public static int m_nodeLevel = 0;
    public int m_nodeId = 0;
    public static int m_curNodeId = 0;
    private Node r_sibling = null;
    private Node lm_sibling = null;
    private Node lm_child = null;


    // The following data members have been added
    // during the implementation of the visitors
    // These could be added using a decorator pattern
    // triggered by a visitor

    // introduced by type checking visitor
    public String m_type = null;

    // introduced by the construct assignment and expression string visitor
    public String m_subtreeString = "";

    // introduced by symbol table creation visitor
    public SymTab m_symTab = null;
    public SymTabEntry m_symTabEntry = null;

    // introduced by code generation visitors
    public  String      m_localRegister      = "";
    public  String      m_leftChildRegister  = "";
    public  String      m_rightChildRegister = "";
    public  String      m_moonVarName        = "";

    public Node() {
    }

    public Node(String p_data) {
        this.setData(p_data);
        this.m_nodeId = Node.m_curNodeId;
        Node.m_curNodeId++;
        this.lm_sibling = this;
        this.m_parent = this;
    }

    public Node(String p_data, int line) {
        this.setData(p_data);
        this.m_nodeId = Node.m_curNodeId;
        Node.m_curNodeId++;
        this.lm_sibling = this;
        this.m_parent = this;
        this.m_line = line;
    }

    public Node(String p_data, Node p_parent) {
        this.setData(p_data);
        this.setParent(p_parent);
        p_parent.addChild(this);
        this.m_nodeId = Node.m_curNodeId;
        Node.m_curNodeId++;
    }


    public Node(String p_data, String p_type) {
        this.setData(p_data);
        this.setType(p_type);
        this.m_nodeId = Node.m_curNodeId;
        Node.m_curNodeId++;
    }

    public List<Node> getChildren() {
        return m_children;
    }

    public void setParent(Node p_parent) {
        this.m_parent = p_parent;
    }

    public Node getParent() {
        return m_parent;
    }


    public void addChild(Node p_child) {
        p_child.setParent(this);
        this.m_children.add(p_child);
    }

    public String getName() {
        return m_sa_name;
    }

    public void setName(String m_sa_name) {
        this.m_sa_name = m_sa_name;
    }

    public String getData() {
        return this.m_data;
    }

    public void setData(String p_data) {
        this.m_data = p_data;
    }

    public String getType() {
        return m_type;
    }

    public void setType(String m_type) {
        this.m_type = m_type;
    }

    public boolean isRoot() {
        return (this.m_parent == null);
    }

    public int getM_line() {
        return m_line;
    }

    public void setM_line(int m_line) {
        this.m_line = m_line;
    }

    public Node getLm_sibling() {
        return lm_sibling;
    }

    public Node getR_sibling() {
        return r_sibling;
    }

    public String getSubtreeString() {
        return m_subtreeString;
    }

    public void setSubtreeString(String p_data) {
        this.m_subtreeString = p_data;
    }

    public boolean isLeaf() {
        if (this.m_children.size() == 0)
            return true;
        else
            return false;
    }

    public void removeParent() {
        this.m_parent = null;
    }

    public void print() {
        System.out.println("============================================================================================================================");
        System.out.println("Node type                                                                   | data                | line      | subtreestring");
        System.out.println("============================================================================================================================");
        this.printSubtree();
        System.out.println("============================================================================================================================");

    }

    public void printSubtree() {
        for (int i = 0; i < Node.m_nodeLevel; i++)
            System.out.print("  ");

        String toprint = String.format("%-75s", this.getClass().getName());
        for (int i = 0; i < Node.m_nodeLevel; i++)
            toprint = toprint.substring(0, toprint.length() - 2);
        toprint += String.format("%-22s", (this.getData() == null || this.getData().isEmpty()) ? " | " : " | " + this.getData());
        toprint += String.format("%-25s", (this.getM_line() == 0 ? " | " : " | " + this.getM_line()));
        System.out.println(toprint);

        Node.m_nodeLevel++;
        List<Node> children = this.getChildren();
        for (int i = 0; i < children.size(); i++) {
            children.get(i).printSubtree();
        }
        Node.m_nodeLevel--;
    }


    /**
     * Inserts a new sibling node y in the list of siblings of node x
     *
     * @param y right sibling
     * @return reference to the rightmost sibling
     */
    public Node makeSiblings(Node y) {
        if (y != null) {

            // find the rightmost node in this list
            Node x_siblings = this;
            while (x_siblings.r_sibling != null) {
                x_siblings = x_siblings.r_sibling;
            }

            // join the lists
            Node y_siblings = y.lm_sibling;
            x_siblings.r_sibling = y_siblings;


            // set references to the new siblings
            y_siblings.lm_sibling = x_siblings.lm_sibling;
            y_siblings.m_parent = x_siblings.m_parent;
            while (y_siblings.r_sibling != null) {
                y_siblings = y_siblings.r_sibling;
                y_siblings.lm_sibling = x_siblings.lm_sibling;
                y_siblings.m_parent = x_siblings.m_parent;
            }
            return y_siblings;
        }
        return this;
    }


    /**
     * Adopts node y and all its siblings under the parent x.
     *
     * @param y child
     * @return reference to itself(parent)
     */
    public Node adoptChildren(Node y) {

        if (y != null) {
            if (this.lm_child != null) {
                return this.lm_child.makeSiblings(y);
            } else {

                Node y_siblings = y.lm_sibling;
                this.lm_child = y_siblings;
                while (y_siblings != null) {
                    this.addChild(y_siblings);
                    y_siblings = y_siblings.r_sibling;
                }
            }
        }
        return this;
    }


    public void accept(Visitor p_visitor) {
        p_visitor.visit(this);
    }

}