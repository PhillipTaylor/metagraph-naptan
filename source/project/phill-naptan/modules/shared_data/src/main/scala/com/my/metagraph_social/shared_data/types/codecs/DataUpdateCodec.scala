package com.my.metagraph_social.shared_data.types.codecs

import com.my.metagraph_social.shared_data.types.States.NaptanEntry
import io.circe.syntax._
import io.circe.{Decoder, Encoder, HCursor, Json}
import io.constellationnetwork.currency.dataApplication.DataUpdate

object DataUpdateCodec {
  implicit val dataUpdateEncoder: Encoder[DataUpdate] = {
    case event: NaptanEntry => event.asJson //Json.obj("naptanCode" -> event.naptanCode.asJson) /* event.asJson */
    case _ => Json.Null
  }

  implicit val dataUpdateDecoder: Decoder[DataUpdate] = (c: HCursor) => c.as[NaptanEntry]
}
