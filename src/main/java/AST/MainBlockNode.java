package AST;


import java.util.List;

public class MainBlockNode extends Node {
		
	public MainBlockNode(){
		super("");
	}


	public MainBlockNode(Node p_parent){
		super("", p_parent);
	}
	
	public MainBlockNode(List<Node> p_listOfStatOrVarDeclNodes){
		super("");
		for (Node child : p_listOfStatOrVarDeclNodes)
			this.addChild(child);
	}
	

}