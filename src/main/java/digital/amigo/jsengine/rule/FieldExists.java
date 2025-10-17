package digital.amigo.jsengine.rule;

public class FieldExists extends FieldCondition {
    public FieldExists(String field, FieldCondition.RefType type) {
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