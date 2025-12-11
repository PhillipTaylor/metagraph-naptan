package com.my.metagraph_social.shared_data.calculated_state

import cats.effect.{Async, Ref}
import cats.syntax.all._
import com.my.metagraph_social.shared_data.types.States._
import io.circe.Json
import io.circe.syntax.EncoderOps
import io.constellationnetwork.schema.SnapshotOrdinal
import io.constellationnetwork.security.hash.Hash

import java.nio.charset.StandardCharsets

trait CalculatedStateService[F[_]] {
  def get: F[CalculatedState]

  def set(
    snapshotOrdinal: SnapshotOrdinal,
    state          : NaptanUnifiedDataset
  ): F[Boolean]

  def hash(
    state: NaptanUnifiedDataset
  ): F[Hash]
}

object CalculatedStateService {
  def make[F[_] : Async](
    externalStorageService: ExternalStorageService[F]
  ): F[CalculatedStateService[F]] =
    Ref.of[F, CalculatedState](CalculatedState.empty).map { stateRef =>
      new CalculatedStateService[F] {
        override def get: F[CalculatedState] = stateRef.get

        override def set(
          snapshotOrdinal: SnapshotOrdinal,
          state          : NaptanUnifiedDataset
        ): F[Boolean] = stateRef.modify { currentState =>
          //val newState = CalculatedState(snapshotOrdinal, NaptanUnifiedDataset(List.empty))
          externalStorageService.set(snapshotOrdinal, state).as(true)
          val newUnifiedModel :NaptanUnifiedDataset = NaptanUnifiedDataset(uniqueNaptanEntries = currentState.state.uniqueNaptanEntries ++ state.uniqueNaptanEntries)
          //val updatedUsers = state.users.foldLeft(currentCalculatedState.users) {
          //  case (acc, (address, value)) =>
          //    acc.updated(address, value)
          //}
          val newState = CalculatedState(snapshotOrdinal, newUnifiedModel)

          newState -> externalStorageService.set(snapshotOrdinal, state).as(true)
        }.flatten

        override def hash(
          state: NaptanUnifiedDataset
        ): F[Hash] = {
          def removeField(json: Json, fieldName: String): Json = {
            json.mapObject(_.filterKeys(_ != fieldName).mapValues(removeField(_, fieldName))).mapArray(_.map(removeField(_, fieldName)))
          }

          val jsonState = removeField(state.asJson, "postTime").deepDropNullValues.noSpaces
          Hash.fromBytes(jsonState.getBytes(StandardCharsets.UTF_8)).pure[F]
        }
      }
    }
}
