/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.store.mock

import cats.effect.IO
import dev.ligature._

private class LigatureMockWriteTx extends LigatureWriteTx {
  override def createCollection(collection: NamedNode): IO[NamedNode] = ???

  override def deleteCollection(collection: NamedNode): IO[NamedNode] = ???

  override def newNode(collection: NamedNode): IO[AnonymousNode] = ???

  override def addStatement(collection: NamedNode, statement: Statement): IO[PersistedStatement] = ???

  override def removeStatement(collection: NamedNode, statement: Statement): IO[Statement] = ???

  override def cancel(): Unit = ???
}
