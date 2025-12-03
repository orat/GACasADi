## Description
Annotation processor for automatic generation of a numeric implementation based on a symbolic implementation. \
The generated code delegates the invocations and thus requires that the symbolic implementation is able to handle purely numeric multivectors. \
This is the case in our current implementation of SparseCGASymbolicMultivector and the caching functionality.


## Usage
- `@GenerateDelegate` can be annotated at a class with the desired numeric implementation.
	- `to`: the class providing the symbolic implementation on which the numeric implementation should be based on.


Example:
```java
@GenerateDelegate(to = SparseCGASymbolicMultivector.class)
public class SparseCGANumericMultivector extends DelegatingSparseCGANumericMultivector implements iMultivectorNumeric<SparseCGANumericMultivector, SparseCGASymbolicMultivector> {
    @Override
    protected SparseCGANumericMultivector create(SparseCGASymbolicMultivector sym) {
        return new SparseCGANumericMultivector(sym);
    }

    @Override
    public iConstantsFactory<SparseCGANumericMultivector> constants() {
        return CGAExprGraphFactory.instance.constantsNumeric();
    }

	// ...
}
```

The generated code will look like:
```java
/**
 * @see de.orat.math.cgacasadi.impl.SparseCGANumericMultivector
 */
public abstract class DelegatingSparseCGANumericMultivector implements iMultivector<SparseCGANumericMultivector> {
	protected final SparseCGASymbolicMultivector delegate;

	protected DelegatingSparseCGANumericMultivector(SparseCGASymbolicMultivector delegate) {
		this.delegate = delegate;
	}

	protected abstract SparseCGANumericMultivector create(SparseCGASymbolicMultivector delegate);

	/**
	 * @see de.orat.math.gacalc.spi.iMultivector#op
	 */
	@Override
	public SparseCGANumericMultivector op(SparseCGANumericMultivector b) {
		return create(this.delegate.op(b.delegate));
	}

	// ...
```


## Rules
#### Generated class
- Will have methods of common supertypes of the annotated class and the class given with the `to` parameter. Except for those methods which are already defined in the annotated class.


#### Annotated class
[All not checked] The annotated class must ...
- extend the generated class.
- implement the abstract `create` method provided by the generated class.
- implement all methods of supertypes which are not provided by the generated class or the supertypes.
- implement all methods of the common supertypes which can not be delegated. (Like the `constants` method.)


#### Methods errors
- Overloaded methods within the common supertype will raise an error.

