/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.store.mock

import dev.ligature
import dev.ligature._
import monix.reactive.Observable
import monix.eval.Task

private final class LigatureMockReadTx(private val data: Map[NamedNode, InMemoryDataset]) extends LigatureReadTx {
  override def datasets: Observable[NamedNode] = Observable.fromIterable(data.keySet)

  override def datasets(prefix: NamedNode): Observable[NamedNode] =
    Observable.fromIterable(data.keys.filter
    { dataset => dataset.name.startsWith(prefix.name) })

  override def datasets(from: NamedNode, to: NamedNode): Observable[NamedNode] =
    Observable.fromIterable(data.keys.filter
    { dataset => dataset.name >= from.name && dataset.name < to.name})

  override def allStatements(dataset: NamedNode): Observable[PersistedStatement] =
    Observable.fromIterable(data(dataset).statements)

  override def matchStatements(dataset: NamedNode,
                               subject: Option[Node],
                               predicate: Option[NamedNode],
                               `object`: Option[ligature.Object]): Observable[PersistedStatement] = ???

  override def matchStatements(dataset: NamedNode,
                               subject: Option[Node],
                               predicate: Option[NamedNode],
                               range: ligature.Range): Observable[PersistedStatement] = ???

  override def statementByContext(dataset: NamedNode, context: AnonymousNode): Task[Option[PersistedStatement]] = Task {
    ???
  }

  def cancel(): Unit = {
    //do nothing
  }
}
