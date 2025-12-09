package com.my.metagraph_social.l0

import cats.data.NonEmptyList
import cats.effect.Async
import cats.syntax.all._
import com.my.metagraph_social.l0.custom_routes.CustomRoutes
import com.my.metagraph_social.shared_data.LifecycleSharedFunctions
import com.my.metagraph_social.shared_data.calculated_state.CalculatedStateService
import com.my.metagraph_social.shared_data.types.States._
//import com.my.metagraph_social.shared_data.types.Updates._
//import com.my.metagraph_social.shared_data.types.codecs.DataUpdateCodec._
import io.circe.generic.auto._
import io.circe.{Decoder, Encoder}
import io.constellationnetwork.currency.dataApplication._
import io.constellationnetwork.currency.dataApplication.dataApplication.{DataApplicationBlock, DataApplicationValidationErrorOr}
import io.constellationnetwork.json.JsonSerializer
import io.constellationnetwork.schema.SnapshotOrdinal
import io.constellationnetwork.security.SecurityProvider
import io.constellationnetwork.security.hash.Hash
import io.constellationnetwork.security.signature.Signed
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.{EntityDecoder, HttpRoutes}

object MetagraphL0Service {

  def make[F[+_] : Async : JsonSerializer](
    calculatedStateService: CalculatedStateService[F]
  ): F[BaseDataApplicationL0Service[F]] = Async[F].delay {
    makeBaseDataApplicationL0Service(
      calculatedStateService
    )
  }

  private def makeBaseDataApplicationL0Service[F[+_] : Async : JsonSerializer](
    calculatedStateService: CalculatedStateService[F]
  ): BaseDataApplicationL0Service[F] =
    BaseDataApplicationL0Service(
      new DataApplicationL0Service[F, NaptanEntry, NaptanEntryOnChainState, NaptanUnifiedDataset] {
        override def genesis: DataState[NaptanEntryOnChainState, NaptanUnifiedDataset] = {
          DataState(
            NaptanEntryOnChainState(List.empty),
            NaptanUnifiedDataset(List.empty)
          )
        }

        override def validateData(
          state  : DataState[NaptanEntryOnChainState, NaptanUnifiedDataset],
          updates: NonEmptyList[Signed[NaptanEntry]]
        )(implicit context: L0NodeContext[F]): F[DataApplicationValidationErrorOr[Unit]] = {
          implicit val sp: SecurityProvider[F] = context.securityProvider
          LifecycleSharedFunctions.validateData(state, updates)
        }

        override def combine(
          state  : DataState[NaptanEntryOnChainState, NaptanUnifiedDataset],
          updates: List[Signed[NaptanEntry]]
        )(implicit context: L0NodeContext[F]): F[DataState[NaptanEntryOnChainState, NaptanUnifiedDataset]] =
          // Todo: consider behaviour here.
          LifecycleSharedFunctions.combine[F](
            state,
            updates
          )

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
        ): F[Array[Byte]] =
          JsonSerializer[F].serialize[Signed[DataApplicationBlock]](block)

        override def deserializeBlock(
          bytes: Array[Byte]
        ): F[Either[Throwable, Signed[DataApplicationBlock]]] =
          JsonSerializer[F].deserialize[Signed[DataApplicationBlock]](bytes)

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

        override def getCalculatedState(implicit context: L0NodeContext[F]): F[(SnapshotOrdinal, NaptanUnifiedDataset)] =
          calculatedStateService.get.map(calculatedState => (calculatedState.ordinal, calculatedState.state))

        override def setCalculatedState(
          ordinal: SnapshotOrdinal,
          state  : NaptanUnifiedDataset
        )(implicit context: L0NodeContext[F]): F[Boolean] =
          calculatedStateService.set(ordinal, state)

        override def hashCalculatedState(
          state: NaptanUnifiedDataset
        )(implicit context: L0NodeContext[F]): F[Hash] =
          calculatedStateService.hash(state)

        override def routes(implicit context: L0NodeContext[F]): HttpRoutes[F] =
          CustomRoutes[F](calculatedStateService).public

        override def serializeCalculatedState(
          state: NaptanUnifiedDataset
        ): F[Array[Byte]] =
          JsonSerializer[F].serialize[NaptanUnifiedDataset](state)

        override def deserializeCalculatedState(
          bytes: Array[Byte]
        ): F[Either[Throwable, NaptanUnifiedDataset]] =
          JsonSerializer[F].deserialize[NaptanUnifiedDataset](bytes)
      })
}
