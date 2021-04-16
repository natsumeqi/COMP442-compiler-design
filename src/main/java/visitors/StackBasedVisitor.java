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
        // generate code
        m_moonExecCode += "% processing temporal(rel): " + p_node.m_moonVarName + " := (" + p_node.getChildren().get(0).m_moonVarName + " " + p_node.getChildren().get(1).getData() + " " + p_node.getChildren().get(2).m_moonVarName + ")\n";


        p_node.getChildren().get(0).accept(this);

        String local_register1 = this.m_registerPool.pop();
        String local_register2 = this.m_registerPool.pop();
        String local_register3 = this.m_registerPool.pop();


        // register 2
        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + ",offset(r0)\n";
        // when the variable is an array, the value at the offset is an address, retrieve the address to get the real offset
        SymTabEntry func_or_var1 = p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName);
        if (func_or_var1.m_name != null) {
            // if it is a parameter variable and an array, retrieve the real address
            if (func_or_var1.m_dims != null && !func_or_var1.m_dims.isEmpty()) {

                if (func_or_var1.m_kind.equals("param")) {
                    m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register1 + "\n";
                    m_moonExecCode += m_mooncodeindent + "lw\t" + local_register2 + ",0(r14)\n";
                    m_moonExecCode += m_mooncodeindent + "sub\tr14,r14," + local_register1 + "\n";
                } else {
                    m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register1 + "\n";
                    m_moonExecCode += m_mooncodeindent + "lw\t" + local_register2 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset + "(r14)\n";
                    m_moonExecCode += m_mooncodeindent + "sub\tr14,r14," + local_register1 + "\n";
                }
            } else {
                // load the values of the operands into registers
                m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register1 + "\n";
                m_moonExecCode += m_mooncodeindent + "lw\t" + local_register2 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset + "(r14)\n";
                m_moonExecCode += m_mooncodeindent + "sub\tr14,r14," + local_register1 + "\n";
            }
        }


        p_node.getChildren().get(2).accept(this);

        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + ",offset(r0)\n";
        // register 3
        // when the variable is an array, the value at the offset is an address, retrieve the address to get the real offset
        SymTabEntry func_or_var2 = p_node.m_symTab.lookupName(p_node.getChildren().get(2).m_moonVarName);
        if (func_or_var2.m_name != null) {
            // if it is a parameter variable and an array, retrieve the real address
            if (func_or_var2.m_dims != null && !func_or_var2.m_dims.isEmpty()) {

                if (func_or_var2.m_kind.equals("param")) {
                    m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register1 + "\n";
                    m_moonExecCode += m_mooncodeindent + "lw\t" + local_register3 + ",0(r14)\n";
                    m_moonExecCode += m_mooncodeindent + "sub\tr14,r14," + local_register1 + "\n";
                } else {
                    m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register1 + "\n";
                    m_moonExecCode += m_mooncodeindent + "lw\t" + local_register3 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(2).m_moonVarName).m_offset + "(r14)\n";
                    m_moonExecCode += m_mooncodeindent + "sub\tr14,r14," + local_register1 + "\n";
                }
            } else {
                // load the values of the operands into registers
                m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register1 + "\n";
                m_moonExecCode += m_mooncodeindent + "lw\t" + local_register3 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(2).m_moonVarName).m_offset + "(r14)\n";
                m_moonExecCode += m_mooncodeindent + "sub\tr14,r14," + local_register1 + "\n";
            }
        }

