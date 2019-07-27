package Parser;

import Parser.ASTNodes.*;

import java.util.function.Supplier;

public class ASTBuilder {
    private TokenHolder tokenHolder;
    public Node build(TokenHolder _tokenHolder) {
        this.tokenHolder = _tokenHolder;
        return parseProgram();
    }
    private ProgramNode parseProgram() {
        Collection<Node> program = new Collection<>();
        while (tokenHolder.hasNext()) {
            program.add(parseExpression());
            checkAndSkip("SEMICOLON");
        }
        return new ProgramNode(program);
    }
    private Node parseExpression() {
        if (checkToken("LET", "CONST")) {
            return parseVariableDefinition();
        } else if (checkToken("NUMBER")) {
            return parseNumber();
        } else if (checkToken("STRING")) {
            return parseString();
        } else {
            throw new RuntimeException("Unexpected token " + tokenHolder.lookUp().getName());
        }
    }
    private boolean checkToken(String... token) {
        return tokenHolder.hasNext() && new Collection<>(token).some(string -> string.equals(tokenHolder.lookUp().getName()));
    }
    private Token skipToken(String... token) {
        if (checkToken(token)) {
            return tokenHolder.next();
        } else {
            throw new RuntimeException("Token " + new Collection<>(token) + " expected, got " + tokenHolder.lookUp().getName());
        }
    }
    private Token checkAndSkip(String... token) {
        if (checkToken(token)) {
            return skipToken(token);
        } else return null;
    }
    private Collection<Node> delimited(String start, String delimiter, String end, Supplier<Node> parser) {
        if (end == null && delimiter == null) {
            throw new RuntimeException("Either end or delimiter should be not null");
        }
        Collection<Node> result = new Collection<>();
        if (start != null) {
            skipToken(start);
        }
        while (true) {
            if (end != null) {
                if (checkToken(end)) {
                    break;
                }
            }
            result.add(parser.get());
            if (delimiter != null) {
                if (checkToken(delimiter)) {
                    skipToken(delimiter);
                } else {
                    break;
                }
            }
        }
        return result;
    }
    private Collection<Node> delimited(String start, String end, Supplier<Node> parser) {
        if (end == null) throw new RuntimeException("End should be not null");
        return delimited(start, null, end, parser);
    }
    private Collection<Node> delimited(String delimiter, Supplier<Node> parser) {
        if (delimiter == null) throw new RuntimeException("Delimiter should be not null");
        return delimited(null, delimiter, null, parser);
    }
    private VariableDefinitionNode parseVariableDefinition() {
        boolean isConstant = checkToken("CONST");
        tokenHolder.next();
        String name = skipToken("IDENTIFIER").getValue();
        TypeNode type = null;
        if (checkToken("COLON")) {
            skipToken("COLON");
            type = parseType();
        }
        Node initializer = null;
        if (checkAndSkip("EQUALS") != null) {
            initializer = parseExpression();
        }
        if (type == null && initializer == null) {
            throw new RuntimeException("There should be either an initializer, or a type specifier");
        }
        VariableDefinitionNode result = new VariableDefinitionNode(name, isConstant);
        result.setType(type);
        result.setInitializer(initializer);
        return result;
    }
    private ValueNode parseNumber() {
        return new ValueNode(Double.valueOf(skipToken("NUMBER").getValue()));
    }
    private ValueNode parseString() {
        String string = skipToken("STRING").getValue();
        string = string.substring(1, string.length() - 1);
        return new ValueNode(string);
    }
    private TypeNode parseType() {
        if (checkToken("IDENTIFIER")) {
            IdentifierTypeNode result = new IdentifierTypeNode();
            result.setName(skipToken("IDENTIFIER").getValue());
            result.setStrict(!checkToken("QUESTION"));
            if (checkToken("QUESTION", "EXCLAMATION")) tokenHolder.next();
            return result;
        }
        throw new RuntimeException("Unrecognized type");
    }
    private
}
