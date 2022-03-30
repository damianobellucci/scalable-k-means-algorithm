package Evaluator

class Evaluator(info:InfoClusterization) extends Serializable {
  def start(f:IndexClustering): Double ={
    f.run(info)
  }
}


