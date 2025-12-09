package com.my.metagraph_social.shared_data.calculated_state

import com.my.metagraph_social.shared_data.types.States._
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import eu.timepit.refined.types.all.NonNegLong
import io.constellationnetwork.schema.SnapshotOrdinal

@derive(decoder, encoder)
case class CalculatedState(ordinal: SnapshotOrdinal, state: NaptanUnifiedDataset)

object CalculatedState {
  def empty: CalculatedState =
    CalculatedState(SnapshotOrdinal(NonNegLong.MinValue), NaptanUnifiedDataset(List.empty))
}
