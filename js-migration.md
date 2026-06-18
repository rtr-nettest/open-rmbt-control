# Migrating the QoS script interpreter off Nashorn

> **Status:** ✅ **implemented on this branch (`feature/js-migration`) and green** — full unit suite
> passes on **JDK 25** (297 tests, 0 failures). Two fixes were needed on top of the original commit:
> (1) removed an unreachable `catch (ScriptException)` in `eval()` that broke compilation; (2) bumped
> GraalVM from `23.1.2` → **`24.2.0`**, because `23.1.x` uses `sun.misc.Unsafe.ensureClassInitialized`
> (removed in JDK 22) and therefore crashed at `Engine.build()` on the JDK 25 runtime with
> `NoSuchMethodError`.
>
> **Version is JDK-selected (Maven profile).** No single Truffle line spans JDK 17 and JDK 22+, so the
> GraalJS version is chosen by the JDK running the build: the base property defaults to `24.2.0`
> (JDK 21+), and a profile `graaljs-jdk-legacy` activated by `<jdk>(,21)</jdk>` falls back to `23.1.2`
> for JDK 17–20. Verified on JDK 25: `graalvm.version` resolves to `24.2.0` (legacy profile inactive).
> Implication: this ties the GraalJS version to the **build** JDK, so build with the JDK you deploy on
> — `24.2.x` needs JDK 21+ at runtime, `23.1.x` runs on JDK 17 but crashes on JDK 22+. The bytecode
> target stays `release 17` either way.

> **Resolved finding — a real bug the old Nashorn code had.** The standalone
> `nashorn-core` exposes its classes under `org.openjdk.nashorn.*`, but `TestScriptInterpreter.eval(...)`
> still detects the script result object by comparing against the *JDK-bundled* class name
> `jdk.nashorn.api.scripting.ScriptObjectMirror` (and the Rhino `sun.org.mozilla.javascript.NativeObject`).
> Neither matches, so an object-literal rule such as `result = {type:'failure', key:'voip.timeout'}` is
> handed back as a raw `ScriptObjectMirror` instead of being converted to an `EvalResult` — the rule's
> result key is silently dropped. Confirmed by running the interpreter end-to-end: the object-literal
> eval returned `org.openjdk.nashorn.api.scripting.ScriptObjectMirror`. This migration deletes that
> reflection entirely (typed `Value.getMember(...)`) and so fixes the bug — covered by
> `TestScriptInterpreterTest.objectLiteralResult_returnsEvalResultWithTypeAndKey`.

## Where JavaScript is used today

The control server runs a tiny embedded-JavaScript DSL **only** for QoS evaluation. The whole
surface area is two files:

- `src/main/java/at/rtr/rmbt/utils/testscript/TestScriptInterpreter.java` — owns the engine and runs the scripts.
- `src/main/java/at/rtr/rmbt/utils/testscript/SystemApi.java` — the host object bound as `nn` (e.g. `nn.coalesce`, `nn.parseTraceroute`, `nn.getCount`).

The scripts themselves are **data**, stored in the database (`qos_test_objective.results`,
`qos_test_type_desc`), e.g.:

```js
var result=null;
if (nn.coalesce(voip_result_in_mean_jitter, 50000000) < 50000000) result=true; else result=false;
```
```js
if (voip_result_status=='TIMEOUT') result={type: 'failure', key: 'voip.timeout'};
```

Engine acquisition (current):

```java
ScriptEngineManager sem = new ScriptEngineManager();
jsEngine = sem.getEngineByName("JavaScript");      // -> Nashorn
```

Dependency (current): `org.openjdk.nashorn:nashorn-core:15.7` (the standalone Nashorn backport,
since the JDK-bundled Nashorn was removed in Java 15 / JEP 372). Target is `java.version = 17`.

### Language features the scripts actually use
Deliberately small — this matters because it keeps the migration low-risk:
- `var`, `if/else`, arithmetic, comparisons
- `parseInt(...)`
- object literals as a result signal: `result = {type:'failure', key:'...'}`
- host calls on `nn` (a Java object): `nn.coalesce(a, b)`, `nn.parseTraceroute(x)`, `nn.getCount(x)`, `nn.isNull(x)`
- variables injected as bindings from the stored result map (`voip_result_in_*`, …)

