package engine.instruction;

import engine.SVariable.SVariable;
import engine.execution.ExecutionContext;
import engine.expander.ExpansionContext;
import engine.validator.InstructionValidator;

import java.util.ArrayList;
import java.util.List;

public class ConstantAssignmentInstruction extends SInstruction {
    private String constantValue;
    private final String argName = "constantValue";

    public ConstantAssignmentInstruction(SInstruction base) {
        super(base);
        this.setCycles(4);
        this.setType("synthetic");
        this.setDegree(2);
        this.setArgumentConst(getArgument(argName));
    }

    public ConstantAssignmentInstruction(String variable, String label, int constantValue) {
        super();
        this.setInstructionName(InstructionName.CONSTANT_ASSIGNMENT);
        this.setSVariable(variable);
        this.setSLabel(label);
        this.setCycles(4);
        this.setType("synthetic");
        this.setDegree(2);
        setArgumentConst(String.format("%d",constantValue));
    }

    public ConstantAssignmentInstruction(ConstantAssignmentInstruction other) {
        super(other);
        this.setCycles(other.getCycles());
        this.setType(other.getType());
        this.setDegree(other.getDegree());
        this.setArgumentConst(other.getArgumentConst());
    }

    @Override
    public ConstantAssignmentInstruction copy() {
        return new ConstantAssignmentInstruction(this);
    }

    public void setArgumentConst(String value) {
        this.constantValue = value.trim();
    }

    @Override
    public String getArgumentConst() {
        return constantValue;
    }

    @Override
    public String getInstructionString() {
        return String.format("%s <- %s", getSVariable(), getArgumentConst());
    }

    public String getArgumentConstName(){
        return argName;
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }

    @Override
    public List<SInstruction> expand(ExpansionContext context, int line){
        List<SInstruction> expanded = new ArrayList<>();
        String V = this.getSVariable();
        int K = Integer.parseInt(this.getArgumentConst()); // if exception, the problem is the validator!

        expanded.add(new ZeroVariableInstruction(V, this.getSLabel()));

        for(int i = 0; i < K; i++){
            expanded.add(new IncreaseInstruction(V, ""));
        }

        for(SInstruction instr : expanded){
            instr.setParentLine(line);
            instr.setParent(this);
        }

        return expanded;
    }

    @Override
    public void execute(ExecutionContext context){
        //String var = this.getSVariable();
        int value = Integer.parseInt(this.getArgumentConst());

        SVariable var = this.getSVariableS();

        if(value < 0){
            value = 0;
        }

        //context.getVariables().put(var, value);
        context.setVariableValue(var, value);

        context.increaseCycles(getCycles());
        context.increasePC(1);
    }


}
