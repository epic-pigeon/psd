package Parser.ASTNodes;

public abstract class TypeNode extends Node {
    @Override
    public Type getType() {
        return Type.TYPE;
    }

    public static enum TypeType {
        MULTIPLE, TUPLE, ARRAY, STRUCT, IDENTIFIER, DICTIONARY
    }

    public abstract TypeType getTypeType();
    private TypeType typeType = getTypeType();

    private boolean strict;

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }
}
