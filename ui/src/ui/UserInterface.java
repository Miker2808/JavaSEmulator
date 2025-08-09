package ui;

import engine.Instruction;
import engine.Interpreter;
import engine.Program;

import java.util.HashMap;
import java.util.Map;

public class UserInterface {

    public static void main(String[] args) {
        /* # f(x) = 3x - 1
        [     ] GOTO L2
        [ L1  ] Y <- Y + 1
        [     ] Y <- Y + 1
        [     ] Y <- Y + 1
        [     ] X18 <- X18 - 1
        [ L2  ] IF X18 != 0 GOTO L1
        [     ] Y <- Y + 1
         */

        Program program = new Program();

        HashMap<String, String> arguments = new HashMap<>();

        arguments.put("gotoLabel", "L2");
        program.appendInstruction(new Instruction(Instruction.InstructionType.GOTO_LABEL, arguments));

        arguments.clear();
        arguments.put("variable", "y");
        arguments.put("label", "L1");
        program.appendInstruction(new Instruction(Instruction.InstructionType.INCREASE, arguments));
        arguments.put("label", "");
        program.appendInstruction(new Instruction(Instruction.InstructionType.INCREASE, arguments));
        program.appendInstruction(new Instruction(Instruction.InstructionType.INCREASE, arguments));
        arguments.put("variable", "x1");
        program.appendInstruction(new Instruction(Instruction.InstructionType.DECREASE, arguments));
        arguments.clear();
        arguments.put("variable", "x1");
        arguments.put("gotoLabel", "L1");
        arguments.put("label", "L2");

        program.appendInstruction(new Instruction(Instruction.InstructionType.JUMP_NOT_ZERO, arguments));

        arguments.clear();
        arguments.put("variable", "y");
        program.appendInstruction(new Instruction(Instruction.InstructionType.DECREASE, arguments));


        System.out.print(program);

        Interpreter mainInterpreter = new Interpreter(program);

        HashMap<String, Integer> input = new HashMap<>();
        input.put("x1", 0);
        HashMap<String, Integer> output = mainInterpreter.run(input);

        for (Map.Entry<String, Integer> entry : output.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

    }
}
