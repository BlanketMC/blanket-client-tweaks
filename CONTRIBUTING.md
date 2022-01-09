# The style of the project


## Code style:
```java
@Mixin(SoemClass.class)
public class Foo_PatchNameMixin {
    
    @Shadow private final String someString;

    // If annotation has multiple params, do write it in multiple line.
    @Inject(
            method = "bar(ZZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lio/github/blanket/ReturnType;baz(ID)Z"
            ),
            index = 2
    )
    private void meaningfulFunctionName(boolean firstArg, boolean secondArg) { //No space before '(', space between ')' and '{'
        if (firstArg || secondArg) { //space between 'if' and `(`, space after the last ')' and '{'
            switch (this.someString) {
                case "a":
                    /*do something*/
                    break;
                default:
                    /*do default*/
            }
        }
    } //No stacked closure '}}}' or ')))' at the end of expressions, classes
}
```

## Config:
Every fix has to be switchable in config
```java
public final class Config {
    
    //by YOU!
    @ConfigEntry(
            description = "A detailed description",
            issues = {"MC-24", "MC-42"}, //Only if there is/are reported issues.
            categories = {BUGFIX, EXPERIMENTAL}
    )
    public static boolean someConfig = false;
}
```
Comment the author,  
Don't write the annotation in multi-line,  
Detail a description.

## Tweaks/Fixes
The mixin class should be named : `"${MixinTargetClassName}_${Fix/Tweak name}Mixin"`  
If you need a helper class, put it in `fixes` package with the fix name.  
If multiple helpers are needed, create a package in `fixes` and put classes there.

## Any change
If the change is not a one-line fix, create a branch and do a PR.  
