package syntacticAnalyzer;

import AST.*;

public class NodeFactory {


    public Node makeNode(String type, String lexeme, int line) {

        switch (type) {
            case"Null":
                return new NullNode() ;
            case "Num":
                return new NumNode(lexeme, line);
            case "Float":
                return new FloatNode(lexeme, line);
            case "Id":
                return new IdNode(lexeme, line);
            case "String":
                return new StringNode(lexeme, line);
            case "AddOp":
                return new AddOpNode(lexeme, line);
            case "MultOp":
                return new MultOpNode(lexeme, line);
            case "RelOp":
                return new RelOpNode(lexeme, line);
            case "Sign":
                return new SignNode(lexeme, line);
            case "Type":
                if (lexeme.equals("Type"))
                    return new TypeNode("");
                else
                    return new TypeNode(lexeme, line);
            case "Dim":
                return new DimNode("[" + lexeme + "]");
            case "DimNull":
                return new DimNode("[]");
            case "Visibility":
                return new VisibilityNode(lexeme, line);
            case "VisibilityDefault":
                return new VisibilityNode();
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
                return new MembListNode();
            case "FParam":
                return new FParamNode();
            case "FParamList":
                return new FParamListNode();
            case "DimList":
                return new DimListNode();
            case "FuncDef":
                return new FuncDefNode();
            case "Scope":
                return new ScopeNode();
            case "MethVar":
                return new MethVarNode();
            case "FuncBody":
                return new FuncBodyNode();
            case "IfStat":
                return new IfStatNode();
            case "WhileStat":
                return new WhileStatNode();
            case "ReadStat":
                return new ReadStatNode();
            case "WriteStat":
                return new WriteStatNode();
            case "ReturnStat":
                return new ReturnStatNode();
            case "BreakStat":
                return new BreakStatNode();
            case "ContiStat":
                return new ContiStatNode();
            case "FuncCallStat":
                return new FuncCallStatNode();
            case "Expr":
                return new ExprNode();
            case "Variable":
                return new VariableNode();
            case "AssignStat":
                return new AssignStatNode();
            case "ArithExpr":
                return new ArithExprNode();
            case "RelExpr":
                return new RelExprNode();
            case "Term":
                return new TermNode();
            case "Not":
                return new NotNode();
            case "InlineIf":
                return new InlineIfNode();
            case "FuncOrVar":
                return new FuncOrVarNode();
            case "Factor":
                return new FactorNode();
            case "FuncCall":
                return new FuncCallNode();
            case "AParams":
                return new AParamsNode();
            case "Indice":
                return new IndiceNode();
            case "DataMem":
                return new DataMemNode();
            case "MembDecl":
                return new MembDeclNode();
            case "Dot":
                return new DotNode();

        }

        return null;
    }

}
