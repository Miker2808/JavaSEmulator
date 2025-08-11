package ui;

import engine.Engine;
import engine.Instruction;
import engine.Interpreter;
import engine.Program;

import java.util.HashMap;
import java.util.Map;

public class UserInterface {

    private Engine engine;

    public static void main(String[] args) {
        /* # f(x) = 3x
        [     ] GOTO L2
        [ L1  ] Y <- Y + 1
        [     ] Y <- Y + 1
        [     ] Y <- Y + 1
        [     ] X18 <- X18 - 1
        [ L2  ] IF X18 != 0 GOTO L1
         */

        Program program = new Program();

        HashMap<String, String> arguments = new HashMap<>();
        arguments.put("gotoLabel", "L2");
        program.appendInstruction(new Instruction(Instruction.InstructionType.GOTO_LABEL, "", "", arguments));
        program.appendInstruction(new Instruction(Instruction.InstructionType.INCREASE,"y", "L1",null));
        program.appendInstruction(new Instruction(Instruction.InstructionType.INCREASE,"y", "",null));
        program.appendInstruction(new Instruction(Instruction.InstructionType.INCREASE,"y", "",null));
        program.appendInstruction(new Instruction(Instruction.InstructionType.DECREASE,"x18", "",null));

        arguments.clear();
        arguments.put("JNZLabel", "L1");
        program.appendInstruction(new Instruction(Instruction.InstructionType.JUMP_NOT_ZERO, "x18","L2",arguments));

        System.out.print(program);

        Interpreter mainInterpreter = new Interpreter(program);

        HashMap<String, Integer> input = new HashMap<>();
        input.put("x18", 100);
        HashMap<String, Integer> output = mainInterpreter.run(input);

        for (Map.Entry<String, Integer> entry : output.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

    }
}
