package AST;



public class AddOpNode extends Node {
	
	public AddOpNode(String p_data){
		super(p_data);
	}
	
	public AddOpNode(String p_data, Node p_parent){
		super(p_data, p_parent);
	}

	public AddOpNode(String p_data, String p_name) {
		super(p_data, p_name);
	}


	public AddOpNode(String p_data, Node p_leftChild, Node p_rightChild){
		super(p_data); 
		this.addChild(p_leftChild);
		this.addChild(p_rightChild);
	}


}