package engine;

import java.util.ArrayList;
import java.util.HashMap;

public class Program {
    private String name;
    private final ArrayList<Instruction> instructions;

    public Program(){
        instructions = new ArrayList<>();
        HashMap<String, String> arguments = new HashMap<>();
        arguments.put("variable", "y");
        instructions.add(new Instruction(Instruction.InstructionType.NEUTRAL, arguments)); // dummy at index 0
        name = "";
    }

    public void appendInstruction(Instruction instruction){
        instructions.add(instruction);
    }

    public void removeInstruction(int line_num){
        instructions.remove(line_num);
    }

    public void insertInstruction(int line_num, Instruction instruction){
        instructions.add(line_num, instruction);
    }

    public Instruction getInstruction(int line_num) {
        return instructions.get(line_num);
    }

    public int Size(){
        return instructions.size() - 1; // number of instructions in the program
    }

    public void rename(String new_name){
        this.name = new_name;
    }

}