//        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register3 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(2).m_moonVarName).m_offset + "(r14)\n";


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
        m_moonExecCode += m_mooncodeindent + rel_op_moon + "\t" + local_register1 + "," + local_register2 + "," + local_register3 + "\n";
        // assign the result into a temporary variable (assumed to have been previously created by the symbol table generator)
        m_moonExecCode += m_mooncodeindent + "sw\t" + p_node.m_symTab.lookupName(p_node.m_moonVarName).m_offset + "(r14)," + local_register1 + "\n";

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

        // reset offset(r0)
        m_moonExecCode += m_mooncodeindent + "sw\toffset(r0),r0\n";

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
        m_moonExecCode += String.format("%-10s", p_node.getChildren().get(1).m_moonVarName) + "\n";
        m_moonExecCode += m_mooncodeindent + "sw\t-4(r14),r15\n";
        //generate the code for the function body
        for (Node child : p_node.getChildren())
            child.accept(this);
        // copy back the jumping-back address into r15
        m_moonExecCode += m_mooncodeindent + "lw\tr15,-4(r14)\n";
        // jump back to the calling function
        m_moonExecCode += m_mooncodeindent + "jr\tr15\n";
        m_moonExecCode += "% end function " + p_node.getChildren().get(1).m_moonVarName + " definition\n";
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
        m_moonExecCode += "%------------------------------------------------------\n";
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
        m_moonDataCode += String.format("%-10s", "offset") + "res 4\n";
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

//        String local_register1 = this.m_registerPool.pop();
//        String local_register2 = this.m_registerPool.pop();
//        // put the value to be printed into a register
//
//
//        // when the variable is an array, the value at the offset is an address, retrieve the address to get the real offset
//
//        SymTabEntry func_or_var = p_node.m_symTab.lookupName(p_node.m_moonVarName);
//        if (func_or_var.m_name != null) {
//
//            // if it is a parameter variable and an array, retrieve the real address
//            if (func_or_var.m_dims != null && !func_or_var.m_dims.isEmpty()) {
//
//                m_moonExecCode += m_mooncodeindent + "lw\t" + local_register2 + ",offset(r0)\n";
//
//                if (func_or_var.m_kind.equals("param")) {
//
//                    m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register1 + "\n";
//                    m_moonExecCode += m_mooncodeindent + "lw\t" + local_register2 + ",0(r14)\n";
//                    m_moonExecCode += m_mooncodeindent + "sub\tr14,r14," + local_register1 + "\n";
//
//                } else {
//
//                    m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register1 + "\n";
//                    m_moonExecCode += m_mooncodeindent + "lw\t" + local_register2 + "," + p_node.m_symTab.lookupName(p_node.m_moonVarName).m_offset + "(r14)\n";
//                    m_moonExecCode += m_mooncodeindent + "sub\tr14,r14," + local_register1 + "\n";
//                }
//            }
//        }
//        this.m_registerPool.push(local_register1);
//        this.m_registerPool.push(local_register2);

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
//        System.out.println(p_node.m_data);
//        System.out.println(p_node.m_type);
        int size_of_type = sizeOfTypeNode(p_node);

//        System.out.println(p_node.m_moonVarName);
//        System.out.println(p_node.m_type);

//        System.out.println("[datamem]p_node.m_date: "+p_node.m_data);
//        System.out.println("[datamem]size_of_type: "+size_of_type);


        SymTabEntry entry = p_node.m_symTab.lookupName(p_node.m_data);

        Vector<String> dims_string = new Vector<>();
        if (entry.m_name != null) {
            dims_string = entry.m_dims;
        } else {
            entry = p_node.m_symTabEntry;
            dims_string = entry.m_dims;
        }

        ArrayList<Integer> dims_col = new ArrayList<>();
        if (dims_string.size() > 1) {

            for (int i = 1; i < dims_string.size(); i++) {
                int dim_col = 1;
                for (int j = i; j < dims_string.size(); j++) {
//                    System.out.println("dims_string: " + dims_string.get(j));
                    String dim_integer = dims_string.get(j).substring(1, dims_string.get(j).indexOf("]"));
//                    System.out.println("dim_integer: " + dim_integer);
                    dim_col *= Integer.parseInt(dim_integer);
                }
                dims_col.add(dim_col);
            }

        }
        dims_col.add(1);
