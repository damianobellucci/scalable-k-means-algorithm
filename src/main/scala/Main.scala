import Evaluator.{Evaluator, InfoClusterization}
import Kmeans.Kmeans
import _root_.Evaluator.Indexes.{CalinskiHarabasz, MeanWCSS, WCSS}
import org.apache.spark.{SparkConf, SparkContext}
import java.io.Serializable
import Utils.Benchmark

object Main extends Serializable {

  def main(args: Array[String]): Unit = {

    val dataset_path = args(0)
    val output_path = args(1)
    val n_threads = args(2)
    val numK = args(3).toInt
    val epsilon = args(4).toDouble

    val conf = new SparkConf().setAppName("KMeans").setMaster("local[" + n_threads + "]")
    val sc = new SparkContext(conf)

    val input = sc.textFile(dataset_path)
    val sparkPoints = input.map(f => {
      f
        .split(",")
        .drop(1)
        .map(_.toDouble)
    })

    val aggregatedResult = (2 to numK).map(num_k=>{

      val result = Benchmark time Kmeans.run(sparkPoints, num_k, epsilon)

      val evaluator = new Evaluator(new InfoClusterization(result._1._1,result._1._2))
      val indexes = List(WCSS,MeanWCSS,CalinskiHarabasz).map(index=>{Benchmark time (evaluator start index)})

      (
        num_k,
        indexes(0)._1,
        indexes(0)._2,
        indexes(1)._1,
        indexes(1)._2,
        indexes(2)._1,
        indexes(2)._2,
        result._1._3, //iterations
        result._2 //time for clusterization
      )
    })

    sc
      .parallelize(aggregatedResult).coalesce(1).saveAsTextFile(output_path)

  }
}