package syntacticAnalyzer;

import AST.*;
import lexicalAnalyzer.Token;

public class NodeFactory {

    public Node makeNode(Token token) {

        String data = token.getLexeme();
        String type = token.getType();
        switch (type) {
            case "intnum":
            case "floatNum":
                return new NumNode(data);
            case "id":
                return new IdNode(data);
            case "plus":
                return new AddOpNode(data, type);
            case "mult":
                return new MultOpNode(data, type);
        }

        return null;
    }

}
