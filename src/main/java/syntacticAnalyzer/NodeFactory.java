package syntacticAnalyzer;

import AST.IdNode;
import AST.Node;
import AST.NumNode;
import lexicalAnalyzer.Token;

public class NodeFactory {

    public static Node makeNode(Token token) {

        switch (token.getType()) {
            case "intnum":
            case "floatNum":
                return new NumNode(token.getLexeme());
            case "id":
                return new IdNode(token.getLexeme());
        }

        return null;
    }

}
