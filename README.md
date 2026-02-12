# JavaSEmulator

A Complete Implementation of the Theoretical S Programming Language

Based on *"Computability, Complexity, and Languages: Fundamentals of Theoretical Computer Science"*  
by Martin D. Davis, Ron Sigal, and Elaine J. Weyuker

---

## Overview

JavaSEmulator is a comprehensive emulator and debugger for the S Programming Language, a theoretical language fundamental to the study of computability theory. This implementation provides an interactive environment for writing, executing, and analyzing S programs, bridging the gap between abstract theoretical concepts and practical computation.

The S language, despite its minimal instruction set, is Turing complete and capable of expressing any computable function. This project serves as both an educational tool for studying computational theory and a practical demonstration of how primitive operations can be composed to achieve universal computation.

---

## Theoretical Background

### Computability and Turing Completeness

The S language is provably Turing complete, meaning it can simulate any Turing machine and compute any function that is computable in the Church-Turing sense. The language achieves this expressiveness through a minimal set of primitive operations:

- Variable manipulation (increment and decrement operations)
- Conditional branching based on zero-testing
- Label-based control flow
- Unbounded variable space

### Language Definition

The S language operates on an infinite set of natural number variables (x₁, x₂, ..., xₙ) and provides:

**Primitive Instructions:**
- Variable increment and decrement operations
- Conditional jumps based on variable equality to zero
- Unconditional jumps to labeled instructions
- Program termination

**Extended Features:**
- Multi-generation instruction expansion system
- Function and macro definitions
- Hierarchical program composition

This implementation maintains fidelity to the formal definition presented in the Davis, Sigal, and Weyuker textbook, ensuring that programs executed in this emulator behave identically to their mathematical specification.

### Academic Significance

Understanding the S language provides insight into several fundamental concepts in computer science:

1. **Minimal Computation Models**: How complex behavior emerges from simple primitive operations
2. **Computational Equivalence**: The relationship between different models of computation
3. **Algorithmic Complexity**: The relationship between program structure and computational cost
4. **Formal Program Semantics**: Rigorous mathematical foundations for program behavior

JavaSEmulator makes these abstract concepts tangible by providing tools to execute, visualize, and analyze S programs in detail.

---

## System Features

### Execution Environment

The emulator provides a complete execution environment for S programs with the following capabilities:

**Program Execution:**
- Instruction-by-instruction execution with full state tracking
- Forward and backward stepping through program execution
- Breakpoint-based debugging
- Execution history with complete state reconstruction
- Cycle counting for complexity analysis

**Program Validation:**
- Static analysis to verify program correctness
- Label resolution and validation
- Type checking for instruction arguments
- Function dependency validation

**Multi-Generation Support:**
The system supports four generations of instruction complexity:
- Generation I: Basic primitive operations
- Generation II: Compound operations
- Generation III: Function calls and macros
- Generation IV: Advanced control structures

Programs can be analyzed by generation composition, providing insight into their architectural complexity.

### User Interface

The desktop application, built with JavaFX, provides:

- Real-time visualization of program execution with instruction highlighting
- Interactive variable state inspection
- Breakpoint management on any instruction line
- Execution control (step, continue, stop, backstep)
- Program statistics and complexity metrics
- Multi-user program sharing and management

### Architecture

The system is designed with a modular architecture:

**Engine Module:**  
Contains the core interpreter (`SInterpreter`), instruction definitions, execution context management, and program validation logic. The engine is independent of the user interface and can be used programmatically or integrated into other systems.

**UI Module:**  
Provides the JavaFX-based desktop application with controllers for different views (login, dashboard, main execution environment), custom UI components, and network communication for the client-server architecture.

**Server Module:**  
Implements a multi-user execution server with session management, user authentication, program storage, execution history tracking, and a credit-based resource management system.

**DTO Module:**  
Defines data transfer objects shared between the client and server, ensuring consistent data representation across the distributed system.

---

## Program Representation

S programs are represented in an XML format that closely mirrors the formal language definition. This approach provides several advantages:

- Human-readable program representation
- Easy version control and sharing
- Straightforward validation against XML schema
- Support for program composition and modularization

Example program structure:

