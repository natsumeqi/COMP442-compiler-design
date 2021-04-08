package A3_semanticAnalyzer;

import AST.Node;
import visitors.ReconstructSourceProgramVisitor;
import visitors.SymTabCreationVisitor;
import visitors.TypeCheckingVisitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class SemanticAnalyzer {


    private Node progNode;

    private final SymTabCreationVisitor STCVisitor = new SymTabCreationVisitor();
    private final TypeCheckingVisitor TCVisitor = new TypeCheckingVisitor();
    private final ReconstructSourceProgramVisitor RSPVisitor = new ReconstructSourceProgramVisitor   ();

    private PrintStream writer_symTab;
    private PrintStream writer_semantic_error;


    public SemanticAnalyzer() {

    }


    public void setProgNode(Node progNode) {
        this.progNode = progNode;
    }

    public void createTableAndChecking(){
        progNode.accept(RSPVisitor);
        progNode.accept(STCVisitor);
        progNode.accept(TCVisitor);
    }


    public void writeToFiles(String src_file_path) {
        try {
            String file_path_temp = src_file_path.substring(0, src_file_path.length() - 4);
            File outfile_symTab = new File(file_path_temp + ".outsymboltables");
            File outfile_semantic_errors = new File(file_path_temp + ".outsemanticerrors");
            System.out.println("[Semantic] Writing to the file: " + outfile_symTab.getName());
            System.out.println("[Semantic] Writing to the file: " + outfile_semantic_errors.getName());
            writer_symTab = new PrintStream(outfile_symTab);
            writer_semantic_error = new PrintStream(outfile_semantic_errors);
            PrintStream console = System.out;
            System.setOut(writer_symTab);
            System.out.println(progNode.m_symTab);
            System.setOut(writer_semantic_error);
            System.out.print(STCVisitor.m_errors);
            System.out.println(TCVisitor.m_errors);
            System.setOut(console);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void closeFiles() {
        System.out.println("[Semantic] Flushing & closing files. ");
        writer_symTab.flush();
        writer_symTab.close();
        writer_semantic_error.flush();
        writer_semantic_error.close();
    }


}
