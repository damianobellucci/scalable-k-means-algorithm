import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import FunctionUtils.{weightedMean,distance}
import java.io.Serializable

abstract class IndexClustering {
  def run(info:InfoClusterization): Double
}

class InfoClusterization(points:RDD[(Int, Array[Double])], centroids: Array[(Int, Array[Double])]) extends Serializable{
  var pairs = points
  val hashCenters = centroids.toMap

}

object WCSS extends IndexClustering {
  override def run(info: InfoClusterization): Double = {
    info.pairs
      .groupBy(_._1) //raggruppo punti per cluster
      .map(cluster=>{ //per ogni punto calcolo norma l2 con suo centroide
        cluster._2.map(el=>{distance(el._2,info.hashCenters(el._1))})
          .sum //risoluzione prima sommatoria, cioè quella intraclasse
      })
      .sum //risoluzione sommatoria intraclasse
  }
}

//funziona male per dati sensori
object CalinskiHarabasz extends IndexClustering{
  override def run(info: InfoClusterization): Double = {

    val pairs = info.pairs.persist()

    val mainCentroid =
      pairs
        .map(pair=>{pair._2})
        .reduce((x,y)=>weightedMean(x,1,y,1))

    val ssb =
      pairs
        .countByKey() //id_cluster,samples_in_cluster
        .map(cluster=>{distance(info.hashCenters(cluster._1),mainCentroid)*cluster._2})
        .sum

    val num_k = info.hashCenters.size

    (ssb/(WCSS).run(info))/(pairs.count() * num_k/(num_k-1))
  }
}

object WBSS extends IndexClustering{
  override def run(info: InfoClusterization): Double = {

    info
      .pairs
      .map(pair=>{(pair._1,(distance(pair._2,info.hashCenters(pair._1)),1))}) //prepare couple for reduction
      .reduceByKey((x,y)=>{(x._1+y._1,x._2+y._2)}) //sum distances and obtain number of points in cluster
      .map(el=>{el._2._1/el._2._2}) //mean variance of cluster
      .sum()/info.hashCenters.size //in between mean variance of clusters

  }

}


class Evaluator(info:InfoClusterization) {
  def start(f:IndexClustering): Double ={
    f.run(info)
  }
}


object Main extends Serializable {
  def main(args: Array[String]): Unit = {

    val dataset_path = args(0)
    val output_path = args(1)
    val n_threads = args(2)
    val numK = args(3).toInt

    val conf = new SparkConf().setAppName("KMeans").setMaster("local[" + n_threads + "]")
    val sc = new SparkContext(conf)

    val input = sc.textFile(dataset_path)
    val sparkPoints = input.map(f => {
      f
        .split(",")
        .drop(1)
        .map(_.toDouble)
    })

    (2 to numK).map((num_k)=>{
      Kmeans.run(sc, sparkPoints, num_k, 0.0001)
    })
      .map(result=>{
        val evaluator = new Evaluator(new InfoClusterization(result._1,result._2))
      (evaluator.start(WCSS),evaluator.start(CalinskiHarabasz))
      }
    ).map(println)



    //sc.parallelize(results).coalesce(1).saveAsTextFile(output_path)
    //println( (System.nanoTime() - globalT) / 1000000000)


  }

}