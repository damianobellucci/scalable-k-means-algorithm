package Evaluator.Indexes

import Evaluator.{IndexClustering, InfoClusterization}
import Utils.UtilsFunctions.{distance, weightedMean}

object CalinskiHarabasz extends IndexClustering {
  override def run(info: InfoClusterization): Double = {

    val pairs = info.pairs.persist()

    val mainCentroid =
      pairs
        .map(pair => {
          pair._2
        })
        .reduce((x, y) => weightedMean(x, 1.0, y, 1.0))

    val ssb =
      pairs
        .countByKey() //id_cluster,samples_in_cluster
        .map(cluster => {
          math.pow(distance(info.hashCenters(cluster._1), mainCentroid),2) * cluster._2
        })
        .sum

    val num_k = info.hashCenters.size

    (ssb / WCSS.run(info)) / ((pairs.count() - num_k) / (num_k - 1))
  }
}
