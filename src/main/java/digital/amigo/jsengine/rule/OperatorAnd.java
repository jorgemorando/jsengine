package digital.amigo.jsengine.rule;

public class OperatorAnd implements Condition {
    private final Condition left;
    private final Condition right;

    OperatorAnd(Condition left, Condition right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return toJavaScript();
    }

    @Override
    public String toJavaScript() {
        return "(" + left.toJavaScript() + " && " + right.toJavaScript() + ")";
    }
}