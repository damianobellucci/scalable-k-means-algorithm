package Utils

object UtilsFunctions {

  def distance(p1: Array[Double], p2: Array[Double]) =
    math.sqrt((p1 zip p2).map(e =>math.pow(e._1 - e._2,2)).sum)

  def findClosest(p: Array[Double],
                  centroids: Array[(Int, Array[Double])]) = {
    centroids
      .map(centroid => {
        (centroid._1, distance(centroid._2, p))
      })
      .minBy(_._2)._1
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
    (c zip newc)
      .map(c => distance(c._1._2, c._2._2))
      .sum/ c.length
  }
}




