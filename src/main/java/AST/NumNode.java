package AST;

import lexicalAnalyzer.Token;

public class NumNode extends Node {
	
	public NumNode(String p_data){
		super(p_data);
	}
	
	public NumNode(String p_data, Node p_parent){
		super(p_data, p_parent);
	}
	
	public NumNode(String p_data, String p_type){
		super(p_data, p_type);
	}



}
