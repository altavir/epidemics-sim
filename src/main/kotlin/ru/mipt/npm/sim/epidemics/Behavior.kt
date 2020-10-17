package ru.mipt.npm.sim.epidemics

import org.apache.commons.math3.random.RandomGenerator

interface Behavior {
    /**
     * Generate daily contact for given [subject]
     */
    fun generateDailyContacts(generator: RandomGenerator, subject: Subject, date: Int): List<Int>
}

object DefaultBehavior : Behavior {

    @OptIn(ExperimentalStdlibApi::class)
    override fun generateDailyContacts(
        generator: RandomGenerator,
        subject: Subject,
        date: Int
    ): List<Int> = buildList {
        subject.closeContacts.forEach { contact ->
            if (generator.nextDouble() < 0.5) {
                add(contact)
            }
        }
        subject.otherContacts.forEach { contact ->
            if (generator.nextDouble() < 0.1) {
                add(contact)
            }
        }
    }
}



