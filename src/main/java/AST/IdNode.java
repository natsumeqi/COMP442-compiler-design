package AST;

import lexicalAnalyzer.Token;

public class IdNode extends Node {
	
	public IdNode(String p_data){
		super(p_data);
	}
	
	public IdNode(String p_data, Node p_parent){
		super(p_data, p_parent);
	}
	
	public IdNode(String p_data, String p_type){
		super(p_data, p_type);
	}

//	@Override
//	Node makeNode(Token token) {
//		return null;
//	}


}
