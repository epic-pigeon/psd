package Parser.ASTNodes;

public class IdentifierTypeNode extends TypeNode {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public TypeType getTypeType() {
        return TypeType.IDENTIFIER;
    }
}
