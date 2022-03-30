package Utils


object Benchmark {
  def time[R](block: => R): (R,Double) = {
    val t0 = System.nanoTime()
    val result = block
    val t1 = System.nanoTime()
    (result,t1-t0)
  }
}



