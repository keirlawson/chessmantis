# Chess Mantis

[![Build Status](https://travis-ci.org/keirlawson/chessmantis.svg)](https://travis-ci.org/keirlawson/chessmantis)

An xboard-compatible Java-based chess engine developed for a group project for the 3rd year of the University of Glasgow's Software Engineering degree.  The report on the project can be viewed [here](report.pdf)

## Running

To run, `mvn package` then simply set xboard's `-fcp` argument to point at Chess Mantis:

```bash
xboard -fcp "java -jar target/chess-mantis-1.0-SNAPSHOT-jar-with-dependencies.jar"
```

## Original authors

Paul Dailly, Dominik Gotojuch, Alec Macdonald, Neil Henning, Keir Lawson and Tamerlan Tajaddinov