```xml
<S-Program name="ProgramName">
  <S-Instructions>
    <S-Instruction type="basic">
      <S-Variable>z1</S-Variable>
      <S-Instruction-Arguments>
        <S-Instruction-Argument name="variable" value="x1"/>
      </S-Instruction-Arguments>
      <S-Label>LOOP</S-Label>
    </S-Instruction>
    <!-- Additional instructions -->
  </S-Instructions>
  <S-Functions>
    <!-- Function definitions -->
  </S-Functions>
</S-Program>
```

---

## Implementation Details

### Execution Model

The `SInterpreter` class implements the execution semantics of the S language:

```java
public ExecutionContext step(boolean keephistory)
```

Each execution step:
1. Validates the program counter is within bounds
2. Retrieves the instruction at the current program counter
3. Executes the instruction, updating the execution context
4. Records the state in execution history if requested
5. Deducts computational credits based on instruction cost
6. Checks for termination conditions

The interpreter supports backward execution by maintaining a history of execution contexts, allowing users to step backward through program execution for detailed analysis.

### State Management

The `ExecutionContext` class encapsulates the complete state of program execution:

- Program counter
- Variable values (stored in a map structure)
- Exit flag
- Cycle count

This design allows for precise state tracking and enables features like execution history, backward stepping, and state comparison.

### Instruction Architecture

Instructions are implemented using polymorphism, with a base `SInstruction` class and specific subclasses for each instruction type. Each instruction implements:

- Execution logic via the `execute(ExecutionContext)` method
- Validation logic via the `validate(InstructionValidator)` method  
- String representation for display purposes
- Cycle cost calculation
- Generation classification

---

## Building and Running

### Prerequisites

- Java Development Kit (JDK) 11 or higher
- JavaFX SDK (for the UI module)
- Jakarta XML Binding (JAXB) for XML processing

### Compilation

The project consists of multiple modules that should be compiled in dependency order:

```bash
# Compile DTO module
cd dto/src
javac -d ../../build/dto dto/**/*.java

# Compile engine module
cd ../../engine/src
javac -cp ../../build/dto -d ../../build/engine engine/**/*.java

# Compile UI module
cd ../../ui/src
javac -cp ../../build/dto:../../build/engine -d ../../build/ui ui/**/*.java
```

### Execution

Run the desktop application:

```bash
java --module-path $JAVAFX_HOME/lib --add-modules javafx.controls,javafx.fxml \
     -cp build/dto:build/engine:build/ui ui.App
```

---

## Demonstration

![Demo](docs/assets/demo.gif)

---

## Project Structure

```
JavaSEmulator/
│
├── engine/
│   └── src/engine/
│       ├── interpreter/          # Core execution engine
│       │   └── SInterpreter.java
│       ├── instruction/          # Instruction definitions
│       │   └── SInstruction.java
│       ├── execution/            # Execution state management
│       │   ├── ExecutionContext.java
│       │   └── ExecutionContextHistory.java
│       ├── functions/            # Function and macro support
│       ├── history/              # Execution history tracking
│       └── validator/            # Program validation
│
├── ui/
│   └── src/ui/
│       ├── controllers/          # JavaFX controllers
│       │   ├── MainController.java
│       │   ├── DashboardController.java
│       │   └── LoginController.java
│       ├── elements/             # Custom UI components
│       └── netcode/              # Client-server communication
│
├── semulator-server/
│   └── src/
│       ├── Storage/              # Session and user management
│       │   └── UserInstance.java
│       └── DTOConverter/         # Data transfer object conversion
│
└── dto/
    └── src/dto/                  # Shared data structures
        ├── SProgramDTO.java
        ├── SInstructionDTO.java
        └── ExecutionDTO.java
```

---

## Use Cases

### Educational Applications

- **Computability Theory Courses**: Students can implement and test algorithms from theoretical exercises
- **Algorithm Analysis**: Measure computational complexity through cycle counting
- **Comparative Study**: Compare different algorithmic approaches to the same problem
- **Proof Verification**: Verify constructive proofs by implementing the construction

### Research Applications

- **Computational Complexity Studies**: Empirical analysis of algorithm efficiency
- **Program Equivalence**: Compare different programs computing the same function
- **Optimization Research**: Study the effects of instruction-level optimizations

---

## References

Davis, M. D., Sigal, R., & Weyuker, E. J. (1994). *Computability, Complexity, and Languages: Fundamentals of Theoretical Computer Science* (2nd ed.). Academic Press.

---

## License

[License information to be added]

---

*JavaSEmulator - A tool for exploring the foundations of computation through the S programming language*
