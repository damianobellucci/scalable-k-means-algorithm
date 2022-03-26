import org.apache.spark.rdd.RDD
import org.apache.spark.{ SparkContext}
import FunctionUtils.{meanDistance,weightedMeanPoint,findClosest}

object Kmeans extends  Serializable {

  var numIterations = 0

  def run(sc: SparkContext, sparkPoints: RDD[Array[Double]], num_k: Int, epsilon: Double) = {

    var centroids = ((0 until num_k) zip sparkPoints.takeSample(false, num_k, seed = 42)).toArray
    var finished = false

    var pairs = sc.emptyRDD[(Int, Array[Double])]

    do {
      pairs =
        sparkPoints
          .map(p => {
            (findClosest(p, centroids), p) //id closest cluster //point
          })

      val newCentroids =
        pairs
          .map(pair => {
            (pair._1, (pair._2, 1.0))
          }) //id_cluster,(point,peso)
          .reduceByKey(weightedMeanPoint)
          .map(c => (c._1, c._2._1))
          .collect()

      if (meanDistance(centroids, newCentroids) < epsilon) {
        finished = true

      } else centroids = newCentroids

      numIterations = numIterations + 1

    } while (!finished)

    (pairs,centroids)
  }
}
