package engine;

import engine.instruction.InstructionName;
import engine.instruction.SInstruction;

public class SProgramExpander {

    private SProgram program;

    SProgramExpander(SProgram program){
        this.program = program;
    }

    public int getMaxUsedLabel(){
        int max_label_num = 1;
        for(int line = 1; line <= this.program.Size(); line++){
            String sLabel = this.program.getInstruction(line).getSLabel();
            int label_num = Integer.parseInt(sLabel.substring(1));
            if(label_num > max_label_num){
                max_label_num = label_num;
            }
        }
        return max_label_num;
    }

    public int getMaxUsedZVariable(){
        int max_z_var = 1;
        for(int line = 1; line <= this.program.Size(); line++){
            String sVariable = this.program.getInstruction(line).getSVariable();
            if(sVariable.startsWith("z")) {
                int var_num = Integer.parseInt(sVariable.substring(1));
                if (var_num > max_z_var) {
                    max_z_var = var_num;
                }
            }
        }
        return max_z_var;
    }

    /*
    public SProgram returnExpandedProgram(int level){
        SProgram expandedProgram = new SProgram();

        for(int line = 1; line <= this.program.Size(); line++){
            SInstruction instr =  this.program.getInstruction(line);
            InstructionName instrName = instr.getInstructionName();

            switch(instrName){
                case INCREASE, DECREASE, JUMP_NOT_ZERO, NEUTRAL:{
                    expandedProgram.appendInstruction(instr);
                }
                case


            }

        }

    }
    */




}
