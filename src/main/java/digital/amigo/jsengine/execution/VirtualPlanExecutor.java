package digital.amigo.jsengine.execution;

import digital.amigo.jsengine.DefaultFact;
import digital.amigo.jsengine.DefaultRuleEngineContext;
import digital.amigo.jsengine.TriggerResult;
import digital.amigo.jsengine.control.EngineControl;
import digital.amigo.jsengine.control.RuleEvaluationControl;

import java.util.*;
import java.util.concurrent.*;

/**
 * PlanExecutor that uses a thread-per-task Executor backed by Java Virtual Threads (Project Loom)
 * for parallel step executions. Sequential steps run on the caller thread for predictability.
 *
 * Constructors:
 * - VirtualPlanExecutor(eval) -> creates a virtual-thread per-task executor (owned).
 * - VirtualPlanExecutor(eval, executor, ownedExecutor) -> provide a custom executor.
 * - VirtualPlanExecutor(eval, useVirtualThreads) -> toggle between virtual threads and cached pool.
 */
public class VirtualPlanExecutor implements PlanExecutor {

    private final EngineControl eval;
    private final ExecutorService executor;
    private final boolean ownedExecutor;

    public VirtualPlanExecutor(EngineControl eval) {
        this(eval, /* useVirtualThreads= */ true);
    }

    public VirtualPlanExecutor(EngineControl eval, boolean useVirtualThreads) {
        this(eval,
             createExecutor(useVirtualThreads),
             true);
    }

    public VirtualPlanExecutor(EngineControl eval, ExecutorService executor, boolean ownedExecutor) {
        this.eval = Objects.requireNonNull(eval, "eval");
        this.executor = Objects.requireNonNull(executor, "executor");
        this.ownedExecutor = ownedExecutor;
    }

    private static ExecutorService createExecutor(boolean virtual) {
        if (virtual) {
            // thread-per-task executor backed by virtual threads (JDK 19+ / Loom)
            return Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());
        } else {
            return Executors.newCachedThreadPool();
        }
    }

    @Override
    public List<StepResult> execute(Plan plan, DefaultFact fact, DefaultRuleEngineContext context) throws InterruptedException {
        List<StepResult> results = new ArrayList<>(plan.steps().size());
        for (Step step : plan.steps()) {
            if (step.type() == Step.Type.SEQUENTIAL) {
                // SEQUENTIAL: exactly one rule name
                String ruleName = step.ruleNames().get(0);
                try {
                    TriggerResult tr = eval.getTriggerControl().evaluate(ruleName, fact, context);
                    results.add(new StepResult.Sequential(step.id(), tr, null));
                } catch (Throwable t) {
                    results.add(new StepResult.Sequential(step.id(), null, t));
                }
            } else {
                // PARALLEL: submit one task per ruleName and wait for all to complete
                Map<String, Future<TriggerResult>> futures = new LinkedHashMap<>();
                for (String rn : step.ruleNames()) {
                    futures.put(rn, executor.submit(() -> eval.getTriggerControl().evaluate(rn, fact, context)));
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