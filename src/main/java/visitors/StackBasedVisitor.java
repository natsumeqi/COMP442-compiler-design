package visitors;

import AST.*;
import symbolTable.SymTabEntry;

import java.util.ArrayList;
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
    public String m_mooncodeindent = "          ";
    public String m_mooncodeindent2 = "     ";


    public StackBasedVisitor() {
        // create a pool of registers as a stack of Strings
        // assuming only r1, ..., r12 are available
        for (int i = 12; i >= 1; i--)
            m_registerPool.push("r" + i);
    }

    public int sizeOfTypeNode(Node p_node) {
        int size = 0;
        if (p_node.m_type != null) {
            if (p_node.m_type.equals("integer")) {
                size = 4;
            } else {
                if (p_node.m_type.equals("float")) {
                    size = 8;
                } else {
//                    System.out.println( "symtabectry: "+p_node.m_symTabEntry);
//
//                    System.out.println("m_symTab: "+p_node.m_symTab);
                    SymTabEntry class_entry = p_node.m_symTab.lookupName(p_node.m_type);
                    if (class_entry.m_name != null) {

                        size = -class_entry.m_subtable.m_size;
                    }
                }
            }
        }
        return size;
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
        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset + "(r14)\n";
        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register2 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(1).m_moonVarName).m_offset + "(r14)\n";
        if (p_node.m_data.equals("or")) {

        } else {
            if (p_node.m_data.equals("+")) {
                // add operands
                m_moonExecCode += m_mooncodeindent + "add\t" + local_register3 + "," + local_register1 + "," + local_register2 + "\n";
            } else {
                m_moonExecCode += m_mooncodeindent + "sub\t" + local_register3 + "," + local_register1 + "," + local_register2 + "\n";
            }
            // assign the result into a temporary variable (assumed to have been previously created by the symbol table generator)
            m_moonExecCode += m_mooncodeindent + "sw\t" + p_node.m_symTab.lookupName(p_node.m_moonVarName).m_offset + "(r14)," + local_register3 + "\n";
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
        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset + "(r14)\n";
        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register2 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(1).m_moonVarName).m_offset + "(r14)\n";
        if (p_node.m_data.equals("and")) {
//            // add operands
//            m_moonExecCode += m_mooncodeindent + "mul "  + local_register3 + "," + local_register1 + "," + local_register2 + "\n";
//            // assign the result into a temporary variable (assumed to have been previously created by the symbol table generator)
//            m_moonExecCode += m_mooncodeindent + "sw "   + p_node.m_symTab.lookupName(p_node.m_moonVarName).m_offset + "(r14)," + local_register3 + "\n";

        } else {
            if (p_node.m_data.equals("*")) {
                m_moonExecCode += m_mooncodeindent + "mul\t" + local_register3 + "," + local_register1 + "," + local_register2 + "\n";

            } else {
                m_moonExecCode += m_mooncodeindent + "div\t" + local_register3 + "," + local_register1 + "," + local_register2 + "\n";
            }
            // assign the result into a temporary variable (assumed to have been previously created by the symbol table generator)
            m_moonExecCode += m_mooncodeindent + "sw\t" + p_node.m_symTab.lookupName(p_node.m_moonVarName).m_offset + "(r14)," + local_register3 + "\n";

        }
        // deallocate the registers
        this.m_registerPool.push(local_register1);
        this.m_registerPool.push(local_register2);
        this.m_registerPool.push(local_register3);
    }


    public void visit(RelExprNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        String local_register1 = this.m_registerPool.pop();
        String local_register2 = this.m_registerPool.pop();
        String local_register3 = this.m_registerPool.pop();
        // generate code
        m_moonExecCode += "% processing temporal(rel): " + p_node.m_moonVarName + " := (" + p_node.getChildren().get(0).m_moonVarName + " " + p_node.getChildren().get(1).getData() + " " + p_node.getChildren().get(2).m_moonVarName + ")\n";
        // load the values of the operands into registers
        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset + "(r14)\n";
        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register2 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(2).m_moonVarName).m_offset + "(r14)\n";


        String rel_op_moon = "";
        String rel_op = p_node.getChildren().get(1).m_data;
        switch (rel_op) {
            case "==":
                rel_op_moon = "ceq";
                break;
            case "<>":
                rel_op_moon = "cne";
                break;
            case "<":
                rel_op_moon = "clt";
                break;
            case ">":
                rel_op_moon = "cgt";
                break;
            case "<=":
                rel_op_moon = "cle";
                break;
            case ">=":
                rel_op_moon = "cge";
                break;
        }

        // relational operands
        m_moonExecCode += m_mooncodeindent + rel_op_moon + "\t" + local_register3 + "," + local_register1 + "," + local_register2 + "\n";
        // assign the result into a temporary variable (assumed to have been previously created by the symbol table generator)
        m_moonExecCode += m_mooncodeindent + "sw\t" + p_node.m_symTab.lookupName(p_node.m_moonVarName).m_offset + "(r14)," + local_register3 + "\n";

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
        m_moonExecCode += m_mooncodeindent + "addi\t" + local_register1 + ",r0," + p_node.getData() + "\n";
        // assign this value to a temporary variable (assumed to have been previously created by the symbol table generator)
        m_moonExecCode += m_mooncodeindent + "sw\t" + p_node.m_symTabEntry.m_offset + "(r14)," + local_register1 + "\n";
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
        m_moonExecCode += "% processing function definition: " + p_node.getChildren().get(1).m_moonVarName + "\n";
        //create the tag to jump onto
        // and copy the jumping-back address value in the called function's stack frame
        m_moonExecCode += String.format("%-10s", p_node.getChildren().get(1).m_moonVarName) + "sw -4(r14),r15\n";
        //generate the code for the function body
        for (Node child : p_node.getChildren())
            child.accept(this);
        // copy back the jumping-back address into r15
        m_moonExecCode += m_mooncodeindent + "lw r15,-4(r14)\n";
        // jump back to the calling function
        m_moonExecCode += m_mooncodeindent + "jr r15\n";
        m_moonExecCode += "% end function " + p_node.getChildren().get(1).m_moonVarName + " definition";
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
        m_moonExecCode += "% set stack pointer\n";
        m_moonExecCode += m_mooncodeindent + "addi\tr14,r0,topaddr\n";
        // propagate acceptance of this visitor to all the children
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        // generate moon program's end point
        m_moonDataCode += "% buffer space used for console output\n";
        // buffer used by the lib.m subroutines
        m_moonDataCode += String.format("%-10s", "buf") + "res 20\n";
        m_moonDataCode += String.format("%-10s", "tempaddr") + "res 20\n";
        // halting point of the entire program
        m_moonExecCode += m_mooncodeindent + "hlt\n\n";
        m_moonExecCode += "% -----------------------------------------------------------------------\n";
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
        // update after computing offsets
        p_node.m_moonVarName = p_node.getChildren().get(0).m_moonVarName;
    }

    public void visit(ExprNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        // update after computing offsets
        p_node.m_moonVarName = p_node.getChildren().get(0).m_moonVarName;
    }

    public void visit(TermNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        // update after computing offsets
        p_node.m_moonVarName = p_node.getChildren().get(0).m_moonVarName;
    }

    public void visit(FactorNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        // update after computing offsets
        p_node.m_moonVarName = p_node.getChildren().get(0).m_moonVarName;
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
        System.out.println(p_node.m_data);
        System.out.println(p_node.m_type);
        int size_of_type = sizeOfTypeNode(p_node);

        System.out.println(p_node.m_moonVarName);
        System.out.println(p_node.m_type);
        System.out.println(p_node.m_data);
        SymTabEntry entry = p_node.m_symTab.lookupName(p_node.m_data);
        System.out.println(entry.m_dims);
        Vector<String> dims_string = new Vector<>();
        if (entry.m_name != null) {
            dims_string = entry.m_dims;
        }

        ArrayList<Integer> dims_col = new ArrayList<>();
        if (dims_string.size() > 1) {

            for (int i = 1; i < dims_string.size(); i++) {
                int dim_col = 1;
                for (int j = i; j < dims_string.size(); j++) {
//                    System.out.println("dims_string: "+dims_string.get(j));
                    String dim_integer = dims_string.get(j).substring(1, dims_string.get(j).indexOf("]"));
//                    System.out.println("dim_integer: "+dim_integer);
                    dim_col *= Integer.parseInt(dim_integer);
                }
                dims_col.add(dim_col);
            }

        }
        dims_col.add(1);
        dims_col.forEach(System.out::println);
        // if it is a array with dimension, calculate offsets

        if (!p_node.getChildren().get(1).isLeaf()) {
            String local_register1 = this.m_registerPool.pop();
            String local_register2 = this.m_registerPool.pop();
            String local_register3 = this.m_registerPool.pop();

            Node first_index_node = p_node.getChildren().get(1).getChildren().get(0);
            for (int i = 0; i < p_node.getChildren().get(1).getChildren().size(); i++) {

                Node index_expr_node = p_node.getChildren().get(1).getChildren().get(i);
                // generate code
                m_moonExecCode += "% processing offsets(mul): " + p_node.m_moonVarName + " := " + index_expr_node.m_moonVarName + " * " + dims_col.get(i) + "\n";
                // load the values of the operands into registers
                m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + "," + p_node.m_symTab.lookupName(index_expr_node.m_moonVarName).m_offset + "(r14)\n";
                m_moonExecCode += m_mooncodeindent + "muli\t" + local_register2 + "," + local_register1 + "," + dims_col.get(i) + "\n";

                // assign the result back into the first literal variable
                m_moonExecCode += m_mooncodeindent + "sw\t" + p_node.m_symTab.lookupName(index_expr_node.m_moonVarName).m_offset + "(r14)," + local_register2 + "\n";

                if (i >= 1) {
                    m_moonExecCode += "% processing offsets(add): " + p_node.m_moonVarName + " := " + index_expr_node.m_moonVarName + " * " + dims_col.get(i) + "\n";
                    m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + "," + p_node.m_symTab.lookupName(index_expr_node.m_moonVarName).m_offset + "(r14)\n";
                    m_moonExecCode += m_mooncodeindent + "lw\t" + local_register2 + "," + p_node.m_symTab.lookupName(first_index_node.m_moonVarName).m_offset + "(r14)\n";
                    m_moonExecCode += m_mooncodeindent + "add\t" + local_register3 + "," + local_register1 + "," + local_register2 + "\n";
                    m_moonExecCode += m_mooncodeindent + "sw\t" + p_node.m_symTab.lookupName(first_index_node.m_moonVarName).m_offset + "(r14)," + local_register3 + "\n";
                }
            }

            // multiply size of type
            m_moonExecCode += "% processing offsets(mul size): " + p_node.m_moonVarName + " := " + p_node.m_moonVarName + " * " + size_of_type + "\n";
            // load the values of the operands into registers
            m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + "," + p_node.m_symTab.lookupName(first_index_node.m_moonVarName).m_offset + "(r14)\n";
            m_moonExecCode += m_mooncodeindent + "muli\t" + local_register2 + "," + local_register1 + "," + size_of_type + "\n";
            // assign the result back into the first literal variable
            m_moonExecCode += m_mooncodeindent + "sw\t" + p_node.m_symTab.lookupName(first_index_node.m_moonVarName).m_offset + "(r14)," + local_register2 + "\n";


//            // add variable offset
//            m_moonExecCode += "% processing offsets(add base offset): " + p_node.m_moonVarName + " := " + p_node.m_moonVarName + " offset + indice offset\n";
////            m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + "," + p_node.m_symTab.lookupName(p_node.m_moonVarName).m_offset + "\n";
//            m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + "," + p_node.m_symTab.lookupName(first_index_node.m_moonVarName).m_offset + "(r14)\n";
//            m_moonExecCode += m_mooncodeindent + "addi\t" + local_register2 + "," + local_register1 + "," + p_node.m_symTab.lookupName(p_node.m_moonVarName).m_offset + "\n";
//            m_moonExecCode += m_mooncodeindent + "sw\t" + p_node.m_symTab.lookupName(p_node.m_moonVarName).m_offset + "(r14)," + local_register2 + "\n";


//            m_moonExecCode += m_mooncodeindent + "addi\t" + local_register2 + ",r0," + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset + "\n";
            m_moonExecCode += m_mooncodeindent + "lw\t" + local_register2 + "," + p_node.m_symTab.lookupName(first_index_node.m_moonVarName).m_offset + "(r14)\n";
//            m_moonExecCode += m_mooncodeindent + "add\t" + local_register3 + ",r0,"+local_register2+ "\n";

            m_moonExecCode += m_mooncodeindent + "sw\ttempaddr(r0)," + local_register2+ "\n";

//            m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register3+"\n";


            // deallocate the registers
            this.m_registerPool.push(local_register1);
            this.m_registerPool.push(local_register2);
            this.m_registerPool.push(local_register3);

//            p_node.m_moonVarName = first_index_node.m_moonVarName;
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
        p_node.getChildren().get(0).accept(this);
        // create a local variable and allocate a register to this subcomputation
        String local_register1 = this.m_registerPool.pop();
        // generate code
        m_moonExecCode += "% processing: \"" + p_node.m_subtreeString.replace("\n", "").replace("  ", " ") + "\"\n";
        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset + "(r14)\n";
        m_moonExecCode += m_mooncodeindent + "bz\t" + local_register1 + ",else" + p_node.m_line + "\n";
        p_node.getChildren().get(1).accept(this);
        m_moonExecCode += m_mooncodeindent + "j\t\tendif" + p_node.m_line + "\n";
        m_moonExecCode += String.format("%-10s", "else" + p_node.m_line) + "\n";
        p_node.getChildren().get(2).accept(this);
        m_moonExecCode += String.format("%-10s", "endif" + p_node.m_line) + "\n";
    }

    public void visit(WhileStatNode p_node) {
        // generate code
        m_moonExecCode += "% processing: \"" + p_node.m_subtreeString.replace("\n", "").replace("  ", " ") + "\"\n";
        m_moonExecCode += String.format("%-10s", "gowhile" + p_node.m_line) + "\n";
        p_node.getChildren().get(0).accept(this);
        // create a local variable and allocate a register to this subcomputation
        String local_register1 = this.m_registerPool.pop();
        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset + "(r14)\n";
        m_moonExecCode += m_mooncodeindent + "bz\t" + local_register1 + ",endwhile" + p_node.m_line + "\n";
        p_node.getChildren().get(1).accept(this);
        m_moonExecCode += m_mooncodeindent + "j\t\tgowhile" + p_node.m_line + "\n";
        m_moonExecCode += String.format("%-10s", "endwhile" + p_node.m_line) + "\n";
    }

    public void visit(ReadStatNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        // create a local variable and allocate a register to this subcomputation
        String local_register1 = this.m_registerPool.pop();
        // generate code
        m_moonExecCode += "% processing: read(" + p_node.getChildren().get(0).m_moonVarName + ")\n";
        // prompt user to enter
        m_moonExecCode += m_mooncodeindent2 + "% ask for " + p_node.getChildren().get(0).m_moonVarName + "\n";
        m_moonExecCode += m_mooncodeindent + "addi\t" + local_register1 + ",r0,ent" + p_node.getChildren().get(0).m_moonVarName + "\n";
        m_moonDataCode += String.format("%-10s", "ent" + p_node.getChildren().get(0).m_moonVarName) + "db\t\"Enter " + p_node.getChildren().get(0).m_moonVarName + ": \", 0\n";
        m_moonExecCode += m_mooncodeindent + "addi\tr14,r14," + p_node.m_symTab.m_size + "\n";
        m_moonExecCode += m_mooncodeindent + "sw\t-8(r14)," + local_register1 + "\n";
        m_moonExecCode += m_mooncodeindent + "jl\tr15, putstr\n";
        m_moonExecCode += m_mooncodeindent + "subi\tr14,r14," + p_node.m_symTab.m_size + "\n";
        // make the stack frame pointer point to the called function's stack frame
        m_moonExecCode += m_mooncodeindent2 + "% link buffer to stack\n";
        m_moonExecCode += m_mooncodeindent + "addi\tr14,r14," + p_node.m_symTab.m_size + "\n";
        m_moonExecCode += m_mooncodeindent + "addi\t" + local_register1 + ",r0, buf\n";
        m_moonExecCode += m_mooncodeindent + "sw\t-8(r14)," + local_register1 + "\n";
        m_moonExecCode += m_mooncodeindent2 + "% read a string from stdin\n";
        m_moonExecCode += m_mooncodeindent + "jl\tr15, getstr\n";
        m_moonExecCode += m_mooncodeindent2 + "% convert string to integer\n";
        m_moonExecCode += m_mooncodeindent + "jl\tr15, strint\n";
        m_moonExecCode += m_mooncodeindent2 + "% store " + p_node.getChildren().get(0).m_moonVarName + "\n";
        // make the stack frame pointer point back to the current function's stack frame
        m_moonExecCode += m_mooncodeindent + "subi\tr14,r14," + p_node.m_symTab.m_size + "\n";
        // receive the return value in r13 and right away put it in the next called function's stack frame
        m_moonExecCode += m_mooncodeindent + "sw\t" + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset + "(r14),r13\n";

        //deallocate local register
        this.m_registerPool.push(local_register1);
    }


    public void visit(WriteStatNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        // create a local variable and allocate a register to this subcomputation
        String local_register1 = this.m_registerPool.pop();
        String local_register2 = this.m_registerPool.pop();
        //generate code
        m_moonExecCode += "% processing: write(" + p_node.getChildren().get(0).m_moonVarName + ")\n";
        // put the value to be printed into a register
        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register2 + ",tempaddr(r0)\n";
        m_moonExecCode += m_mooncodeindent + "sub\tr14,r14," + local_register2+"\n";

        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset + "(r14)\n";
        m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register2+"\n";

//        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register2 + ",tempaddr(r0)\n";
//        m_moonExecCode += m_mooncodeindent + "add\tr14,r0," + local_register2 + "\n";   //todo

        m_moonExecCode += m_mooncodeindent2 + "% put value on stack\n";
        // make the stack frame pointer point to the called function's stack frame
        m_moonExecCode += m_mooncodeindent + "addi\tr14,r14," + p_node.m_symTab.m_size + "\n";
        // copy the value to be printed in the called function's stack frame
        m_moonExecCode += m_mooncodeindent + "sw\t-8(r14)," + local_register1 + "\n";
        m_moonExecCode += m_mooncodeindent2 + "% link buffer to stack\n";
        m_moonExecCode += m_mooncodeindent + "addi\t " + local_register1 + ",r0, buf\n";
        m_moonExecCode += m_mooncodeindent + "sw\t-12(r14)," + local_register1 + "\n";
        m_moonExecCode += m_mooncodeindent2 + "% convert int to string for output\n";
        m_moonExecCode += m_mooncodeindent + "jl\tr15, intstr\n";
        // receive the return value in r13 and right away put it in the next called function's stack frame
        m_moonExecCode += m_mooncodeindent + "sw\t-8(r14),r13\n";
        m_moonExecCode += m_mooncodeindent2 + "% output to console\n";
        m_moonExecCode += m_mooncodeindent + "jl\tr15, putstr\n";
        // make the stack frame pointer point back to the current function's stack frame

        m_moonExecCode += m_mooncodeindent + "subi\tr14,r14," + p_node.m_symTab.m_size + "\n";



        // assign the value to the assigned variable
//        m_moonExecCode += m_mooncodeindent + "sw\t0(r14)," + local_register1 + "\n";
//        m_moonExecCode += m_mooncodeindent + "sw\t" + local_register3 + "(r14)," + local_register1 + "\n";



        // output newline
        m_moonExecCode += m_mooncodeindent + "addi\t " + local_register1 + ",r0, 13\n";
        m_moonExecCode += m_mooncodeindent + "putc\t " + local_register1 + "\n";
        m_moonExecCode += m_mooncodeindent + "addi\t " + local_register1 + ",r0, 10\n";
        m_moonExecCode += m_mooncodeindent + "putc\t " + local_register1 + "\n";
        //deallocate local register
        this.m_registerPool.push(local_register1);
        this.m_registerPool.push(local_register2);


    }

    public void visit(ReturnStatNode p_node) {
        // propagate accepting the same visitor to all the children
        // this effectively achieves Depth-First AST Traversal
        String local_register1 = this.m_registerPool.pop();
        for (Node child : p_node.getChildren())
            child.accept(this);
        // copy the result of the return value into the first memory cell in the current stack frame
        // this way, the return value is conveniently at the top of the calling function's stack frame
        m_moonExecCode += "% processing: return(" + p_node.getChildren().get(0).m_moonVarName + ")\n";
        m_moonExecCode += m_mooncodeindent + "lw " + local_register1 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset + "(r14)\n";
        m_moonExecCode += m_mooncodeindent + "sw " + "0(r14)," + local_register1 + "\n";
        this.m_registerPool.push(local_register1);
    }


    public void visit(VariableNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
//        System.out.println(p_node.m_type);

        // update after computing offsets
        p_node.m_moonVarName = p_node.getChildren().get(0).m_moonVarName;
    }


    public void visit(DotNode p_node) {
        // process the first data member
        p_node.getChildren().get(0).accept(this);

        // todo test
        // allocate local registers
        String local_register1 = this.m_registerPool.pop();
        String local_register2 = this.m_registerPool.pop();
        String local_register3 = this.m_registerPool.pop();
        String local_register4 = this.m_registerPool.pop();
        for (int i = 1; i < p_node.getChildren().size(); i++) {


//            m_moonExecCode += m_mooncodeindent + "sw\ttempaddr(r0),r14\n";
//            m_moonExecCode += m_mooncodeindent + "lw\t" + local_register2 + "," + p_node.m_symTab.lookupName(first_index_node.m_moonVarName).m_offset + "(r14)\n";
//            m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register2+"\n";

            m_moonExecCode += "% processing dot: " + p_node.m_subtreeString + "\n";
            // store the offset of the previous data member
            m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + ",tempaddr(r0)\n";

            // add the two offsets
            m_moonExecCode += m_mooncodeindent + "add\t" + local_register2 + "," + local_register1 + ", r14\n";
            m_moonExecCode += m_mooncodeindent + "addi\t" + local_register3 + "," + local_register2 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(i).m_moonVarName).m_offset + "\n";


            if (!p_node.getChildren().get(i).getChildren().get(1).isLeaf()) {
                // reset r14
                m_moonExecCode += m_mooncodeindent + "add\tr14,r0," + local_register1 + "\n";   //todo
                p_node.getChildren().get(i).accept(this);
                m_moonExecCode += m_mooncodeindent + "lw\t" + local_register4 + ",tempaddr(r0)\n";
                m_moonExecCode += m_mooncodeindent + "add\tr14," + local_register3 + "," + local_register4 + "\n";
                m_moonExecCode += m_mooncodeindent + "sw\ttempaddr(r0),r14\n";
            } else {
                p_node.getChildren().get(i).accept(this);
                m_moonExecCode += m_mooncodeindent + "add\tr14,r0," + local_register3 + "\n";
                m_moonExecCode += m_mooncodeindent + "sw\ttempaddr(r0),r14\n";
            }
        }
        this.m_registerPool.push(local_register1);
        this.m_registerPool.push(local_register2);


    }


    public void visit(AssignStatNode p_node) {
//        for (Node child : p_node.getChildren()) {
//            child.accept(this);
//        }
        // first generate the right side of '='
        p_node.getChildren().get(1).accept(this);

        // allocate local registers
        String local_register1 = this.m_registerPool.pop();
        String local_register2 = this.m_registerPool.pop();

        String local_register3 = this.m_registerPool.pop();
        //generate code
        m_moonExecCode += "% processing assignment: " + p_node.getChildren().get(0).m_subtreeString + " := " + p_node.getChildren().get(1).m_moonVarName + "\n";
        // load the assigned value into a register
        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(1).m_moonVarName).m_offset + "(r14)\n";


        //  then generate the left side of '='
        p_node.getChildren().get(0).accept(this);


