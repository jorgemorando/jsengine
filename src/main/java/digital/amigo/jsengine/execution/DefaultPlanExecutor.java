package digital.amigo.jsengine.execution;

import digital.amigo.jsengine.DefaultFact;
import digital.amigo.jsengine.DefaultRuleEngineContext;
import digital.amigo.jsengine.TriggerResult;
import digital.amigo.jsengine.control.RuleEvaluationControl;

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

    private final RuleEvaluationControl eval;
    private final ExecutorService executor;
    private final boolean ownedExecutor;

    public DefaultPlanExecutor(RuleEvaluationControl eval, ExecutorService executor, boolean ownedExecutor) {
        this.eval = Objects.requireNonNull(eval, "eval");
        this.executor = Objects.requireNonNull(executor, "executor");
        this.ownedExecutor = ownedExecutor;
    }

    public DefaultPlanExecutor(RuleEvaluationControl eval) {
        this(eval, Executors.newCachedThreadPool(), true);
    }

    @Override
    public List<StepResult> execute(Plan plan, DefaultFact fact, DefaultRuleEngineContext context) throws InterruptedException {
        List<StepResult> results = new ArrayList<>(plan.steps().size());
        for (Step step : plan.steps()) {
            if (step.type() == StepType.SEQUENTIAL) {
                // Expect exactly one rule name
                String ruleName = step.ruleNames().get(0);
                try {
                    TriggerResult tr = eval.evaluate(ruleName, fact, context);
                    results.add(new StepResult.SequentialStepResult(step.id(), tr, null));
                } catch (Throwable t) {
                    results.add(new StepResult.SequentialStepResult(step.id(), null, t));
                }
            } else {
                // PARALLEL: submit one task per ruleName and wait for all to complete
                Map<String, Future<TriggerResult>> futures = new LinkedHashMap<>();
                for (String rn : step.ruleNames()) {
                    futures.put(rn, executor.submit(() -> {
                        try {
                            return eval.evaluate(rn, fact, context);
                        } catch (Throwable t) {
                            throw t;
                        }
                    }));
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
                results.add(new StepResult.ParallelStepResult(step.id(), collected, errors));
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