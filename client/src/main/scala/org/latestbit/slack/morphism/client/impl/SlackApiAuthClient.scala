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

package org.latestbit.slack.morphism.client.impl

import org.latestbit.slack.morphism.client._
import org.latestbit.slack.morphism.client.ratectrl._
import org.latestbit.slack.morphism.client.reqresp.auth._
import org.latestbit.slack.morphism.codecs.implicits._

/**
 * Support for Slack Auth API methods
 */
trait SlackApiAuthClient[F[_]] extends SlackApiHttpProtocolSupport[F] {

  object auth {

    /**
     * https://api.slack.com/methods/auth.test
     */
    def test()( implicit
        slackApiToken: SlackApiToken,
        backendType: SlackApiClientBackend.BackendType[F]
    ): F[Either[SlackApiClientError, SlackApiAuthTestResponse]] = {

      http.post[SlackApiEmptyType, SlackApiAuthTestResponse](
        "auth.test",
        SlackEmptyRequest
      )
    }

    /**
     * https://api.slack.com/methods/auth.revoke
     */
    def revoke( req: SlackApiAuthRevokeRequest )( implicit
        slackApiToken: SlackApiToken,
        backendType: SlackApiClientBackend.BackendType[F]
    ): F[Either[SlackApiClientError, SlackApiAuthRevokeResponse]] = {

      http.post[SlackApiAuthRevokeRequest, SlackApiAuthRevokeResponse](
        "auth.revoke",
        req,
        methodRateControl = Some( SlackApiMethodRateControlParams( tier = Some( SlackApiRateControlParams.Tier3 ) ) )
      )
    }

  }

}
