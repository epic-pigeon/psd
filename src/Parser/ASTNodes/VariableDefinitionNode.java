package Parser.ASTNodes;

public class VariableDefinitionNode extends Node {
    @Override
    public Type getType() {
        return Type.VARIABLE_DEFINITION;
    }

    private String name;
    private boolean isConstant;
    private TypeNode type;
    private Node initializer;

    public VariableDefinitionNode(String name, boolean isConstant) {
        this.name = name;
        this.isConstant = isConstant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isConstant() {
        return isConstant;
    }

    public void setConstant(boolean constant) {
        isConstant = constant;
    }

    public void setType(TypeNode type) {
        this.type = type;
    }

    public Node getInitializer() {
        return initializer;
    }

    public void setInitializer(Node initializer) {
        this.initializer = initializer;
    }
}
