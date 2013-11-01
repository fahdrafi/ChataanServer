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
  implicit def toXML(e:Any):Elem = e match {
    case x:String => <string>{x}</string>
    case x:Double => <real>{x}</real>
    case _ => <unknownType/>
  }

  // May be ambiguity on type Any/Map[String,Any] -- TODO: Fix this
  implicit def toXML(m: Map[String,Any]):Elem =
    <dict/>.copy(child = m.map(e => List(<key>{e._1}</key>,XMLize.toXML(e._2))).flatten.toSeq)

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
  val sampleMap = Map("key1" -> "ValueHere", "key2" -> 1.2)
  def topEntities = Action {
    Ok(XMLize.toXML(sampleMap))
  }
}
