# Davis-Putnam Algorithm Implementation

This repository contains a Java implementation of the Davis-Putnam algorithm, which is a complete, backtracking-based algorithm used to solve instances of the Boolean satisfiability problem (SAT).

## Overview

The implementation is divided into three main components:

- `DavisPutnam.java`: The core algorithm implementation that reads clauses from a file, applies the Davis-Putnam procedure, and writes the results to another file.
- `backEnd.java`: A utility class that processes the results of the Davis-Putnam algorithm and performs additional operations.
- `frontEnd.java`: [Description needed]

## Usage

To use this implementation:

1. Ensure you have Java installed on your system.
2. Clone this repository to your local machine.
3. Prepare an input file named `clauses.txt` with your SAT instance represented in DIMACS format.
4. Run `DavisPutnam.java` to execute the algorithm.
5. Check `results.txt` for the output of the SAT instance.

## Input Format

The input file `clauses.txt` should contain the clauses of the SAT instance, each clause separated by a newline and terminated by a `0`. For example:

```
1 -3 0
2 3 -1 0
```

## Output Format

The output file `results.txt` will contain the bindings for each atom in the format `<atom> <T/F>`, where `T` stands for True and `F` stands for False.
