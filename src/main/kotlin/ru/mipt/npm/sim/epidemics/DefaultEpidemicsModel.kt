package ru.mipt.npm.sim.epidemics

import org.apache.commons.math3.random.JDKRandomGenerator
import org.apache.commons.math3.random.RandomGenerator

typealias Population = Map<Int, SubjectState>


interface EpidemicsModel {
    fun generatePopulation(generator: RandomGenerator, size: Int = 1000): Population
    fun propagateSubjectState(generator: RandomGenerator, subjectState: SubjectState, date: Int): SubjectState
    fun propagate(generator: RandomGenerator, population: Population, date: Int): Population
}

//TODO add model parametrization
object DefaultEpidemicsModel : EpidemicsModel {

    override fun generatePopulation(generator: RandomGenerator, size: Int): Population {
        //TODO add custom distributions
        return generatePopulation(generator, size, 3, 10)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun generatePopulation(
            generator: RandomGenerator,
            size: Int,
            closeContactsNum: Int,
            otherContactsNum: Int
    ): Population {
        return (0 until size).associateWith { subjectId ->
            val closeContacts = buildList<Int> {
                repeat(closeContactsNum) {
                    val id = generator.nextInt(size)
                    if (id != subjectId) {
                        add(id)
                    }
                }
            }

            val otherContacts = buildList<Int> {
                repeat(otherContactsNum) {
                    val id = generator.nextInt(size)
                    if (id != subjectId) {
                        add(id)
                    }
                }
            }

            val subject = Subject(subjectId, closeContacts, otherContacts)
            return@associateWith SubjectState(subject)
        }
    }

    override fun propagateSubjectState(generator: RandomGenerator, subjectState: SubjectState, date: Int): SubjectState {
        when (subjectState.state) {
            InfectionState.SUSCEPTIBLE, InfectionState.RECOVERED -> {
                //do nothing
            }
            InfectionState.EXPOSED -> {
                //TODO add custom distributions
                if (date - subjectState.exposedDate!! >= 3) {
                    subjectState.infect(date)
                }
            }
            InfectionState.INFECTIOUS -> {
                if (generator.nextDouble() < 0.1) {
                    subjectState.recover(date)
                }
            }
        }
        return subjectState
    }

    val behavior = DefaultBehavior

    override fun propagate(generator: RandomGenerator, population: Population, date: Int): Population {
        //TODO check population health and change behavior
        population.values.forEach {
            propagateSubjectState(generator, it, date)
        }
        population.forEach { (_, subj) ->
            val contacts: List<Int> = behavior.generateDailyContacts(generator, subj.subject, date)
            contacts.forEach { contactId ->
                val otherContact = population[contactId]
                        ?: error("Undefined subject with id $contactId")

                if (subj.state == InfectionState.INFECTIOUS && generator.nextDouble() < 0.25) {
                    otherContact.expose(date)
                }

                if (otherContact.state == InfectionState.INFECTIOUS && generator.nextDouble() < 0.25) {
                    subj.expose(date)
                }
            }
        }
        return population
    }
}