package Kmeans

import Utils.UtilsFunctions.{findClosest, meanDistance, weightedMeanPoint}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

object Kmeans extends Serializable {

  var numIterations = 0

  def run(sparkPoints: RDD[Array[Double]], num_k: Int, epsilon: Double) = {

    var centroids = ((0 until num_k) zip sparkPoints.takeSample(false, num_k, seed = 42)).toArray
    var finished = false

    var pairs =
      sparkPoints
      .map(p => {
        (findClosest(p, centroids), p) //id closest cluster //point
      })
    do {
      val newCentroids =
        pairs
          .map(pair => {
            (pair._1, (pair._2, 1.0))
          })
          .reduceByKey(weightedMeanPoint)
          .map(c => (c._1, c._2._1))
          .collect()

      if (meanDistance(centroids, newCentroids) < epsilon) {
        finished = true

      } else
        {
          centroids = newCentroids
          pairs =
            sparkPoints
              .map(p => {
                (findClosest(p, centroids), p) //id closest cluster,point
              })
        }
      numIterations = numIterations + 1
    } while (!finished)
    //couples id_cluster,point for final assignment of points to cluster
    (pairs, centroids,numIterations)
  }
}



