package Parser.ASTNodes;

public class TypeNode extends Node {
    @Override
    public Type getType() {
        return Type.TYPE;
    }

    private boolean strict;

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }
}
