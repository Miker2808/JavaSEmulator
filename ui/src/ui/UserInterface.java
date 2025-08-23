package ui;

import engine.*;
import engine.execution.ExecutionResult;

import java.util.*;

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

        System.out.println(".------------------------------.");
        System.out.println("|  S-Emulator Console Version  |");
        System.out.print("'------------------------------'");

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
        }

        System.out.println("[ 6 ] Exit");

        if(engine.isProgramLoaded()) {
            System.out.println("[ 7 ] Save S-Emulator session");
        }
        System.out.println("[ 8 ] Load S-Emulator session");
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
            SProgram loadedProgram = engine.getLoadedProgram();

            switch (option) {
                case 1 -> loadFile();
                case 2 -> printProgram(loadedProgram);
                case 3 -> expandProgramOption();
                case 4 -> executeProgramOption();
                case 5 -> printHistory();
                case 6 -> System.exit(0);
                case 7 -> saveOption();
                default -> System.out.println("Invalid option, please choose from the options in the menu");
            }
        }
        else{
            switch (option) {
                case 1 -> loadFile();
                case 6 -> System.exit(0);
                default -> System.out.println("Invalid option, please load a file first.");
            }
        }
    }


    public void printProgram(SProgram program){
        System.out.println("Program name: " + program.getName());

        System.out.println("Used input variables:");
        program.getInputVariablesUsed().forEach(System.out::println);
        System.out.println("Used labels:");
        program.getLabelsUsed().forEach(System.out::println);
        System.out.println("Program:");

        for (int line = 1; line <= program.Size(); line++){
            System.out.printf("#%d %s\n", line, program.getInstruction(line));
        }

    }


    public void loadFile(){
        System.out.print("Path to XML file: ");
        String path = scanner.nextLine();

        try {
            engine.loadFromXML(path);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Program loaded successfully");

    }


    public void expandProgramOption() {
        SProgram loaded = engine.getLoadedProgram();

        int chosenDegree = getMaxDegreeFromUser(loaded);

        SProgram expanded = engine.expandProgram(loaded, chosenDegree);

        printProgram(expanded);

    }

    public void executeProgramOption(){

        SProgram program = engine.getLoadedProgram();

        int degree = getMaxDegreeFromUser(program);

        // print input variables used in the program
        System.out.print("Input variables used in the program:\n");
        program.getInputVariablesUsed().forEach(System.out::println);

        // ask for variables from user
        ArrayList<Integer> input;
        while(true){
            try {
                input = readPositiveIntegers();
                break;
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

        printProgram(engine.expandProgram(program, degree));

        ExecutionResult result = engine.runProgram(program, input, degree);

        System.out.println("Program finished execution: ");

        for (Map.Entry<String, Integer> entry : result.getVariables().entrySet()) {
            System.out.printf("%s = %d\n", entry.getKey(), entry.getValue());
        }
        System.out.printf("Cycles: " + result.getCycles());
    }


    public void saveOption(){
        System.out.println("Not implemented yet");
    }


    public static ArrayList<Integer> readPositiveIntegers() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter positive integers separated by commas (or leave empty): ");
        String input = scanner.nextLine().trim();

        ArrayList<Integer> numbers = new ArrayList<>();

        // Allow empty input -> return empty list
        if (input.isEmpty()) {
            return numbers;
        }

        String[] tokens = input.split(",");

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();

            if (token.isEmpty()) {
                throw new IllegalArgumentException("Empty value at position " + (i + 1));
            }

            try {
                int value = Integer.parseInt(token);

                if (value <= 0) {
                    throw new IllegalArgumentException("Invalid number at position " + (i + 1) +
                            ": " + value + " (must be positive).");
                }

                numbers.add(value);

            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid input at position " + (i + 1) +
                        ": \"" + token + "\" is not an integer.");
            }
        }

        return numbers;
    }


    public int getMaxDegreeFromUser(SProgram program){
        int maxDegree = program.getMaxDegree();
        int chosenDegree = -1;

        while (true) {
            System.out.printf("Program can be expanded up to degree %d\n", maxDegree);
            System.out.print("Choose expansion degree: ");

            String input = scanner.nextLine().trim();
            try {
                chosenDegree = Integer.parseInt(input);
                if (chosenDegree >= 0 && chosenDegree <= maxDegree) {
                    break;
                } else {
                    System.out.printf("Invalid degree input, please choose between 0 and %d\n", maxDegree);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please enter an integer.");
            }
        }
        return chosenDegree;
    }

    public void printHistory(){
        List<ExecutionHistory> history = engine.getExecutionHistory();

        if(history.isEmpty()){
            System.out.println("Execution history is empty");
            return;
        }

        System.out.print("Execution history:");

        for(int i=0; i<history.size(); i++){
            ExecutionHistory eh = history.get(i);

            System.out.printf("\nExecution %d\n", i+1);
            System.out.printf("Degree: %d\n", eh.getDegree());
            System.out.println("Input variables:");
            for(int j=0; j<eh.getVariables().size(); j++){
                System.out.printf("x%d = %s\n", j+1, eh.getVariables().get(j));
            }
            System.out.printf("y = %d\n", eh.getY());
            System.out.printf("Cycles: %d\n", eh.getCycles());
        }


    }


}
