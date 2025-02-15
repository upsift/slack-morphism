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

package org.latestbit.slack.morphism.client

import java.time.Instant

import cats.instances.future._
import cats.data.EitherT
import io.circe._
import io.circe.syntax._
import org.asynchttpclient.util.HttpConstants.Methods
import org.latestbit.slack.morphism.codecs.implicits._
import org.latestbit.slack.morphism.client.reqresp.chat._
import org.latestbit.slack.morphism.client.reqresp.conversations._
import org.latestbit.slack.morphism.client.reqresp.events.SlackApiEventMessageReply
import org.latestbit.slack.morphism.client.reqresp.test._
import org.latestbit.slack.morphism.common._
import org.latestbit.slack.morphism.events.SlackUserMessage
import org.scalatest.flatspec.AsyncFlatSpec
import sttp.client3.Response
import sttp.client3.testing.SttpBackendStub
import sttp.model.{ Header, MediaType, StatusCode }

import scala.collection.immutable.Seq
import scala.concurrent.Future

class CoreProtocolTestsSuite extends AsyncFlatSpec with SlackApiClientTestsSuiteSupport {

  "A Slack client" should "able to connect and make a test api call" in {

    val mockResponse = SlackApiTestResponse( args = Map( "test" -> "test" ) )
    implicit val testingBackend =
      SttpBackendStub.asynchronousFuture.whenAnyRequest
        .thenRespondF(
          createJsonResponseStub( mockResponse )
        )
    val slackApiClient = SlackApiClient.create()

    slackApiClient.api.test( SlackApiTestRequest() ).map {
      case Right( resp ) => assert( mockResponse === resp )
      case Left( ex )    => fail( ex )
    }
  }

  it should "detect Slack API error responses" in {
    implicit val testingBackend: SlackApiClientBackend.SttpBackendType[Future] =
      SttpBackendStub.asynchronousFuture.whenAnyRequest
        .thenRespondF(
          Future {
            Response(
              statusText = "OK",
              code = StatusCode.Ok,
              body = JsonObject(
                "ok"    -> false.asJson,
                "error" -> "slack-test-error".asJson
              ).asJson.dropNullValues.noSpaces,
              headers = Seq(
                Header.contentType( MediaType.ApplicationJson )
              )
            )
          }
        )

    val slackApiClient = SlackApiClient.create()

    slackApiClient.api.test( SlackApiTestRequest() ).map {
      case Right( resp )                     => fail( s"Unexpected resp: ${resp}" )
      case Left( ex: SlackApiResponseError ) => assert( ex.errorCode === "slack-test-error" )
      case Left( ex )                        => fail( ex )
    }
  }

  it should "able to make some basic API calls" in {
    import cats.implicits._

    implicit val testingBackend: SlackApiClientBackend.SttpBackendType[Future] =
      SttpBackendStub.asynchronousFuture
        .whenRequestMatches( _.uri.path.contains( "conversations.list" ) )
        .thenRespondF(
          createJsonResponseStub(
            SlackApiConversationsListResponse(
              channels = List(
                SlackChannelInfo(
                  id = SlackChannelId( "channel-id" ),
                  name = Some( "general" ),
                  flags = SlackChannelFlags(
                    is_general = Some( true )
                  ),
                  created = Instant.now()
                )
              )
            )
          )
        )
        .whenRequestMatches( _.uri.path.contains( "chat.postMessage" ) )
        .thenRespondF(
          createJsonResponseStub(
            SlackApiChatPostMessageResponse(
              ts = SlackTs( "message-ts" ),
              message = SlackUserMessage(
                ts = SlackTs( "message-ts" ),
                user = SlackUserId( "user-id" )
              )
            )
          )
        )

    val slackApiClient = SlackApiClient.create[Future]()
    implicit val slackApiToken: SlackApiToken =
      SlackApiToken.createFrom( SlackApiTokenType.Bot, SlackAccessTokenValue( "xoxb-89....." ) )

    EitherT( slackApiClient.conversations.list( SlackApiConversationsListRequest() ) )
      .flatMap { channelsResp =>
        channelsResp.channels
          .find( _.flags.is_general.contains( true ) )
          .map { generalChannel =>
            EitherT(
              slackApiClient.chat
                .postMessage(
                  SlackApiChatPostMessageRequest(
                    channel = generalChannel.id,
                    text = "Hello"
                  )
                )
            ).map { resp => resp.ts.some }
          }
          .getOrElse(
            EitherT[Future, SlackApiClientError, Option[SlackTs]](
              Future.successful( None.asRight )
            )
          )
      }
      .value
      .map {
        case Right( Some( res ) ) => {
          assert( res.value == "message-ts" )

        }
        case Right( _ )  => fail()
        case Left( err ) => fail( err )
      }
  }

  it should "able to post event replies using response_url without tokens" in {

    val testReply = SlackApiEventMessageReply(
      text = "hey"
    )

    implicit val testingBackend =
      SttpBackendStub.asynchronousFuture
        .whenRequestMatches { req =>
          req.uri.path.contains( "some-response-url" ) &&
          req.method.method == Methods.POST &&
          isExpectedJsonBody( req.body, testReply )
        }
        .thenRespondF(
          createTextResponseStub( "Ok" )
        )

    val slackApiClient = SlackApiClient.create()

    slackApiClient.events
      .reply(
        "https://example.net/some-response-url",
        testReply
      )
      .map {
        case Right( resp ) => assert( resp != null )
        case Left( ex )    => fail( ex )
      }
  }

  it should "able to post webhook messages using urls without tokens" in {

    val testWebHookMessage = SlackApiPostWebHookRequest(
      text = "hey"
    )

    implicit val testingBackend =
      SttpBackendStub.asynchronousFuture
        .whenRequestMatches { req =>
          req.uri.path.contains( "some-webhook-url" ) &&
          req.method.method == Methods.POST &&
          isExpectedJsonBody( req.body, testWebHookMessage )
        }
        .thenRespondF(
          createTextResponseStub( "Ok" )
        )

    val slackApiClient = SlackApiClient.create()

    slackApiClient.chat
      .postWebhookMessage(
        "https://example.net/some-webhook-url",
        testWebHookMessage
      )
      .map {
        case Right( resp ) => assert( resp != null )
        case Left( ex )    => fail( ex )
      }
  }

}
