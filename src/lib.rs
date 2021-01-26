// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this
// file, You can obtain one at http://mozilla.org/MPL/2.0/.

use ligature::{
    BlankNode, Dataset, Ligature, LigatureError, QueryResult, QueryTx, Statement, WriteTx,
};
use std::cell::RefCell;
use std::collections::BTreeMap;
use std::sync::RwLock;

#[derive(Debug, Clone)]
struct Store {
    datasets: BTreeMap<Dataset, Vec<Statement>>,
    cntr: u64,
}

impl Store {
    fn new() -> Store {
        Store {
            datasets: BTreeMap::new(),
            cntr: 0,
        }
    }
}

pub struct LigatureMock {
    store: RwLock<RefCell<Store>>,
}

impl LigatureMock {
    pub fn new() -> LigatureMock {
        LigatureMock {
            store: RwLock::new(RefCell::new(Store::new())),
        }
    }
}

impl Ligature for LigatureMock {
    fn all_datasets(&self) -> Box<dyn Iterator<Item = Dataset>> {
        let v: Vec<Dataset> = self
            .store
            .read()
            .unwrap()
            .borrow()
            .datasets
            .keys()
            .cloned()
            .collect();
        Box::from(v.into_iter())
    }

    fn match_datasets(&self, prefix: &str) -> Box<dyn Iterator<Item = Dataset>> {
        //TODO needs to match prefix only
        let v: Vec<Dataset> = self
            .store
            .read()
            .unwrap()
            .borrow()
            .datasets
            .keys()
            .cloned()
            .collect();
        Box::from(v.into_iter())
    }

    fn match_datasets_range(&self, from: &str, to: &str) -> Box<dyn Iterator<Item = Dataset>> {
        //TODO needs to match range only
        let v: Vec<Dataset> = self
            .store
            .read()
            .unwrap()
            .borrow()
            .datasets
            .keys()
            .cloned()
            .collect();
        Box::from(v.into_iter())
    }

    fn create_dataset(&self, dataset: Dataset) -> Result<(), LigatureError> {
        self.store
            .write()
            .unwrap()
            .borrow_mut()
            .datasets
            .insert(dataset, Vec::new());
        Ok(())
    }

    fn delete_dataset(&self, dataset: Dataset) -> Result<(), LigatureError> {
        self.store
            .write()
            .unwrap()
            .borrow_mut()
            .datasets
            .remove(&dataset);
        Ok(())
    }

    fn query(&self, dataset: Dataset) -> Result<Box<dyn QueryTx>, LigatureError> {
        let t = self
            .store
            .read()
            .unwrap();
        let tt = t
            .borrow();
        let dataset_copy = tt
            .datasets
            .get(&dataset)
            .clone()
            .ok_or_else(|| LigatureError(format!("Dataset {} does not exist.", dataset.name())))?;
        Ok(Box::new(LigatureMockQueryTx {
            dataset: dataset_copy.to_vec(),
        }))
    }

    fn write(&self, dataset: Dataset) -> Result<Box<dyn WriteTx>, LigatureError> {
        todo!()
    }
}

struct LigatureMockQueryTx {
    dataset: Vec<Statement>,
}

impl QueryTx for LigatureMockQueryTx {
    fn all_statements(&self) -> Box<dyn Iterator<Item = Statement>> {
        todo!()
    }

    fn sparql_query(&self, query: String) -> Result<QueryResult, LigatureError> {
        todo!()
    }

    fn wander_query(&self, query: String) -> Result<QueryResult, LigatureError> {
        todo!()
    }
}

struct LigatureMockWriteTx {
    store: RefCell<Store>,
}

impl WriteTx for LigatureMockWriteTx {
    fn new_blank_node(&self) -> Result<BlankNode, LigatureError> {
        todo!()
    }

    fn add_statement(&self, statement: Statement) -> Result<Statement, LigatureError> {
        todo!()
    }

    fn remove_statement(&self, statement: Statement) -> Result<Statement, LigatureError> {
        todo!()
    }

    fn cancel(&self) -> Result<(), LigatureError> {
        todo!()
    }

    fn commit(&self) -> Result<(), LigatureError> {
        todo!()
    }
}

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

//     override def addStatement(dataset: IRI, statement: Statement): Task[Statement] = Task {
//       if (!workingCopy.keySet.contains(dataset)) {
//         workingCopy = workingCopy + (dataset -> InMemoryDataset())
//       }
//       val curCollection: InMemoryDataset = workingCopy(dataset)
//       val nextId = curCollection.anonymousCounter + 1
//       val statements = curCollection.statements
//       val newStatement = Statement(dataset, statement, BlankNode(nextId))
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
