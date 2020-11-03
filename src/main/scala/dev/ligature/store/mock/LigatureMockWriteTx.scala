/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.store.mock

import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}

import scala.collection.immutable.Map
import cats.effect.IO
import dev.ligature._

private class LigatureMockWriteTx(private val data: AtomicReference[Map[NamedNode, Collection]]) extends LigatureWriteTx {
  private val workingCopy: AtomicReference[Map[NamedNode, Collection]] = new AtomicReference(data.get())
  private val isOpen = new AtomicBoolean(true)

  override def createCollection(collection: NamedNode): IO[NamedNode] = IO {
    val working = workingCopy.get()
    if (!working.keySet.contains(collection)) {
      val newWorking = working + (collection -> Collection())
      workingCopy.set(newWorking)
    }
    collection
  }

  override def deleteCollection(collection: NamedNode): IO[NamedNode] = IO {
    ???
  }

  override def newNode(collection: NamedNode): IO[AnonymousNode] = IO {
    ???
  }

  override def addStatement(collection: NamedNode, statement: Statement): IO[PersistedStatement] = IO {
    ???
  }

  override def removeStatement(collection: NamedNode, statement: Statement): IO[Statement] = IO {
    ???
  }

  override def cancel(): Unit = {
    isOpen.set(false)
  }

  def close(): Unit = {
    if (isOpen.get()) {
      data.set(workingCopy.get())
      isOpen.set(false)
    } else {
      //do nothing
    }
  }
}
