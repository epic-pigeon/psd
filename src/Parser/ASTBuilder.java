package Parser;

import Parser.ASTNodes.*;
import jdk.nashorn.internal.ir.BinaryNode;

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
    private Node parseExpression(int precedence) {
        return maybeBinary(parseAtom(), precedence);
    }
    private Node parseExpression() {
        return parseExpression(0);
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
    private<T> Collection<T> delimited(String start, String delimiter, String end, Supplier<T> parser) {
        if (end == null && delimiter == null) {
            throw new RuntimeException("Either end or delimiter should be not null");
        }
        Collection<T> result = new Collection<>();
        if (start != null) {
            skipToken(start);
        }
        while (true) {
            if (end != null) {
                if (checkToken(end)) {
                    skipToken(end);
                    break;
                }
            }
            result.add(parser.get());
            if (delimiter != null) {
                if (checkToken(delimiter)) {
                    skipToken(delimiter);
                } else {
                    if (end != null) skipToken(end);
                    break;
                }
            }
        }
        return result;
    }
    private<T> Collection<T> delimited(String start, String end, Supplier<T> parser) {
        if (end == null) throw new RuntimeException("End should be not null");
        return delimited(start, null, end, parser);
    }
    private<T> Collection<T> delimited(String delimiter, Supplier<T> parser) {
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
    private Node maybeBinary(Node expression, int precedence) {
        if (tokenHolder.lookUp() != null) {
            BinaryOperatorNode.BinaryOperatorType type =
                    BinaryOperatorNode.BinaryOperatorType.forName(tokenHolder.lookUp().getName());
            if (type != null) {
                if (type.getPrecedence() > precedence) {
                    tokenHolder.next();
                    Node right = maybeBinary(parseAtom(), type.getPrecedence());
                    BinaryOperatorNode node = new BinaryOperatorNode(expression, right, type);
                    return maybeBinary(node, precedence);
                }
            }
        }
        return expression;
    }
    private Node parseAtom() {
        Node node;
        if (checkToken("LET", "CONST")) {
            node = parseVariableDefinition();
        } else if (checkToken("NUMBER")) {
            node = parseNumber();
        } else if (checkToken("STRING")) {
            node = parseString();
        } else if (checkToken("IDENTIFIER")) {
            node = parseVariable();
        } else {
            throw new RuntimeException("Unexpected token " + tokenHolder.lookUp().getName());
        }
        while (checkToken("DOT", "QUESTION", "LEFT_PAREN")) {
            node = maybeCall(maybeMemberAccess(node));
        }
        return node;
    }
    private VariableNode parseVariable() {
        return new VariableNode(skipToken("IDENTIFIER").getValue());
    }
    private Node maybeCall(Node function) {
        if (checkToken("LEFT_PAREN")) {
            return parseCall(function);
        } else {
            return function;
        }
    }
    private Node maybeMemberAccess(Node left) {
        boolean strict = true;
        if (checkToken("QUESTION")) {
            if (tokenHolder.lookUp(1).getName().equals("DOT")) {
                strict = false;
                skipToken("QUESTION");
            } else {
                return left;
            }
        }
        if (checkToken("DOT")) {
            skipToken("DOT");
            String name = skipToken("IDENTIFIER").getValue();
            MemberAccessNode node = new MemberAccessNode(left, name);
            node.setStrict(strict);
            return node;
        } else {
            return left;
        }
    }
    private CallNode parseCall(Node function) {
        CallNode node = new CallNode(function);
        node.setArguments(parseArguments());
        return node;
    }
    private Collection<CallNode.Argument> parseArguments() {
        return delimited("LEFT_PAREN", "COMMA", "RIGHT_PAREN", this::parseArgument);
    }
    private CallNode.Argument parseArgument() {
        String name;
        Node value;
        if (tokenHolder.lookUp(1) != null && tokenHolder.lookUp(0).getName().equals("IDENTIFIER") && tokenHolder.lookUp(1).getName().equals("COLON")) {
            name = skipToken("IDENTIFIER").getValue();
            skipToken("COLON");
            value = parseExpression();
        } else {
            name = null;
            value = parseExpression();
        }
        return new CallNode.Argument(name, value);
    }
}