//        dims_col.forEach(System.out::println);
//        System.out.println("dims_col size: "+dims_col.size());

        // if it is a array with dimension, calculate offsets
        if (!p_node.getChildren().get(1).isLeaf()) {
            String local_register1 = this.m_registerPool.pop();
            String local_register2 = this.m_registerPool.pop();
            String local_register3 = this.m_registerPool.pop();
            String local_register4 = this.m_registerPool.pop();

            // if it is a parameter variable, retrieve the real address
            SymTabEntry func_or_var = p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName);
//            System.out.println(func_or_var.m_kind);
//            System.out.println(p_node.getChildren().get(0).m_moonVarName);
            if (func_or_var.m_name != null) {
                if (func_or_var.m_kind.equals("param") && func_or_var.m_dims != null && !func_or_var.m_dims.isEmpty()) {
//                System.out.println("???: "+p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset );
                    m_moonExecCode += m_mooncodeindent + "lw\t" + local_register4 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset + "(r14)\n";
                } else {
                    m_moonExecCode += m_mooncodeindent + "addi\t" + local_register4 + ",r0,0\n";
                }
            }


//            Node first_index_node = p_node.getChildren().get(1).getChildren().get(0);
            String index_temp_varible = p_node.getChildren().get(1).m_moonVarName;
//            System.out.println( "indice size: "+p_node.getChildren().get(1).getChildren().size());
            for (int i = 0; i < p_node.getChildren().get(1).getChildren().size(); i++) {

                Node index_expr_node = p_node.getChildren().get(1).getChildren().get(i);
                // generate code
//                System.out.println(i);
//                System.out.println(p_node.m_line);
                m_moonExecCode += "% processing offsets(mul): " + p_node.m_moonVarName + " := " + index_expr_node.m_moonVarName + " * " + dims_col.get(i) + "\n";
                // load the values of the operands into registers
                m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + "," + p_node.m_symTab.lookupName(index_expr_node.m_moonVarName).m_offset + "(r14)\n";
                m_moonExecCode += m_mooncodeindent + "muli\t" + local_register2 + "," + local_register1 + "," + dims_col.get(i) + "\n";

                // assign the result back into the first literal variable
                m_moonExecCode += m_mooncodeindent + "sw\t" + p_node.m_symTab.lookupName(index_temp_varible).m_offset + "(r14)," + local_register2 + "\n";

                if (i >= 1) {
                    m_moonExecCode += "% processing offsets(add): " + p_node.m_moonVarName + " := " + index_expr_node.m_moonVarName + " * " + dims_col.get(i) + "\n";
                    m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + "," + p_node.m_symTab.lookupName(index_expr_node.m_moonVarName).m_offset + "(r14)\n";
                    m_moonExecCode += m_mooncodeindent + "lw\t" + local_register2 + "," + p_node.m_symTab.lookupName(index_temp_varible).m_offset + "(r14)\n";
                    m_moonExecCode += m_mooncodeindent + "add\t" + local_register3 + "," + local_register1 + "," + local_register2 + "\n";
                    m_moonExecCode += m_mooncodeindent + "sw\t" + p_node.m_symTab.lookupName(index_temp_varible).m_offset + "(r14)," + local_register3 + "\n";
                }
            }

            // multiply size of type
            m_moonExecCode += "% processing offsets(mul size): " + p_node.m_moonVarName + " := " + p_node.m_moonVarName + " * " + size_of_type + "\n";
            // load the values of the operands into registers
            m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + "," + p_node.m_symTab.lookupName(index_temp_varible).m_offset + "(r14)\n";
            m_moonExecCode += m_mooncodeindent + "muli\t" + local_register2 + "," + local_register1 + "," + size_of_type + "\n";


//             add the probable address retrieve from before

            m_moonExecCode += m_mooncodeindent + "add\t" + local_register1 + "," + local_register4 + "," + local_register2 + "\n";
