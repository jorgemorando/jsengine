package digital.amigo.jsengine.rule;

import static digital.amigo.jsengine.utils.JSONUtils.toJsValue;

public class IsEqualTo extends FieldCondition {
    private final Object value;

    IsEqualTo(String field, Object value, RefType refType) {
        super(field,refType);
        this.value = value;
    }

    @Override
    public String toJavaScript() {
        return ref() + " == " + toJsValue(value) ;
    }

    @Override
    public String toString() {
        return toJavaScript();
    }

}