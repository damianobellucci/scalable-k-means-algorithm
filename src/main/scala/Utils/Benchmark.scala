package Utils

//1000000000
object Benchmark {
  def time[R](block: => R): (R,Double) = {
    val t0 = System.nanoTime()
    val result = block
    val t1 = System.nanoTime()
    (result,(t1-t0)/1000000000)
  }
}