package Parser.ASTNodes;

import Parser.Collection;

public class BinaryOperatorNode extends Node {
    @Override
    public Type getType() {
        return Type.OPERATOR;
    }
    public static enum BinaryOperatorType {
        ASSIGN("ASSIGN", 10),
        EQUALS("EQUALS", 11),
        IDENTICAL("IDENTICAL", 11),
        PLUS("PLUS", 15),
        MINUS("MINUS", 15),
        MULTIPLICATION("MULTIPLICATION", 20),
        DIVISION("DIVISION", 20),
        REMAINDER("REMINDER", 15),
        OR("OR", 5),
        AND("AND", 5),
        XOR("XOR", 5),
        ELVIS("QUESTION_EXCLAMATION", 8),
        BINARY_OR("ELVIS", 25),
        BINARY_AND("REFERENCE", 25),
        BINARY_XOR("BINARY_XOR", 25);

        private String name;
        private int precedence;
        BinaryOperatorType(String name, int precedence) {
            this.name = name;
            this.precedence = precedence;
        }

        public static BinaryOperatorType forName(String name) {
            return new Collection<>(BinaryOperatorType.values()).findFirst(
                    type -> type.name.equals(name)
            );
        }

        public int getPrecedence() {
            return precedence;
        }
    }
    private Node left;
    private Node right;
    private BinaryOperatorType operatorType;

    public BinaryOperatorNode(Node left, Node right, BinaryOperatorType type) {
        this.left = left;
        this.right = right;
        this.operatorType = type;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public void setType(BinaryOperatorType type) {
        this.operatorType = type;
    }

    public BinaryOperatorType getOperatorType() {
        return operatorType;
    }
}
