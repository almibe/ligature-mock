/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.store.mock

import dev.ligature.PersistedStatement

import scala.collection.immutable.{HashSet, Set}

private final case class InMemoryDataset(anonymousCounter: Long = 0L,
                                         statements: Set[PersistedStatement] = HashSet[PersistedStatement]())
