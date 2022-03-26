import Evaluator.{Evaluator, InfoClusterization}
import Kmeans.Kmeans
import _root_.Evaluator.Indexes.{CalinskiHarabasz, WBSS, WCSS}
import org.apache.spark.{SparkConf, SparkContext}

import java.io.Serializable


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

      val t0 = System.nanoTime()

      val result = Kmeans.run(sc, sparkPoints, num_k, epsilon)

      val evaluator = new Evaluator(new InfoClusterization(result._1,result._2))
      val indexes = List(WCSS,WBSS,CalinskiHarabasz).map(index=>{evaluator start index})

      (num_k,indexes(0),indexes(1),indexes(2),result._3,(System.nanoTime()-t0)/1000000000)
    })

    sc
      .parallelize(aggregatedResult).coalesce(1).saveAsTextFile(output_path)

  }
}