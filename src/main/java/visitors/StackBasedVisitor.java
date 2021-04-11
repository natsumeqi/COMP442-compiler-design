package visitors;

import AST.*;
import symbolTable.*;

import java.util.Stack;
import java.util.Vector;

/**
 * template from lecture material: astvisitor
 * Visitor to generate moon code for simple expressions and assignment and put
 * statements. Also include code for function calls using a stack-based model.
 */

public class StackBasedVisitor extends Visitor {

    public Stack<String> m_registerPool = new Stack<>();
    public String m_moonExecCode = "";              // moon instructions part
    public String m_moonDataCode = "";              // moon data part
    public String m_mooncodeindent = new String("          ");
    public String m_mooncodeindent2=new String("     ");


    public StackBasedVisitor() {
        // create a pool of registers as a stack of Strings
        // assuming only r1, ..., r12 are available
        for (int i = 12; i >= 1; i--)
            m_registerPool.push("r" + i);
    }

    public void visit(ProgNode p_node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        for (Node child : p_node.getChildren())
            child.accept(this);
    }


    public void visit(AddOpNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        String local_register1 = this.m_registerPool.pop();
        String local_register2 = this.m_registerPool.pop();
        String local_register3 = this.m_registerPool.pop();
        // generate code
        m_moonExecCode += "% processing temporal(add): " + p_node.m_moonVarName + " := " + p_node.getChildren().get(0).m_moonVarName + " " + p_node.getData() + " " + p_node.getChildren().get(1).m_moonVarName + "\n";
        // load the values of the operands into registers
        m_moonExecCode += m_mooncodeindent + "lw " + local_register1 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset + "(r14)\n";
        m_moonExecCode += m_mooncodeindent + "lw " + local_register2 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(1).m_moonVarName).m_offset + "(r14)\n";
        if (p_node.m_data.equals("or")) {

        } else {
            if (p_node.m_data.equals("+")) {
                // add operands
                m_moonExecCode += m_mooncodeindent + "add " + local_register3 + "," + local_register1 + "," + local_register2 + "\n";
            } else {
                m_moonExecCode += m_mooncodeindent + "sub " + local_register3 + "," + local_register1 + "," + local_register2 + "\n";
            }
            // assign the result into a temporary variable (assumed to have been previously created by the symbol table generator)
            m_moonExecCode += m_mooncodeindent + "sw " + p_node.m_symTab.lookupName(p_node.m_moonVarName).m_offset + "(r14)," + local_register3 + "\n";
        }

