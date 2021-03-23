package visitors;

import AST.*;
import symbolTable.VarEntry;

import java.util.Vector;

public class SymTabCreationVisitor extends Visitor {


    public void visit(VarDeclNode node) {
        // aggregate information from the subtree
        // get the type from the first child node and aggregate here
        String var_type = node.getChildren().get(0).getData() + ':';
        // get the id from the second child node and aggregate here
        String var_id = node.getChildren().get(1).getData() + ':';
        // loop over the list of dimension nodes and aggregate here
        Vector<Integer> dim_list = new Vector<>();
        for (Node dim : node.getChildren().get(2).getChildren()){
            Integer dim_val = Integer.parseInt(dim.getData());
            dim_list.add(dim_val);
        }
        // create the symbol table entry for this variable
        // it will be picked-up by another node above later
        node.m_symTabEntry = new VarEntry("localVar", var_type, var_id, dim_list);
    }
}
