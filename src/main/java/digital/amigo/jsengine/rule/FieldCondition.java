package digital.amigo.jsengine.rule;

public abstract class FieldCondition implements Condition {
    private final String field;
    private final RefType refType;

    protected FieldCondition(String field, RefType type) {
        this.field = field;
        this.refType = type;
    }

    String ref(){
        return switch (refType){
            case FACT -> factRef();
            case CONTEXT -> contextRef();
        };
    }

    private String factRef() {
        return "fact." + field;
    }

     private String contextRef() {
        return "context." + field;
    }

    public enum RefType{
        FACT,CONTEXT
    }
}