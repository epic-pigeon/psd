package Parser.ASTNodes;

import Parser.Collection;

public class BodyNode extends Node {
    @Override
    public Type getType() {
        return Type.BODY;
    }

    private Collection<Node> expressions;

    public BodyNode(Collection<Node> expressions) {
        this.expressions = expressions;
    }

    public Collection<Node> getExpressions() {
        return expressions;
    }

    public void setExpressions(Collection<Node> expressions) {
        this.expressions = expressions;
    }
}
