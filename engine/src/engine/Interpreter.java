package engine;

import engine.Program;
import engine.Instruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Interpreter
{
    private Program program;
    private int pc;
    private int cycles;
    private final HashMap<String, Integer> variables;
    private final HashMap<String, Integer> labelMap;
    private boolean exit;

    public Interpreter(Program program){
        this.program = (program == null) ? new Program() : program;
        variables = new HashMap<>();
        labelMap = new HashMap<>();
        pc = 1;
        cycles = 0;
        exit = false;

        mapLabels();
    }

    // reset variables, labelMap, pc, cycles, and exit status
    private void reset(){
        variables.clear();
        labelMap.clear();
        pc = 1;
        cycles = 0;
        exit = false;
    }

    // emulates a run on a clean environment
    public HashMap<String, Integer> run(HashMap<String, Integer> input_variables){
        reset();
        mapLabels();
        variables.putAll(input_variables);
        int num_lines = program.Size();
        while(!exit && pc <= num_lines){
            step();
        }

        return variables;
    }

    public int getCycles(){
        return cycles;
    }

    public void loadProgram(Program program){
        this.program = (program == null) ? new Program() : program;
        pc = 1;
        cycles = 0;
        exit = false;
        variables.clear();
        mapLabels();
    }

    public HashMap<String, Integer> getVariables(){
        return variables;
    }

    // maps labels to line number
    // simply assigns label to map on first encounter each line
    private void mapLabels(){
        labelMap.clear();
        int size = program.Size();
        for (int line=1; line <= size; line++){
            Instruction instr = program.getInstruction(line);
            String label = instr.getLabel();
            if(label != null){
                if(!labelMap.containsKey(label)){
                    labelMap.put(label, line);
                }
            }
        }
    }

    // Runs a single step in execution
    public void step(){
        Instruction instr = program.getInstruction(pc);
        switch (instr.getInstructionType()) {
            case INCREASE -> stepIncrease(instr);
            case DECREASE -> stepDecrease(instr);
            case JUMP_NOT_ZERO -> stepJumpNotZero(instr);
            case NEUTRAL -> stepNeutral(instr);
            case ZERO_VARIABLE -> stepZeroVariable(instr);
            case GOTO_LABEL -> stepGotoLabel(instr);
            case ASSIGNMENT -> stepAssignment(instr);
            case CONSTANT_ASSIGNMENT -> stepConstAssignment(instr);
            case JUMP_ZERO -> stepJumpZero(instr);
            case JUMP_EQUAL_CONSTANT -> stepJumpEqualConst(instr);
            case JUMP_EQUAL_VARIABLE -> stepJumpEqualVariable(instr);
            case QUOTE -> stepQuote(instr);
            case JUMP_EQUAL_FUNCTION -> stepJumpEqualFunction(instr);
        }
    }


    // stepInstruction implies backstepInstruction will exist

    private void stepIncrease(Instruction instr){
        String variable = instr.getVariable();
        int value = variables.computeIfAbsent(variable, k -> 0) + 1;
        variables.put(variable, value);
        cycles += instr.getCycles();
        pc += 1;
    }

    private void stepDecrease(Instruction instr){
        String variable = instr.getVariable();
        int value = variables.computeIfAbsent(variable, k -> 0) - 1;
        if(value >= 0) {
            variables.put(variable, value);
        }
        cycles += instr.getCycles();
        pc += 1;
    }

    private void stepJumpNotZero(Instruction instr){
        String variable = instr.getVariable();
        HashMap<String, String> args =  instr.getArguments();
        String goto_label = args.get("gotoLabel");

        int value = variables.computeIfAbsent(variable, k -> 0);

        if(value != 0){
            if(Objects.equals(goto_label, "EXIT")) {
                exit = true;
            }
            else{
                pc = labelMap.get(goto_label);
            }
        }
        else{
            pc += 1;
        }

        cycles += instr.getCycles();
    }

    private void stepNeutral(Instruction instr){
        pc += 1;
    }

    private void stepZeroVariable(Instruction instr){
        String variable = instr.getVariable();
        variables.put(variable, 0);
        cycles += instr.getCycles();
        pc += 1;
    }

    private void stepGotoLabel(Instruction instr){
        HashMap<String, String> args =  instr.getArguments();
        String goto_label = args.get("gotoLabel");

        if(Objects.equals(goto_label, "EXIT")) {
            exit = true;
        }
        else{
            pc = labelMap.get(goto_label);
        }

        cycles += instr.getCycles();
    }

    private void stepAssignment(Instruction instr){
        String variable = instr.getVariable();
        HashMap<String, String> args =  instr.getArguments();
        String assignedVariable = args.get("assignedVariable");
        int value = variables.computeIfAbsent(assignedVariable, k -> 0);

        variables.put(variable, value);
        cycles += instr.getCycles();
        pc += 1;
    }

    private void stepConstAssignment(Instruction instr){
        String variable = instr.getVariable();
        HashMap<String, String> args =  instr.getArguments();
        int constValue = Integer.parseInt(args.get("constantValue"));

        variables.put(variable, constValue);
        cycles += instr.getCycles();
        pc += 1;
    }

    private void stepJumpZero(Instruction instr){
        String variable = instr.getVariable();
        HashMap<String, String> args =  instr.getArguments();
        String goto_label = args.get("JZLabel");

        int value = variables.computeIfAbsent(variable, k -> 0);

        if(value == 0){
            if(Objects.equals(goto_label, "EXIT")) {
                exit = true;
            }
            else{
                pc = labelMap.get(goto_label);
            }
        }
        else{
            pc += 1;
        }

        cycles += instr.getCycles();
    }

    private void stepJumpEqualConst(Instruction instr){
        HashMap<String, String> args =  instr.getArguments();
        String paramLabel = args.get("JEConstantLabel");
        int constValue = Integer.parseInt(args.get("constantValue"));
        int value = variables.computeIfAbsent(instr.getVariable(), k -> 0);

        if(value == constValue){
            if(Objects.equals(paramLabel, "EXIT")) {
                exit = true;
            }
            else{
                pc = labelMap.get(paramLabel);
            }
        }
        else{
            pc += 1;
        }

        cycles += instr.getCycles();
    }

    private void stepJumpEqualVariable(Instruction instr){
        HashMap<String, String> args =  instr.getArguments();
        int varValue = variables.computeIfAbsent(instr.getVariable(), k -> 0);
        int paramValue = variables.computeIfAbsent(args.get("variableName"), k -> 0);
        String paramLabel =  args.get("JEVariableLabel");

        if(varValue == paramValue){
            if(Objects.equals(paramLabel, "EXIT")) {
                exit = true;
            }
            else{
                pc = labelMap.get(paramLabel);
            }
        }
        else{
            pc += 1;
        }

        cycles += instr.getCycles();
    }

    private void stepQuote(Instruction instr){
        // TODO: complicated
    }

    private void stepJumpEqualFunction(Instruction instr){
        // TODO: complicated (not as much as the one above tho)
    }







}
