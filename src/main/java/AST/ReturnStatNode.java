package AST;


public class ReturnStatNode extends Node {
	
	public ReturnStatNode(){
		super("");
	}


	public ReturnStatNode(Node p_child){
		super(""); 
		this.addChild(p_child);
	}
	

}
