package ui;

import engine.Instruction;

import java.util.HashMap;

public class UserInterface {

    public static void main(String[] args) {

        HashMap<String, String> arguments = new HashMap<>();
        arguments.put("variable", "z123");
        //arguments.put("label", "L12");
        arguments.put("JEVariableLabel", "L5");
        arguments.put("assignedVariable", "y");
        arguments.put("variableName", "x99");
        Instruction i1 = new Instruction(Instruction.InstructionType.JUMP_EQUAL_VARIABLE, arguments);
        System.out.println(i1);

    }
}
