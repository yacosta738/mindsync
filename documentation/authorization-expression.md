# Authorization Expression

It is an authorization evaluation mechanism build on top of spring security. Authorization Expression is there to ease business facing authorization checks.

It will allow you to use a new `can('action', #element)` expression in `@PreAuthorize` and `@PostAuthorize`:

Java Version:

```java
@PreAuthorize("can('update', #dummy)")
public void update(AuthDummy dummy) {
  // ...
}
```

Kotlin Version:

```kotlin
@PreAuthorize("can('update', #dummy)")
fun update(dummy: AuthDummy) {
  // ...
}
```

Those expressions will then be evaluated in dedicated `AccessChecker`. To do so, you'll have to define spring beans (`@Component` or `@Service`) implementing `AccessChecker<T>`:

Java Version:

```java
@Component
class DummyAccessChecker implements AccessChecker<AuthDummy> {

  @Override
  public boolean can(AccessContext<AuthDummy> access) {
    //TODO: your business authorization logic
  }
}
```

Kotlin Version:

```kotlin
@Component
class DummyAccessChecker: AccessChecker<AuthDummy> {
    override fun can(access: AccessContext<AuthDummy>): Boolean {
        //TODO: your business authorization logic
    }
}
```

Authorization Expression's only job is to call the `AccessChecker` for your object class.
