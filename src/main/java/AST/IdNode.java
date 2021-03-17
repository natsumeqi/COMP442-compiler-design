package AST;


public class IdNode extends Node {
	
	public IdNode(String p_data, int line){
		super(p_data, line);
	}
	
	public IdNode(String p_data, Node p_parent){
		super(p_data, p_parent);
	}
	
	public IdNode(String p_data, String p_type){
		super(p_data, p_type);
	}



}
