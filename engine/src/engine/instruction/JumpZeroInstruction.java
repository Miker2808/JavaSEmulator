package engine.instruction;

import engine.execution.ExecutionContext;
import engine.expander.ExpansionContext;
import engine.validator.InstructionValidator;

import java.util.ArrayList;
import java.util.List;

public class JumpZeroInstruction extends SInstruction {
    private String JZLabel;
    private final String argName = "JZLabel";

    public JumpZeroInstruction(SInstruction base) {
        super(base);
        this.setArgumentLabel(getArgument("JZLabel"));
        this.setCycles(2);
        this.setType("synthetic");
        this.setDegree(2);
    }

    public JumpZeroInstruction(String variable, String label, String JZLabel) {
        super();
        this.setInstructionName(InstructionName.JUMP_ZERO);
        this.setSVariable(variable);
        this.setSLabel(label);
        this.setArgumentLabel(JZLabel);
        this.setCycles(1);
        this.setType("synthetic");
        this.setDegree(2);
    }

    public void setArgumentLabel(String label) {
        this.JZLabel = label.trim().toUpperCase();
    }

    @Override
    public String getArgumentLabel() {
        return JZLabel;
    }

    @Override
    protected String getOperationString(String variable) {
        return String.format("IF %s = 0 GOTO %s", variable, getArgumentLabel());
    }

    public String getArgumentLabelName(){
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
        String L1 = context.freshLabel();
        String L = this.getArgumentLabel();

        expanded.add(new JumpNotZeroInstruction(V, this.getSLabel(), L1));
        expanded.add(new GotoLabelInstruction("", L));
        expanded.add(new NeutralInstruction("y", L1));

        for(SInstruction instr : expanded){
            instr.setParentLine(line);
            instr.setParent(this);
        }

        return expanded;
    }

    @Override
    public void execute(ExecutionContext context){
        String var = this.getSVariable();
        String argLabel = this.getArgumentLabel();
        int value = context.getVariables().computeIfAbsent(var, k -> 0);

        if(value == 0){
            if(argLabel.equals("EXIT")){
                context.setExit(true);
            }
            else{
                context.setPC(context.getLabelLine(argLabel));
            }
        }
        else{
            context.increasePC(1);
        }

        context.increaseCycles(getCycles());

    }

}
