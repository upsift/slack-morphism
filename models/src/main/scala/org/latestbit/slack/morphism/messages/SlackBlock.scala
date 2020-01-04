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

package org.latestbit.slack.morphism.messages

import cats.data.NonEmptyList
import io.circe._
import io.circe.generic.semiauto._
import org.latestbit.circe.adt.codec._

/**
 * Blocks are a series of components that can be combined to create visually rich and compellingly interactive messages and views.
 * https://api.slack.com/reference/block-kit/blocks
 */
sealed trait SlackBlock {
  val block_id: Option[String]
}

@JsonAdt( "section" )
case class SlackSectionBlock(
    text: SlackBlockText,
    override val block_id: Option[String] = None,
    fields: Option[List[SlackBlockText]] = None,
    accessory: Option[SlackBlockElement] = None
) extends SlackBlock

@JsonAdt( "divider" )
case class SlackDividerBlock( override val block_id: Option[String] = None ) extends SlackBlock

@JsonAdt( "image" )
case class SlackImageBlock(
    image_url: String,
    alt_text: String,
    title: Option[SlackBlockPlainText] = None,
    override val block_id: Option[String] = None
) extends SlackBlock

@JsonAdt( "actions" )
case class SlackActionsBlock( elements: NonEmptyList[SlackBlockElement], override val block_id: Option[String] = None )
    extends SlackBlock

@JsonAdt( "context" )
case class SlackContextBlock( elements: NonEmptyList[SlackBlockElement], override val block_id: Option[String] = None )
    extends SlackBlock

@JsonAdt( "input" )
case class SlackInputBlock(
    label: SlackBlockPlainText,
    element: SlackBlockElement,
    hint: Option[SlackBlockPlainText] = None,
    optional: Option[Boolean] = None,
    override val block_id: Option[String] = None
) extends SlackBlock

@JsonAdt( "file" )
case class SlackFileBlock(
    external_id: String,
    source: String = "remote",
    override val block_id: Option[String] = None
) extends SlackBlock

@JsonAdt( "rich_text" )
case class SlackRichTextBlock( elements: List[SlackBlockElement], override val block_id: Option[String] = None )
    extends SlackBlock
