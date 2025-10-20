package digital.amigo.jsengine.rule;

public class IsLowerOrEquals extends FieldCondition {
    private final Number value;

    IsLowerOrEquals(String field, Number value, RefType refType) {
        super(field,refType);
        this.value = value;
    }

    @Override
    public String toJavaScript() {
        return ref() + " <= " + value;
    }

    @Override
    public String toString() {
        return toJavaScript();
    }

}