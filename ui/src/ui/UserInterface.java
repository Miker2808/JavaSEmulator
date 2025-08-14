package ui;

import engine.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UserInterface {

    public static void main(String[] args) {

        test2();

    }

    public static void test2(){
        Engine engine = new Engine();
        Scanner scanner = new Scanner(System.in);  // Create a Scanner object to read input from console

        while(true) {
            System.out.print("Write path to xml file: ");
            String path = scanner.nextLine();  // Read a whole line of input

            try {
                engine.loadFromXML(path);

                System.out.println(engine.getLoadedProgramString());

                SInterpreter mainInterpreter = new SInterpreter(engine.getLoadedProgram());

                HashMap<String, Integer> input = new HashMap<>();
                input.put("x1", 10000);
                input.put("x2", 8766);
                HashMap<String, Integer> output = mainInterpreter.run(input);

                System.out.println("Variables results:");
                System.out.println(SInterpreter.convertVariablesToString(output));

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    public static void test1(){
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
        arguments.put("gotoLabel", "L8");
        program.appendInstruction(new SInstruction("GOTO_LABEL", "", "", arguments));
        program.appendInstruction(new SInstruction("INCREASE","y", "L1",null));
        program.appendInstruction(new SInstruction("INCREASE","y", "",null));
        program.appendInstruction(new SInstruction("INCREASE","y", "",null));
        program.appendInstruction(new SInstruction("DECREASE","x18", "",null));

        arguments.clear();
        arguments.put("JNZLabel", "L1");
        program.appendInstruction(new SInstruction("JUMP_NOT_ZERO", "x18","L8",arguments));
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
