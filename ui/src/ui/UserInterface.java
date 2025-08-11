package ui;

import engine.*;

import java.util.HashMap;
import java.util.Map;

public class UserInterface {

    private Engine engine;

    public static void main(String[] args) {
        /* # f(x) = 3x - 2
        [     ] GOTO L2
        [ L1  ] Y <- Y + 1
        [     ] Y <- Y + 1
        [     ] Y <- Y + 1
        [     ] X18 <- X18 - 1
        [ L2  ] IF X18 != 0 GOTO L1
        [     ] Y <- Y - 1
        [     ] Y <- Y - 1
         */

        SProgram program = new SProgram();

        HashMap<String, String> arguments = new HashMap<>();
        arguments.put("gotoLabel", "L2");
        program.appendInstruction(new SInstruction("GOTO_LABEL", "", "", arguments));
        program.appendInstruction(new SInstruction("INCREASE","y", "L1",null));
        program.appendInstruction(new SInstruction("INCREASE","y", "",null));
        program.appendInstruction(new SInstruction("INCREASE","y", "",null));
        program.appendInstruction(new SInstruction("DECREASE","x18", "",null));

        arguments.clear();
        arguments.put("JNZLabel", "L1");
        program.appendInstruction(new SInstruction("JUMP_NOT_ZERO", "x18","L2",arguments));
        program.appendInstruction(new SInstruction("DECREASE","y","",null));
        program.appendInstruction(new SInstruction("DECREASE","y","",null));

        System.out.print(program);

        SInterpreter mainInterpreter = new SInterpreter(program);

        HashMap<String, Integer> input = new HashMap<>();
        input.put("x18", 100);
        HashMap<String, Integer> output = mainInterpreter.run(input);

        for (Map.Entry<String, Integer> entry : output.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

    }
}
