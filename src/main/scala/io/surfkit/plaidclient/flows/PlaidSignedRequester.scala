package io.surfkit.plaidclient.flows

import java.util.UUID

import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.stream.{KillSwitches, Materializer}
import akka.NotUsed
import akka.actor.{ActorSystem, Cancellable}
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model._
import akka.stream.scaladsl.{Keep, Source}
import akka.util.ByteString
import io.surfkit.plaidclient.data.Plaid

import scala.concurrent.Future
import org.joda.time.DateTimeZone
import org.joda.time.DateTime
import play.api.libs.json.{Json, Writes}

import scala.concurrent.duration.FiniteDuration
import scala.util.Try


class PlaidSignedRequester(/*creds: () => Future[Questrade.Login]*/)(implicit system: ActorSystem, materializer: Materializer){
  /*
  import system.dispatcher

  def get(path: String) = {
    creds().flatMap { login =>
      val baseUrl = s"${login.api_server}v1/"
      println(s"curl -XGET '${baseUrl}${path}' -H 'Authorization: Bearer ${login.access_token}'")
      Http().singleRequest(HttpRequest(uri = s"${baseUrl}${path}").addHeader(Authorization(OAuth2BearerToken(login.access_token))))
    }
  }

  def post[T <: Plaid](path: String, post: T)(implicit uw: Writes[T]) = {
    creds().flatMap { login =>
      val baseUrl = s"${login.api_server}v1/"
      val json = Json.stringify(uw.writes(post))
      val jsonEntity = HttpEntity(ContentTypes.`application/json`, json)
      println(s"curl -XPOST '${baseUrl}${path}' -H 'Authorization: Bearer ${login.access_token}' -d '${json}'")
      Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = s"${baseUrl}${path}", entity = jsonEntity).addHeader(Authorization(OAuth2BearerToken(login.access_token))))
    }
  }

  def delete(path: String) = {
    creds().flatMap { login =>
      val baseUrl = s"${login.api_server}v1/"
      println(s"url: ${baseUrl}${path}")
      Http().singleRequest(HttpRequest(method = HttpMethods.DELETE, uri = s"${baseUrl}${path}").addHeader(Authorization(OAuth2BearerToken(login.access_token))))
    }
  }
  */
}