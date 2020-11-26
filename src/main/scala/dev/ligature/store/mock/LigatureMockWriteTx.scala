/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.store.mock

import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}

import scala.collection.immutable.Map
import dev.ligature._
import monix.eval.Task

private final class LigatureMockWriteTx(private val data: AtomicReference[Map[NamedNode, InMemoryDataset]]) extends LigatureWriteTx {
  private var workingCopy: Map[NamedNode, InMemoryDataset] = data.get()
  private val isOpen = new AtomicBoolean(true)

  override def createDataset(dataset: NamedNode): Task[NamedNode] = Task {
    if (!workingCopy.keySet.contains(dataset)) {
      workingCopy = workingCopy + (dataset -> InMemoryDataset())
    }
    dataset
  }

  override def deleteDataset(dataset: NamedNode): Task[NamedNode] = Task {
    if (workingCopy.keySet.contains(dataset)) {
      workingCopy = workingCopy.removed(dataset)
    }
    dataset
  }

  override def newNode(dataset: NamedNode): Task[AnonymousNode] = Task {
    if (!workingCopy.keySet.contains(dataset)) {
      workingCopy = workingCopy + (dataset -> InMemoryDataset())
    }
    val curCollection = workingCopy(dataset)
    val nextId = curCollection.anonymousCounter + 1
    workingCopy = workingCopy + (dataset -> curCollection.copy(anonymousCounter = nextId))
    AnonymousNode(nextId)
  }

  override def addStatement(dataset: NamedNode, statement: Statement): Task[PersistedStatement] = Task {
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

  override def removeStatement(dataset: NamedNode, statement: Statement): Task[Statement] = Task {
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
