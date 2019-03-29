package ru.mipt.npm.integration.multivariate

import kotlin.reflect.KClass

/**
 * Marker interface for integrator parameters
 */
interface IntegrationKey<T : Any>

/**
 * A formulation of integration problem
 */
class IntegrationProblem private constructor(private val map: MutableMap<IntegrationKey<*>, Any>) {

    constructor() : this(HashMap<IntegrationKey<*>, Any>())

    fun <T : Any> get(key: IntegrationKey<T>, type: KClass<T>): T? = type.java.cast(map[key])

    inline operator fun <reified T : Any> get(key: IntegrationKey<T>) = get(key, T::class)

    operator fun <T : Any> set(key: IntegrationKey<T>, obj: T?) {
        if (obj == null) {
            map.remove(key)
        } else {
            map[key] = obj
        }
    }

    operator fun plus(problem: IntegrationProblem): IntegrationProblem {
        val map = this.map + problem.map
        return IntegrationProblem(map.toMutableMap())
    }

    /**
     * Copy this problem applying transformation
     */
    fun copy(block: IntegrationProblem.() -> Unit = {}) =
        IntegrationProblem(HashMap(map)).apply(block)
}

/**
 * A type alias for multivariate function
 * TODO replace by dimension checked version
 */
typealias MultivariateFunction = (DoubleArray) -> Double

object ObjectiveFunction : IntegrationKey<MultivariateFunction>

val IntegrationProblem.function get() = this[ObjectiveFunction]


class HyperBounds(val ranges: List<ClosedFloatingPointRange<Double>>) {

    val volume get() = ranges.map { it.endInclusive - it.start }.fold(1.0) { left, right -> left * right }

    val dimension get() = ranges.size

    companion object : IntegrationKey<HyperBounds>
}

@Suppress("FunctionName")
fun HyperBounds(vararg ranges: ClosedFloatingPointRange<Double>) = HyperBounds(ranges.asList())

operator fun HyperBounds.get(index: Int) = ranges[index]


object IntegrationResult : IntegrationKey<Double>

val IntegrationProblem.result get() = this[IntegrationResult]

interface MultivariateIntegrator {
    fun integrate(problem: IntegrationProblem): IntegrationProblem
}