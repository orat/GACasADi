## Disclaimer
The project is in an early state of development, so it is not advised to use it in real world applications. If you have feedback or feature suggestions, please create a new [GitHub Issue](https://github.com/orat/CGACasADi/issues).


## Description
This is a [CasADi](https://web.casadi.org/) based implementation of the [GACalcAPI](https://github.com/orat/GACalcAPI), which allows to create Geometric Algebra expression graphs for symbolic calculations for arbitray Geometric Algebra signatures. The aim of this implementation is to allow fast computations especially of Jacobian and Hessian derivatives by [CasADi](https://web.casadi.org/)'s automated differenciation functionality.

This project depends on [JCasADi](https://github.com/MobMonRob/JCasADi), a java wrapper for [CasADi](https://web.casadi.org/).

The readme for the annotation which generates a cached version of [`iMultivectorSymbolic`](https://github.com/orat/GACalcAPI/blob/master/src/main/java/de/orat/math/gacalc/spi/iMultivectorSymbolic.java) can be found in [GACasADi_SymbolicMultivectorCachingProcessor](GACasADi_SymbolicMultivectorCachingProcessor/README.md).

## Project status
At the moment only the algebras CGA and PGA are supported. For futher ones code has to be written, e.g. for specific support of 3d-Visualisation and implementation of some features, which are dependend to the algebra specifities. In the future instead of writing Java-code, all speficic functionality should be configurable in specific ascii files.

## Implementation of Projective Geometric Algebra (PGA)

### Symbols
| symbol           | latex        | Unicode      | description |
| :--------------: | ------------ | ------------ | ----------- |
| &#x03B5;&#x2080; | \epsilon_0 | \u03B5\u2080 | base vector representing the origin |
| &#x03B5;&#x1D62; | \epsilon_i | \u03B5\u1D62 | base vector representing the infinity |
| &#x03B5;&#x2081; | \epsilon_1 | \u03B5\u2081 | base vector representing x direction |
| &#x03B5;&#x2082; | \epsilon_2 | \u03B5\u2082 | base vector representing y direction |
| &#x03B5;&#x2083; | \epsilon_3 | \u03B5\u2083 | base vector representing z direction |
| &#x03C0;         | \pi        | \u03C0       | Ludolphs- or circle constant | Math.PI |
| &#x0045;&#x2083; | E_3        | \u0045\u2083 | Euclidean pseudoscalar | &#x03B5;&#x2081; &#x2227; &#x03B5;&#x2082; &#x2227; &#x03B5;&#x2083;     |
| &#x0045;         | E          | \u0045       | Pseudoscalar | &#x03B5;&#x1D62; &#x2227; &#x03B5;&#x2081; &#x2227; &#x03B5;&#x2082; &#x2227; &#x03B5;&#x2083; &#x2227; &#x03B5;&#x2080;|

### Builtin functions
The following functions are implemented specific for PGA:

| symbol      | description |
| :---------- | ------------ |
| exp()       | exponential of a bivector or a scalar |
| log()       | logarithm of general rotor/even multivector (should be normalized) |
| sqrt()      | squared root of a general rotor/even multivector or a scalar |

The following functions, which are available in general, are not supported for PGA:

| symbol      | description |
| :---------- | ------------ |
| negate14()  | negate the signs of the vector- and 4-vector parts of an multivector. Usable to implement general-inverse. |

The following functions are not yet supported:

| symbol      | description |
| :---------- | ------------ |
| up()        | up-projection of a euclidean vector into the projected space |
| down()      | down-projection of a multivector into the euclidean space by normalization and rejection from the minkowski plane E0 |
| euclid()    | euclidean part of the multivector, only blades not formed basis vectors squaring to 0 or -1 |
| idle()      | idle part of the multivector, only blades formaed by basis vectors which includes at minimum one which squares to 0 or -1 |

### Builtin opererators
