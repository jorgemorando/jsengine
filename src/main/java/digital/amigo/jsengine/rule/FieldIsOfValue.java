package digital.amigo.jsengine.rule;

public class FieldIsOfValue extends FieldCondition {
    private final Object value;

    public FieldIsOfValue(String field, Object value, RefType refType) {
        super(field,refType);
        this.value = value;
    }

    @Override
    public String toJavaScript() {
        return ref() + " == \"" + value + "\"";
    }

    @Override
    public String toString() {
        return toJavaScript();
    }

}