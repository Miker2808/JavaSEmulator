package engine.expander;


import engine.SProgram;
import engine.instruction.SInstruction;

import java.util.List;

public class SProgramExpander {

    static public SProgram expand(SProgram program, int level){
        SProgram currentProgram = program;

        ExpansionContext expansionContext = new ExpansionContext(
                program.getMaxUsedZVariable(),
                program.getMaxUsedLabel()
        );

        // expansion is done iteratively (not recursively). each iteration (=degree) expands
        // on the previous expansion
        for(int i = 0; i < level; i++) {

            SProgram expandedProgram = new SProgram();
            expandedProgram.setName(program.getName());

            for (int line = 1; line <= currentProgram.Size(); line++) {
                SInstruction instr = currentProgram.getInstruction(line);
                if(instr.getDegree() > 0) {
                    List<SInstruction> expanded = instr.expand(expansionContext, line);
                    expandedProgram.addAll(expanded);
                }
                else{
                    expandedProgram.appendInstruction(instr);
                }
            }
            currentProgram = expandedProgram;
        }

        return currentProgram;
    }






}
