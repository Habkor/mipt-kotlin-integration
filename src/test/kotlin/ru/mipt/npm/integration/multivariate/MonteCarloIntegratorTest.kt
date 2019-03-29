package ru.mipt.npm.integration.multivariate

import org.junit.Test

class MonteCarloIntegratorTest {
    val function: MultivariateFunction = { (x, y) -> x * x + y * y }
    val bounds = HyperBounds((-1.0..1.0), (-1.0..0.0))

    @Test
    fun testSimpleMonteCarlo() {
        val res = MonteCarloIntegrator.integrate(bounds, function).result!!
        println(res)
    }
}