/*
 * Copyright 2019 Abdulla Abdurakhmanov (abdulla@latestbit.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package org.latestbit.slack.morphism.events.signature

import org.latestbit.slack.morphism.common.SlackApiError

sealed abstract class SlackSignatureVerificationError( message: String, cause: Option[Throwable] = None )
    extends SlackApiError( message, cause )

case class SlackSignatureCryptoInitError( cause: Throwable )
    extends SlackSignatureVerificationError(
      s"Unable to init crypto algorithm: ${SlackEventSignatureVerifier.SIGNING_ALGORITHM}",
      Some( cause )
    )

case class SlackSignatureWrongSignatureError(
    receivedHash: String,
    generatedHash: String,
    timestamp: String,
    cause: Option[Throwable] = None
) extends SlackSignatureVerificationError( s"""
| Received hash from Slack '${receivedHash}' doesn't match with generated: ${generatedHash}. Received timestamp: '${timestamp}''
|""".stripMargin, cause )