package ru.mipt.npm.sim.epidemics

import kscience.plotly.*


data class PopulationState(val infected: Int, val recovered: Int)

@OptIn(ExperimentalStdlibApi::class)
fun Population.collectData(): Map<Int, PopulationState>{
    val maxRecoveryDate = values.map{ it.recoveryDate ?: 0 }.max() ?: 1
    return (0..maxRecoveryDate).associateWith {date->
        val infected = values.count {
            (it.exposedDate ?: Int.MAX_VALUE) <= date && it.recoveryDate?:Int.MAX_VALUE > date
        }
        val recovered = values.count { (it.recoveryDate ?: Int.MAX_VALUE) <= date }
        PopulationState(infected,recovered)
    }
}


fun Population.show(): Plot {
    val data = collectData()

    val plot = Plotly.plot {
        trace {
            name = "infected"
            x.set(data.keys.toIntArray())
            y.set(data.values.map { it.infected })
        }
        trace{
            name = "recovered"
            x.set(data.keys.toIntArray())
            y.set(data.values.map { it.recovered })
        }
    }

    plot.makeFile()

    return plot
}