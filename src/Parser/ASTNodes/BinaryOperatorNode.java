package Parser.ASTNodes;

public class BinaryOperatorNode extends Node {
    @Override
    public Type getType() {
        return Type.OPERATOR;
    }
    public static enum BinaryOperatorType {
        PLUS, MINUS, MULTIPLICATION, DIVISION, REMAINDER, OR, AND, BINARY_OR, BINARY_AND, XOR
    }
}
