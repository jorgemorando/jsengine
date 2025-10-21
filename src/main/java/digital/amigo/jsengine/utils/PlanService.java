package digital.amigo.jsengine.utils;

import digital.amigo.jsengine.DefaultFact;
import digital.amigo.jsengine.DefaultRuleEngineContext;
import digital.amigo.jsengine.control.EngineControl;
import digital.amigo.jsengine.execution.DefaultPlanExecutor;
import digital.amigo.jsengine.execution.Plan;
import digital.amigo.jsengine.execution.StepResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {

    private final EngineControl engineControl;
    private final DefaultPlanExecutor executor;

    public PlanService(EngineControl engineControl) {
        this.engineControl = engineControl;
        // use virtual threads by default for parallel steps
        this.executor = new DefaultPlanExecutor(engineControl);
    }

    public List<StepResult> runPlan(Plan plan, DefaultFact fact, DefaultRuleEngineContext ctx) throws InterruptedException {
        return executor.execute(plan, fact, ctx);
    }

    //    @PreDestroy
    public void stop() {
        executor.shutdown();
    }
}