package digital.amigo.jsengine.rule;

public class IsNull extends FieldCondition {
    IsNull(String field, RefType type) {
        super(field,type);
    }

    @Override
    public String toJavaScript() {
        return ref() + " == null";
    }

    @Override
    public String toString() {
        return toJavaScript();
    }

}