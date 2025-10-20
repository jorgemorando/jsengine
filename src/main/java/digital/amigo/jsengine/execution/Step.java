package digital.amigo.jsengine.execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Simple, human-friendly Step model.
 *
 * - SEQUENTIAL steps must contain exactly one rule name.
 * - PARALLEL steps contain one or more rule names (each will be executed concurrently).
 *
 * Steps do not carry execution logic; the PlanExecutor performs the actual evaluations.
 */
public final class Step {

    private final String id;
    private final StepType type;
    private final List<String> ruleNames;

    private Step(String id, StepType type, List<String> ruleNames) {
        this.id = Objects.requireNonNull(id, "id");
        this.type = Objects.requireNonNull(type, "type");
        this.ruleNames = ruleNames == null ? List.of() : List.copyOf(ruleNames);
    }

    public String id() { return id; }
    public StepType type() { return type; }
    public List<String> ruleNames() { return ruleNames; }

    public static Step sequential(String id, String ruleName) {
        Objects.requireNonNull(ruleName, "ruleName");
        return new Step(id, StepType.SEQUENTIAL, List.of(ruleName));
    }

    public static Step parallel(String id, List<String> ruleNames) {
        Objects.requireNonNull(ruleNames, "ruleNames");
        if (ruleNames.isEmpty()) throw new IllegalArgumentException("parallel step requires >= 1 rule");
        return new Step(id, StepType.PARALLEL, Collections.unmodifiableList(new ArrayList<>(ruleNames)));
    }
}