package engine.instruction;

import engine.expander.ExpansionContext;
import engine.validator.InstructionValidator;

import java.util.ArrayList;
import java.util.List;

public class JumpEqualVariableInstruction extends SInstruction {
    private String variableName;
    private final String argVarName = "variableName";
    private String JEVariableLabel;
    private final String argLabelName = "JEVariableLabel";

    public JumpEqualVariableInstruction(SInstruction base) {
        super(base);
        this.setCycles(2);
        this.setDegree(3);
        this.setArgumentVariable(getArgument(argVarName));
        this.setArgumentLabel(getArgument(argLabelName));

    }

    public JumpEqualVariableInstruction( String variable,
                                        String label, String variableName, String JEVariableLabel) {
        super();
        this.setInstructionName(InstructionName.JUMP_EQUAL_VARIABLE);
        this.setSVariable(variable);
        this.setSLabel(label);
        this.setCycles(2);
        this.setDegree(3);
        this.setArgumentVariable(variableName.trim());
        this.setArgumentLabel(JEVariableLabel.trim());
    }

    public void setArgumentVariable(String variable) {
        this.variableName = variable.trim().toLowerCase();
    }

    @Override
    public String getArgumentVariable() {
        return variableName;
    }

    public void setArgumentLabel(String label) {
        this.JEVariableLabel = label.trim().toUpperCase();
    }

    @Override
    public String getArgumentLabel() {
        return JEVariableLabel;
    }

    @Override
    protected String getOperationString(String variable) {
        return String.format("IF %s = %s GOTO %s", variable, getArgumentVariable(), getArgumentLabel());
    }

    public String getArgumentVariableName(){
        return argVarName;
    }

    public String getArgumentLabelName(){
        return argLabelName;
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }

    @Override
    public List<SInstruction> expand(ExpansionContext context, int line){
        List<SInstruction> expanded =  new ArrayList<SInstruction>();
        String V = this.getSVariable();
        String V_tag = this.getArgumentVariable();
        String z1 = context.freshVar();
        String z2 = context.freshVar();
        String L = this.getArgumentLabel();
        String L1 = context.freshLabel();
        String L2 = context.freshLabel();
        String L3 = context.freshLabel();

        expanded.add(new AssignmentInstruction(z1, this.getSLabel(), V));
        expanded.add(new AssignmentInstruction(z2, "", V_tag));
        expanded.add(new JumpZeroInstruction(z1, L2, L3));
        expanded.add(new JumpZeroInstruction(z2, "", L1));
        expanded.add(new DecreaseInstruction(z1, ""));
        expanded.add(new DecreaseInstruction(z2, ""));
        expanded.add(new GotoLabelInstruction("", L2));
        expanded.add(new JumpZeroInstruction(z2, L3, L));
        expanded.add(new NeutralInstruction("y", L1));

        for(SInstruction instr : expanded){
            instr.setParentLine(line);
            instr.setParent(this);
        }

        return expanded;
    }

}
