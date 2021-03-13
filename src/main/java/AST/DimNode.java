package AST;


public class DimNode extends Node {
	
	public DimNode(String p_data){
		super(p_data);
	}
	
	public DimNode(String p_data, Node p_parent){
		super(p_data, p_parent);
	}
	
	public DimNode(String p_data, String p_type){
		super(p_data, p_type);
	}

}
