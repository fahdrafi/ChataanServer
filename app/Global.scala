import play.api._

/**
 * Created with IntelliJ IDEA.
 * User: frafi
 * Date: 31/10/13
 * Time: 15:35
 * File/Class documentation goes here
 */
object Global extends GlobalSettings  {
  override def onStart(app: Application) {
    Logger.info("Starting Chataan Server ...")
  }
}
