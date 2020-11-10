/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.store.mock

import cats.effect.IO
import dev.ligature
import dev.ligature._
import fs2.Stream

private class LigatureMockReadTx(private val data: Map[Dataset, InMemoryDataset]) extends LigatureReadTx {
  override def datasets: Stream[IO, Dataset] = Stream.fromIterator[IO] {
    data.keysIterator
  }

  override def datasets(prefix: Dataset): Stream[IO, Dataset] = Stream.fromIterator[IO] {
    data.keys.filter { dataset => dataset.name.startsWith(prefix.name) }.iterator
  }

  override def datasets(from: Dataset, to: Dataset): Stream[IO, Dataset] = Stream.fromIterator[IO] {
    data.keys.filter { dataset => dataset.name >= from.name && dataset.name < to.name}.iterator
  }

  override def allStatements(dataset: Dataset): Stream[IO, PersistedStatement] = Stream.fromIterator[IO] {
    data(dataset).statements.iterator
  }

  override def matchStatements(dataset: Dataset,
                               subject: Option[Node],
                               predicate: Option[NamedNode],
                               `object`: Option[ligature.Object]): Stream[IO, PersistedStatement] = ???

  override def matchStatements(dataset: Dataset,
                               subject: Option[Node],
                               predicate: Option[NamedNode],
                               range: ligature.Range): Stream[IO, PersistedStatement] = ???

  override def statementByContext(dataset: Dataset, context: AnonymousNode): IO[Option[PersistedStatement]] = IO {
    ???
  }

  def cancel(): Unit = {
    //do nothing
  }
}
