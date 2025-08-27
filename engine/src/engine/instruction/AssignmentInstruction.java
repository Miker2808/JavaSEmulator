package engine.instruction;

import engine.execution.ExecutionContext;
import engine.expander.ExpansionContext;
import engine.validator.InstructionValidator;

import java.util.ArrayList;
import java.util.List;

public class AssignmentInstruction extends SInstruction {
    private String assignedVariable;
    private final String argName = "assignedVariable";

    public AssignmentInstruction(SInstruction base) {
        super(base);
        this.setArgumentVariable(getArgument(argName));
        this.setCycles(4);
        this.setType("synthetic");
        this.setDegree(2);
    }

    public AssignmentInstruction(String variable, String label, String assignedVariable) {
        super();
        this.setInstructionName(InstructionName.ASSIGNMENT);
        this.setSVariable(variable);
        this.setSLabel(label);
        this.setArgumentVariable(assignedVariable);
        this.setCycles(4);
        this.setType("synthetic");
        this.setDegree(2);
    }

    public void setArgumentVariable(String variable) {
        this.assignedVariable = variable.trim().toLowerCase();
    }

    @Override
    public String getArgumentVariable() {
        return assignedVariable;
    }

    @Override
    protected String getOperationString(String variable) {
        return String.format("%s <- %s", variable, getArgumentVariable());
    }

    public String getArgumentVariableName(){
        return argName;
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }

    @Override
    public List<SInstruction> expand(ExpansionContext context, int line){
        List<SInstruction> expanded = new ArrayList<>();

        String z1 = context.freshVar();
        String L1 = context.freshLabel();
        String L2 = context.freshLabel();
        String L3 = context.freshLabel();
        String v = this.getSVariable();
        String v_tag = this.getArgumentVariable();

        expanded.add(new ZeroVariableInstruction(v, this.getSLabel()));
        expanded.add(new JumpNotZeroInstruction(v_tag, "", L1));
        expanded.add(new GotoLabelInstruction("", L3));
        expanded.add(new DecreaseInstruction(v_tag, L1));
        expanded.add(new IncreaseInstruction(z1, ""));
        expanded.add(new JumpNotZeroInstruction(v_tag, "", L1));
        expanded.add(new DecreaseInstruction(z1, L2));
        expanded.add(new IncreaseInstruction(v,""));
        expanded.add(new IncreaseInstruction(v_tag, ""));
        expanded.add(new JumpNotZeroInstruction(z1, "", L2));
        expanded.add(new NeutralInstruction(v, L3));

        for(SInstruction instr : expanded){
            instr.setParentLine(line);
            instr.setParent(this);
        }

        return expanded;
    }

    @Override
    public void execute(ExecutionContext context){
        String var = this.getSVariable();
        String argVar = this.getArgumentVariable();
        int value =  context.getVariables().computeIfAbsent(argVar, k -> 0);
        context.getVariables().put(var, value);
        context.increaseCycles(getCycles());
        context.increasePC(1);
    }

}
