package AST;

import java.util.List;


public class ClassDeclNode extends Node {
		
	public ClassDeclNode(){
		super("");
	}


	public ClassDeclNode(Node p_parent){
		super("", p_parent);
	}
	
	public ClassDeclNode(Node p_id, List<Node> p_listOfClassMemberNodes){
		super("");
		this.addChild(p_id);
		for (Node child : p_listOfClassMemberNodes)
			this.addChild(child);
	}
	

}