package com.my.metagraph_social.shared_data.types.codecs

//import com.my.metagraph_social.shared_data.types.Updates.SocialUpdate
//import com.my.metagraph_social.shared_data.types.States.NaptanEntry
//import io.circe.syntax.EncoderOps
//import io.circe.{Decoder, Encoder, HCursor, Json}
//import io.constellationnetwork.currency.dataApplication.DataUpdate
//
//object DataUpdateCodec {
//  implicit val dataUpdateEncoder: Encoder[NaptanEntry] = {
//    case event: NaptanEntry => event.asJson
//    case _ => Json.Null
//  }
//
//  implicit val dataUpdateDecoder: Decoder[NaptanEntry] = (c: HCursor) => c.as[NaptanEntry]
//}
