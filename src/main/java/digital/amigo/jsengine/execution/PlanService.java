package digital.amigo.jsengine.execution;

import digital.amigo.jsengine.DefaultFact;
import digital.amigo.jsengine.DefaultRuleEngineContext;
import digital.amigo.jsengine.control.RuleEvaluationControl;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.Executors;

@Service
public class PlanService {

    private final RuleEvaluationControl eval;
    private final DefaultPlanExecutor executor;

    public PlanService(RuleEvaluationControl eval) {
        this.eval = eval;
        // use virtual threads by default for parallel steps
        this.executor = new DefaultPlanExecutor(eval);
    }

    public List<StepResult> runPlan(Plan plan, DefaultFact fact, DefaultRuleEngineContext ctx) throws InterruptedException {
        return executor.execute(plan, fact, ctx);
    }

    @PreDestroy
    public void stop() {
        executor.shutdown();
    }
}