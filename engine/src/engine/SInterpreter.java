package engine;

import engine.instruction.InstructionName;
import engine.instruction.SInstruction;

import java.util.*;

public class SInterpreter
{
    private SProgram program;
    private int pc;
    private int cycles;
    private final HashMap<String, Integer> variables;
    private final HashMap<String, Integer> labelMap;
    private boolean exit;

    public SInterpreter(SProgram program){
        this.program = (program == null) ? new SProgram() : program;
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

    public void loadProgram(SProgram program){
        this.program = (program == null) ? new SProgram() : program;
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
            SInstruction instr = program.getInstruction(line);
            String label = instr.getSLabel();
            if(label != null){
                if(!labelMap.containsKey(label)){
                    labelMap.put(label, line);
                }
            }
        }
    }

    // Runs a single step in execution
    public void step(){
        SInstruction instr = program.getInstruction(pc);
        switch (instr.getInstructionName()) {
            case InstructionName.INCREASE -> stepIncrease(instr);
            case InstructionName.DECREASE -> stepDecrease(instr);
            case InstructionName.JUMP_NOT_ZERO -> stepJumpNotZero(instr);
            case InstructionName.NEUTRAL -> stepNeutral(instr);
            case InstructionName.ZERO_VARIABLE -> stepZeroVariable(instr);
            case InstructionName.GOTO_LABEL -> stepGotoLabel(instr);
            case InstructionName.ASSIGNMENT -> stepAssignment(instr);
            case InstructionName.CONSTANT_ASSIGNMENT -> stepConstAssignment(instr);
            case InstructionName.JUMP_ZERO -> stepJumpZero(instr);
            case InstructionName.JUMP_EQUAL_CONSTANT -> stepJumpEqualConst(instr);
            case InstructionName.JUMP_EQUAL_VARIABLE -> stepJumpEqualVariable(instr);
            case InstructionName.QUOTE -> stepQuote(instr);
            case InstructionName.JUMP_EQUAL_FUNCTION -> stepJumpEqualFunction(instr);
        }
    }


    // stepInstruction implies backstepInstruction will exist

    private void stepIncrease(SInstruction instr){
        String variable = instr.getSVariable();
        int value = variables.computeIfAbsent(variable, k -> 0) + 1;
        variables.put(variable, value);
        cycles += instr.getCycles();
        pc += 1;
    }

    private void stepDecrease(SInstruction instr){
        String variable = instr.getSVariable();
        int value = variables.computeIfAbsent(variable, k -> 0) - 1;
        if(value >= 0) {
            variables.put(variable, value);
        }
        cycles += instr.getCycles();
        pc += 1;
    }

    private void stepJumpNotZero(SInstruction instr){
        String variable = instr.getSVariable();
        String goto_label = instr.getArgument("JNZLabel");

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

    private void stepNeutral(SInstruction instr){
        pc += 1;
    }

    private void stepZeroVariable(SInstruction instr){
        String variable = instr.getSVariable();
        variables.put(variable, 0);
        cycles += instr.getCycles();
        pc += 1;
    }

    private void stepGotoLabel(SInstruction instr){
        String goto_label = instr.getArgument("gotoLabel");

        if(Objects.equals(goto_label, "EXIT")) {
            exit = true;
        }
        else{
            pc = labelMap.get(goto_label);
        }

        cycles += instr.getCycles();
    }

    private void stepAssignment(SInstruction instr){
        String variable = instr.getSVariable();
        String assignedVariable = instr.getArgument("assignedVariable");
        int value = variables.computeIfAbsent(assignedVariable, k -> 0);

        variables.put(variable, value);
        cycles += instr.getCycles();
        pc += 1;
    }

    private void stepConstAssignment(SInstruction instr){
        String variable = instr.getSVariable();
        int constValue = Integer.parseInt(instr.getArgument("constantValue"));

        variables.put(variable, constValue);
        cycles += instr.getCycles();
        pc += 1;
    }

    private void stepJumpZero(SInstruction instr){
        String variable = instr.getSVariable();
        String goto_label = instr.getArgument("JZLabel");

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

    private void stepJumpEqualConst(SInstruction instr){
        String paramLabel = instr.getArgument("JEConstantLabel");
        int constValue = Integer.parseInt(instr.getArgument("constantValue"));
        int value = variables.computeIfAbsent(instr.getSVariable(), k -> 0);

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

    private void stepJumpEqualVariable(SInstruction instr){
        int varValue = variables.computeIfAbsent(instr.getSVariable(), k -> 0);
        int paramValue = variables.computeIfAbsent(instr.getArgument("variableName"), k -> 0);
        String paramLabel =  instr.getArgument("JEVariableLabel");

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

    private void stepQuote(SInstruction instr){
        // TODO: complicated
    }

    private void stepJumpEqualFunction(SInstruction instr){
        // TODO: complicated (not as much as the one above tho)
    }









}
