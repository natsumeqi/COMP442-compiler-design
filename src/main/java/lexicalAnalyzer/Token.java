package lexicalAnalyzer;

public class Token {

    private String type;
    private String lexeme;
    private int location;
    private boolean is_error;

    public Token(String type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    public Token(String type, String lexeme, int location) {
        this.type = type;
        this.lexeme = lexeme;
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public boolean isError() {
        return is_error;
    }

    public void setIs_error(boolean is_error) {
        this.is_error = is_error;
    }

    @Override
    public String toString() {
        return "[" + type + ", " + lexeme + ", " + location + ']';
    }
}