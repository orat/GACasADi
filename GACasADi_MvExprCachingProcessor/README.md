## Description
Annotation processor for automatic generation of caching for implementations of [`iMultivectorSymbolic`](https://github.com/orat/GACalcAPI/blob/master/src/main/java/de/orat/math/gacalc/spi/iMultivectorSymbolic.java).

It generates a subclass of the annotated class which uses the [`CGASymbolicFunctionCache`](../CGACasADi/src/main/java/de/orat/math/cgacasadi/caching/CGASymbolicFunctionCache.java).


## Usage
- [`@GenerateCached`](src/main/java/de/orat/math/cgacasadi/caching/annotation/api/GenerateCached.java) can be annotated at a class which implements `iMultivectorSymbolic`. All possible methods of `iMultivectorSymbolic` and the annotated class will be cached.
	- `warnFailedToCache`: if true, will issue a warning for all methods for which a caching override method could not be generated for other reasons than `@Uncached`.
	- `warnUncached`: if true, will issue a warning for the methods annotated with `@Uncached`.
- [`@Uncached`](src/main/java/de/orat/math/cgacasadi/caching/annotation/api/Uncached.java) can be annotated at a method of a class annotated with `@GenerateCached`. It suppresses the generation of a caching override method.

Example:
```java
@GenerateCached(warnFailedToCache = true, warnUncached = true)
public abstract class SparseCGASymbolicMultivector implements iMultivectorSymbolic<SparseCGASymbolicMultivector> {

	@Uncached
	public SparseCGASymbolicMultivector scalar(double value) {
		...
	}

	@Override
	public SparseCGASymbolicMultivector pseudoscalar() {
		...
	}
}
```


## Rules
#### Annotated class
- The annotated class must implement `iMultivectorSymbolic`. Otherwise an error will be issued.
- The annotated class must implement `iMultivectorSymbolic<itself>`, but this is not enforced with an explicit error.
- The annotated class is prohibited to define type variables (generics). Otherwise an error will be issued.
- The annotated class cannot be final. Otherwise an error will be issued.


#### Methods default caching
- All methods of `iMultivectorSymbolic` (and all methods of the interfaces which it does directly or indirectly extend) which return an object which type will be substituted by `SparseCGASymbolicMultivector` will be cached per default.
- All methods of the annotated class which return an object of the annotated class will be cached per default.


#### Methods errors
- Parameters of methods can be of the types "annoted class" or `int`. Otherwise an error will be issued. To suppress the error, annotate an invalid method with `@Uncached`.
- Overloaded methods will not be cached.
	- An error will be issued. To suppress the error, use `@Uncached`.
	- An overloaded method will be cached nonetheless, if all overloads but one are by themselves invalid (`private`, `static`, ...) or `@Uncached`.
	- This is the case for the union set of the methods of `iMultivectorSymbolic` and the annotated class.
	- Overloads could be permitted in a future version if their name string keys in the cache would be extended with something like "_1", "_2", ... .


#### Methods warnings
- `private` methods will not be cached. A warning will be issued if `warnFailedToCache == true`.
- `static` methods will not be cached. A warning will be issued if `warnFailedToCache == true`.
- `abstract` methods will not be cached. A warning will be issued if `warnFailedToCache == true`.


## Hint: `@Uncached` default methods
To set the semantics of `@Uncached` for a default method of `iMultivectorSymbolic` in the annotated class, override the method, annotate it and delegate to the super method.

Example:
```java
@GenerateCached(warnFailedToCache = true, warnUncached = true)
public abstract class SparseCGASymbolicMultivector implements iMultivectorSymbolic<SparseCGASymbolicMultivector> {

	@Override
	@Uncached
	public SparseCGASymbolicMultivector op(SparseCGASymbolicMultivector b) {
		return iMultivectorSymbolic.super.op(b);
	}
}
```

