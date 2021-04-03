package semanticAnalyzer;

import AST.Node;
import syntacticAnalyzer.SyntacticAnalyzer;
import visitors.ReconstructSourceProgramVisitor;
import visitors.SymTabCreationVisitor;
import visitors.TypeCheckingVisitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class SemanticAnalyzer {

    // import from other classes
    private Node progNode;

    private SymTabCreationVisitor STCVisitor = new SymTabCreationVisitor();
    private TypeCheckingVisitor TCVisitor = new TypeCheckingVisitor();
    private ReconstructSourceProgramVisitor RSPVisitor = new ReconstructSourceProgramVisitor   ();

    private PrintStream writer_symTab;


    public SemanticAnalyzer() {

    }


    public void setProgNode(Node progNode) {
        this.progNode = progNode;
    }

    public void createSymTables(){
        progNode.accept(STCVisitor);
        progNode.accept(RSPVisitor);
        progNode.accept(TCVisitor);
//            System.out.println(progNode.m_symTab);
    }


    public void writeToFiles(String src_file_path) {
        try {
            String file_path_temp = src_file_path.substring(0, src_file_path.length() - 4);
            File outfile_symTab = new File(file_path_temp + ".outsymboltables");
            System.out.println("[Semantic] Writing to the file: " + outfile_symTab.getName());
            writer_symTab = new PrintStream(outfile_symTab);
            PrintStream console = System.out;
            System.setOut(writer_symTab);
            System.out.println(progNode.m_symTab);
            System.setOut(console);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void closeFiles() {
        System.out.println("[Semantic] Flushing & closing files. ");
        writer_symTab.flush();
        writer_symTab.close();
    }


}
