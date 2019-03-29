package ru.mipt.npm.integration.multivariate

import kotlin.reflect.KClass

/**
 * Marker interface for integrator parameters
 */
interface IntegrationKey<T : Any>

/**
 * A formulation of integration problem
 */
class IntegrationProblem {
    private val map = HashMap<IntegrationKey<*>, Any>()


    fun <T : Any> get(key: IntegrationKey<T>, type: KClass<T>): T? = type.java.cast(map[key])

    inline operator fun <reified T : Any> get(key: IntegrationKey<T>) = get(key, T::class)

    operator fun <T : Any> set(key: IntegrationKey<T>, obj: T?) {
        if (obj == null) {
            map.remove(key)
        } else {
            map[key] = obj
        }
    }
}

/**
 * A type alias for multivariate function
 */
typealias MultivariateFunction = (DoubleArray) -> Double

object ObjectiveFunction : IntegrationKey<MultivariateFunction>

var IntegrationProblem.function
    get() = this[ObjectiveFunction]
    set(value) {
        this[ObjectiveFunction] = value
    }

class HyperVolume(val ranges: List<ClosedFloatingPointRange<Double>>) {

    val volume get() = ranges.map { it.endInclusive - it.start }.fold(1.0) { left, right -> left * right }

    companion object : IntegrationKey<HyperVolume>
}

operator fun HyperVolume.get(index: Int) = ranges[index]

interface MultivariateIntegrator {
    fun integrate(problem: IntegrationProblem): IntegrationProblem
}