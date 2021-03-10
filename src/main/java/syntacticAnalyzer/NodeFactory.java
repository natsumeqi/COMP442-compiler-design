package syntacticAnalyzer;

import AST.*;

public class NodeFactory {

    public Node makeNode(String type, String lexeme) {

        switch (type) {
            case "intnum":
            case "floatNum":
                return new NumNode(lexeme);
            case "Id":
                return new IdNode(lexeme);
            case "plus":
                return new AddOpNode("+");
            case "mult":
                return new MultOpNode("*");
            case "Prog":
                return new ProgNode();
            case "ClassList":
                return new ClassListNode();
            case "FuncDefList":
                return new FuncDefListNode();
            case "MainBlock":
                return new MainBlockNode();
            case "StatBlock":
                return new StatBlockNode();
            case "ClassDecl":
                return new ClassDeclNode();
            case "InherList":
                return new InherListNode();
            case "FuncDecl":
                return new FuncDeclNode();
            case "VarDecl":
                return new VarDeclNode();

            case "MembList":
                return  new MembListNode();
        }

        return null;
    }

}
