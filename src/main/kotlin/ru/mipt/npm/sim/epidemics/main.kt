package ru.mipt.npm.sim.epidemics

import org.apache.commons.math3.random.JDKRandomGenerator

fun main() {
    val generator = JDKRandomGenerator()
    val population = DefaultEpidemicsModel.generatePopulation(generator)
    var date = 0

    val patientZero = generator.nextInt(population.size)
    population[patientZero]?.expose(0)

    while (
        !population.values.all {
            it.state == InfectionState.RECOVERED || it.state == InfectionState.SUSCEPTIBLE
        }
    ) {
        date++
        DefaultEpidemicsModel.propagate(generator, population, date)

    }

    println("Epidemics ended at $date")
    println(population.count { it.value.state == InfectionState.RECOVERED })
    population.show()
}