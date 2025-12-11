package com.my.metagraph_social.data_l1

import cats.effect.Async
import com.my.metagraph_social.shared_data.LifecycleSharedFunctions
import com.my.metagraph_social.shared_data.types.States._
import com.my.metagraph_social.shared_data.types.States.NaptanEntry._
import com.my.metagraph_social.shared_data.types.codecs.DataUpdateCodec._
import io.circe.{Decoder, Encoder}
import io.constellationnetwork.currency.dataApplication._
import io.constellationnetwork.currency.dataApplication.dataApplication.{DataApplicationBlock, DataApplicationValidationErrorOr}
import io.constellationnetwork.json.JsonSerializer
import io.constellationnetwork.security.signature.Signed
import io.constellationnetwork.security.signature.Signed._
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.{EntityDecoder, HttpRoutes}

object DataL1Service {

  def make[F[+_] : Async : JsonSerializer]: F[BaseDataApplicationL1Service[F]] = Async[F].delay {
    makeBaseDataApplicationL1Service
  }

  private def makeBaseDataApplicationL1Service[F[+_] : Async : JsonSerializer]: BaseDataApplicationL1Service[F] = BaseDataApplicationL1Service(
    new DataApplicationL1Service[F, NaptanEntry, NaptanEntryOnChainState, NaptanUnifiedDataset] {

      override def validateUpdate(
        update: NaptanEntry
      )(implicit context: L1NodeContext[F]): F[DataApplicationValidationErrorOr[Unit]] =
        Async[F].pure(LifecycleSharedFunctions.validateUpdate(update))

      override def routes(implicit context: L1NodeContext[F]): HttpRoutes[F] =
        HttpRoutes.empty

      override def dataEncoder: Encoder[NaptanEntry] =
        implicitly[Encoder[NaptanEntry]]

      override def dataDecoder: Decoder[NaptanEntry] =
        implicitly[Decoder[NaptanEntry]]

      override def calculatedStateEncoder: Encoder[NaptanUnifiedDataset] =
        implicitly[Encoder[NaptanUnifiedDataset]]

      override def calculatedStateDecoder: Decoder[NaptanUnifiedDataset] =
        implicitly[Decoder[NaptanUnifiedDataset]]

      override def signedDataEntityDecoder: EntityDecoder[F, Signed[NaptanEntry]] =
        circeEntityDecoder


      override def serializeBlock(
        block: Signed[DataApplicationBlock]
      ): F[Array[Byte]] = {
        JsonSerializer[F].serialize[Signed[DataApplicationBlock]](block)
      }

      override def deserializeBlock(
        bytes: Array[Byte]
      ): F[Either[Throwable, Signed[DataApplicationBlock]]] = {
        JsonSerializer[F].deserialize[Signed[DataApplicationBlock]](bytes)
      }

      override def serializeState(
        state: NaptanEntryOnChainState
      ): F[Array[Byte]] =
        JsonSerializer[F].serialize[NaptanEntryOnChainState](state)

      override def deserializeState(
        bytes: Array[Byte]
      ): F[Either[Throwable, NaptanEntryOnChainState]] =
        JsonSerializer[F].deserialize[NaptanEntryOnChainState](bytes)

      override def serializeUpdate(
        update: NaptanEntry
      ): F[Array[Byte]] =
        JsonSerializer[F].serialize[NaptanEntry](update)

      override def deserializeUpdate(
        bytes: Array[Byte]
      ): F[Either[Throwable, NaptanEntry]] =
        JsonSerializer[F].deserialize[NaptanEntry](bytes)

      override def serializeCalculatedState(
        state: NaptanUnifiedDataset
      ): F[Array[Byte]] =
        JsonSerializer[F].serialize[NaptanUnifiedDataset](state)

      override def deserializeCalculatedState(
        bytes: Array[Byte]
      ): F[Either[Throwable, NaptanUnifiedDataset]] =
        JsonSerializer[F].deserialize[NaptanUnifiedDataset](bytes)

    }
  )
}
