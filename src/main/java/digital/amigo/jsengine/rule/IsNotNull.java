package digital.amigo.jsengine.rule;

public class IsNotNull extends FieldCondition {
    IsNotNull(String field, FieldCondition.RefType type) {
        super(field,type);
    }

    @Override
    public String toJavaScript() {
        return ref() + " !== undefined && " + ref() + " !== null";
    }

    @Override
    public String toString() {
        return toJavaScript();
    }

}