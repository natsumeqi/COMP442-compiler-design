package semanticAnalyzer;

import AST.Node;
import syntacticAnalyzer.SyntacticAnalyzer;

import java.io.File;

public class SemanticAnalyzerDriver {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter the path the file or folder");
        }

        File f = new File(args[0]);
        if (f.isDirectory()) {
            File[] files = f.listFiles(((dir, name) -> name.endsWith(".src")));
            if (files != null) {
                for (File file : files) {
                    semanticAnalyzerOneFile(file.getAbsolutePath());
                }
            }
        } else {
            if (f.getAbsolutePath().endsWith(".src")) {
                semanticAnalyzerOneFile(f.getAbsolutePath());
            }
        }
    }

    // tokenizing single file
    static void semanticAnalyzerOneFile(String file_path) {

        // parse the src file
        SyntacticAnalyzer syntacticAnalyzer = new SyntacticAnalyzer();
        syntacticAnalyzer.parserLexicalSetup(file_path);
        syntacticAnalyzer.parserIOFileSetup(file_path);
        boolean syntacticCorrect = syntacticAnalyzer.parse();
        if (syntacticCorrect) {
            System.out.println("The program is syntactically correct.");

            // do semantic analysis
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
            semanticAnalyzer.setProgNode(syntacticAnalyzer.getProgNode());
            syntacticAnalyzer.getProgNode().print();


        } else {
            System.out.println("The program has syntax error(s).");
        }
        syntacticAnalyzer.parserIOFileClose();







    }
}