//            m_moonExecCode += m_mooncodeindent + "sub\t" + local_register2 + ",r0," + local_register1 + "\n";

            m_moonExecCode += m_mooncodeindent + "sw\toffset(r0)," + local_register1 + "\n";


            // deallocate the registers
            this.m_registerPool.push(local_register1);
            this.m_registerPool.push(local_register2);
            this.m_registerPool.push(local_register3);
            this.m_registerPool.push(local_register4);


        } else {// when the data member is not a array(IndiceNode is empty)

            m_moonExecCode += m_mooncodeindent + "sw\toffset(r0),r0\n";
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
        String local_register3 = this.m_registerPool.pop();
        // generate code
        m_moonExecCode += "% processing: \"" + p_node.m_subtreeString.replace("\n", "").replace("  ", " ") + "\"\n";
        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register3 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset + "(r14)\n";
        m_moonExecCode += m_mooncodeindent + "bz\t" + local_register3 + ",else" + p_node.m_line + "\n";
        p_node.getChildren().get(1).accept(this);
        m_moonExecCode += m_mooncodeindent + "j\t\tendif" + p_node.m_line + "\n";
        m_moonExecCode += String.format("%-10s", "else" + p_node.m_line) + "\n";
        p_node.getChildren().get(2).accept(this);
        m_moonExecCode += String.format("%-10s", "endif" + p_node.m_line) + "\n";
        this.m_registerPool.push(local_register3);

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
//        // put the value to be printed into a register


        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register2 + ",offset(r0)\n";
        // when the variable is an array, the value at the offset is an address, retrieve the address to get the real offset
        // if it is a parameter variable, retrieve the real address
        SymTabEntry func_or_var = p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName);
        if (func_or_var.m_name != null) {

            if (func_or_var.m_dims != null && !func_or_var.m_dims.isEmpty()) {

                if (func_or_var.m_kind.equals("param")) {

                    m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register2 + "\n";
                    m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + ",0(r14)\n";
                    m_moonExecCode += m_mooncodeindent + "sub\tr14,r14," + local_register2 + "\n";

                } else {
                    m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register2 + "\n";
                    m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset + "(r14)\n";
                    m_moonExecCode += m_mooncodeindent + "sub\tr14,r14," + local_register2 + "\n";
                }

            } else {
                m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register2 + "\n";
                m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset + "(r14)\n";
                m_moonExecCode += m_mooncodeindent + "sub\tr14,r14," + local_register2 + "\n";
            }
        }


//        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register2 + ",offset(r0)\n";
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
        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset + "(r14)\n";
        m_moonExecCode += m_mooncodeindent + "sw\t" + "0(r14)," + local_register1 + "\n";
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

        if (!p_node.getChildren().get(0).getChildren().get(1).isLeaf()) {

            m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + ",offset(r0)\n";
//            m_moonExecCode += m_mooncodeindent + "sub\tr14,r14," + local_register1 + "\n";
        } else {
            m_moonExecCode += m_mooncodeindent + "addi\t" + local_register1 + ",r0,0\n";
        }


//            m_moonExecCode += m_mooncodeindent + "sw\toffset(r0),r14\n";
//            m_moonExecCode += m_mooncodeindent + "lw\t" + local_register2 + "," + p_node.m_symTab.lookupName(first_index_node.m_moonVarName).m_offset + "(r14)\n";
//            m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register2+"\n";

        m_moonExecCode += "% processing dot: " + p_node.m_subtreeString + "\n";


//            // add the two offsets
//            m_moonExecCode += m_mooncodeindent + "add\t" + local_register2 + "," + local_register1 + ", r14\n";
//            m_moonExecCode += m_mooncodeindent + "addi\t" + local_register3 + "," + local_register2 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(i).m_moonVarName).m_offset + "\n";

