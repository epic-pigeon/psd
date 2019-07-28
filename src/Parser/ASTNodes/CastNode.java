package Parser.ASTNodes;

public class CastNode extends Node {
    @Override
    public Type getType() {
        return Type.CAST;
    }

    private Node value;
    private TypeNode toType;
    private boolean strict;



    public Node getValue() {
        return value;
    }

    public void setValue(Node value) {
        this.value = value;
    }

    public TypeNode getToType() {
        return toType;
    }

    public void setToType(TypeNode toType) {
        this.toType = toType;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }
}
