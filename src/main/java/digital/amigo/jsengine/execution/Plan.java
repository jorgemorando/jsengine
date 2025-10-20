package digital.amigo.jsengine.execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Ordered plan that contains top-level steps only (no nested/inner steps).
 */
public final class Plan {

    private final List<Step> steps;

    private Plan(List<Step> steps) {
        this.steps = steps == null ? List.of() : List.copyOf(steps);
    }

    public List<Step> steps() { return steps; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final List<Step> steps = new ArrayList<>();

        public Builder addStep(Step step) {
            steps.add(Objects.requireNonNull(step));
            return this;
        }

        public Plan build() {
            return new Plan(Collections.unmodifiableList(new ArrayList<>(steps)));
        }
    }
}