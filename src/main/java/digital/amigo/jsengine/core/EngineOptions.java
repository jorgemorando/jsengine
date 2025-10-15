package digital.amigo.jsengine.core;

public record EngineOptions(String factName, String contextName, boolean loadLibs) {

    public static EngineOptions defaultOptions() {
        return defaultBuilder().get();
    }

    public static EngineOptions.Builder defaultBuilder() {
        return new Builder();
    }

    public static class Builder {
        private boolean withLibs = false;
        private String contextName = "context";
        private String factName = "fact";
        private Builder() {}

        public EngineOptions.Builder loadLibs(boolean withLibs) {
            this.withLibs = withLibs;
            return this;
        }

        public EngineOptions.Builder withContextName(String context) {
            this.contextName = context;
            return this;
        }

        public EngineOptions.Builder withFactName(String factName) {
            this.factName = factName;
            return this;
        }

        public EngineOptions get() {
            return new EngineOptions(this.factName, this.contextName, this.withLibs);
        }

    }
}
