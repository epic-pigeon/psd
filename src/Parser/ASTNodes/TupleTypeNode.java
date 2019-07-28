package Parser.ASTNodes;

import Parser.Collection;

public class TupleTypeNode extends TypeNode {
    private Collection<TypeNode> elements;

    public TupleTypeNode(Collection<TypeNode> elements) {
        this.elements = elements;
    }

    public Collection<TypeNode> getElements() {
        return elements;
    }

    public void setElements(Collection<TypeNode> elements) {
        this.elements = elements;
    }

    @Override
    public TypeType getTypeType() {
        return TypeType.TUPLE;
    }
}
