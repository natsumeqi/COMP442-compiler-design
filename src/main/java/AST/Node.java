package AST;

import java.util.ArrayList;
import java.util.List;


public abstract class Node {

    public String m_sa_name = null;
    private List<Node> m_children = new ArrayList<Node>();
    private Node m_parent = null;
    public String m_data = null;
    public static int m_nodelevel = 0;
    public int m_nodeId = 0;
    public static int m_curNodeId = 0;
    private Node r_sibling = null;
    private Node lm_sibling = null;
    private Node lm_child = null;

    public String m_type = null;

    public Node() {

    }

    public Node(String p_data) {
        this.setData(p_data);
        this.m_nodeId = Node.m_curNodeId;
        Node.m_curNodeId++;
        this.lm_sibling = this;
        this.m_parent = this;
    }

    public Node(String p_data, Node p_parent) {
        this.setData(p_data);
        this.setParent(p_parent);
        p_parent.addChild(this);
        this.m_nodeId = Node.m_curNodeId;
        Node.m_curNodeId++;
    }


    public Node(String p_data, String p_name) {
        this.setData(p_data);
        this.setName(p_name);
        this.m_nodeId = Node.m_curNodeId;
        Node.m_curNodeId++;
        this.lm_sibling = this;
        this.m_parent = this;
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
        System.out.println("add child ok");
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
        System.out.println("=====================================================================");
        System.out.println("Node type                 | data      | type      | subtreestring");
        System.out.println("=====================================================================");
        this.printSubtree();
        System.out.println("=====================================================================");

    }

    public void printSubtree() {
        for (int i = 0; i < Node.m_nodelevel; i++)
            System.out.print("  ");

        String toprint = String.format("%-25s", this.getClass().getName());
        for (int i = 0; i < Node.m_nodelevel; i++)
            toprint = toprint.substring(0, toprint.length() - 2);
        toprint += String.format("%-12s", (this.getData() == null || this.getData().isEmpty()) ? " | " : " | " + this.getData());
        toprint += String.format("%-20s", (this.getType() == null || this.getType().isEmpty()) ? " | " : " | " + this.getType());
        System.out.println(toprint);

        Node.m_nodelevel++;
        List<Node> children = this.getChildren();
        for (int i = 0; i < children.size(); i++) {
            children.get(i).printSubtree();
        }
        Node.m_nodelevel--;
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
            System.out.println(x_siblings.getName());
            System.out.println(x_siblings.lm_sibling);

            y_siblings.lm_sibling = x_siblings.lm_sibling;
            y_siblings.m_parent = x_siblings.m_parent;
            System.out.println(y_siblings);
//            System.out.println("[y_siblings1] "+y_siblings.toString());
            while (y_siblings.r_sibling != null) {
                y_siblings = y_siblings.r_sibling;
                y_siblings.lm_sibling = x_siblings.lm_sibling;
                y_siblings.m_parent = x_siblings.m_parent;
            }
//            System.out.println("[y_siblings] "+y_siblings.toString());
            System.out.println("+++++");
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
//                System.out.println("[y]" + y.getName());
//                System.out.println("[y_leftmost_sibling]" + y.lm_sibling.getName());
//                System.out.println("[this_leftmost_child]" + this.lm_child.getName());
//                System.out.println("[y_siblings]" + y_siblings.getName());
                while (y_siblings != null) {
//                    System.out.println("[y_siblings]" + y_siblings.getName());
                    this.addChild(y_siblings);
                    y_siblings = y_siblings.r_sibling;
                }

            }
        }
        return this;
    }


}