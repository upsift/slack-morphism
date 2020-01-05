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

package org.latestbit.slack.morphism.common

import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._

case class SlackUserInfo(
    id: String,
    team_id: Option[String] = None,
    name: Option[String] = None,
    deleted: Option[Boolean] = None,
    color: Option[String] = None,
    real_name: Option[String] = None,
    tz: Option[String] = None,
    tz_label: Option[String] = None,
    tz_offset: Option[Int] = None,
    updated: Option[SlackDateTime] = None,
    locale: Option[String] = None,
    profile: Option[SlackUserProfile] = None,
    flags: SlackUserFlags = SlackUserFlags()
)

case class SlackUserProfile(
    id: Option[String] = None,
    avatar_hash: Option[String] = None,
    status_text: Option[String] = None,
    status_expiration: Option[SlackDateTime] = None,
    real_name: Option[String] = None,
    display_name: Option[String] = None,
    real_name_normalized: Option[String] = None,
    display_name_normalized: Option[String] = None,
    email: Option[String] = None,
    icon: Option[SlackIcon] = None,
    team: Option[String] = None
)

case class SlackUserFlags(
    is_admin: Option[Boolean] = None,
    is_owner: Option[Boolean] = None,
    is_primary_owner: Option[Boolean] = None,
    is_restricted: Option[Boolean] = None,
    is_ultra_restricted: Option[Boolean] = None,
    is_bot: Option[Boolean] = None,
    is_stranger: Option[Boolean] = None,
    is_app_user: Option[Boolean] = None,
    has_2fa: Option[Boolean] = None
)

case class SlackBasicUserInfo( id: String, team_id: Option[String] = None, username: Option[String] = None )