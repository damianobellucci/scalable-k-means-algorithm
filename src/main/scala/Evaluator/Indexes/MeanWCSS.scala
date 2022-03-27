package Evaluator.Indexes

import Evaluator.{IndexClustering, InfoClusterization}
import Utils.UtilsFunctions.distance


object MeanWCSS extends IndexClustering {
  override def run(info: InfoClusterization): Double = {

    info
      .pairs
      .map(pair => {
        (pair._1,  (math.pow(distance(pair._2, info.hashCenters(pair._1)),2), 1))
      }) //prepare couple for reduction
      .reduceByKey((x, y) => {
        (x._1 + y._1, x._2 + y._2)
      }) //sum distances and obtain number of points in cluster
      .map(el => {
        el._2._1 / el._2._2
      }) //mean variance of cluster
      .sum()

  }

}

