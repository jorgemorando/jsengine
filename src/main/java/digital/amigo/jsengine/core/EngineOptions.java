package digital.amigo.jsengine.core;

public class EngineOptions {

    private boolean loadLibs;

     public EngineOptions(){
        this.loadLibs = true;
    }

    public boolean loadLibs() {
        return loadLibs;
    }

    public void loadLibs(boolean load) {
        this.loadLibs = load;
    }
}
