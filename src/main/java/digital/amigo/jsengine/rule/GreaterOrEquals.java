package digital.amigo.jsengine.rule;

public class GreaterOrEquals extends FieldCondition {
    private final Number value;

    public GreaterOrEquals(String field, Number value, RefType refType) {
        super(field,refType);
        this.value = value;
    }

    @Override
    public String toJavaScript() {
        return ref() + " >= " + value;
    }

    @Override
    public String toString() {
        return toJavaScript();
    }

}