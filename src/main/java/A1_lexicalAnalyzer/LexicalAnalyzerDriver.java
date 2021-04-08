package A1_lexicalAnalyzer;

import java.io.File;

public class LexicalAnalyzerDriver {


    public static void main(String[] args) {

//        File f = new File("./test/LexicalAnalyzerTest");
        if (args.length == 0) {
            System.out.println("Please enter the path the file or folder");
        }

        File f = new File(args[0]);
        if (f.isDirectory()) {
            File[] files = f.listFiles(((dir, name) -> name.endsWith(".src")));
            if (files != null) {
                for (File file : files) {
                    LexicalAnalyzerOneFile(file.getAbsolutePath());
                }
            }
        } else {
            if (f.getAbsolutePath().endsWith(".src")) {
                LexicalAnalyzerOneFile(f.getAbsolutePath());
            }
        }
    }

    // tokenizing single file
    static void LexicalAnalyzerOneFile(String file_path) {
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
        lexicalAnalyzer.createTable();
        lexicalAnalyzer.IOFileSetup(file_path);

        while (lexicalAnalyzer.nextToken() != null) {
            if (lexicalAnalyzer.isFinished())
                break;
        }
        lexicalAnalyzer.IOFileClose();
    }
}
