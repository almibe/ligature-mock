/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.store.mock

import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}

import scala.collection.immutable.Map
import dev.ligature._
import dev.ligature.iris.IRI
import monix.eval.Task

private final class LigatureMockWriteTx(private val data: AtomicReference[Map[IRI, InMemoryDataset]]) extends LigatureWriteTx {
  private var workingCopy: Map[IRI, InMemoryDataset] = data.get()
  private val isOpen = new AtomicBoolean(true)

  override def createDataset(dataset: IRI): Task[IRI] = Task {
    if (!workingCopy.keySet.contains(dataset)) {
      workingCopy = workingCopy + (dataset -> InMemoryDataset())
    }
    dataset
  }

  override def deleteDataset(dataset: IRI): Task[IRI] = Task {
    if (workingCopy.keySet.contains(dataset)) {
      workingCopy = workingCopy.removed(dataset)
    }
    dataset
  }

  override def newNode(dataset: IRI): Task[BlankNode] = Task {
    if (!workingCopy.keySet.contains(dataset)) {
      workingCopy = workingCopy + (dataset -> InMemoryDataset())
    }
    val curCollection = workingCopy(dataset)
    val nextId = curCollection.anonymousCounter + 1
    workingCopy = workingCopy + (dataset -> curCollection.copy(anonymousCounter = nextId))
    BlankNode(nextId)
  }

  override def addStatement(dataset: IRI, statement: Statement): Task[PersistedStatement] = Task {
    if (!workingCopy.keySet.contains(dataset)) {
      workingCopy = workingCopy + (dataset -> InMemoryDataset())
    }
    val curCollection: InMemoryDataset = workingCopy(dataset)
    val nextId = curCollection.anonymousCounter + 1
    val statements = curCollection.statements
    val newStatement = PersistedStatement(dataset, statement, BlankNode(nextId))
    val nextStatements = statements + newStatement
    workingCopy = workingCopy + (dataset -> InMemoryDataset(nextId, nextStatements))
    newStatement
  }

  override def removeStatement(dataset: IRI, statement: Statement): Task[Statement] = Task {
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
