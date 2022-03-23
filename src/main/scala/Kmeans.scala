import java.io._
import org.apache.spark.{SparkConf, SparkContext}

object Kmeans extends Serializable {

  val epsilon = 0.0001
  val numK = 3
  var numIterations = 0

  def distance(p1: Array[Double], p2: Array[Double]) =
    math.sqrt((p1 zip p2).map(e => e._1 - e._2).map(e => e * e).sum)


  //return a tuple with the id of nearest cluster and distance between this and the point
  def findClosest(p: Array[Double],
                  centroids: Array[(Int, Array[Double])]) = {
    centroids
      .map(centroid=>{ (centroid._1, distance(centroid._2,p))})
      .minBy(_._2)
  }


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

      (2 to numK).foreach(num_k => {

        val t0 = System.nanoTime()

        var centroids =
          ((0 until num_k) zip
            sparkPoints.takeSample(false, num_k, seed = 42)).
            toArray

        var finished = false

        var pairs =
          sparkPoints
            .map(p =>{
              val closestCluster =  findClosest(p, centroids)
              (
                closestCluster._1 //id closest cluster
                ,
                p //point
                ,
                closestCluster._2 //distance cluster point
              )
            }
            )

        do {
          //pairs = list(id_cluster,point)
          pairs =
            sparkPoints
              .map(p =>{
                val closestCluster =  findClosest(p, centroids)
                (
                  closestCluster._1 //id closest cluster
                  ,
                  p //point
                  ,
                  closestCluster._2 //distance cluster point
                )
              }
              )

          val newCentroids =
            pairs
              .map(pair=>{(pair._1,(pair._2,1.0))})  //id_cluster,point,peso
              .reduceByKey(weightedMeanPoint)
              .map(c => (c._1, c._2._1)).
              collect()

          if (meanDistance(centroids, newCentroids) < epsilon)
            finished = true
          else centroids = newCentroids

          numIterations = numIterations + 1

        } while (!finished)


        //parallel computation for getting list of wcss for each cluster. list[(id_cluster,wcss)]

          pairs
            .map(pair=>{
              (
                pair._1 //key of cluster to reduce by key then
                ,
                (pair._3,1.0) //distanza punto cluster precedentemente calcolata e peso punto nella somma finale
              )})
            .reduceByKey(weightedSum)
            .map((el)=>{
              (
                1,
                  (
                el._2._1/ el._2._2 //wcss in a cluster
                ,
                1.0)
                )
            })
            .
            reduceByKey((x,y)=>{(x._1+y._1,x._2+y._2)})
            .map(el=>{
              (num_k
                ,
                el._2._1/el._2._2
                ,
                numIterations
                ,
                (System.nanoTime() - t0) / 1000000000
              )
            })
            .saveAsTextFile(output_path+"/"+num_k)
      })
  }
}
