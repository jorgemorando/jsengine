package digital.amigo.jsengine.rule;

public class FieldExpression {
    private final String field;
    private final FieldCondition.RefType refType;

    private FieldExpression(String field, FieldCondition.RefType refType) {
        this.field = field;
        this.refType = refType;
    }

    public static FieldExpression factField(String fieldName) {
        return new FieldExpression(fieldName, FieldCondition.RefType.FACT);
    }

    public static FieldExpression contextField(String fieldName) {
        return new FieldExpression(fieldName, FieldCondition.RefType.CONTEXT);
    }

    public Condition isEqualTo(Object value) {
        return new IsEqualTo(field, value, refType);
    }

    public Condition isGreaterOrEqualThan(Number value) {
        return new IsGreaterOrEquals(field, value, refType);
    }

    public Condition isLowerOrEqualThan(Number value) {
        return new IsLowerOrEquals(field, value, refType);
    }

    public Condition isGreaterThan(Number value) {
        return new IsGreater(field, value, refType);
    }

    public Condition isLowerThan(Number value) {
        return new IsLower(field, value, refType);
    }

    public Condition isNotNull() {
        return new IsNotNull(field, refType);
    }

    public Condition isNull() {
        return new IsNull(field, refType);
    }
}