package Parser.ASTNodes;

public class ValueNode extends Node {
    @Override
    public Type getType() {
        return Type.VALUE;
    }
    public static enum ValueType {
        NUMBER,
        STRING,
    }
    private ValueType valueType;
    private Object value;

    public ValueNode(Object value) {
        setValue(value);
    }

    public ValueType getValueType() {
        return valueType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
        if (value instanceof Number) {
            valueType = ValueType.NUMBER;
        } else if (value instanceof String) {
            valueType = ValueType.STRING;
        } else {
            throw new RuntimeException("Unknown type");
        }
    }
}
