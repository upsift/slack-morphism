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

import io.circe.generic.auto._
import org.latestbit.slack.morphism.client._
import org.latestbit.slack.morphism.client.models.channels._
import org.latestbit.slack.morphism.client.models.messages.SlackMessage
import org.latestbit.slack.morphism.client.streaming.SlackApiResponseScroller
import sttp.client._

import scala.concurrent.{ ExecutionContext, Future }

/**
  * Support for Slack Channels API methods
  */
trait SlackApiChannelsClient extends SlackApiHttpProtocolSupport { self: SlackApiClient =>

  object channels {

    /**
      * https://api.slack.com/methods/channels.archive
      */
    def archive( req: SlackApiChannelsArchiveRequest )(
        implicit slackApiToken: SlackApiUserToken,
        backend: SttpBackend[Future, Nothing, NothingT],
        ec: ExecutionContext
    ): Future[Either[SlackApiError, SlackApiChannelsArchiveResponse]] = {

      protectedSlackHttpApiPost[SlackApiChannelsArchiveRequest, SlackApiChannelsArchiveResponse](
        "channels.archive",
        req
      )
    }

    /**
      * https://api.slack.com/methods/channels.create
      */
    def create( req: SlackApiChannelsCreateRequest )(
        implicit slackApiToken: SlackApiUserToken,
        backend: SttpBackend[Future, Nothing, NothingT],
        ec: ExecutionContext
    ): Future[Either[SlackApiError, SlackApiChannelsCreateResponse]] = {

      protectedSlackHttpApiPost[SlackApiChannelsCreateRequest, SlackApiChannelsCreateResponse](
        "channels.create",
        req
      )
    }

    /**
      * https://api.slack.com/methods/channels.history
      */
    def history( req: SlackApiChannelsHistoryRequest )(
        implicit slackApiToken: SlackApiUserToken,
        backend: SttpBackend[Future, Nothing, NothingT],
        ec: ExecutionContext
    ): Future[Either[SlackApiError, SlackApiChannelsHistoryResponse]] = {

      protectedSlackHttpApiPost[SlackApiChannelsHistoryRequest, SlackApiChannelsHistoryResponse](
        "channels.history",
        req
      )
    }

    /**
      * Scrolling support for
      * https://api.slack.com/methods/channels.history
      */
    def historyScroller( req: SlackApiChannelsHistoryRequest )(
        implicit slackApiToken: SlackApiUserToken,
        backend: SttpBackend[Future, Nothing, NothingT],
        ec: ExecutionContext
    ): SlackApiResponseScroller[SlackMessage, String] = {
      new SlackApiResponseScroller[SlackMessage, String](
        initialLoader = { () =>
          history( req )
        },
        batchLoader = { pos =>
          history(
            SlackApiChannelsHistoryRequest(
              channel = req.channel,
              oldest = Some( pos ),
              count = req.count
            )
          )
        }
      )
    }

    /**
      * https://api.slack.com/methods/channels.info
      */
    def info( req: SlackApiChannelsInfoRequest )(
        implicit slackApiToken: SlackApiUserToken,
        backend: SttpBackend[Future, Nothing, NothingT],
        ec: ExecutionContext
    ): Future[Either[SlackApiError, SlackApiChannelsInfoResponse]] = {

      protectedSlackHttpApiPost[SlackApiChannelsInfoRequest, SlackApiChannelsInfoResponse](
        "channels.info",
        req
      )
    }

  }

}