//        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register2 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(0).getChildren().get(0).getChildren().get(1).getChildren().get(0).m_moonVarName).m_offset + "(r14)\n";
//        m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register2+"\n";
       // retrieve the temporary address
        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register3 + ",tempaddr(r0)\n";
        m_moonExecCode += m_mooncodeindent + "sub\tr14,r14," + local_register3+"\n";

        // assign the value to the assigned variable
        m_moonExecCode += m_mooncodeindent + "sw\t" + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset +"(r14)," + local_register1 + "\n";
//        m_moonExecCode += m_mooncodeindent + "sw\t" + local_register3 + "(r14)," + local_register1 + "\n";
        m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register3+"\n";
//        if (p_node.getChildren().get(0).getChildren().get(0).m_sa_name.equals("DataMem_s") && !p_node.getChildren().get(0).getChildren().get(0).getChildren().get(1).isLeaf()) {
//            m_moonExecCode += m_mooncodeindent + "lw\t" + local_register3 + ",tempaddr(r0)\n";
//            m_moonExecCode += m_mooncodeindent + "add\tr14,r0," + local_register3 + "\n";   //todo
//        }

//                calculateOffsets(p_node.getChildren().get(0).m_moonVarName, p_node.getChildren().get(0).m_subtreeString, p_node.m_type))

        // deallocate local registers
        this.m_registerPool.push(local_register1);
        this.m_registerPool.push(local_register2);
        this.m_registerPool.push(local_register3);
    }