No ES modules, no async, no DOM, no `require`. This is ES5-level scripting.

## Why migrate

- **Nashorn is end-of-life.** Deprecated in JDK 11 (JEP 335), removed from the JDK in JDK 15
  (JEP 372). The standalone `nashorn-core` artifact still exists but is effectively in maintenance
  and trails the language/JDK; on JDK 21+/25 it relies on increasingly fragile internals.
- **JDK 25 readiness** (see project memory): the codebase is being pushed toward building/testing on
  JDK 21–25. Standalone Nashorn is the riskiest dependency for that.
- **Nashorn-specific reflection in our code.** `TestScriptInterpreter` already hard-codes Nashorn
  internals to read the result object:
  ```java
  if (jsEngine.getClass().getCanonicalName().equals("jdk.nashorn.api.scripting.NashornScriptEngine"))
      if (result.getClass().getCanonicalName().equals("jdk.nashorn.api.scripting.ScriptObjectMirror"))
          ...
  jsEngineNativeObjectGetter = result.getClass().getMethod("get", Object.class);
  type = (String) jsEngineNativeObjectGetter.invoke(result, "type");
  ```
  This brittle block is exactly what a modern engine lets us delete.

## Options

### Option A — GraalJS (recommended, and what was implemented)
GraalVM's JavaScript engine, usable on a **stock** OpenJDK (no GraalVM required; it just runs
interpreted-only, which is fine for these microscopic scripts). Actively maintained, ES2023+,
truthful `ScriptEngine` (JSR-223) implementation **and** a richer Polyglot API. Note the minimum
runtime JDK: `24.2.x` needs **JDK 21+** (used here, since the build/test runtime is JDK 25); the older
`23.1.x` ran on JDK 17 but crashes on JDK 22+ (removed `sun.misc.Unsafe.ensureClassInitialized`).

Two integration styles:

1. **JSR-223 drop-in** — keep `ScriptEngine`/`Bindings`, switch the engine name:
   ```java
   jsEngine = new ScriptEngineManager().getEngineByName("graal.js");
   ```
   Smallest diff, but needs a couple of options set (host access for `nn`, and Nashorn-compat for
   the loose typing the scripts rely on).

2. **Polyglot `Context`** — `org.graalvm.polyglot.Context`/`Value`. More control over sandboxing
   and host access; cleanest way to read `result.type`/`result.key`. Slightly larger rewrite.

**Recommendation: GraalJS via the Polyglot API**, because it lets us delete the Nashorn reflection
block and read the result object with a typed `Value` instead. (If we want the absolute minimum
change first, JSR-223 `graal.js` is a valid intermediate step.)

### Option B — keep standalone Nashorn, just bump it
Lowest effort (`nashorn-core` newer line), but it only postpones the problem and stays the
weak link for JDK 25. Not recommended beyond a stopgap.

### Option C — drop JavaScript entirely, evaluate the DSL in Java
The scripts are simple enough to interpret with a small expression evaluator (or precompiled
rules). **But** the scripts are shared DB content across the RMBT ecosystem and authored as JS;
re-implementing a compatible evaluator (operators, `parseInt`, object-literal results, `nn.*`) is
more total work and risk than adopting GraalJS, and it diverges from upstream. Not recommended
unless the goal is to remove scripting as a concept.

## Recommended migration plan (GraalJS / Polyglot)

1. **Dependencies** — replace
   ```xml
   <dependency>
     <groupId>org.openjdk.nashorn</groupId>
     <artifactId>nashorn-core</artifactId>
     <version>15.7</version>
   </dependency>
   ```
   with the GraalJS artifacts (community edition):
   ```xml
   <dependency>
     <groupId>org.graalvm.polyglot</groupId>
     <artifactId>polyglot</artifactId>
     <version>24.2.0</version>
   </dependency>
   <dependency>
     <groupId>org.graalvm.polyglot</groupId>
     <artifactId>js-community</artifactId>   <!-- pulls the JS language; use 'js' for GraalVM EE -->
     <version>24.2.0</version>
     <type>pom</type>
   </dependency>
   ```
   (If staying on JSR-223 instead: `org.graalvm.js:js` + `org.graalvm.js:js-scriptengine` on the
   older coordinates. Pick one coordinate family and keep versions aligned.)

