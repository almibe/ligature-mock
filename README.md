# ligature-mock
A painfully simple (yet working) implementation of [Ligature](https://github.com/almibe/ligature).

## Goals
 * Correctness - this project should pass all tests and should probably be the first project to pass all newly added tests
 * Simplicity - this project should be easy to understand so that the more complex implementations can reference it

## Non-Goals
 * Performance - there's a lot of linear scans used for the sake of simplicity
 * Persistence

## Building
This project requires SBT to be installed.
On Linux/Mac I recommend using https://sdkman.io/ to manage SBT installs.
Once that is set up use `sbt test` to run tests `sbt publishLocal` to install the artifact locally.