//    private int calculateOffsets(String var_name, String var_string, String type) {
////        System.out.println(type);
//        int size_of_type = 4;
//
//        if (type.equals("float")) {
//            size_of_type = 8;
//        }
//
//
//        if (var_name.equals(var_string)) {
//            return 0;
//        } else {
//            String indice_string = var_string.substring(var_name.length());
////            int index=1;
//            int offsets = 0;
//            ArrayList<Integer> indice_list = new ArrayList<>();
//            while (indice_string.length() != 0) {
//
//
////               if( indice_string.substring(indice_string.indexOf("[") + 1, indice_string.indexOf("]"))){
////
////               }
////                int indice =
////                        indice_list.add(indice);
//                indice_string = indice_string.substring(indice_string.indexOf("]") + 1);
//
//            }
//            System.out.println(indice_string);
//            indice_list.forEach(System.out::println);
//            return 0;
//        }
//
//    }


    public void visit(TypeNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
    }

    public void visit(FuncCallNode p_node) {
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
        // update after computing offsets
        p_node.m_moonVarName = p_node.getChildren().get(0).m_moonVarName;
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
        for (Node child : p_node.getChildren()) {
            child.accept(this);
        }
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

    public void visit(RelOpNode p_node) {
    }

    public void visit(SignNode p_node) {
    }

    public void visit(StringNode p_node) {
    }

    public void visit(VisibilityNode p_node) {
    }


}
