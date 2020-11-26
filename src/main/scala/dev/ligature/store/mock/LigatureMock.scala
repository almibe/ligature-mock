/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.store.mock

import cats.effect.Resource
import dev.ligature.{Ligature, LigatureInstance}
import monix.eval.Task

object LigatureMock extends Ligature {
  private val acquire: Task[LigatureMockInstance] = Task(new LigatureMockInstance())

  private def release(session: LigatureMockInstance): Task[Unit] = {
    Task { session.close() }
  }

  override def instance: Resource[Task, LigatureInstance] = {
    Resource.make(acquire)(release)
  }
}
