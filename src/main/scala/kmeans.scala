package hackathon

import java.util.Random
import spark.SparkContext
import spark.util.Vector
import spark.SparkContext._
import spark.RDD
import scala.collection.mutable.HashMap
import scala.collection.mutable.HashSet

/**
 * K-means clustering.
 */
object kmeans {
  def closestPoint(p: Vector, centers: Array[Vector]): Int = {
    var index = 0
    var bestIndex = 0
    var closest = Double.PositiveInfinity
  
    for (i <- 0 until centers.length) {
      val tempDist = p.squaredDist(centers(i))
      if (tempDist < closest) {
        closest = tempDist
        bestIndex = i
      }
    }
  
    return bestIndex
  }

  def cluster(data: RDD[Vector]): RDD[Seq[Vector]] = cluster(10, 30)(data)
  def cluster(K: Int, topN: Int)(data: RDD[Vector]) : RDD[Seq[Vector]] = {

    val convergeDist = 0.001
  
    var kPoints: Array[Vector] = data.takeSample(false, K, 42).toArray
    var tempDist = 1.0

    while(tempDist > convergeDist) {
      var closest = data.map (p => (closestPoint(p, kPoints), (p, 1)))
      
      var pointStats = closest.reduceByKey{case ((x1, y1), (x2, y2)) => (x1 + x2, y1 + y2)}
      
      var newPoints = pointStats.map {pair => (pair._1, pair._2._1 / pair._2._2)}.collectAsMap()
      
      tempDist = 0.0
      for (i <- 0 until kPoints.length) {
        tempDist += kPoints(i).squaredDist(newPoints(i))
      }
      
      for (newP <- newPoints) {
        kPoints(newP._1) = newP._2
      }
      // kPoints = kPoints.filter(_ == Vector(0.0, 0.0))
      println("Finished iteration (delta = " + tempDist + ")")
    }

    println("Final centers:")
    kPoints.foreach(println)

    data.groupBy (p => closestPoint(p, kPoints)).map {
      case (point, list) => list.take(topN)
    }
  }
}
