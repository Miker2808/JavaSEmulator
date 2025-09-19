package engine.expander;


import engine.SInstructions;
import engine.SProgram;
import engine.SProgramView;
import engine.functions.SFunction;
import engine.instruction.SInstruction;

import java.util.List;

public class SProgramExpander {

    static public SInstructions expand(SInstructions program, int level){
        SInstructions currentProgram = program;

        ExpansionContext expansionContext = new ExpansionContext(
                program.getMaxUsedZVariable(),
                program.getMaxUsedLabel()
        );

        // expansion is done iteratively (not recursively). each iteration (=degree) expands
        // on the previous expansion
        for(int i = 0; i < level; i++) {

            SInstructions expandedProgram = new SInstructions();

            for (int line = 1; line <= currentProgram.size(); line++) {
                SInstruction instr = currentProgram.getInstruction(line);
                if(instr.getDegree() > 0) {
                    List<SInstruction> expanded = instr.expand(expansionContext, line);
                    expandedProgram.addAll(expanded);
                }
                else{
                    SInstruction instr_copy = instr.copy();
                    expandedProgram.appendInstruction(instr_copy);
                }
            }

            for (int line = 1; line <= expandedProgram.size(); line++) {
                expandedProgram.getInstruction(line).setLine(line);
            }

            currentProgram = expandedProgram;
        }

        return currentProgram;
    }

    static public SProgram expand(SProgram program, int level){
        SProgram expandedProgram = new SProgram();
        expandedProgram.setName(program.getName());

        SInstructions expanded_instructions = expand(program.getSInstructions(), level);

        expandedProgram.setSInstructions(expanded_instructions);

        return expandedProgram;
    }

    static public SFunction expand(SFunction program, int level){
        SFunction expandedFunction = new SFunction();
        expandedFunction.setName(program.getName());
        expandedFunction.setUserString(program.getUserString());

        SInstructions expanded_instructions = expand(program.getSInstructions(), level);

        expandedFunction.setSInstructions(expanded_instructions);

        return expandedFunction;
    }

    static public SProgramView expand(SProgramView program, int level){
        if(program.getProgramType().equals(SProgramView.ProgramType.PROGRAM)){
            return expand((SProgram)program, level);
        }
        else{
            return expand((SFunction)program, level);
        }
    }

}
