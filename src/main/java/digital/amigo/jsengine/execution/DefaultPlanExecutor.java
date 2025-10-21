package digital.amigo.jsengine.execution;

import digital.amigo.jsengine.DefaultFact;
import digital.amigo.jsengine.DefaultRuleEngineContext;
import digital.amigo.jsengine.TriggerResult;
import digital.amigo.jsengine.control.EngineControl;

import java.util.*;
import java.util.concurrent.*;

/**
 * Simple, readable PlanExecutor:
 * - Sequential step => evaluate single rule synchronously (on caller thread).
 * - Parallel step   => evaluate each rule concurrently using the provided ExecutorService.
 *
 * The executor is optionally provided; if omitted a cached thread pool is created and owned.
 * This design keeps steps minimal and concentrates execution policy inside the executor.
 */
public class DefaultPlanExecutor implements PlanExecutor {

    private final EngineControl engineControl;
    private final ExecutorService executor;
    private final boolean ownedExecutor;

    public DefaultPlanExecutor(EngineControl engineControl, ExecutorService executor, boolean ownedExecutor) {
        this.engineControl = Objects.requireNonNull(engineControl, "EngineControl should not be null");
        this.executor = Objects.requireNonNull(executor, "executor");
        this.ownedExecutor = ownedExecutor;
    }

    public DefaultPlanExecutor(EngineControl control) {

        this(control, Executors.newCachedThreadPool(), true);
    }

    @Override
    public List<StepResult> execute(Plan plan, DefaultFact fact, DefaultRuleEngineContext context) throws InterruptedException {
        List<StepResult> results = new ArrayList<>(plan.steps().size());
        for (Step step : plan.steps()) {
            if (step.type() == Step.Type.SEQUENTIAL) {
                // Expect exactly one rule name
                String ruleName = step.ruleNames().get(0);
                try {
                    TriggerResult tr = engineControl.getTriggerControl().evaluate(ruleName, fact, context);
                    results.add(new StepResult.Sequential(step.id(), tr, null));
                } catch (Throwable t) {
                    results.add(new StepResult.Sequential(step.id(), null, t));
                }
            } else {
                // PARALLEL: submit one task per ruleName and wait for all to complete
                Map<String, Future<TriggerResult>> futures = new LinkedHashMap<>();
                for (String rn : step.ruleNames()) {
                    futures.put(rn, executor.submit(() -> engineControl.getTriggerControl().evaluate(rn, fact, context)));
                }

                Map<String, TriggerResult> collected = new LinkedHashMap<>();
                Map<String, Throwable> errors = new LinkedHashMap<>();
                for (Map.Entry<String, Future<TriggerResult>> e : futures.entrySet()) {
                    String rn = e.getKey();
                    Future<TriggerResult> f = e.getValue();
                    try {
                        TriggerResult tr = f.get();
                        collected.put(rn, tr);
                    } catch (ExecutionException ee) {
                        errors.put(rn, ee.getCause());
                    } catch (CancellationException ce) {
                        errors.put(rn, ce);
                    }
                }
                results.add(new StepResult.Parallel(step.id(), collected, errors));
            }
        }
        return results;
    }

    @Override
    public void shutdown() {
        if (ownedExecutor) {
            executor.shutdownNow();
        }
    }
}