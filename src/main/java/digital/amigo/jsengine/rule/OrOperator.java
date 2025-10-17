package digital.amigo.jsengine.rule;

public class OrOperator implements Condition {
    private final Condition left;
    private final Condition right;

    public OrOperator(Condition left, Condition right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toJavaScript() {
        return "(" + left.toJavaScript() + " || " + right.toJavaScript() + ")";
    }

    @Override
    public String toString() {
        return toJavaScript();
    }

}