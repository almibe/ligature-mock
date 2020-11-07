/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.store.mock

import cats.effect.{IO, Resource}
import dev.ligature.{Ligature, LigatureInstance}

object LigatureMock extends Ligature {
  private val acquire: IO[LigatureMockInstance] = IO(new LigatureMockInstance())

  private def release(session: LigatureMockInstance): IO[Unit] = {
    IO { session.close() }
  }

  override def instance: Resource[IO, LigatureInstance] = {
    Resource.make(acquire)(release)
  }
}
