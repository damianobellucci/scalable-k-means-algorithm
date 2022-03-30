package Evaluator.Indexes

import Evaluator.{IndexClustering, InfoClusterization}
import Utils.UtilsFunctions.distance

object WCSS extends IndexClustering {
  override def run(info: InfoClusterization): Double = {
    info.pairs
      .map(pair=>{
        math.pow(distance(pair._2, info.hashCenters(pair._1)),2)
      })
      .sum
  }
}


