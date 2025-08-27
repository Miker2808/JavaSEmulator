package engine.instruction;

import engine.execution.ExecutionContext;
import engine.expander.ExpansionContext;
import engine.validator.InstructionValidator;

import java.util.ArrayList;
import java.util.List;

public class ZeroVariableInstruction extends  SInstruction {

    public ZeroVariableInstruction(SInstruction base) {
        super(base);
        // any additional initialization
        this.setCycles(1);
        this.setType("synthetic");
        this.setDegree(1);
    }

    public ZeroVariableInstruction(String variable, String label){
        super();
        this.setInstructionName(InstructionName.ZERO_VARIABLE);
        this.setSVariable(variable);
        this.setSLabel(label);
        this.setCycles(1);
        this.setType("synthetic");
        this.setDegree(1);
    }

    @Override
    protected String getOperationString(String variable) {
        return String.format("%s <- 0", variable);
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }

     // converts L1 x2 <- 0 to:
     // L1 x2 <- x2
     // L2 x2 <- x2
     //    IF x2 != 0 GOTO L2
    @Override
    public List<SInstruction> expand(ExpansionContext context, int line){
        List<SInstruction> expanded = new ArrayList<>();
        String var = this.getSVariable();
        String L1 = context.freshLabel();

        expanded.add(new NeutralInstruction(var, this.getSLabel()));
        expanded.add(new DecreaseInstruction(var, L1));
        expanded.add(new JumpNotZeroInstruction(var, "", L1));

        for(SInstruction instr : expanded){
            instr.setParentLine(line);
            instr.setParent(this);
        }

        return expanded;
    }

    @Override
    public void execute(ExecutionContext context){
        String var = this.getSVariable();
        context.getVariables().put(var, 0);
        context.increaseCycles(getCycles());
        context.increasePC(1);
    }

}
