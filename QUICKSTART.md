# QUICKSTART — Developer tutorial

A short, hands‑on tutorial that follows the scenarios exercised in the test-suite (src/test/java/.../RuleEngineTests.java). Each section is a small step you can copy & run while adapting the placeholders (CLEAN_RULE, RULE_NAME, etc.) to your own Rule/RuleVersion instances.

### Prerequisites
- Project built and on your IDE classpath.
- GraalVM JDK configured as JAVA_HOME for running tests / app.
- Familiarity with the domain objects: Rule, RuleVersion, DefaultFact, DefaultRuleEngineContext.

## Creating an engine instance (one-liner)
You can use the RuleEngineBuilder to load the embedded JS helpers and operate it through the controls interfaces located in the `.control.*` package.

```java
EngineControl controls = RuleEngine.newBuilder().build();
// quick check:
assert controls != null;
```

## Registering rules at bootstrap (or later)

The rule registration process is straight forward. Register rules when building the engine or via RulesControl after startup. 

Let's start by having some simple helper factory methods that creat 2 rules. `foo_rule` and `bar_rule`
```java
   public static Rule rule1() {
    return Rule.createWithName("foo_rule")
            .when(
                    factField("name").isEqualTo("foo")
            )
            .then("true")
            .build();
    }

    public static Rule rule2() {
        return Rule.createWithName("bar_rule")
                .when(
                        factField("name").isEqualTo("bar")
                )
                .then("true")
                .build();
    }

    /** Same rule name as rule1 but with different condition (therefore a new version v2). */
    public static Rule rule1v2() {
        return Rule.createWithName("foo_rule")
                .when(  
                        factField("name").exists()
                        .and(
                        factField("name").isEqualTo("bar"))
                )
                .then("true")
                .build();
    }
```
You have to provide a structured code that will be executed within the JS engine with the rule logic. In our case, our logic checks a variable ***name*** of `fact` against the literal ***foo*** and ***bar*** respectively and assignes result to a variable of the same name as the rule within the `context`. 
`fact` and `context` are reserved words, the first accesses the structure of the object the rule has been evaluated for, the second holds contextual information for the current evaluation execution.

Bootstrap-time registration (convenience):
```java
EngineControl controls = RuleEngine.newBuilder()
    .withRules(rule1(), rule2()) // test helpers; replace with your Rule instances
    .build();

RulesControl rules = controls.getRulesControl();
assert rules.isRegistered(rule1().getName());
```

Registering after boot:
```java
RulesControl rules = RuleEngine.newBuilder().build().getRulesControl();
rules.register(rule1());            // register foo_rule 
rules.register(rule2());            // register bar_rule 
rules.register(rule1v2());     // register different versions of foo_rule
```

## Evaluating a specific rule (success / failure)
You can evaluate by rule name. This will check the fact against a single rule specified by the name. The object TriggerResult will report if fired and success.

```java
RuleEvaluationControl eval = controls.getTriggerControl();

DefaultFact fact = new DefaultFact();
fact.put("name", "foo"); // will make the test rule1 succeed

TriggerResult r = eval.evaluate(rule1().getName(), fact);//executes the latest version of the rule against the fact
if (r.isEvaluated()) {//true
    boolean ok = r.isTriggered();//true
    // inspect payload and rule version if needed
    r.getRuleVersion().version() // 2
}
```

Notice the result contains `isFired()` method to check if the rule was actually triggered by the `fact`. It may be the conditions were not met and the rule was not fired at all.

## Evaluating multiple rules and share execution context
Summary: run many rules against the same fact and a shared RuleEngineContext so rules can read/write shared state.

```java
var trigger = RuleEngine.newBuilder()
    .withRules(rule1(), rule2())
    .build()
    .getTriggerControl();

DefaultRuleEngineContext ctx = new DefaultRuleEngineContext();
ctx.put("shared_var",1)//this variable will be accessible by all rules that fire. In fact, conditions of rules can use context variables to check if they are suitable to fire.
DefaultFact fact = new DefaultFact();
fact.put("name", "foo");


MultiTriggerResult results = trigger.evaluateRulesFor(fact, ctx);

// examine results and shared context
var successful = results.getSuccessful(); //1 - foo_rule
var failed     = results.getFailed(); //1 - bar_rule
var sharedCtx  = results.getContext();//3 shared_var,foo_rule, bar_rule

results.getContext().forEach((k,v) -> System.out.println(k + " -> " + v));
```

Tip: MultiTriggerResult contains ordered successful/failed TriggerResult lists and the context map modified by each rule.

## Versioning — register & evaluate specific versions
The rule registry supports named/versioned rules. You can register multiple versions and request a specific version when evaluating.

Register versions:
```java
RulesControl rules = RuleEngine.newBuilder().build().getRulesControl();
rules.register(rule1());         // v1 foo_rule
rules.register(rule1v2());      // v2 foo_rule
```

Evaluate a specific version (inferred API):
```java
// evaluate by name + version number (method signatures used in tests)
var RULE_NAME = rule1().getName();
TriggerResult v1 = eval.evaluate(RULE_NAME, 1, fact, null); // use version 1
TriggerResult latest = eval.evaluate(RULE_NAME, fact, null); // use latest registered
int usedVersion = latest.getRuleVersion().version();
```
NOTICE: The last parameter of evaluate is the context, we're not passing any context to the trigger.

8) Spring integration (autowiring controls)
Summary: the engine exposes control beans suitable for autowiring when running as a Spring Boot application.

```java
@Service
public class RuleService {
    @Autowired
    private RulesControl rulesControl;

    @Autowired
    private RuleEvaluationControl evaluationControl;

    public void register(Rule r) { rulesControl.register(r); }
    public TriggerResult evaluate(String name, DefaultFact f) {
        return evaluationControl.evaluate(name, f, null);
    }
}
```

> Practical tips
> - Use DefaultRuleEngineContext when rules must share state across multiple evaluations.
> - Keep rule scripts small and deterministic for best performance under GraalVM.

