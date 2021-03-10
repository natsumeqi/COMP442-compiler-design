package AST;

import lexicalAnalyzer.Token;

public class VarDeclNode extends Node {

	public VarDeclNode(){
		super("");
	}

//	@Override
//	Node makeNode(Token token) {
//		return null;
//	}

	public VarDeclNode(Node p_parent){
		super("", p_parent);
	}
	
	public VarDeclNode(Node p_type, Node p_id, Node p_dimList){
		super(""); 
		this.addChild(p_type);
		this.addChild(p_id);
		this.addChild(p_dimList);		
	}
	
	public VarDeclNode(Node p_type, Node p_id){
		super(""); 
		this.addChild(p_type);
		this.addChild(p_id);
		this.addChild(new DimListNode());		
	}
	

	
}