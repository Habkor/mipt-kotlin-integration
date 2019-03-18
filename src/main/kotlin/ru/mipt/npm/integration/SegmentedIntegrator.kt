package ru.mipt.npm.integration

/**
 * An integrator that allows to split integration region into subregions and integrate them separately
 */
class SegmentedIntegrator(
        val buildIntegrator: (from: Double, to: Double) -> UnivariateIntegrator
) : UnivariateIntegrator {

    override fun integrate(from: Double, to: Double, function: (Double) -> Double): Double {
        return buildIntegrator(from, to).integrate(from, to, function)
    }

    /**
     * Integrate using provided points as region borders.
     */
    fun integrate(vararg borders: Double, function: (Double) -> Double): Double {
        require(borders.size >= 2) { "There should be at least two border parameters" }
        return borders
                .sorted()
                .distinct()
                .zipWithNext()
                .map { pair ->
                    buildIntegrator(pair.first, pair.second)
                            .integrate(pair.first, pair.second, function)
                }.sum()
    }
}