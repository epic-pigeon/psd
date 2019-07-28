package Parser.ASTNodes;

import java.util.Map;

public class StructTypeNode extends TypeNode {
    @Override
    public TypeType getTypeType() {
        return TypeType.STRUCT;
    }

    private Map<String, TypeNode> props;

    public StructTypeNode(Map<String, TypeNode> props) {
        this.props = props;
    }

    public Map<String, TypeNode> getProps() {
        return props;
    }

    public void setProps(Map<String, TypeNode> props) {
        this.props = props;
    }
}
