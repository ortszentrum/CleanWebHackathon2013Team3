package hackathon

import spark.RDD
import spark.SparkContext
import spark.SparkContext._
import spark.util.Vector

import java.io._

object Hackathon {

  case class Sample(taxi: Long, time: String, pos: Vector,s : Boolean) {
    def this(rows: Seq[String]) = this(rows(0).toLong, rows(1),
      Vector(rows(2).toDouble, rows(3).toDouble), rows(5) == "1")
    def this(s: String) = this(s.split(","))

    def hourString: String = time.split(" ").tail.head
    // FIXME: sample data are afternoon only
    def isNight = hourString < "18:00:00" && hourString > "03:00:00"
  }

  def main(args: Array[String]) {

    val (k, topN) = args.toList match {
	case Nil => (10, 30)	
	case k :: Nil => (k.toInt, 30)
	case k :: n :: Nil => (k.toInt, n.toInt)
    }

    val context = "local[4]"
    val sc = new SparkContext(context, "hackathon", "/var/opt/spark-0.7.2")

    val dataset = sc.textFile("sample.csv").map(new Sample(_))

    val trip = dataset.groupBy(_.taxi).flatMap{
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
          p1.pos.squaredDist(p2.pos) * 1.852 < 3
        }.map{
          case (one, two) => one
        }
      }
    }.cache()
    trip.map(_.pos.toString).saveAsTextFile("depart.txt")

    val shift: RDD[Sample] = sc.parallelize(trip.collect.drop(1))

    val groups = kmeans.cluster(k, topN)(trip.map(_.pos))

    groups.map(list => list.mkString("")).saveAsTextFile("groups.txt")
  }
}
