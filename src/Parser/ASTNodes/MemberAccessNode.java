package Parser.ASTNodes;

public class MemberAccessNode extends Node {
    @Override
    public Type getType() {
        return Type.MEMBER_ACCESS;
    }

    private Node value;
    private String identifier;
    private boolean strict;

    public MemberAccessNode(Node value, String identifier) {
        this.value = value;
        this.identifier = identifier;
    }

    public Node getValue() {
        return value;
    }

    public void setValue(Node value) {
        this.value = value;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }
}