2. **Engine/context setup** in `TestScriptInterpreter`:
   - Build a `Context` that allows calling methods on the `nn` host object:
     ```java
     Context ctx = Context.newBuilder("js")
         .allowHostAccess(HostAccess.EXPLICIT)   // or a curated HostAccess exposing SystemApi methods
         .build();
     ctx.getBindings("js").putMember("nn", new SystemApi());
     ```
   - Expose only what's needed. `HostAccess.EXPLICIT` + `@HostAccess.Export` on the `SystemApi`
     methods is the safe choice (annotate `coalesce`, `parseTraceroute`, `getCount`, `isEmpty`,
     `isNull`). Avoid `allowAllAccess(true)`.

3. **Per-eval bindings** — keep injecting the result map as variables. With Polyglot, set members on
   a fresh bindings scope per evaluation (don't share mutable global state across requests).

4. **Replace the Nashorn reflection block** (the `ScriptObjectMirror`/`NativeObject` `getMethod("get")`
   dance) with typed `Value` access:
   ```java
   Value result = ctx.getBindings("js").getMember("result");
   if (result != null && result.hasMembers() && result.hasMember("type")) {
       String type = result.getMember("type").asString();
       String key  = result.hasMember("key") ? result.getMember("key").asString() : null;
       evalResult = new EvalResult(EvalResultType.valueOf(type.toUpperCase(Locale.US)), key);
   }
   ```
   This deletes the `jsEngineNativeObjectGetter`/`alredayLookedForGetter` machinery entirely.

5. **Loose-typing / Nashorn-compat** — the scripts compare Java `Integer`/`Long` bindings against JS
   number literals and do string `==` (`voip_result_status=='TIMEOUT'`). GraalJS handles host
   numbers fine; verify the `==` string comparisons and the `result=true/false` boolean reads.
   If any rough edges appear, GraalJS offers `js.nashorn-compat=true` as a transitional option —
   prefer fixing the few spots over enabling broad compat long-term.

6. **Thread-safety** — today `jsEngine` is a `static` singleton with a shared `GLOBAL_SCOPE`
   binding; concurrent QoS evaluations share it. A Polyglot `Context` is **not** safe for concurrent
   use by multiple threads. Options: a `Context` per evaluation (cheap enough here), a small pool,
   or a `ThreadLocal<Context>`. This is a latent correctness issue worth fixing during the move.

7. **Keep `SystemApi` engine-agnostic** — it already is (plain Java). Just add the
   `@HostAccess.Export` annotations (or a curated `HostAccess`). Note the recently added
   `coalesce(value, fallback)` must remain exported.

8. **Tests** — `SystemApiTest` stays valid. Add an interpreter-level test that runs a representative
   stored script (e.g. the VOIP jitter rule and the `result={type,key}` timeout rule) end-to-end
   through the new engine, asserting SUCCESS/FAILURE/EvalResult. This guards the object-literal path
   that previously needed Nashorn reflection.

## Rollout / risk notes
- Behaviour-preserving change: same scripts, same `nn` API, same `EvalResult` contract. The only
  observable goal is "identical verdicts, modern engine."
- Validate against a corpus of real stored results across all QoS test types (voip, dns, tcp, udp,
  http, website, traceroute, non_transparent_proxy), not just VOIP — the traceroute description
  path uses `nn.parseTraceroute` and the `%$IF ... %`/`%$SWITCH ...%` control macros in
  `TestScriptInterpreter` also run through the engine.
- On a stock JDK, GraalJS runs interpreted (no JIT compilation of JS). For these sub-millisecond
  rule scripts that's irrelevant; do not pull in the Graal compiler just for this.
- Bundle-size: GraalJS is heavier than `nashorn-core`. Acceptable for a server app.

## Suggested sequencing
1. Land the `nn.coalesce` fix (done) so evaluation is correct on the current engine.
2. Introduce GraalJS behind the same `TestScriptInterpreter` API; delete the Nashorn reflection.
3. Fix the threading model (Context per eval / pool).
4. Verify verdict parity on a result corpus, then drop the `nashorn-core` dependency.