if(p_node.getChildren().get(1).getChildren().size()>=2){
        if (!p_node.getChildren().get(1).getChildren().get(1).isLeaf()) {
//                m_moonExecCode += m_mooncodeindent + "add\tr14,r0," + local_register1 + "\n";   //todo
            p_node.getChildren().get(1).accept(this);
            m_moonExecCode += m_mooncodeindent + "lw\t" + local_register2 + ",offset(r0)\n";
            m_moonExecCode += m_mooncodeindent + "add\t" + local_register3 + "," + local_register1 + "," + local_register2 + "\n";
            m_moonExecCode += m_mooncodeindent + "sw\toffset(r0)," + local_register3 + "\n";
        } else {
            p_node.getChildren().get(1).accept(this);
            m_moonExecCode += m_mooncodeindent + "add\t" + local_register3 + ",r0," + local_register1 + "\n";
            m_moonExecCode += m_mooncodeindent + "sw\toffset(r0)," + local_register3 + "\n";

        }}
        this.m_registerPool.push(local_register1);
        this.m_registerPool.push(local_register2);
        this.m_registerPool.push(local_register3);
    }


    public void visit(AssignStatNode p_node) {

        //generate code
        m_moonExecCode += "% processing assignment: " + p_node.getChildren().get(0).m_subtreeString + " := " + p_node.getChildren().get(1).m_moonVarName + "\n";

        // first generate the right side of '='
        p_node.getChildren().get(1).accept(this);

        // allocate local registers
        String local_register1 = this.m_registerPool.pop();
        String local_register2 = this.m_registerPool.pop();

        // store value in register 2
        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + ",offset(r0)\n";
        // when the variable is an array, the value at the offset is an address, retrieve the address to get the real offset
        SymTabEntry func_or_var1 = p_node.m_symTab.lookupName(p_node.getChildren().get(1).m_moonVarName);

        if (func_or_var1.m_name != null) {
//                    System.out.println("func_or_var1: "+func_or_var1);
            // if it is a parameter variable and an array, retrieve the real address
            if (func_or_var1.m_dims != null && !func_or_var1.m_dims.isEmpty()) {
//                System.out.println("func_or_var1: " + func_or_var1);
                if (func_or_var1.m_kind.equals("param")) {
//                    System.out.println("param generate coed : " + p_node.m_subtreeString);
                    m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register1 + "\n";
                    m_moonExecCode += m_mooncodeindent + "lw\t" + local_register2 + ",0(r14)\n";
                    m_moonExecCode += m_mooncodeindent + "sub\tr14,r14," + local_register1 + "\n";
                } else {
                    m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register1 + "\n";
                    m_moonExecCode += m_mooncodeindent + "lw\t" + local_register2 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(1).m_moonVarName).m_offset + "(r14)\n";
                    m_moonExecCode += m_mooncodeindent + "sub\tr14,r14," + local_register1 + "\n";
                }
            } else {
                // load the values of the operands into registers
                m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register1 + "\n";
                m_moonExecCode += m_mooncodeindent + "lw\t" + local_register2 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(1).m_moonVarName).m_offset + "(r14)\n";
                m_moonExecCode += m_mooncodeindent + "sub\tr14,r14," + local_register1 + "\n";
            }
        }


        // load the assigned value into a register
//        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + "," + p_node.m_symTab.lookupName(p_node.getChildren().get(1).m_moonVarName).m_offset + "(r14)\n";


        //  then generate the left side of '='
        p_node.getChildren().get(0).accept(this);


        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + ",offset(r0)\n";
        // assign value from register2 into the destination
        // when the variable is an array, the value at the offset is an address, retrieve the address to get the real offset
        SymTabEntry func_or_var2 = p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName);
        if (func_or_var2.m_name != null) {
            // if it is a parameter variable and an array, retrieve the real address
            if (func_or_var2.m_dims != null && !func_or_var2.m_dims.isEmpty()) {

                if (func_or_var2.m_kind.equals("param")) {
                    m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register1 + "\n";
                    m_moonExecCode += m_mooncodeindent + "sw\t0(r14)," + local_register2 + "\n";
                    m_moonExecCode += m_mooncodeindent + "sub\tr14,r14," + local_register1 + "\n";
                } else {
                    m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register1 + "\n";
                    m_moonExecCode += m_mooncodeindent + "sw\t" + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset + "(r14)," + local_register2 + "\n";
                    m_moonExecCode += m_mooncodeindent + "sub\tr14,r14," + local_register1 + "\n";
                }
            } else {
                // load the values of the operands into registers
                m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register1 + "\n";
                m_moonExecCode += m_mooncodeindent + "sw\t" + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset + "(r14)," + local_register2 + "\n";
                m_moonExecCode += m_mooncodeindent + "sub\tr14,r14," + local_register1 + "\n";
            }
        }


        // retrieve the offset
