package ui;

import engine.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UserInterface {
    Engine engine;
    Scanner scanner;

    public static void main(String[] args) {
        UserInterface ui = new UserInterface();
        ui.run();
    }

    public void run(){
        engine = new Engine();
        scanner = new Scanner(System.in);  // Create a Scanner object to read input from console

        System.out.println("S-Emulator Console Version");

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
                System.out.println("File failed to load");
                System.out.println(e.getMessage());
            }
        }

    }

    public void printMenu(){
        System.out.println("Menu:");
        System.out.println("[ 1 ] Load program");

        if(engine.isProgramLoaded()){
            System.out.println("[ 2 ] Print program");
            System.out.println("[ 3 ] Expand program");
            System.out.println("[ 4 ] Run program");
            System.out.println("[ 5 ] Print execution history");
            System.out.println("[ 6 ] Save");
        }

        System.out.println("[ 7 ] Exit");

    }

    // scans option, and returns number if valid, 0 if invalid
    public int scanOption(){
        System.out.print("Enter option: ");
        String input = scanner.nextLine().trim();
        System.out.println();
        int option = 0;
        if(input.matches("\\d+")){
            option = Integer.parseInt(input);
        }
        return option;
    }

    public int executeOption(int option){
        switch(option){
            case 1 -> loadFile();
            case 2 -> printProgram();
            case 3 -> expandProgramOption();
            case 4 -> executeProgramOption();
            case 5 -> printHistoryOption();
            case 6 -> saveOption();
            case 7 -> System.exit(0);
            default -> System.out.println("Invalid option, please choose from the options in the menu");
        }
    }

    public void printProgram(){

    }

    public void loadFile(){
        System.out.print("Path to XML file: ");
        String path = scanner.nextLine();

    }

}
