/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.store.mock

import java.util.concurrent.atomic.AtomicReference

import cats.effect.IO
import dev.ligature._

private class LigatureMockWriteTx(private val data: AtomicReference[Map[NamedNode, Collection]]) extends LigatureWriteTx {
  //private val workingCopy: AtomicReference[Map[NamedNode, Collection]] = new AtomicReference(data.get())

  override def createCollection(collection: NamedNode): IO[NamedNode] = IO {
    ???
  }

  override def deleteCollection(collection: NamedNode): IO[NamedNode] = IO {
    ???
  }

  override def newNode(collection: NamedNode): IO[AnonymousNode] = IO {
    ???
  }

  override def addStatement(collection: NamedNode, statement: Statement): IO[PersistedStatement] = IO {
    ???
  }

  override def removeStatement(collection: NamedNode, statement: Statement): IO[Statement] = IO {
    ???
  }

  override def cancel(): Unit = {
    ???
  }

  def close(): Unit = {
    ???
  }
}
