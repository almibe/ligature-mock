/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.store.mock

import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}

import scala.collection.immutable.Map
import cats.effect.IO
import dev.ligature._

private class LigatureMockWriteTx(private val data: AtomicReference[Map[Dataset, InMemoryDataset]]) extends LigatureWriteTx {
  private var workingCopy: Map[Dataset, InMemoryDataset] = data.get()
  private val isOpen = new AtomicBoolean(true)

  override def createDataset(dataset: Dataset): IO[Dataset] = IO {
    if (!workingCopy.keySet.contains(dataset)) {
      workingCopy = workingCopy + (dataset -> InMemoryDataset())
    }
    dataset
  }

  override def deleteDataset(dataset: Dataset): IO[Dataset] = IO {
    if (workingCopy.keySet.contains(dataset)) {
      workingCopy = workingCopy.removed(dataset)
    }
    dataset
  }

  override def newNode(dataset: Dataset): IO[AnonymousNode] = IO {
    if (!workingCopy.keySet.contains(dataset)) {
      workingCopy = workingCopy + (dataset -> InMemoryDataset())
    }
    val curCollection = workingCopy(dataset)
    val nextId = curCollection.anonymousCounter + 1
    workingCopy = workingCopy + (dataset -> curCollection.copy(anonymousCounter = nextId))
    AnonymousNode(nextId)
  }

  override def addStatement(dataset: Dataset, statement: Statement): IO[PersistedStatement] = IO {
    if (!workingCopy.keySet.contains(dataset)) {
      workingCopy = workingCopy + (dataset -> InMemoryDataset())
    }
    val curCollection: InMemoryDataset = workingCopy(dataset)
    val nextId = curCollection.anonymousCounter + 1
    val statements = curCollection.statements
    val newStatement = PersistedStatement(dataset, statement, AnonymousNode(nextId))
    val nextStatements = statements + newStatement
    workingCopy = workingCopy + (dataset -> InMemoryDataset(nextId, nextStatements))
    newStatement
  }

  override def removeStatement(dataset: Dataset, statement: Statement): IO[Statement] = IO {
    if (workingCopy.keySet.contains(dataset)) {
      val curCollection: InMemoryDataset = workingCopy(dataset)
      val statements = curCollection.statements
      val nextStatements = statements.filterNot { ps =>
        ps.statement == statement
      }
      workingCopy = workingCopy + (dataset -> curCollection.copy(statements = nextStatements))
    }
    statement
  }

  override def cancel(): Unit = {
    isOpen.set(false)
  }

  def close(): Unit = {
    if (isOpen.get()) {
      data.set(workingCopy)
      isOpen.set(false)
    } else {
      //do nothing
    }
  }
}
