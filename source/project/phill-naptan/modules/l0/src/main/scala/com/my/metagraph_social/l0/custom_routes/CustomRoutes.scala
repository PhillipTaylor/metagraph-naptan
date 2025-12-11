package com.my.metagraph_social.l0.custom_routes

import cats.effect.Async
import cats.effect.unsafe.implicits.global
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.my.metagraph_social.shared_data.calculated_state.CalculatedStateService
import com.my.metagraph_social.shared_data.types.States.NaptanEntry
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import eu.timepit.refined.auto._
import io.constellationnetwork.ext.http4s.AddressVar
import io.constellationnetwork.routes.internal.{InternalUrlPrefix, PublicRoutes}
import io.constellationnetwork.schema.address.Address
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.middleware.CORS
import org.http4s.{HttpRoutes, Request, Response}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import io.circe._
import io.circe.syntax._

case class CustomRoutes[F[_] : Async](calculatedStateService: CalculatedStateService[F]) extends Http4sDsl[F] with PublicRoutes[F] {
  implicit val logger: SelfAwareStructuredLogger[F] = Slf4jLogger.getLogger[F]

  private def getAllNaptanEntries: F[Response[F]] = {
    logger.info(s"Returning all naptan entries")
    calculatedStateService.get
      .map(_.state.uniqueNaptanEntries)
      .flatMap { allNaptanEntries =>
        println(s"Found ${allNaptanEntries.length}")
        Ok(allNaptanEntries)
      }
  }

  private def getNaptanEntryByCode(naptanCode :String): F[Response[F]] = {
    logger.info(s"Search all naptan entries for $naptanCode")
    calculatedStateService.get
      .map(_.state)
      .map { state =>
        state.uniqueNaptanEntries
          .filter { naptanEntry => naptanEntry.naptanCode.equalsIgnoreCase(naptanCode) }

      }
      .flatMap { allNaptanEntries =>
        println(s"Found ${allNaptanEntries.length} matches for $naptanCode")
        Ok(allNaptanEntries)
      }
  }

  private def addNaptanEntry(request :Request[F]) :F[Response[F]] = {
    logger.info(s"Placeholder for returning a naptan entry")
    //val payload :String = request.body.through(fs2.text.utf8.decode).compile.string.unsafeRunSync() // temporary hack
    request.body.through(fs2.text.utf8.decode).compile.string.map { payload =>
      logger.info(s"Placeholder for returning a naptan entry")
      io.circe.parser.decode[NaptanEntry](payload) match {
        case Left(circeExc) => BadRequest(s"Unable to decode submitted payload: $circeExc from $payload")
        case Right(naptanEntry) =>
          logger.info(s"Successfully parsed JSON into Naptan Entry. Ready to commit")
          Ok(naptanEntry.asJson)
      }
    }.flatten //.unsafeRunSync()
  }

  private val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "version" => Ok(Json.obj(
      "applicationName" -> "Naptan Database".asJson,
      "applicationVersion" -> "0.0.1".asJson,
      "now" -> java.time.ZonedDateTime.now().toString().asJson
    ))
    case GET -> Root / "naptanEntries" / "all" => getAllNaptanEntries
    case GET -> Root / "naptanEntries" / "search" / naptanCode => getNaptanEntryByCode(naptanCode)
    case req @ POST -> Root / "naptanEntries" / "insertOne" => addNaptanEntry(req)
  }

  val public: HttpRoutes[F] =
    CORS
      .policy
      .withAllowCredentials(false)
      .httpRoutes(routes)

  override protected def prefixPath: InternalUrlPrefix = "/"
}
