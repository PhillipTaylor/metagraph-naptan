package com.my.metagraph_social.shared_data.types

//import com.my.metagraph_social.shared_data.types.Updates.SocialUpdate
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import io.constellationnetwork.currency.dataApplication.{DataCalculatedState, DataOnChainState}
import io.constellationnetwork.schema.SnapshotOrdinal
import io.constellationnetwork.schema.address.Address
import io.constellationnetwork.currency.dataApplication.DataUpdate

import java.time.LocalDateTime

@derive(decoder, encoder)
case class StringWithLang(v :String, lang :String)

object States {

  @derive(decoder, encoder)
  case class NaptanEntry(
    atcoCode :String,
    naptanCode :String,
    plateCode :String,
    cleardownCode :String,
    commonName :StringWithLang,
    shortCommonName :StringWithLang,
    landmark :StringWithLang,
    street :StringWithLang,
    crossing :StringWithLang,
    indicator :StringWithLang,
    bearing :String,
    nptgLocalityCode :String,
    localityName :String,
    parentLocalityName :String,
    grandParentLocalityName :String,
    town :StringWithLang,
    suburb :StringWithLang,
    localityCentre :String,
    gridType :String,
    easting :String,
    northing :String,
    longitude :String,
    latitude :String,
    stopType :String,
    busStopType :String,
    timingStatus :String,
    defaultWaitTime :String,
    notes :StringWithLang,
    administrativeAreaCode :String,
    creationDateTime :String,
    modificationDateTime :String,
    revisionNumber :String,
    // This can be new, deleted or revised.
    // It can actually integrate quite well
    // with our natural block chain operations
    modification :String,
    status :String,
    ordinal: SnapshotOrdinal,
    postTime: LocalDateTime
  ) extends DataUpdate

  @derive(decoder, encoder)
  case class NaptanEntryOnChainState(updates: List[NaptanEntry]) extends DataOnChainState

  @derive(decoder, encoder)
  case class NaptanUnifiedDataset(uniqueNaptanEntries: List[NaptanEntry]) extends DataCalculatedState
}
