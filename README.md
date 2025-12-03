## Disclaimer
The project is in an early state of development, so it is not advised to use it in real world applications. If you have feedback or feature suggestions, please create a new [GitHub Issue](https://github.com/orat/CGACasADi/issues).


## Description
This is a [CasADi](https://web.casadi.org/) based implementation of the [GACalcAPI](https://github.com/orat/GACalcAPI), which allows to create Geometric Algebra expression graphs for symbolic calculations for arbitray Geometric Algebra signatures. The aim of this implementation is to allow fast computations especially of Jacobian and Hessian derivatives by [CasADi](https://web.casadi.org/)'s automated differenciation functionality.

This project depends on [JCasADi](https://github.com/MobMonRob/JCasADi), a java wrapper for [CasADi](https://web.casadi.org/).

The readme for the annotation which generates a cached version of [`iMultivectorSymbolic`](https://github.com/orat/GACalcAPI/blob/master/src/main/java/de/orat/math/gacalc/spi/iMultivectorSymbolic.java) can be found in [GACasADi_SymbolicMultivectorCachingProcessor](GACasADi_SymbolicMultivectorCachingProcessor/README.md).

