package Evaluator

class Evaluator(info:InfoClusterization) {
  def start(f:IndexClustering): Double ={
    f.run(info)
  }
}