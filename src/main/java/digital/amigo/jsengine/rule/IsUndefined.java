package digital.amigo.jsengine.rule;

public class IsUndefined extends FieldCondition {
    IsUndefined(String field, RefType type) {
        super(field,type);
    }

    @Override
    public String toJavaScript() {
        return ref() + " == undefined";
    }

    @Override
    public String toString() {
        return toJavaScript();
    }

}