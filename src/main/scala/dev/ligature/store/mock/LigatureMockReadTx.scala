/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.store.mock

import cats.effect.IO
import dev.ligature
import dev.ligature._
import fs2.Stream

private class LigatureMockReadTx extends LigatureReadTx {
  override def collections: Stream[IO, NamedNode] = ???

  override def collections(prefix: NamedNode): Stream[IO, NamedNode] = ???

  override def collections(from: NamedNode, to: NamedNode): Stream[IO, NamedNode] = ???

  override def allStatements(collection: NamedNode): Stream[IO, PersistedStatement] = ???

  override def matchStatements(collection: NamedNode, subject: Option[Node], predicate: Option[NamedNode], `object`: Option[ligature.Object]): Stream[IO, PersistedStatement] = ???

  override def matchStatements(collection: NamedNode, subject: Option[Node], predicate: Option[NamedNode], range: ligature.Range): Stream[IO, PersistedStatement] = ???

  override def statementByContext(collection: NamedNode, context: AnonymousNode): IO[Option[PersistedStatement]] = ???
}
