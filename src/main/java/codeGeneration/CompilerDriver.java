package codeGeneration;

import lexicalAnalyzer.LexicalAnalyzer;
import semanticAnalyzer.SemanticAnalyzer;
import syntacticAnalyzer.SyntacticAnalyzer;

import java.io.File;

public class CompilerDriver {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter the path of the file or the folder.");
            System.exit(0);
        }

        File f = new File(args[0]);

        if (f.isDirectory()) {
            File[] files = f.listFiles(((dir, name) -> name.endsWith(".src")));
            if (files != null) {

                for (File file : files) {
                    if(!tokenizingOneFile(file.getAbsolutePath())){

                        compileOneFile(file.getAbsolutePath());
                        System.out.println();
                    }
                }
            }
        } else {
            if (f.getAbsolutePath().endsWith(".src")) {
                compileOneFile(f.getAbsolutePath());
            }
        }
    }

    static boolean tokenizingOneFile(String file_path){
        boolean lexer_error = false;

        // lexical analysis
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
        lexicalAnalyzer.createTable();
        lexicalAnalyzer.IOFileSetup(file_path);

        while (lexicalAnalyzer.nextToken() != null) {
            if (lexicalAnalyzer.isFinished())
                break;
        }
        lexicalAnalyzer.IOFileClose();

        if(!lexicalAnalyzer.lexical_errors.isEmpty()){
            lexer_error = true;
//            System.out.println("lexer errors");
        }
        return lexer_error;
    }


    // processing single file
    static void compileOneFile(String file_path) {

        // parse the src file
        SyntacticAnalyzer syntacticAnalyzer = new SyntacticAnalyzer();
        syntacticAnalyzer.parserLexicalSetup(file_path);
//        syntacticAnalyzer.setLexical_analyzer(lexicalAnalyzer);
        syntacticAnalyzer.parserIOFileSetup(file_path);
        boolean syntacticCorrect = syntacticAnalyzer.parse();
        if (syntacticCorrect) {
            System.out.println("[Parser][OK] The program is syntactically correct.");
            syntacticAnalyzer.parserIOFileClose();
        } else {
            System.out.println("[Parser][ERROR]The program has syntax error(s).");
            syntacticAnalyzer.parserIOFileClose();
//            System.out.println(syntacticAnalyzer.parser_errors);
        }


        // do semantic analysis
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
        semanticAnalyzer.setProgNode(syntacticAnalyzer.getProgNode());
        semanticAnalyzer.createTableAndChecking();
        semanticAnalyzer.setParser_errors(syntacticAnalyzer.parser_errors);
        semanticAnalyzer.writeToFiles(file_path);
        semanticAnalyzer.closeFiles();

        // generate code
        CodeGeneration codeGeneration = new CodeGeneration();
        codeGeneration.setProgNode(syntacticAnalyzer.getProgNode());
        codeGeneration.generateCode();
        codeGeneration.writeToFiles(file_path);
        codeGeneration.closeFiles();

    }
}
