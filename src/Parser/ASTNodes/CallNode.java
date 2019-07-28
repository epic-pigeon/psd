package Parser.ASTNodes;

import Parser.Collection;
import Parser.JSONToString;

public class CallNode extends Node {
    @Override
    public Type getType() {
        return Type.CALL;
    }

    public static class Argument extends JSONToString {
        private Node node;
        private String name;

        public Argument(String name, Node node) {
            this.node = node;
            this.name = name;
        }

        public Node getNode() {
            return node;
        }

        public void setNode(Node node) {
            this.node = node;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private Node function;
    private Collection<Argument> arguments;
    private boolean strict;

    public CallNode(Node function) {
        this.function = function;
    }

    public Node getFunction() {
        return function;
    }

    public void setFunction(Node function) {
        this.function = function;
    }

    public Collection<Argument> getArguments() {
        return arguments;
    }

    public void setArguments(Collection<Argument> arguments) {
        this.arguments = arguments;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }
}
