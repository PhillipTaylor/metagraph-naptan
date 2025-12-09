package com.my.metagraph_social.shared_data.combiners

import cats.effect.Async
import cats.syntax.all._
import com.my.metagraph_social.shared_data.Utils.getFirstAddressFromProofs
import com.my.metagraph_social.shared_data.types.States._
//import com.my.metagraph_social.shared_data.types.Updates._
import io.constellationnetwork.currency.dataApplication.DataState
import io.constellationnetwork.json.JsonSerializer
import io.constellationnetwork.schema.SnapshotOrdinal
import io.constellationnetwork.security.SecurityProvider
import io.constellationnetwork.security.hash.Hash
import io.constellationnetwork.security.signature.Signed
import monocle.Monocle.toAppliedFocusOps

import java.time.LocalDateTime

object Combiners {

  def combineNewNaptanEntry[F[_] : Async : SecurityProvider : JsonSerializer](
    signedUpdate  : Signed[NaptanEntry],
    state         : DataState[NaptanEntryOnChainState, NaptanUnifiedDataset],
    currentOrdinal: SnapshotOrdinal
  ): F[DataState[NaptanEntryOnChainState, NaptanUnifiedDataset]] = {
    val update = signedUpdate.value
    for {
      updateBytes <- JsonSerializer[F].serialize[NaptanEntry](update)
      postId = Hash.fromBytes(updateBytes).toString
      updateAddress <- getFirstAddressFromProofs(signedUpdate.proofs)

      onChainStateUpdated = NaptanEntryOnChainState(state.onChain.updates :+ signedUpdate.value)

      // Todo: This should filter out old values so only the latest entries remain
      calculatedStateUpdated = state.calculated
        .focus(_.uniqueNaptanEntries)
        .modify(existingEntries => existingEntries ++ List(update))
    } yield DataState(onChainStateUpdated, calculatedStateUpdated)
  }

}
