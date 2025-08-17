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

    public void run() {
        engine = new Engine();
        scanner = new Scanner(System.in);  // Create a Scanner object to read input from console

        System.out.println("S-Emulator Console Version");
        System.out.println("------------------------------------");

        int option = 0;
        while (true) {

            printMenu();

            option = scanOption();
            executeOption(option);

        }

    }

    public void printMenu() {
        System.out.println("\nMenu:");
        System.out.println("[ 1 ] Load program");

        if (engine.isProgramLoaded()) {
            System.out.println("[ 2 ] Print program");
            System.out.println("[ 3 ] Expand program");
            System.out.println("[ 4 ] Run program");
            System.out.println("[ 5 ] Print execution history");
            System.out.println("[ 6 ] Save");
        }

        System.out.println("[ 7 ] Exit");

    }

    // scans option, and returns number if valid, 0 if invalid
    public int scanOption() {
        System.out.print("Enter option: ");
        String input = scanner.nextLine().trim();
        System.out.println();
        int option = 0;
        if (input.matches("\\d+")) {
            option = Integer.parseInt(input);
        }
        return option;
    }

    public void executeOption(int option) {
        boolean isProgramLoaded = engine.isProgramLoaded();

        if(isProgramLoaded) {
            switch (option) {
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
        else{
            switch (option) {
                case 1 -> loadFile();
                case 7 -> System.exit(0);
                default -> System.out.println("Invalid option, please load a file first.");
            }
        }
    }

    public void printProgram(){
        SProgram loadedProgram = engine.getLoadedProgram();

        System.out.println("Program name: " + loadedProgram.getName());

        System.out.println("Used input variables (in order of appearance):");
        loadedProgram.getInputVariablesUsed().forEach(System.out::println);
        System.out.println("Used labels (in order of appearance):");
        loadedProgram.getLabelsUsed().forEach(System.out::println);
        System.out.println("Program:");

        for (int line = 1; line <= loadedProgram.Size(); line++){
            System.out.printf("#%d %s\n", line, loadedProgram.getInstruction(line));
        }

    }

    public void loadFile(){
        System.out.print("Path to XML file: ");
        String path = scanner.nextLine();

        try {
            engine.loadFromXML(path);
        }
        catch(Exception e){
            System.out.print("Failed to load XML file: ");
            System.out.println(e.getMessage());
            System.out.println();
            return;
        }

        System.out.println("Program loaded successfully");

    }

    public void expandProgramOption(){
        System.out.println("Not implemented yet");
    }

    public void executeProgramOption(){
        System.out.println("Not implemented yet");
    }

    public void printHistoryOption(){
        System.out.println("Not implemented yet");
    }

    public void saveOption(){
        System.out.println("Not implemented yet");
    }




}
