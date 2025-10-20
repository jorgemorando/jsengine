package digital.amigo.jsengine.execution;

import digital.amigo.jsengine.TriggerResult;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Result models for Plan execution.
 *
 * - SequentialStepResult: result for a single rule evaluation (may have an exception).
 * - ParallelStepResult: mapping of ruleName -> TriggerResult (exceptions represented as null + errors map).
 *
 * Keep these small and explicit for readability.
 */
public abstract class StepResult {

    private final String stepId;

    protected StepResult(String stepId) {
        this.stepId = Objects.requireNonNull(stepId);
    }

    public String stepId() { return stepId; }

    public abstract boolean success();

    public static final class SequentialStepResult extends StepResult {
        private final TriggerResult result;
        private final Throwable error;

        public SequentialStepResult(String stepId, TriggerResult result, Throwable error) {
            super(stepId);
            this.result = result;
            this.error = error;
        }

        public TriggerResult result() { return result; }
        public Throwable error() { return error; }

        @Override
        public boolean success() {
            return error == null && result != null && result.isTriggered();
        }

        @Override
        public String toString() {
            return "SequentialStepResult{" + "stepId=" + stepId() + ", success=" + success() + ", result=" + result + ", error=" + error + '}';
        }
    }

    public static final class ParallelStepResult extends StepResult {
        private final Map<String, TriggerResult> results;
        private final Map<String, Throwable> errors;

        public ParallelStepResult(String stepId, Map<String, TriggerResult> results, Map<String, Throwable> errors) {
            super(stepId);
            this.results = results == null ? Collections.emptyMap() : Collections.unmodifiableMap(results);
            this.errors = errors == null ? Collections.emptyMap() : Collections.unmodifiableMap(errors);
        }

        public Map<String, TriggerResult> results() { return results; }
        public Map<String, Throwable> errors() { return errors; }

        @Override
        public boolean success() {
            if (!errors.isEmpty()) return false;
            // consider the parallel step successful only if all rule evaluations triggered
            return results.values().stream().allMatch(r -> r != null && r.isTriggered());
        }

        @Override
        public String toString() {
            return "ParallelStepResult{" + "stepId=" + stepId() + ", success=" + success() + ", results=" + results.keySet() + ", errors=" + errors.keySet() + '}';
        }
    }
}