        // deallocate the registers
        this.m_registerPool.push(local_register1);
        this.m_registerPool.push(local_register2);
        this.m_registerPool.push(local_register3);
    }

    public void visit(MultOpNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        String local_register1 = this.m_registerPool.pop();
        String local_register2 = this.m_registerPool.pop();
        String local_register3 = this.m_registerPool.pop();
        // generate code
        m_moonExecCode += "% processing temporal(mul): " + p_node.m_moonVarName + " := " + p_node.getChildren().get(0).m_moonVarName + " " + p_node.getData() + " " + p_node.getChildren().get(1).m_moonVarName + "\n";
        // load the values of the operands into registers
        m_moonExecCode += m_mooncodeindent + "lw " + local_register1 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset + "(r14)\n";
        m_moonExecCode += m_mooncodeindent + "lw " + local_register2 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(1).m_moonVarName).m_offset + "(r14)\n";
        if (p_node.m_data.equals("and")) {
//            // add operands
//            m_moonExecCode += m_mooncodeindent + "mul "  + local_register3 + "," + local_register1 + "," + local_register2 + "\n";
//            // assign the result into a temporary variable (assumed to have been previously created by the symbol table generator)
//            m_moonExecCode += m_mooncodeindent + "sw "   + p_node.m_symTab.lookupName(p_node.m_moonVarName).m_offset + "(r14)," + local_register3 + "\n";

        } else {
            if (p_node.m_data.equals("*")) {
                m_moonExecCode += m_mooncodeindent + "mul " + local_register3 + "," + local_register1 + "," + local_register2 + "\n";

            } else {
                m_moonExecCode += m_mooncodeindent + "div " + local_register3 + "," + local_register1 + "," + local_register2 + "\n";
            }
            // assign the result into a temporary variable (assumed to have been previously created by the symbol table generator)
            m_moonExecCode += m_mooncodeindent + "sw " + p_node.m_symTab.lookupName(p_node.m_moonVarName).m_offset + "(r14)," + local_register3 + "\n";

        }
        // deallocate the registers
        this.m_registerPool.push(local_register1);
        this.m_registerPool.push(local_register2);
        this.m_registerPool.push(local_register3);
    }

    public void visit(FloatNode p_node) {

    }

    public void visit(NumNode p_node) {      // float todo
        // create a local variable and allocate a register to this subcomputation
        String local_register1 = this.m_registerPool.pop();
        // generate code
        m_moonExecCode += "% processing literal: " + p_node.m_moonVarName + " := " + p_node.getData() + "\n";
        // create a value corresponding to the literal value
        m_moonExecCode += m_mooncodeindent + "addi " + local_register1 + ",r0," + p_node.getData() + "\n";
        // assign this value to a temporary variable (assumed to have been previously created by the symbol table generator)
        m_moonExecCode += m_mooncodeindent + "sw " + p_node.m_symTabEntry.m_offset + "(r14)," + local_register1 + "\n";
        // deallocate the register for the current node
        this.m_registerPool.push(local_register1);
    }


    public void visit(ClassListNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }


    public void visit(ClassDeclNode p_node) {
        for (Node member : p_node.getChildren()) {
            member.accept(this);
        }
    }

    public void visit(InherListNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(MembListNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(MembDeclNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }


    private String returnTypeDate(Node p_node) {
        if (p_node.isLeaf()) {
            return p_node.getData();
        } else {
            return p_node.getChildren().get(0).getData().toUpperCase();
        }
    }


    public void visit(FuncDeclNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }


    public void visit(VarDeclNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }


    public void visit(FuncDefListNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }


    public void visit(FuncDefNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(FuncBodyNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(MethVarNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(MainBlockNode p_node) {
        // generate moon program's entry point
        m_moonExecCode += m_mooncodeindent + "entry\n";
        // make the stack frame pointer (address stored in r14) point
        // to the top address allocated to the moon processor
        m_moonExecCode += m_mooncodeindent + "addi r14,r0,topaddr\n";
        // propagate acceptance of this visitor to all the children
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        // generate moon program's end point
        m_moonDataCode +=  "% buffer space used for console output\n";
        // buffer used by the lib.m subroutines
        m_moonDataCode += String.format("%-10s" , "buf") + "res 20\n";
        // halting point of the entire program
        m_moonExecCode += m_mooncodeindent + "hlt\n";
    }

    public void visit(DotNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(FuncCallStatNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(ArithExprNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(ExprNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(TermNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(FactorNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(FuncOrVarNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(DataMemNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(IdNode p_node) {

    }


    public void visit(ScopeNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(FParamListNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(FParamNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(DimListNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }


    public void visit(StatBlockNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }


    public void visit(IfStatNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(WhileStatNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(ReadStatNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }


    public void visit(WriteStatNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        // create a local variable and allocate a register to this subcomputation
        String localregister1 = this.m_registerPool.pop();
        //generate code
        m_moonExecCode +=  "% processing: put("  + p_node.getChildren().get(0).m_moonVarName + ")\n";
        // put the value to be printed into a register
        m_moonExecCode += m_mooncodeindent + "lw " + localregister1 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset + "(r14)\n";
        m_moonExecCode += m_mooncodeindent2 + "% put value on stack\n";
        // make the stack frame pointer point to the called function's stack frame
        m_moonExecCode += m_mooncodeindent + "addi r14,r14," + p_node.m_symTab.m_size + "\n";
        // copy the value to be printed in the called function's stack frame
        m_moonExecCode += m_mooncodeindent + "sw -8(r14)," + localregister1 + "\n";
        m_moonExecCode += m_mooncodeindent2 + "% link buffer to stack\n";
        m_moonExecCode += m_mooncodeindent + "addi " + localregister1 + ",r0, buf\n";
        m_moonExecCode += m_mooncodeindent + "sw -12(r14)," + localregister1 + "\n";
        m_moonExecCode += m_mooncodeindent2 + "% convert int to string for output\n";
        m_moonExecCode += m_mooncodeindent + "jl r15, intstr\n";
        // receive the return value in r13 and right away put it in the next called function's stack frame
        m_moonExecCode += m_mooncodeindent + "sw -8(r14),r13\n";
        m_moonExecCode += m_mooncodeindent2 + "% output to console\n";
        m_moonExecCode += m_mooncodeindent + "jl r15, putstr\n";
        // make the stack frame pointer point back to the current function's stack frame
        m_moonExecCode += m_mooncodeindent + "subi r14,r14," + p_node.m_symTab.m_size + "\n";
        //deallocate local register
        this.m_registerPool.push(localregister1);
    }

    public void visit(ReturnStatNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }


    public void visit(VariableNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }

    }

    public void visit(AssignStatNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        // allocate local registers
        String local_register1 = this.m_registerPool.pop();
        String local_register2 = this.m_registerPool.pop();
        //generate code
        m_moonExecCode +=  "% processing assignment: "  + p_node.getChildren().get(0).m_moonVarName + " := " + p_node.getChildren().get(1).m_moonVarName + "\n";
        // load the assigned value into a register
        m_moonExecCode += m_mooncodeindent + "lw "   + local_register2 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(1).m_moonVarName).m_offset + "(r14)\n";
        // assign the value to the assigned variable
        m_moonExecCode += m_mooncodeindent + "sw "   + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset + "(r14)," + local_register2 + "\n";
        // deallocate local registers
        this.m_registerPool.push(local_register1);
        this.m_registerPool.push(local_register2);
    }

    public void visit(TypeNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(FuncCallNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }


    public void visit(AParamsNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(BreakStatNode p_node) {
    }

    public void visit(ContiStatNode p_node) {
    }

    public void visit(DimNode p_node) {
    }


    public void visit(IndiceNode p_node) {
    }

    public void visit(InlineIfNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(NotNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }


    public void visit(RelExprNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(RelOpNode p_node) {
    }

    public void visit(SignNode p_node) {
    }

    public void visit(StringNode p_node) {
    }

    public void visit(VisibilityNode p_node) {
    }


}
