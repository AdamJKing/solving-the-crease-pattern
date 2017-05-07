# Crease-Pattern Solver

An application for reading and "solving" Origami crease patterns. This includes finding a workable
fold-order for the folds in the crease pattern.

## 1 Modules

* Core (crease-pattern-solver-core)

  This module contains all the code that supports the crease-pattern model. It produces a separate
  JAR that would be used by other modules as a dependency. This would allow multiple applications
  to be developed using the model.


* Interface (crease-pattern-solver-interface)

  This module contains all the code relevant to the user interface as well as the execution of the
  application. Unlike the Core module this module produces a runnable JAR that can be executed on
  the command-line. Interface (crease-pattern-solver-interface)

## 2 Prerequisites

To build and run this application you will need the following:
* A valid JVM installation ([Java Download](https://java.com/en/download/))
* The Maven build tool ([Maven download](https://maven.apache.org/download.cgi))

## 3 How to Build

To build the entire application with tests:
```
mvn clean install
```

To build all modules without tests:
```
mvn clean install -skipTests
```

## 4 How To Install/Run

Once you have completed the instructions in section 3, you can run the application using the
following command:
```
cd {REPOSITORY_ROOT}/crease-pattern-solver-interface/
java -jar ./target/crease-pattern-solver-interface-2.0-SNAPSHOT-jar-with-dependencies.jar
```

### 4.1 Prepackaged Crease Pattern Files

The application comes with some prepackaged crease pattern files you may use with the application.
These can be found in the **Crease Pattern Files** folder.
They are:
* *multi-layered-crease-pattern.json*
  
  This is a mid-level complexity crease-pattern with 3 folds.
  
* *simple-crease-pattern.json*

  This is a simple complexity crease-pattern with only 1 fold.
