package com.my.metagraph_social.shared_data.validations

import cats.effect.Async
import cats.syntax.all._
import com.my.metagraph_social.shared_data.Utils.getFirstAddressFromProofs
import com.my.metagraph_social.shared_data.errors.Errors.valid
import com.my.metagraph_social.shared_data.types.States.NaptanUnifiedDataset
import com.my.metagraph_social.shared_data.types.States.NaptanEntry
//import com.my.metagraph_social.shared_data.types.Updates._
import com.my.metagraph_social.shared_data.validations.TypeValidators._
import io.constellationnetwork.currency.dataApplication.dataApplication.DataApplicationValidationErrorOr
import io.constellationnetwork.json.JsonSerializer
import io.constellationnetwork.security.SecurityProvider
import io.constellationnetwork.security.hash.Hash
import io.constellationnetwork.security.signature.Signed

object Validations {
  def createPostValidationL1(
    update: NaptanEntry,
  ): DataApplicationValidationErrorOr[Unit] = {
    //validateIfPostContentIsGreaterThan200Chars(update.content)
    //().validNec[DataApplicationValidationError]
    valid
  }

  def editPostValidationL1(
    update: NaptanEntry,
  ): DataApplicationValidationErrorOr[Unit] = {
    //validateIfPostContentIsGreaterThan200Chars(update.content)
    //().validNec[DataApplicationValidationError]
    valid
  }

  //def deletePostValidationL1(
  //  update: DeletePost,
  //): DataApplicationValidationErrorOr[Unit] = valid

  //def subscriptionValidationL1(
  //  update: Subscribe,
  //): DataApplicationValidationErrorOr[Unit] = valid

  //def createPostValidationL0[F[_] : Async : SecurityProvider : JsonSerializer](
  //  signedUpdate   : Signed[CreatePost],
  //  calculatedState: NaptanUnifiedDataset
  //): F[DataApplicationValidationErrorOr[Unit]] = for {
  //    updateBytes <- JsonSerializer[F].serialize[SocialUpdate](signedUpdate.value)
  //    postId = Hash.fromBytes(updateBytes).toString
  //    userId <- getFirstAddressFromProofs(signedUpdate.proofs)
  //    l1Validations = createPostValidationL1(signedUpdate.value)
  //    postAlreadyExists = validateIfPostAlreadyExists(postId, userId, calculatedState)
  //  } yield l1Validations.productR(postAlreadyExists)

  //def editPostValidationL0[F[_] : Async : SecurityProvider](
  //  signedUpdate   : Signed[EditPost],
  //  calculatedState: NaptanUnifiedDataset
  //): F[DataApplicationValidationErrorOr[Unit]] = for {
  //  userId <- getFirstAddressFromProofs(signedUpdate.proofs)
  //  l1Validations = editPostValidationL1(signedUpdate.value)
  //  postNotExists = validateIfPostExists(signedUpdate.value.postId, userId, calculatedState)
  //} yield l1Validations.productR(postNotExists)

  //def deletePostValidationL0[F[_] : Async : SecurityProvider](
  //  signedUpdate   : Signed[DeletePost],
  //  calculatedState: NaptanUnifiedDataset
  //): F[DataApplicationValidationErrorOr[Unit]] = for {
  //  userId <- getFirstAddressFromProofs(signedUpdate.proofs)
  //  l1Validations = deletePostValidationL1(signedUpdate.value)
  //  postNotExists = validateIfPostExists(signedUpdate.value.postId, userId, calculatedState)
  //} yield l1Validations.productR(postNotExists)

  //def subscriptionValidationL0[F[_] : Async : SecurityProvider](
  //  signedUpdate   : Signed[Subscribe],
  //  calculatedState: NaptanUnifiedDataset
  //): F[DataApplicationValidationErrorOr[Unit]] =
  //  for {
  //    userId <- getFirstAddressFromProofs(signedUpdate.proofs)
  //    l1Validations = subscriptionValidationL1(signedUpdate.value)
  //    subscriptionUserExists = validateIfSubscriptionUserExists(signedUpdate.value, calculatedState)
  //    userAlreadySubscribed = validateIfUserAlreadySubscribed(signedUpdate.value, userId, calculatedState)
  //    userTryingToSubscribeSelf = validateIfUserIsSubscribingToSelf(signedUpdate.value, userId)
  //  } yield l1Validations
  //    .productR(subscriptionUserExists)
  //    .productR(userAlreadySubscribed)
  //    .productR(userTryingToSubscribeSelf)

}

