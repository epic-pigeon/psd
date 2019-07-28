package Parser.ASTNodes;

public class DictionaryTypeNode extends TypeNode {
    @Override
    public TypeType getTypeType() {
        return TypeType.DICTIONARY;
    }
    private TypeNode keyType;
    private TypeNode valueType;

    public DictionaryTypeNode(TypeNode keyType, TypeNode valueType) {
        this.keyType = keyType;
        this.valueType = valueType;
    }

    public TypeNode getKeyType() {
        return keyType;
    }

    public void setKeyType(TypeNode keyType) {
        this.keyType = keyType;
    }

    public TypeNode getValueType() {
        return valueType;
    }

    public void setValueType(TypeNode valueType) {
        this.valueType = valueType;
    }
}
