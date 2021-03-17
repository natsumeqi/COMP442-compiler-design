package syntacticAnalyzer;

import java.io.File;

public class SyntacticAnalyzerDriver {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter the path the file or folder");
        }

        File f = new File(args[0]);
        if (f.isDirectory()) {
            File[] files = f.listFiles(((dir, name) -> name.endsWith(".src")));
            if (files != null) {
                for (File file : files) {
                    syntacticAnalyzerOneFile(file.getAbsolutePath());
                }
            }
        } else {
            if (f.getAbsolutePath().endsWith(".src")) {
                syntacticAnalyzerOneFile(f.getAbsolutePath());
            }
        }
    }

    // tokenizing single file
    static void syntacticAnalyzerOneFile(String file_path) {
        SyntacticAnalyzer syntacticAnalyzer = new SyntacticAnalyzer();
        syntacticAnalyzer.parserSetup(file_path);
        syntacticAnalyzer.parserIOFileSetup(file_path);

        boolean syntacticCorrect = syntacticAnalyzer.parse();
        if (syntacticCorrect) {
            System.out.println("The program is syntactically correct.");
        } else {
            System.out.println("The program has syntax error(s).");
        }
        syntacticAnalyzer.parserIOFileClose();
    }
}
