import java.io._
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.HashMap

object Kmeans extends Serializable {

  val epsilon = 0.0001
  val numK = 20
  var numIterations = 0

  def distance(p1: Array[Double], p2: Array[Double]) =
    math.sqrt((p1 zip p2).map(e => e._1 - e._2).map(e => e * e).sum)

  def findClosest(p: Array[Double],
                  centroids: Array[(Int, Array[Double])]): Int =
    centroids.map(c => (c._1, distance(c._2, p))).
      minBy(_._2)._1

  def weightedMean(x: Array[Double], n: Double, y: Array[Double], m: Double) =
    (x zip y).map(e => {
      (e._1 * n + e._2 * m) / (n + m)
    })

  def weightedMeanPoint(p1: (Array[Double], Double),
                        p2: (Array[Double], Double)) = {
    (weightedMean(p1._1, p1._2, p2._1, p2._2), p1._2 + p2._2)
  }

  def meanDistance(c: Array[(Int, Array[Double])],
                   newc: Array[(Int, Array[Double])]) = {
    ((c zip newc).
      map(c => distance(c._1._2, c._2._2)).
      sum) / c.length
  }

  var hashMap = new HashMap[Int, Array[Double]]

  def weightedSum(p1: (Double, Double),
                  p2: (Double, Double)) = {
    (p1._1+p2._1,p1._2+p2._2)
  }

  def main(args: Array[String]): Unit = {

    val dataset_path = args(0)
    val output_path = args(1)
    val n_threads = args(2)

    val conf = new SparkConf().setAppName("KMeans").setMaster("local["+n_threads+"]")
    val sc = new SparkContext(conf)

    val input = sc.textFile(dataset_path)
    val sparkPoints = input.map(f => {
      f
        .split(",")
        .drop(1)
        .map(_.toDouble)
    })

    val tt0 = System.nanoTime()

    val result =
      (2 to numK).map(num_k => {

        val t0 = System.nanoTime()

        var centroids =
          ((0 until num_k) zip
            sparkPoints.takeSample(false, num_k, seed = 42)).
            toArray

        var finished = false

        var pairs = sparkPoints.map(p => (findClosest(p, centroids), p))

        do {
          //pairs = list(id_cluster,point)
          pairs =
            sparkPoints
              .map(p => (findClosest(p, centroids), p))

          val newCentroids =
            pairs
              .map(pair=>{(pair._1,(pair._2,1.0))})
              .reduceByKey(weightedMeanPoint)
              .map(c => (c._1, c._2._1)).
              collect()

          if (meanDistance(centroids, newCentroids) < epsilon)
            finished = true
          else centroids = newCentroids

          numIterations = numIterations + 1

        } while (!finished)

        centroids.map(el => {
          hashMap.put(el._1, el._2)
        })

        //parallel computation for getting list of wcss for each cluster. list[(id_cluster,wcss)]

        val wcss_list =
          pairs
            .map(pair=>{
              val centroid = hashMap.getOrElse(pair._1, 0).asInstanceOf[Array[Double]] //lo zero in get or else viene messo per unwrappare l'option
              (
                //key of cluster to reduce by key then
                pair._1
                ,
                (distance(centroid,pair._2),1.0)
              )})
            .reduceByKey(weightedSum)
            .map((el)=>{
              (
                el._1,
                el._2._1/ el._2._2
              )
            }).collect()

        //return (num_k, mean_wcss, time_execution)
        (num_k, wcss_list.map(_._2).sum/num_k,numIterations,(System.nanoTime() - t0) / 1000000000)
      }).toList

    sc.parallelize(result).saveAsTextFile(output_path)

    val t1 = ((System.nanoTime() - tt0) / 1000000000)

    println(t1)
  }
}
