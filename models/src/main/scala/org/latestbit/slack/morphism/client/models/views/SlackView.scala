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

package org.latestbit.slack.morphism.client.models.views

import io.circe._
import io.circe.generic.auto._
import org.latestbit.circe.adt.codec._
import org.latestbit.slack.morphism.client.models.messages.{ SlackBlock, SlackBlockPlainText }

sealed trait SlackView

@JsonAdt( "modal" )
case class SlackModalView(
    title: SlackBlockPlainText,
    blocks: List[SlackBlock],
    close: Option[SlackBlockPlainText] = None,
    submit: Option[SlackBlockPlainText] = None,
    private_metadata: Option[String] = None,
    callback_id: Option[String] = None,
    clear_on_close: Option[Boolean] = None,
    notify_on_close: Option[Boolean] = None,
    hash: Option[String] = None,
    external_id: Option[String] = None
) extends SlackView

@JsonAdt( "home" )
case class SlackHomeView(
    blocks: List[SlackBlock],
    private_metadata: Option[String] = None,
    callback_id: Option[String] = None,
    external_id: Option[String] = None
) extends SlackView

object SlackView {
  implicit val encoder = JsonTaggedAdtCodec.createEncoder[SlackView]( "type" )
  implicit val decoder = JsonTaggedAdtCodec.createDecoder[SlackView]( "type" )
}

case class SlackStatefulViewParams(
    id: String,
    team_id: String,
    state: Option[SlackViewState] = None,
    hash: String,
    previous_view_id: Option[String] = None,
    root_view_id: Option[String] = None,
    app_id: Option[String] = None,
    bot_id: Option[String] = None
)

case class SlackStatefulView(
    params: SlackStatefulViewParams,
    view: SlackView
)

case class SlackViewStateValue( `type`: String, value: String )

case class SlackViewState( values: Map[String, Json] = Map() ) {}

object SlackStatefulView {

  def createEncoder()(
      implicit viewEncoder: Encoder.AsObject[SlackView],
      paramsEncoder: Encoder.AsObject[SlackStatefulViewParams]
  ): Encoder.AsObject[SlackStatefulView] = (model: SlackStatefulView) => {
    viewEncoder.encodeObject( model.view ).deepMerge( paramsEncoder.encodeObject( model.params ) )
  }

  def createDecoder(): Decoder[SlackStatefulView] = (cursor: HCursor) => {
    for {
      view <- cursor.as[SlackView]
      params <- cursor.as[SlackStatefulViewParams]
    } yield SlackStatefulView(
      params,
      view
    )
  }

  implicit val encoder = createEncoder()
  implicit val decoder = createDecoder()
}
