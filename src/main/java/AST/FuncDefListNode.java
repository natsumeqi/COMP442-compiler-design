package AST;
import lexicalAnalyzer.Token;

import java.util.List;


public class FuncDefListNode extends Node {
	
	public FuncDefListNode(){
		super("");
	}

//	@Override
//	Node makeNode(Token token) {
//		return null;
//	}

	public FuncDefListNode(Node p_parent){
		super("", p_parent);
	}
	
	public FuncDefListNode(List<Node> p_listOfFuncDefNodes){
		super("");
		for (Node child : p_listOfFuncDefNodes)
			this.addChild(child);
	}
	

}