package Evaluator.Indexes

import Evaluator.{IndexClustering, InfoClusterization}
import Utils.UtilsFunctions.distance

object WCSS extends IndexClustering {
  override def run(info: InfoClusterization): Double = {
    info.pairs
      .groupBy(_._1) //raggruppo punti per cluster
      .map(cluster => { //per ogni punto calcolo norma l2 con suo centroide
        cluster._2.map(el => {
          distance(el._2, info.hashCenters(el._1))
        })
          .sum //risoluzione prima sommatoria, cioè quella intraclasse
      })
      .sum //risoluzione sommatoria intraclasse
  }
}
