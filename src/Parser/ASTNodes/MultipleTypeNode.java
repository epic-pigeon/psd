package Parser.ASTNodes;

import Parser.Collection;

public class MultipleTypeNode extends TypeNode {
    private Collection<TypeNode> nodes;

    public Collection<TypeNode> getNodes() {
        return nodes;
    }

    public void setNodes(Collection<TypeNode> nodes) {
        this.nodes = nodes;
    }

    public MultipleTypeNode(Collection<TypeNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    public TypeType getTypeType() {
        return TypeType.MULTIPLE;
    }
}
