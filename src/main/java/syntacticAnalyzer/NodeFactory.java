package syntacticAnalyzer;

import AST.*;
import lexicalAnalyzer.Token;

public class NodeFactory {

    public Node makeNode(String type, String lexeme ) {

        switch (type) {
            case "intnum":
            case "floatNum":
                return new NumNode(lexeme);
            case "id":
                return new IdNode(lexeme);
            case "plus":
                return new AddOpNode("+");
            case "mult":
                return new MultOpNode("*");
            case "prog":
                return new ProgNode();
            case "ClassList":
                return new ClassListNode();
            case "FuncDefList":
                return new FuncDefListNode();
            case "StatBlock":
                    return new StatBlockNode();
        }

        return null;
    }

}
