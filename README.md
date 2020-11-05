# ligature-xodus
An implementation of Ligature based on Xodus's EntityStore api.
This project focuses on implementing Ligature with more of a graph-centric approach
as opposed to the other key-value based stores.

## Goals
 * Correctness - this project should pass all tests and should probably be the first project to pass all newly added tests
 * Simplicity - this project should be easy to understand so that the more complex implementations can reference it

## Building
This project requires SBT to be installed.
On Linux/Mac I recommend using https://sdkman.io/ to manage SBT installs.
Once that is set up use `sbt test` to run tests `sbt publishLocal` to install the artifact locally.
