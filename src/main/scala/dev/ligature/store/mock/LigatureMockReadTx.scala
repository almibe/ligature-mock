/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.store.mock

import dev.ligature
import dev.ligature._
import dev.ligature.iris.IRI
import monix.reactive.Observable
import monix.eval.Task

private final class LigatureMockReadTx(private val data: Map[IRI, InMemoryDataset]) extends LigatureReadTx {
  override def datasets: Observable[IRI] = Observable.fromIterable(data.keySet)

  override def datasets(prefix: String): Observable[IRI] =
    Observable.fromIterable(data.keys.filter
    { dataset => dataset.value.startsWith(prefix) })

  override def datasets(from: String, to: String): Observable[IRI] =
    Observable.fromIterable(data.keys.filter
    { dataset => dataset.value >= from && dataset.value < to})

  override def allStatements(dataset: IRI): Observable[PersistedStatement] =
    Observable.fromIterable(data(dataset).statements)

  override def matchStatements(dataset: IRI,
                               subject: Option[Subject],
                               predicate: Option[IRI],
                               `object`: Option[Object]): Observable[PersistedStatement] = ???

  override def matchStatements(dataset: IRI,
                               subject: Option[Subject],
                               predicate: Option[IRI],
                               range: Range): Observable[PersistedStatement] = ???

  override def statementByContext(dataset: IRI, context: BlankNode): Task[Option[PersistedStatement]] = Task {
    ???
  }

  def cancel(): Unit = {
    //do nothing
  }
}
