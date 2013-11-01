package controllers

import play.api._
import play.api.mvc._
import scala.xml.{Node, NodeSeq, Elem}

/**
 * Created with IntelliJ IDEA.
 * User: Fahd
 * Date: 31/10/2013
 * Time: 23:09
 * XML responses for Chataan Clients
 */

object XMLize {
  def toPList[T](x: T) = {
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">" +
      <pList version="1.0">{XMLize.toXML(x)}</pList>
  }
  def toXML[T](x: T): Elem = x match {
    case e: String => <string>{e}</string>
    case e: Double => <real>{e}</real>
    case e: Integer => <integer>{e}</integer>
    case true => <true/> case false => <false/>
    case m: Map[String,AnyRef] => <dict/>.copy(child = m.map(e => List(<key>{e._1}</key>,XMLize.toXML(e._2))).flatten.toSeq)
    case l: List[AnyRef] => <array/>.copy(child = l.map(e => XMLize.toXML(e)).toSeq)
    case e => <unknown>{e.toString}</unknown>
  }
}

object Chataan extends Controller {
  val trueFalseList = List(true, false, false)
  val sampleMap = Map(
    "key1" -> "string value here",
    "key2" -> List (
      0.1, 0.5, 0.4, 0.6, 0.1, 0.7, 0.2, 0.9
    ),
    "a number" -> 5,
    "trueFalseList" -> trueFalseList
  )
  def topEntities = Action {
    Ok(XMLize.toPList(sampleMap)).as("application/xml")
  }
}
