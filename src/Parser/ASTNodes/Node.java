package Parser.ASTNodes;

import Parser.JSONToString;

public abstract class Node extends JSONToString {
    public static enum Type {
        VARIABLE_DEFINITION,
        PROGRAM,
        TYPE,
        VALUE,
        MEMBER_ACCESS,
        OPERATOR,
        CALL,
        VARIABLE,
        CAST,
    }
    public abstract Type getType();
    private Type type = getType();
}
