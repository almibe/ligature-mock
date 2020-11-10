/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.store.mock

import java.util.concurrent.atomic.AtomicReference

import cats.effect.{IO, Resource}
import dev.ligature.{Dataset, LigatureInstance, LigatureReadTx, LigatureWriteTx}

import scala.collection.immutable.{HashMap, Map}

private class LigatureMockInstance extends LigatureInstance {
  private val data: AtomicReference[Map[Dataset, InMemoryDataset]] = new AtomicReference(HashMap[Dataset, InMemoryDataset]())

  def close(): Unit = { data.set(HashMap[Dataset, InMemoryDataset]()) }

  private val startReadTx: IO[LigatureMockReadTx] = IO { new LigatureMockReadTx(data.get()) }

  private def releaseReadTx(tx: LigatureMockReadTx): IO[Unit] = IO { tx.cancel() }

  override def read: Resource[IO, LigatureReadTx] = Resource.make(startReadTx)(releaseReadTx)

  private val startWriteTx: IO[LigatureMockWriteTx] = IO { new LigatureMockWriteTx(data) }

  private def releaseWriteTx(tx: LigatureMockWriteTx): IO[Unit] =
    IO { tx.close() } //close double checks if transaction has been canceled

  override def write: Resource[IO, LigatureWriteTx] = Resource.make(startWriteTx)(releaseWriteTx)
}
