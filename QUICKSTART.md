# QUICKSTART

This quickstart documents the common usage scenarios. Each scenario includes a short summary and a minimal example that follows the APIs exercised in the tests.

Notes:
- Replace test helpers (e.g. CLEAN_RULE, CLEAN_RULE2) with your Rule/RuleVersion instances when using in production.
- All examples assume the project classes on the classpath (digital.amigo.jsengine.*).

---

## 1) Instantiate engine
Summary: create an EngineControl instance using the builder. The engine will load embedded JS helpers from resources.

Example:
```java
EngineControl controls = RuleEngine.newBuilder().build();
```
What to check: controls != null.

---

## 2) Register rules (initial registration)
Summary: register one or many rules at engine bootstrap. Tests use convenience builder `.withRules(...)` but you can also register later through RulesControl.

Example (bootstrap-time):
```java
EngineControl controls = RuleEngine.newBuilder()
    .withRules(CLEAN_RULE) // CLEAN_RULE is a test helper; use your Rule instances
    .build();

RulesControl rulesControl = controls.getRulesControl();
boolean registered = rulesControl.isRegistered(RULE_NAME);
```
What to check: rule present in registry and list().size() matches expected.

---

## 3) Shared RuleEngineContext across multiple rule evaluations
Summary: evaluate multiple rules for a single fact while sharing a RuleEngineContext so JS can read/write shared data during a multi-rule trigger.

Example:
```java
var triggerControl = RuleEngine.newBuilder()
    .withRules(CLEAN_RULE, CLEAN_RULE2)
    .build()
    .getTriggerControl();

DefaultFact fact = new DefaultFact();
fact.put("name", RULE_1_VALUE);

DefaultRuleEngineContext ctx = new DefaultRuleEngineContext();
MultiTriggerResult results = triggerControl.evaluateRulesFor(fact, ctx);

// Inspect returned context and aggregated results
var sharedCtx = results.getContext();
```
What to check: results.getContext() is not null and contains entries for each evaluated rule.

---

## 4) Single rule fire (success / failure)
Summary: execute a single rule by name against a fact and optional context. TriggerResult indicates if the rule fired and if it succeeded.

Example:
```java
EngineControl controls = RuleEngine.newBuilder().withRules(CLEAN_RULE).build();
RuleEvaluationControl eval = controls.getTriggerControl();

DefaultFact fact = new DefaultFact();
fact.put("name", "decision"); // value that makes rule succeed in tests

TriggerResult r1 = eval.evaluate(RULE_NAME, fact, null);
boolean fired = r1.isFired();
boolean success = r1.isSuccess();

// failing case:
fact.put("name", "other");
TriggerResult r2 = eval.evaluate(RULE_NAME, fact, null);
```
What to check: r.isFired(), r.isSuccess() as expected.

---

## 5) Multiple rules fire and inspect successes / failures
Summary: evaluate several registered rules for the same fact/context and inspect MultiTriggerResult for successful and failed rule results.

Example:
```java
EngineControl controls = RuleEngine.newBuilder().withRules(CLEAN_RULE, CLEAN_RULE2).build();
RuleEvaluationControl eval = controls.getTriggerControl();

DefaultFact fact = new DefaultFact();
fact.put("name", "decision"); // triggers success on CLEAN_RULE, fail on CLEAN_RULE2

MultiTriggerResult multi = eval.evaluateRulesFor(fact, new DefaultRuleEngineContext());

var successful = multi.getSuccessful(); // ordered collection of successful TriggerResults
var failed = multi.getFailed();         // ordered collection of failed TriggerResults
```
What to check: multi.getResultCount() equals number of evaluated rules; sizes of successful/failed match expected.

---

## 6) Rule versioning and selecting versions
Summary: the registry supports versioned rules. Register multiple versions and evaluate by explicit version or default-to-latest behaviour.

Registering versions:
```java
RulesControl rules = RuleEngine.newBuilder().build().getRulesControl();
rules.register(CLEAN_RULE);     // v1 in tests
rules.register(CLEAN_RULE_v2);  // v2 in tests
```

Evaluate a specific version:
```java
// evaluate rule by name + explicit integer version
TriggerResult r = eval.evaluate(RULE_NAME, 1, fact, null); // evaluate v1
TriggerResult r2 = eval.evaluate(RULE_NAME, 2, fact, null); // evaluate v2

// evaluate using default latest
TriggerResult latest = eval.evaluate(RULE_NAME, fact, null); // uses latest registered version
int usedVersion = latest.getRuleVersion().version();
```
What to check: result.getRuleVersion().version() matches the requested version (or latest when omitted).

---

## 7) Performance / speed testing
Summary: tests include simple loops that repeatedly evaluate a compiled rule to measure throughput. The evaluation API is used in tight loops to measure "fires per second".

Example pattern:
```java
RuleEvaluationControl eval = RuleEngine.newBuilder().withRules(CLEAN_RULE).build().getTriggerControl();
DefaultFact fact = new DefaultFact();
fact.put("name", "decision");

int iterations = 10_000;
long start = System.currentTimeMillis();
for (int i = 0; i < iterations; i++) {
    TriggerResult r = eval.evaluate(RULE_NAME, fact, null);
    assert r.isFired() && r.isSuccess();
}
long elapsed = System.currentTimeMillis() - start;
double fps = iterations / (elapsed / 1000.0);
```
What to check: throughput meets your expectations; tests assert thresholds (e.g. >1000, >2000, >6000 fps in test-suite environment).

---

## 8) Spring integration (controls are Spring beans)
Summary: when running as a Spring Boot app, the control components (RulesControl, RuleEvaluationControl, EngineControl) can be autowired into services or controllers.

Example usage:
```java
@Service
public class RuleService {
    @Autowired
    private RulesControl rulesControl;

    @Autowired
    private RuleEvaluationControl evaluationControl;

    public void registerRule(Rule r) { rulesControl.register(r); }
    public TriggerResult evaluate(String name, DefaultFact fact) {
        return evaluationControl.evaluate(name, fact, null);
    }
}
```
What to check: use the actual bean names / method signatures in control classes (see src/main/java/digital/amigo/jsengine/control).

---

If you want, a runnable example can be prepared that constructs Rule and RuleVersion instances (replacing CLEAN_RULE/CLEAN_RULE2) and demonstrates register + evaluate end-to-end matching the exact method signatures in this codebase.