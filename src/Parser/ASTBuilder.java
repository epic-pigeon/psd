package Parser;

import Parser.ASTNodes.*;
import jdk.nashorn.internal.ir.BinaryNode;

import java.util.Map;
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
        } else if (checkToken("LEFT_PAREN")) {
            skipToken("LEFT_PAREN");
            TypeNode node = parseType();
            if (checkToken("COMMA", "RIGHT_PAREN")) {
                Collection<TypeNode> result = new Collection<>(node);
                if (checkToken("COMMA")) result.addAll(delimited("COMMA", "COMMA", "RIGHT_PAREN", this::parseType));
                TupleTypeNode typeNode = new TupleTypeNode(result);
                typeNode.setStrict(!checkToken("QUESTION"));
                if (checkToken("QUESTION", "EXCLAMATION")) tokenHolder.next();
                return typeNode;
            } else if (checkToken("BINARY_OR")) {
                Collection<TypeNode> result = new Collection<>(node);
                result.addAll(delimited("BINARY_OR", "BINARY_OR", "RIGHT_PAREN", this::parseType));
                MultipleTypeNode typeNode = new MultipleTypeNode(result);
                typeNode.setStrict(!checkToken("QUESTION"));
                if (checkToken("QUESTION", "EXCLAMATION")) tokenHolder.next();
                return typeNode;
            } else throw new RuntimeException("Unexpected token " + tokenHolder.lookUp().getName());
        } else if (checkToken("LEFT_SQUARE_PAREN")) {
            skipToken("LEFT_SQUARE_PAREN");
            TypeNode typeNode = parseType();
            if (checkToken("COLON")) {
                skipToken("COLON");
                TypeNode result = new DictionaryTypeNode(typeNode, parseType());
                skipToken("RIGHT_SQUARE_PAREN");
                result.setStrict(!checkToken("QUESTION"));
                if (checkToken("QUESTION", "EXCLAMATION")) tokenHolder.next();
                return result;
            } else if (checkToken("RIGHT_SQUARE_PAREN")) {
                skipToken("RIGHT_SQUARE_PAREN");
                TypeNode result = new ArrayTypeNode(typeNode);
                result.setStrict(!checkToken("QUESTION"));
                if (checkToken("QUESTION", "EXCLAMATION")) tokenHolder.next();
                return result;
            } else throw new RuntimeException("Unexpected token " + tokenHolder.lookUp().getValue());
        } else if (checkToken("LEFT_CURLY_PAREN")) {
            TypeNode result = new StructTypeNode(
                    delimited("LEFT_CURLY_PAREN", "COMMA", "RIGHT_CURLY_PAREN", this::parseStructParameter).toMap()
            );
            result.setStrict(!checkToken("QUESTION"));
            if (checkToken("QUESTION", "EXCLAMATION")) tokenHolder.next();
            return result;
        }
        throw new RuntimeException("Unrecognized type");
    }
    private Map.Entry<String, TypeNode> parseStructParameter() {
        String name = skipToken("IDENTIFIER").getValue();
        skipToken("COLON");
        TypeNode type = parseType();
        return new Map.Entry<String, TypeNode>() {
            @Override
            public String getKey() {
                return name;
            }

            @Override
            public TypeNode getValue() {
                return type;
            }

            @Override
            public TypeNode setValue(TypeNode value) {
                return null;
            }
        };
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
        } else if (checkToken("LEFT_PAREN")) {
            skipToken("LEFT_PAREN");
            node = parseExpression();
            skipToken("RIGHT_PAREN");
        } else {
            throw new RuntimeException("Unexpected token " + tokenHolder.lookUp().getName());
        }
        while (checkToken("DOT", "QUESTION", "LEFT_PAREN", "AS")) {
            node = maybeCall(maybeMemberAccess(maybeCast(node)));
        }
        return node;
    }
    private Node maybeCast(Node expression) {
        if (checkToken("AS")) {
            return parseCast(expression);
        } else {
            return expression;
        }
    }
    private CastNode parseCast(Node expression) {
        skipToken("AS");
        CastNode castNode = new CastNode();
        castNode.setStrict(checkToken("EXCLAMATION"));
        skipToken("QUESTION", "EXCLAMATION");
        castNode.setValue(expression);
        castNode.setToType(parseType());
        return castNode;
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
