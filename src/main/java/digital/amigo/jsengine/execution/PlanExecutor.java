package digital.amigo.jsengine.execution;

import digital.amigo.jsengine.DefaultFact;
import digital.amigo.jsengine.DefaultRuleEngineContext;

import java.util.List;

/**
 * PlanExecutor executes a Plan and returns ordered StepResult instances (in plan order).
 *
 * Implementations use provided RuleEvaluationControl (or any evaluation strategy) to run rules.
 */
public interface PlanExecutor {
    /**
     * Execute the plan using provided fact and context for every rule invocation.
     *
     * This method blocks until the plan completes (successfully or with failures).
     */
    List<StepResult> execute(Plan plan, DefaultFact fact, DefaultRuleEngineContext context) throws InterruptedException;

    /**
     * Shut down underlying resources (executor) if owned by the implementation.
     */
    void shutdown();
}

