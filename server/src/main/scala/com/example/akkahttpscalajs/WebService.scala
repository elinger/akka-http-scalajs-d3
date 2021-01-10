package com.example.akkahttpscalajs

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.directives.Credentials
import scalatags.Text.all._

import java.security.MessageDigest

class WebService() extends Directives {

  val hasher = MessageDigest.getInstance("SHA-256")

  def myUserPassAuthenticator(credentials: Credentials): Option[String] =
    credentials match {
      case p @ Credentials.Provided(id)
          if p.identifier == "test" && p.verify(
            "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08",
            p =>
              hasher
                .digest(p.getBytes("UTF-8"))
                .map("%02x".format(_))
                .mkString) =>
        Some(id)
      case _ => None
    }

  val route = {
    pathSingleSlash {
      get {
        authenticateBasic(realm = "secure site", myUserPassAuthenticator) {
          userName =>
            println(s"User ${userName}")
            // The Circle case class from shared can be used on the server side as well. Try out with:
            // Circle("test", 10, 20, 3)
            complete {
              HttpEntity(
                ContentTypes.`text/html(UTF-8)`,
                html(
                  scalatags.Text.all.head(
                    link(rel := "stylesheet",
                         href := "https://unpkg.com/purecss@2.0.3/build/pure-min.css")
                  ),
                  body(
                    raw(scalajs.html
                      .scripts("client",
                               name => s"/assets/$name",
                               name =>
                                 getClass.getResource(s"/public/$name") != null)
                      .body)
                  )
                ).render
              )
            }
        }
      }
    } ~
      pathPrefix("assets" / Remaining) { file =>
        println(s"Asset: $file")
        // optionally compresses the response with Gzip or Deflate
        // if the client accepts compressed responses
        encodeResponse {
          getFromResource("public/" + file)
        }
      }
  }
}
