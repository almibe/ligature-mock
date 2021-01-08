// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this
// file, You can obtain one at http://mozilla.org/MPL/2.0/.

use ligature::{Dataset, Ligature, LigatureError, QueryTx, WriteTx};

pub struct LigatureMock {
    datasets: Vec<Dataset>,
}

impl LigatureMock {
    pub fn new() -> LigatureMock {
        LigatureMock {
            datasets: vec!(),
        }
    }
}

impl Ligature for LigatureMock {
    fn all_datasets(&self) -> Box<dyn Iterator<Item = Dataset>> {
        Box::from(std::iter::empty())
    }

    fn match_datasets(&self, prefix: &str) -> Box<dyn Iterator<Item = Dataset>> {
        Box::from(std::iter::empty())
    }

    fn match_datasets_range(
        &self,
        from: &str,
        to: &str,
    ) -> Box<dyn Iterator<Item = Dataset>> {
        Box::from(std::iter::empty())
    }

    fn create_dataset(&self, dataset: Dataset) -> Result<(), LigatureError> {
        todo!()
    }

    fn delete_dataset(&self, dataset: Dataset) -> Result<(), LigatureError> {
        todo!()
    }

    fn query(&self, dataset: Dataset) -> Result<Box<dyn QueryTx>, LigatureError> {
        todo!()
    }

    fn write(&self, dataset: Dataset) -> Result<Box<dyn WriteTx>, LigatureError> {
        todo!()
    }
}

// object LigatureMock extends Ligature {
//   private val acquire: Task[LigatureMockInstance] = Task(new LigatureMockInstance())

//   private def release(session: LigatureMockInstance): Task[Unit] = {
//     Task { session.close() }
//   }

//   override def instance: Resource[Task, LigatureInstance] = {
//     Resource.make(acquire)(release)
//   }
// }

// private final case class InMemoryDataset(anonymousCounter: Long = 0L,
//     statements: Set[PersistedStatement] = HashSet[PersistedStatement]())

//     private final class LigatureMockInstance extends LigatureInstance {
//         private val data: AtomicReference[Map[IRI, InMemoryDataset]] = new AtomicReference(HashMap[IRI, InMemoryDataset]())

//         def close(): Unit = { data.set(HashMap[IRI, InMemoryDataset]()) }

//         private val startReadTx: Task[LigatureMockReadTx] = Task { new LigatureMockReadTx(data.get()) }

//         private def releaseReadTx(tx: LigatureMockReadTx): Task[Unit] = Task { tx.cancel() }

//         override def read: Resource[Task, LigatureReadTx] = Resource.make(startReadTx)(releaseReadTx)

//         private val startWriteTx: Task[LigatureMockWriteTx] = Task { new LigatureMockWriteTx(data) }

//         private def releaseWriteTx(tx: LigatureMockWriteTx): Task[Unit] =
//           Task { tx.close() } //close double checks if transaction has been canceled

//         override def write: Resource[Task, LigatureWriteTx] = Resource.make(startWriteTx)(releaseWriteTx)
// }

// private final class LigatureMockReadTx(private val data: Map[IRI, InMemoryDataset]) extends LigatureReadTx {
//     override def datasets: Observable[IRI] = Observable.fromIterable(data.keySet)

//     override def datasets(prefix: String): Observable[IRI] =
//       Observable.fromIterable(data.keys.filter
//       { dataset => dataset.value.startsWith(prefix) })

//     override def datasets(from: String, to: String): Observable[IRI] =
//       Observable.fromIterable(data.keys.filter
//       { dataset => dataset.value >= from && dataset.value < to})

//     override def allStatements(dataset: IRI): Observable[PersistedStatement] =
//       Observable.fromIterable(data(dataset).statements)

//     override def matchStatements(dataset: IRI,
//                                  subject: Option[Subject],
//                                  predicate: Option[IRI],
//                                  `object`: Option[Object]): Observable[PersistedStatement] = ???

//     override def matchStatements(dataset: IRI,
//                                  subject: Option[Subject],
//                                  predicate: Option[IRI],
//                                  range: Range): Observable[PersistedStatement] = ???

//     override def statementByContext(dataset: IRI, context: BlankNode): Task[Option[PersistedStatement]] = Task {
//       ???
//     }

//     def cancel(): Unit = {
//       //do nothing
//     }
//   }

//   private final class LigatureMockWriteTx(private val data: AtomicReference[Map[IRI, InMemoryDataset]]) extends LigatureWriteTx {
//     private var workingCopy: Map[IRI, InMemoryDataset] = data.get()
//     private val isOpen = new AtomicBoolean(true)

//     override def createDataset(dataset: IRI): Task[IRI] = Task {
//       if (!workingCopy.keySet.contains(dataset)) {
//         workingCopy = workingCopy + (dataset -> InMemoryDataset())
//       }
//       dataset
//     }

//     override def deleteDataset(dataset: IRI): Task[IRI] = Task {
//       if (workingCopy.keySet.contains(dataset)) {
//         workingCopy = workingCopy.removed(dataset)
//       }
//       dataset
//     }

//     override def newNode(dataset: IRI): Task[BlankNode] = Task {
//       if (!workingCopy.keySet.contains(dataset)) {
//         workingCopy = workingCopy + (dataset -> InMemoryDataset())
//       }
//       val curCollection = workingCopy(dataset)
//       val nextId = curCollection.anonymousCounter + 1
//       workingCopy = workingCopy + (dataset -> curCollection.copy(anonymousCounter = nextId))
//       BlankNode(nextId)
//     }

//     override def addStatement(dataset: IRI, statement: Statement): Task[PersistedStatement] = Task {
//       if (!workingCopy.keySet.contains(dataset)) {
//         workingCopy = workingCopy + (dataset -> InMemoryDataset())
//       }
//       val curCollection: InMemoryDataset = workingCopy(dataset)
//       val nextId = curCollection.anonymousCounter + 1
//       val statements = curCollection.statements
//       val newStatement = PersistedStatement(dataset, statement, BlankNode(nextId))
//       val nextStatements = statements + newStatement
//       workingCopy = workingCopy + (dataset -> InMemoryDataset(nextId, nextStatements))
//       newStatement
//     }

//     override def removeStatement(dataset: IRI, statement: Statement): Task[Statement] = Task {
//       if (workingCopy.keySet.contains(dataset)) {
//         val curCollection: InMemoryDataset = workingCopy(dataset)
//         val statements = curCollection.statements
//         val nextStatements = statements.filterNot { ps =>
//           ps.statement == statement
//         }
//         workingCopy = workingCopy + (dataset -> curCollection.copy(statements = nextStatements))
//       }
//       statement
//     }

//     override def cancel(): Unit = {
//       isOpen.set(false)
//     }

//     def close(): Unit = {
//       if (isOpen.get()) {
//         data.set(workingCopy)
//         isOpen.set(false)
//       } else {
//         //do nothing
//       }
//     }
//   }
