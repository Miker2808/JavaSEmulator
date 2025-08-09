package engine;

import engine.Program;
import engine.Instruction;

import java.util.ArrayList;
import java.util.HashMap;

public class Interpreter
{

    private Program program;
    private int pc;
    private HashMap<String, Integer> variables;
    private HashMap<String, Integer> labelMap;


    public Interpreter(Program program){
        program = new Program();
        pc = 1;
        variables = new HashMap<>();
    }

    public void loadProgram(Program program){
        this.program = program;
    }


    // maps label to line numbers
    private void mapLabels(){
        labelMap.clear();
        int size = program.Size();

        for (int line=1; line <= size; line++){
            Instruction instr = program.getInstruction(line);
            String label = instr.getLabel();
            if(label != null){ // this is internal, so no need to check validity, it is check in load from xml phase
                if(!labelMap.containsKey(label)){
                    labelMap.put(label, line);
                }
            }
        }
    }

    // Runs a single step in execution
    public void step(){

    }






}
