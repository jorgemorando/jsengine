package digital.amigo.jsengine.rule;

public class IsNotUndefined extends FieldCondition {
    IsNotUndefined(String field, RefType type) {
        super(field,type);
    }

    @Override
    public String toJavaScript() {
        return ref() + " !== undefined";
    }

    @Override
    public String toString() {
        return toJavaScript();
    }

}