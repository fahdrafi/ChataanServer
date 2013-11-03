package controllers

import play.api._
import play.api.mvc._
import scala.xml.{Node, NodeSeq, Elem}
import com.mongodb.casbah.Imports._
import collection.JavaConversions._

/**
 * Created with IntelliJ IDEA.
 * User: Fahd
 * Date: 31/10/2013
 * Time: 23:09
 * XML responses for Chataan Clients
 */

object XMLize {
  private def toPListXML[T](x: T): Elem = x match {
    case e: String => <string>{e}</string>
    case e: Double => <real>{e}</real>
    case e: Integer => <integer>{e}</integer>
    case o: DBObject => XMLize.toPListXML((o.toMap).toMap)
    case true => <true/>
    case false => <false/>
    case m: Map[String,AnyRef] => <dict/>.copy(child = m.map(e => List(<key>{e._1}</key>,XMLize.toPListXML(e._2))).flatten.toSeq)
    case l: List[AnyRef] => <array/>.copy(child = l.map(e => XMLize.toPListXML(e)).toSeq)
    case e => <unknown>{e.toString}</unknown>
  }
  def toPList[T](x: T) = {
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">" +
      <pList version="1.0">{XMLize.toPListXML(x)}</pList>
  }
}

object Chataan extends Controller {
  lazy val  mongoClient = MongoClient("localhost", 27017)
  lazy val  db = mongoClient("newsdb")
  lazy val  collectionEntities = db("entities")
  lazy val  collectionEntityCount = db("EntityCount")
  lazy val  collectionNewsLinks = db("newslinks")

  def fetchTopEntities() = {
    val t=  collectionEntityCount.find().sort(MongoDBObject("value" -> -1))
    val ids = t.limit(10).toList
    ids.map(e => e ++ ("EntityName" ->
      ((collectionEntities.findOne(MongoDBObject("EntityID" -> e("_id")))).get)("EntityName") ))

  }

  def fetchStoriesByEntity(id : String) = {
    (collectionEntities.findOne(MongoDBObject("EntityID" -> id)).get)("StoryList").asInstanceOf[BasicDBList].toList
      .map(link => collectionNewsLinks.findOne(MongoDBObject("Link" -> link),
      MongoDBObject("Title" -> true) ++ ("Description" -> true) ++ ("Link" -> true)).get)
  }

  val sampleResponse = Map(
    "key1" -> "string value here",
    "key2" -> List (0.1, 0.5, 0.4, 0.6, 0.1, 0.7, 0.2, 0.9),
    "a number" -> 5,
    "trueFalseList" -> List(true, false, false)
  )
  def topEntities = Action {
    Ok(XMLize.toPList(fetchTopEntities())).as("application/xml")
  }

  def storiesByEntity(id : String) = Action {
    Ok(XMLize.toPList(fetchStoriesByEntity(id))).as("application/xml")
  }
}
