package Parser.ASTNodes;

import Parser.Collection;

public class ProgramNode extends Node {
    @Override
    public Type getType() {
        return Type.PROGRAM;
    }

    private Collection<Node> nodes;

    public Collection<Node> getNodes() {
        return nodes;
    }

    public void setNodes(Collection<Node> nodes) {
        this.nodes = nodes;
    }

    public ProgramNode(Collection<Node> nodes) {
        this.nodes = nodes;
    }
}
