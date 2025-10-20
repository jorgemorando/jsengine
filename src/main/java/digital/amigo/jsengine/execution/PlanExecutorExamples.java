package digital.amigo.jsengine.execution;

import digital.amigo.jsengine.DefaultFact;
import digital.amigo.jsengine.DefaultRuleEngineContext;
import digital.amigo.jsengine.TriggerResult;
import digital.amigo.jsengine.control.RuleEvaluationControl;
import digital.amigo.jsengine.control.EngineControl;
import digital.amigo.jsengine.core.RuleEngine;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Step execution mode.
 */
public enum StepType {
    SEQUENTIAL, // a single rule is executed for this step
    PARALLEL    // multiple rules are executed in parallel for this step
}

/**
 * Small examples that show common usage patterns for DefaultPlanExecutor (virtual-thread backed by default).
 * Adapt rule names and facts to your project/test fixtures (e.g. TestUtils constants).
 */
public class PlanExecutorExamples {

    @Test
    public void simplePlanExecution() throws InterruptedException {
        // obtain a RuleEvaluationControl from the engine (bootstrap with your rules as needed)
        EngineControl engine = RuleEngine.newBuilder()
                // .withRules(...) // register rules as needed
                .build();
        RuleEvaluationControl eval = engine.getTriggerControl();

        // Build a plan:
        Plan plan = Plan.builder()
                // sequential step -> executes single rule synchronously
                .addStep(Step.sequential("check-age", "ageCheck"))
                // parallel step -> runs listed rules concurrently (virtual threads by default)
                .addStep(Step.parallel("parallel-decisions", List.of("ruleA", "ruleB", "ruleC")))
                .build();

        DefaultFact fact = new DefaultFact();
        fact.put("age", 30);
        DefaultRuleEngineContext ctx = new DefaultRuleEngineContext();

        // Create executor (uses virtual threads by default)
        DefaultPlanExecutor executor = new DefaultPlanExecutor(eval);

        try {
            List<StepResult> results = executor.execute(plan, fact, ctx);

            for (StepResult r : results) {
                System.out.println("STEP: " + r.stepId());
                if (r instanceof StepResult.SequentialStepResult s) {
                    TriggerResult tr = s.result();
                    System.out.println("  sequential -> triggered=" + (tr != null && tr.isTriggered()) + ", error=" + s.error());
                } else if (r instanceof StepResult.ParallelStepResult p) {
                    for (Map.Entry<String, TriggerResult> e : p.results().entrySet()) {
                        System.out.println("  parallel rule=" + e.getKey() + " -> triggered=" + (e.getValue() != null && e.getValue().isTriggered()));
                    }
                    p.errors().forEach((name, err) -> System.out.println("  parallel rule error: " + name + " -> " + err));
                }
            }
        } finally {
            executor.shutdown();
        }
    }

    @Test
    public void customExecutorUsage() throws InterruptedException {
        EngineControl engine = RuleEngine.newBuilder().build();
        RuleEvaluationControl eval = engine.getTriggerControl();

        Plan plan = Plan.builder()
                .addStep(Step.parallel("many-rules", List.of("r1", "r2", "r3", "r4")))
                .build();

        DefaultFact fact = new DefaultFact();
        DefaultRuleEngineContext ctx = new DefaultRuleEngineContext();

        // Create an explicit virtual-thread executor and pass it to the PlanExecutor
        var virtualThreadExecutor = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());
        DefaultPlanExecutor executor = new DefaultPlanExecutor(eval, virtualThreadExecutor, true);

        try {
            List<StepResult> results = executor.execute(plan, fact, ctx);
            // handle results (same pattern as above)
        } finally {
            executor.shutdown();
        }
    }
}