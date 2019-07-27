import Parser.*;
import Parser.ASTNodes.Node;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class Main {
    private static Collection<Rule> rules = new Collection<>(
            new Rule("LEFT_PAREN", Pattern.compile("\\(")),
            new Rule("RIGHT_PAREN", Pattern.compile("\\)")),
            new Rule("LEFT_CURLY_PAREN", Pattern.compile("\\{")),
            new Rule("LEFT_SQUARE_PAREN", Pattern.compile("\\[")),
            new Rule("RIGHT_SQUARE_PAREN", Pattern.compile("]")),
            new Rule("RIGHT_CURLY_PAREN", Pattern.compile("}")),
            new Rule("COMMA", Pattern.compile(",")),
            new Rule("QUESTION", Pattern.compile("\\?")),
            new Rule("EXCLAMATION", Pattern.compile("!")),
            new Rule("REFERENCE", Pattern.compile("&")),
            new Rule("COMMA", Pattern.compile(",")),
            new Rule("DOT", Pattern.compile("\\.")),
            new Rule("DOUBLE_PLUS", Pattern.compile("\\+\\+")),
            new Rule("PLUS", Pattern.compile("\\+")),
            new Rule("DOUBLE_MINUS", Pattern.compile("--")),
            new Rule("MINUS", Pattern.compile("-")),
            new Rule("DOUBLE_EQUALS", Pattern.compile("==")),
            new Rule("EQUALS", Pattern.compile("=")),
            new Rule("SINGLE_LINE_COMMENT", Pattern.compile("//[^\n]*")),
            new Rule("NUMBER", Pattern.compile("\\d+(\\.\\d+)?")),
            new Rule("STRING", Pattern.compile("('([^']|(\\\\.))*')|(\"([^\"]|(\\\\.))*\")")),
            new Rule("MULTILINE_COMMENT", Pattern.compile("/\\*([^/])*\\*/")),
            new Rule("SEMICOLON", Pattern.compile(";")),
            new Rule("COLON", Pattern.compile(":")),
            new KeywordRule("LET", "let"),
            new KeywordRule("CONST", "const"),



            new Rule("IDENTIFIER", Pattern.compile("([a-zA-Z$_][a-zA-Z0-9$_]*)|(`[a-zA-Z0-9.,\\- $_]+`)"))
    );

    private static Rule toSkip = new Rule("kar", Pattern.compile("\\s+"));

    public static void main(String[] args) throws IOException {
        File file = new File("example.psds");
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();

        String code = new String(data, StandardCharsets.UTF_8);

        TokenHolder tokenHolder = new Lexer().lex(code, rules, toSkip);

        System.out.println(tokenHolder);

        Node programNode = new ASTBuilder().build(tokenHolder);

        System.out.println(programNode);
    }
}
