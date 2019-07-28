package Parser.ASTNodes;

import Parser.Collection;

public class LambdaBodyNode extends BodyNode {
    public LambdaBodyNode(Collection<Node> expressions) {
        super(expressions);
    }

    @Override
    public Type getType() {
        return Type.LAMBDA_BODY;
    }

    public static class Parameter {
        private String name;
        private TypeNode type;

        public Parameter(String name, TypeNode type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public TypeNode getType() {
            return type;
        }

        public void setType(TypeNode type) {
            this.type = type;
        }
    }

    private Collection<Parameter> parameters;

    public Collection<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(Collection<Parameter> parameters) {
        this.parameters = parameters;
    }
}
