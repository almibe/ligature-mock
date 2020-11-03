/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.store.mock

import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}

import scala.collection.immutable.Map
import cats.effect.IO
import dev.ligature._

private class LigatureMockWriteTx(private val data: AtomicReference[Map[NamedNode, Collection]]) extends LigatureWriteTx {
  private var workingCopy: Map[NamedNode, Collection] = data.get()
  private val isOpen = new AtomicBoolean(true)

  override def createCollection(collection: NamedNode): IO[NamedNode] = IO {
    if (!workingCopy.keySet.contains(collection)) {
      workingCopy = workingCopy + (collection -> Collection())
    }
    collection
  }

  override def deleteCollection(collection: NamedNode): IO[NamedNode] = IO {
    if (workingCopy.keySet.contains(collection)) {
      workingCopy = workingCopy.removed(collection)
    }
    collection
  }

  override def newNode(collection: NamedNode): IO[AnonymousNode] = IO {
    if (!workingCopy.keySet.contains(collection)) {
      workingCopy = workingCopy + (collection -> Collection())
    }
    val curCollection = workingCopy(collection)
    val nextId = curCollection.anonymousCounter + 1
    workingCopy = workingCopy + (collection -> curCollection.copy(anonymousCounter = nextId))
    AnonymousNode(nextId)
  }

  override def addStatement(collection: NamedNode, statement: Statement): IO[PersistedStatement] = IO {
    if (!workingCopy.keySet.contains(collection)) {
      workingCopy = workingCopy + (collection -> Collection())
    }
    val curCollection: Collection = workingCopy(collection)
    val nextId = curCollection.anonymousCounter + 1
    val statements = curCollection.statements
    val newStatement = PersistedStatement(collection, statement, AnonymousNode(nextId))
    val nextStatements = statements + newStatement
    workingCopy = workingCopy + (collection -> Collection(nextId, nextStatements))
    newStatement
  }

  override def removeStatement(collection: NamedNode, statement: Statement): IO[Statement] = IO {
    if (workingCopy.keySet.contains(collection)) {
      val curCollection: Collection = workingCopy(collection)
      val statements = curCollection.statements
      val nextStatements = statements.filterNot { ps =>
        ps.statement == statement
      }
      workingCopy = workingCopy + (collection -> curCollection.copy(statements = nextStatements))
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
