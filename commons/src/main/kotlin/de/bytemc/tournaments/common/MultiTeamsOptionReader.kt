package de.bytemc.tournaments.common

import de.bytemc.tournaments.api.TournamentMap
import de.bytemc.tournaments.api.TournamentTeamsOption
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * @author Nico_ND1
 */
class MultiTeamsOptionReader {

    fun readOptions(name: String): ArrayList<TournamentTeamsOption> {
        val result: ArrayList<TournamentTeamsOption> = ArrayList()
        val file = File("/home/maps/$name")
        val serviceInfo = readServiceInfo(File(file, "service.properties"))

        val files = file.listFiles { pathname: File? -> pathname?.isDirectory ?: false } ?: return result
        for (listFile in files) {
            if (listFile == null) continue

            val sizeName = listFile.name

            val maps: ArrayList<TournamentMap> = arrayListOf()
            for (mapFile in listFile.listFiles()) {
                val mapName = mapFile.name
                maps.add(TournamentMap(mapName))
            }

            val size = serviceInfo.getSize(sizeName)
            if (size != -1) {
                result.add(TournamentTeamsOption(size,
                    maps,
                    "${serviceInfo.servicePrefix}-$sizeName")
                )
            }
        }

        return result
    }

    private data class ServiceInfo(val servicePrefix: String, val sizes: Map<String, Int>) {
        fun getSize(name: String): Int {
            return sizes[name] ?: -1
        }
    }

    private fun readServiceInfo(file: File): ServiceInfo {
        val properties = Properties()
        properties.load(InputStreamReader(FileInputStream(file), StandardCharsets.UTF_8))

        var servicePrefix: String? = null
        val sizes: HashMap<String, Int> = HashMap()
        for (key in properties.keys()) {
            if (key !is String) continue

            if (key == "servicePrefix") {
                servicePrefix = properties.getProperty("servicePrefix")
            } else {
                sizes[key] = Integer.parseInt(properties.getProperty(key))
            }
        }

        return ServiceInfo(servicePrefix!!, sizes)
    }

}
