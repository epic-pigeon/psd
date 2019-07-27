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
    }
    public abstract Type getType();
}
