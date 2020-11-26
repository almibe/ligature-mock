/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.store.mock

import java.util.concurrent.atomic.AtomicReference

import cats.effect.Resource
import dev.ligature.{LigatureInstance, LigatureReadTx, LigatureWriteTx, NamedNode}
import monix.eval.Task

import scala.collection.immutable.{HashMap, Map}

private final class LigatureMockInstance extends LigatureInstance {
  private val data: AtomicReference[Map[NamedNode, InMemoryDataset]] = new AtomicReference(HashMap[NamedNode, InMemoryDataset]())

  def close(): Unit = { data.set(HashMap[NamedNode, InMemoryDataset]()) }

  private val startReadTx: Task[LigatureMockReadTx] = Task { new LigatureMockReadTx(data.get()) }

  private def releaseReadTx(tx: LigatureMockReadTx): Task[Unit] = Task { tx.cancel() }

  override def read: Resource[Task, LigatureReadTx] = Resource.make(startReadTx)(releaseReadTx)

  private val startWriteTx: Task[LigatureMockWriteTx] = Task { new LigatureMockWriteTx(data) }

  private def releaseWriteTx(tx: LigatureMockWriteTx): Task[Unit] =
    Task { tx.close() } //close double checks if transaction has been canceled

  override def write: Resource[Task, LigatureWriteTx] = Resource.make(startWriteTx)(releaseWriteTx)
}
