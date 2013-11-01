package controllers

import play.api._
import play.api.mvc._
import scala.xml.{Node, NodeSeq, Elem}

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 31/10/2013
 * Time: 23:09
 * To change this template use File | Settings | File Templates.
 */

object XMLize {
  implicit def toXML(string: String):Elem = <string>string</string>

  implicit def toXML(m: Map[String,String]) =
    <dict/>.copy(child = m.map(e => <key>{e._1}</key>).toSeq)

    implicit def toXML(list: List[_]) = {
      <array>
        {for (e <- list)
      yield <real>{e}</real>
        }
      </array>
    }
}

object Chataan extends Controller {
  val sampleList = List(1.1, 1.2, 1.3)
  val sampleMap = Map("key1" -> "Value1", "key2" -> "value2")
  def topEntities = Action {
    Ok(XMLize.toXML(sampleMap))
  }
}
