package digital.amigo.jsengine.rule;

public interface Condition {

    String toJavaScript(); // e.g. fact.age >= 18

    /**
     * Fluent AND chaining helper.
     */
    default Condition and(Condition other) {
        return new OperatorAnd(this, other);
    }

    /**
     * Fluent OR chaining helper.
     */
    default Condition or(Condition other) {
        return new OperatorOr(this, other);
    }
}