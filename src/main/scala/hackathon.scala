package hackathon

import spark.RDD
import spark.SparkContext
import spark.SparkContext._
import spark.util.Vector

import java.io._

object Hackathon {

  case class TaxiRide(taxi: Long, time: String, from: Vector, to: Vector,s : Boolean) {
    def pos = from + to / 2
    def this(rows: Seq[String]) = this(rows(0).toLong, rows(1),
      Vector(rows(2).toDouble, rows(3).toDouble), Vector(), rows(5) == "1")
    def this(s: String) = this(s.split(","))

    def hourString: String = time.split(" ").tail.head
    // FIXME: sample data are afternoon only
    def isNight = hourString < "18:00:00" && hourString > "03:00:00"
    override def toString = from.toString + to.toString
    def until(that: TaxiRide) = new TaxiRide(taxi, time, from, that.from, s)
  }

  def main(args: Array[String]) {

    val (k, topN) = args.toList match {
	case Nil => (10, 30)	
	case k :: Nil => (k.toInt, 30)
	case k :: n :: Nil => (k.toInt, n.toInt)
    }

    val context = "local[4]"
    val sc = new SparkContext(context, "hackathon", "/var/opt/spark-0.7.2")

    val dataset = sc.textFile("sample.csv").map(new TaxiRide(_))

    val rides = dataset.groupBy(_.taxi).flatMap{
      case (taxi, list) =>
      val shift = list.tail
      val changes = list.zip(shift).filter{
        case (one, two) => one.s != two.s
      }.map {
        case (one, two) => one
      }
      if (changes.length < 2) List()
      else {
        val changesShift = changes.tail
        changes.zip(changesShift).filter{
          case (p1, p2) =>
          p1.from.dist(p2.from) * 1.852 < 3
        }.map{
          case (one, two) => one.until(two)
        }
      }
    }.cache()
    rides.map(_.toString).saveAsTextFile("rides.txt")

    val shift: RDD[TaxiRide] = sc.parallelize(rides.collect.drop(1))

    val centers = kmeans.cluster(k, topN)(rides.map(_.pos))

    val points = rides.groupBy (p => kmeans.closestPoint(p.pos, centers)).flatMap {
      case (point, list) => 
      list.sortBy(trip => trip.pos.dist(centers(point))).take(topN)
    }

    points.map(_.toString).saveAsTextFile("results.txt")
  }
}
