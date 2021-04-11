package codeGeneration;

import AST.Node;
import visitors.ComputeMemSizeVisitor;
import visitors.StackBasedVisitor;
import visitors.SymTabCreationVisitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class CodeGeneration {

    private Node progNode;


//    private final ComputeMemSizeVisitor CMSVisitor = new ComputeMemSizeVisitor();
    private final StackBasedVisitor SBVisitor = new StackBasedVisitor();


    private PrintStream writer_moon;


    public CodeGeneration() {
    }

    public void setProgNode(Node progNode) {
        this.progNode = progNode;
    }

    public void generateCode(){

//        progNode.accept(CMSVisitor);
        progNode.accept(SBVisitor);
    }



    public void writeToFiles(String src_file_path) {
        try {
            String file_path_temp = src_file_path.substring(0, src_file_path.length() - 4);
            File outfile_moon = new File(file_path_temp + ".m");
            System.out.println("[Code] Writing to the file: " + outfile_moon.getName());
            writer_moon = new PrintStream(outfile_moon);
            PrintStream console = System.out;
            System.setOut(writer_moon);
            System.out.println(SBVisitor.m_moonExecCode);
            System.out.println(SBVisitor.m_moonDataCode);
            System.setOut(console);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void closeFiles() {
        System.out.println("[Code] Flushing & closing files. ");
        writer_moon.flush();
        writer_moon.close();

    }

}
