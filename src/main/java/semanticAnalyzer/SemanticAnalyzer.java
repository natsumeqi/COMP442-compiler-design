package semanticAnalyzer;

import AST.Node;
import visitors.ComputeMemSizeVisitor;
import visitors.ReconstructSourceVisitor;
import visitors.SymTabCreationVisitor;
import visitors.TypeCheckingVisitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class SemanticAnalyzer {


    private Node progNode;

    private final SymTabCreationVisitor STCVisitor = new SymTabCreationVisitor();
    private final TypeCheckingVisitor TCVisitor = new TypeCheckingVisitor();
    private final ReconstructSourceVisitor RSPVisitor = new ReconstructSourceVisitor();
    private final ComputeMemSizeVisitor CMSVisitor = new ComputeMemSizeVisitor();

    private PrintStream writer_symTab;
    private PrintStream writer_semantic_error;
    public String parser_errors;


    public SemanticAnalyzer() {

    }


    public void setProgNode(Node progNode) {
        this.progNode = progNode;
    }

    public void createTableAndChecking() {
        if (progNode != null) {
            System.out.println("[Semantic] ReconstructSource visitor starts: ");
            // reconstruct source
            progNode.accept(RSPVisitor);
            System.out.println("[Semantic] SymTabCreation visitor starts: ");
            // symbol table creation
            progNode.accept(STCVisitor);
            System.out.println("[Semantic] TypeChecking visitor starts: ");
            // check type
            progNode.accept(TCVisitor);
            System.out.println("[Semantic] ComputeMemSize visitor starts: ");
            // add new variables to table, computes memory size and offset
            progNode.accept(CMSVisitor);
        }
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
            if (!parser_errors.isEmpty()) {
                System.out.println("-----------------------------------------ERROR-----------------------------------------");
                System.out.println();
                System.out.println(parser_errors);
            }
            if (!STCVisitor.m_errors.toString().isEmpty()) {
                if(parser_errors.isEmpty()){
                    System.out.println("-----------------------------------------ERROR-----------------------------------------");
                }
                System.out.print(STCVisitor.m_errors);
            }
            if (!TCVisitor.m_errors.isEmpty()) {
                if(parser_errors.isEmpty() && STCVisitor.m_errors.toString().isEmpty()){
                    System.out.println("-----------------------------------------ERROR-----------------------------------------");

                }
                System.out.println(TCVisitor.m_errors);
                System.out.println("-----------------------------------------ERROR-----------------------------------------");
            }

            PrintStream console = System.out;
            System.setOut(writer_symTab);
            if (progNode != null) {
                System.out.println(progNode.m_symTab);
            }
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

    public void setParser_errors(String errors) {
        parser_errors = errors;
    }

}
