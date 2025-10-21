package digital.amigo.jsengine.execution;

import digital.amigo.jsengine.DefaultFact;
import digital.amigo.jsengine.DefaultRuleEngineContext;
import digital.amigo.jsengine.TriggerResult;
import digital.amigo.jsengine.control.EngineControl;

import digital.amigo.jsengine.core.RuleEngine;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import static digital.amigo.jsengine.execution.Step.parallel;
import static digital.amigo.jsengine.execution.Step.sequential;
import static digital.amigo.util.TestUtils.CLEAN_RULE;
import static digital.amigo.util.TestUtils.CLEAN_RULE2;
import static digital.amigo.util.TestUtils.CLEAN_RULE3;


/**
 * Small examples that show common usage patterns for DefaultPlanExecutor (virtual-thread backed by default).
 * Adapt rule names and facts to your project/test fixtures (e.g. TestUtils constants).
 */
public class PlanExecutorTests {

    @Test
    public void simplePlanExecution() throws InterruptedException {
        // obtain a RuleEvaluationControl from the engine (bootstrap with your rules as needed)
        EngineControl engineControl = RuleEngine.newBuilder()
                .withRules(CLEAN_RULE,CLEAN_RULE2,CLEAN_RULE3) // register rules as needed
                .build();

        // Build a plan:
        Plan plan = Plan.builder()
                // sequential step -> executes single rule synchronously
                .addStep(sequential("run_foo", CLEAN_RULE.getName()))
                // parallel step -> runs listed rules concurrently (virtual threads by default)
                .addStep(parallel("run_bar_&_baz_in_parallel", List.of(CLEAN_RULE2.getName(), CLEAN_RULE3.getName())))
                .build();

        DefaultFact fact = new DefaultFact();
        fact.put("name", "baz");
        DefaultRuleEngineContext ctx = new DefaultRuleEngineContext();

        // Create executor (uses virtual threads by default)
        VirtualPlanExecutor executor = new VirtualPlanExecutor(engineControl);

        try {
            List<StepResult> results = executor.execute(plan, fact, ctx);

            for (StepResult r : results) {
                System.out.println("STEP: " + r.stepId());
                if (r instanceof StepResult.Sequential s) {
                    TriggerResult tr = s.result();
                    System.out.println(tr);
                } else if (r instanceof StepResult.Parallel p) {
                    for (Map.Entry<String, TriggerResult> e : p.results().entrySet()) {
                        System.out.println(e.getValue());
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
        EngineControl engineControl = RuleEngine.newBuilder()
                .withRules(CLEAN_RULE,CLEAN_RULE2,CLEAN_RULE3)
                .build();

        Plan plan = Plan.builder()
                .addStep(parallel("many-rules", List.of(CLEAN_RULE.getName(), CLEAN_RULE2.getName(), CLEAN_RULE3.getName())))
                .build();

        DefaultFact fact = new DefaultFact();
        fact.put("name", "baz");
        DefaultRuleEngineContext ctx = new DefaultRuleEngineContext();

        // Create an explicit virtual-thread executor and pass it to the PlanExecutor
        var virtualThreadExecutor = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());
        DefaultPlanExecutor executor = new DefaultPlanExecutor(engineControl, virtualThreadExecutor, true);

        try {
            List<StepResult> results = executor.execute(plan, fact, ctx);
            // handle results (same pattern as above)
        } finally {
            executor.shutdown();
        }
    }
}