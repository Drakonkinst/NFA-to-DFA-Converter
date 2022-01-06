# NFA-to-DFA Converter

A Java program that converts an **NFA** (nondeterministic finite state automata) to a **DFA** (deterministic finite state automata). It resolves all branching paths and epsilon transitions by creating new states to ensure that the result is deterministic.

The input file specifying the NFA is passed as a command line argument, and should be of the following format. Examples can be found in the repository.

- Line 1: A list of states, *Q*, separated by tabs.
- Line 2: A list of the symbols in Σ, separated by tabs.
- Line 3:  The start state, *q0* ∈ *Q*.
- Line 4:  The set of valid accept states, *F* , separated by tabs.
- Line 5 to EOF: The transition function. Each line should be of the form `A, x = B`, which translates to mean that reading symbol `x` in state `A` causes a transition to state `B`. The string `EPS` can be used in place of `x` to represent an epsilon transition.

The output is a text file with the `.DFA` extension, which is `output.DFA` by default. `EM` is encoded as the empty state.

## Identifying Information

- Name: Wesley Ho
- Chapman ID: 2382489
- Email: weho@chapman.edu
- Course: CPSC-406-01 (Algorithm Analysis)
- Assignment: Course Project

This is my own work, and I did not cheat on this assignment.

## Source Files

- FSA.java
- Main.java
- State.java
- Transition.java

## References

- [Reading a file in Java](https://www.journaldev.com/709/java-read-file-line-by-line)
- [Writing a file in Java](https://stackoverflow.com/questions/2885173/how-do-i-create-a-file-and-write-to-it)
- [Iterating through the permutations of a bit array](https://stackoverflow.com/questions/39863994/permutations-of-a-boolean-array)
- [String.join](https://stackoverflow.com/questions/599161/best-way-to-convert-an-arraylist-to-a-string)

## Known Errors

None.

## Execution Instructions

- Compile all Java files using `javac *.java`.
- To run the program, use `java Main <input>` where `<input>` is replaced by the input file with a correct input format. Some example files, including `input.nfa` and `nfa_example.nfa`, are provided.
- The converted DFA should appear both in console and in the file `output.DFA`.
