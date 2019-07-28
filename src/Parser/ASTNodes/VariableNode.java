package Parser.ASTNodes;

public class VariableNode extends Node {
    @Override
    public Type getType() {
        return Type.VARIABLE;
    }

    private String name;

    public VariableNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