//        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register3 + ",offset(r0)\n";
//        m_moonExecCode += m_mooncodeindent + "add\tr14,r14," + local_register3 + "\n";

//        m_moonExecCode += m_mooncodeindent + "sw\t" + p_node.m_symTab.lookupName(p_node.getChildren().get(0).m_moonVarName).m_offset + "(r14)," + local_register2 + "\n";
//        m_moonExecCode += m_mooncodeindent + "sub\tr14,r14," + local_register3 + "\n";

        // deallocate local registers
        this.m_registerPool.push(local_register1);
        this.m_registerPool.push(local_register2);
//        this.m_registerPool.push(local_register3);
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
        // update after computing offsets
        p_node.m_data = p_node.getChildren().get(0).m_data;

        String local_register1 = this.m_registerPool.pop();

        // pass parameters
        // here we assume that the parameters are the size of a word,
        // which is not true for arrays and objects.
        // In those cases, a loop copying the values e.g. byte-by-byte is necessary
        SymTabEntry table_entry_of_called_function = p_node.m_symTab.lookupName(p_node.m_data);
//        System.out.println("funccall node data: " + p_node.getData());
//        System.out.println("table_entry: " + table_entry_of_called_function.m_name);
        int index_of_param = 0;
        m_moonExecCode += "% processing: function call to " + p_node.getChildren().get(0).m_moonVarName + " \n";
        for (Node param : p_node.getChildren().get(1).getChildren()) {

//            System.out.println("param.moonvarname: " + param.m_moonVarName);

            // if the parameter is an array, pass the address instead
            if (p_node.m_symTab.lookupName(param.m_moonVarName).m_dims != null && !p_node.m_symTab.lookupName(param.m_moonVarName).m_dims.isEmpty()) {

                m_moonExecCode += m_mooncodeindent + "addi\t" + local_register1 + ",r0," + (p_node.m_symTab.lookupName(param.m_moonVarName).m_offset - p_node.m_symTab.m_size) + "\n";
            } else {
                m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + "," + p_node.m_symTab.lookupName(param.m_moonVarName).m_offset + "(r14)\n";
            }
            if(table_entry_of_called_function.m_subtable!=null) {
                int offset_of_param = p_node.m_symTab.m_size + table_entry_of_called_function.m_subtable.m_symList.get(index_of_param).m_offset;
                m_moonExecCode += m_mooncodeindent + "sw\t" + offset_of_param + "(r14)," + local_register1 + "\n";
            }

            index_of_param++;
        }
        // make the stack frame pointer point to the called function's stack frame
        m_moonExecCode += m_mooncodeindent + "addi\tr14,r14," + p_node.m_symTab.m_size + "\n";
        // jump to the called function's code
        // here the function's name is the label
        // a unique label generator is necessary in the general case
        m_moonExecCode += m_mooncodeindent + "jl\tr15," + p_node.getData() + "\n";
        // upon jumping back, set the stack frame pointer back to the current function's stack frame
        m_moonExecCode += m_mooncodeindent + "subi\tr14,r14," + p_node.m_symTab.m_size + "\n";
        // copy the return value in memory space to store it on the current stack frame
        // to evaluate the expression in which it is
        m_moonExecCode += m_mooncodeindent + "lw\t" + local_register1 + "," + p_node.m_symTab.m_size + "(r14)\n";
        m_moonExecCode += m_mooncodeindent + "sw\t" + p_node.m_symTab.lookupName(p_node.m_moonVarName).m_offset + "(r14)," + local_register1 + "\n";
        this.m_registerPool.push(local_register1);
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
