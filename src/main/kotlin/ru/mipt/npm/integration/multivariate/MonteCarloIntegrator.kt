package ru.mipt.npm.integration.multivariate

import kotlin.random.Random

interface RandomGenerator {
    fun next(): Double

    companion object : IntegrationKey<RandomGenerator>
}

class JDKRandomGenerator(seed: Int = -1) : RandomGenerator {
    private val random = Random(seed)
    override fun next(): Double = random.nextDouble()
}

val IntegrationProblem.generator get() = this[RandomGenerator] ?: JDKRandomGenerator()

object CallNumber : IntegrationKey<Int>

/**
 * Simple hyper-rectangle Monte-Carlo integration
 */
object MonteCarloIntegrator : MultivariateIntegrator {
    const val DEFAULT_CALL_NUM = 10000

    override fun integrate(problem: IntegrationProblem): IntegrationProblem {

        val function = problem.function ?: error("Function not defined")
        val volume = problem[HyperBounds] ?: error("Volume not defined")
        val callNum = problem[CallNumber] ?: DEFAULT_CALL_NUM

        val generator = problem.generator

        fun HyperBounds.sample(): DoubleArray {
            return DoubleArray(dimension) {
                val range = ranges[it]
                range.start + generator.next() * (range.endInclusive - range.start)
            }
        }

        var sum = 0.0
        repeat(callNum) {
            sum += function(volume.sample())
        }

        val res=  sum*volume.volume/callNum
        //TODO use previous result
        return problem.copy{
            this[IntegrationResult] = res
        }
    }

    fun integrate(rect: HyperBounds, function: MultivariateFunction): IntegrationProblem =
        integrate(IntegrationProblem().apply {
            this[ObjectiveFunction] = function
            this[HyperBounds] = rect
        })
}