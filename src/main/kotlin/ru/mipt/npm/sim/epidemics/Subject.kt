package ru.mipt.npm.sim.epidemics

enum class InfectionState {
    SUSCEPTIBLE,
    EXPOSED,
    INFECTIOUS,
    RECOVERED
}

class Subject(
        val id: Int,
        val closeContacts: List<Int>,
        val otherContacts: List<Int>
)

class SubjectState(
        val subject: Subject
) {
    var state: InfectionState = InfectionState.SUSCEPTIBLE
        private set

    var exposedDate: Int? = null
        private set

    var infectedDate: Int? = null
        private set

    var recoveryDate: Int? = null
        private set


    fun expose(date: Int) {
        if (state == InfectionState.SUSCEPTIBLE) {
            state = InfectionState.EXPOSED
            exposedDate = date
        }
    }

    fun infect(date: Int) {
        if (state != InfectionState.EXPOSED) error("Expected 'EXPOSED' state")
        state = InfectionState.INFECTIOUS
        infectedDate = date
    }

    fun recover(date: Int) {
        if (state != InfectionState.INFECTIOUS) error("Expected 'EXPOSED' state")
        state = InfectionState.RECOVERED
        recoveryDate = date
    }
}

