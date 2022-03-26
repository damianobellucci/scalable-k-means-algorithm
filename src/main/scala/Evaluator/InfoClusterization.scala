package Evaluator

import org.apache.spark.rdd.RDD

import java.io.Serializable

class InfoClusterization(points:RDD[(Int, Array[Double])], centroids: Array[(Int, Array[Double])]) extends Serializable{
  var pairs = points
  val hashCenters = centroids.toMap
}