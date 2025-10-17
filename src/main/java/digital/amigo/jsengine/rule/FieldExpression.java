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
        return new FieldIsOfValue(field, value, refType);
    }

    public Condition greaterOrEqual(Number value) {
        return new GreaterOrEquals(field, value, refType);
    }

    public Condition lowerOrEqual(Number value) {
        return new LowerOrEquals(field, value, refType);
    }

    public Condition exists() {
        return new FieldExists(field, refType);
    }
}