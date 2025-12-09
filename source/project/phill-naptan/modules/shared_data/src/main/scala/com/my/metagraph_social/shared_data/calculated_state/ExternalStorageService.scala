package com.my.metagraph_social.shared_data.calculated_state

import com.my.metagraph_social.shared_data.types.States.NaptanUnifiedDataset
import io.constellationnetwork.schema.SnapshotOrdinal

trait ExternalStorageService[F[_]] {
  def get(ordinal: SnapshotOrdinal): F[CalculatedState]

  def set(snapshotOrdinal: SnapshotOrdinal, state: NaptanUnifiedDataset): F[Boolean]

  def getLatest: F[CalculatedState]

}
