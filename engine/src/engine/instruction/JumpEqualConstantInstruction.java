package engine.instruction;

import engine.execution.ExecutionContext;
import engine.expander.ExpansionContext;
import engine.validator.InstructionValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JumpEqualConstantInstruction extends SInstruction {
    private String constantValue;
    private final String argConstName = "constantValue";
    private String JEConstantLabel;
    private final String argLabelName = "JEConstantLabel";

    public JumpEqualConstantInstruction(SInstruction base) {
        super(base);
        this.setCycles(2);
        this.setDegree(3);
        this.setArgumentConst(getArgument(argConstName));
        this.setArgumentLabel(getArgument(argLabelName));
    }

    public JumpEqualConstantInstruction( String variable, String label, String JEConstantLabel, String constantValue) {
        super();
        this.setInstructionName(InstructionName.JUMP_EQUAL_CONSTANT);
        this.setSVariable(variable);
        this.setSLabel(label);
        this.setCycles(2);
        this.setDegree(3);
        this.setArgumentConst(constantValue.trim());
        this.setArgumentLabel(JEConstantLabel.trim());
    }

    public void setArgumentConst(String value) {
        this.constantValue = value.trim();
    }

    @Override
    public String getArgumentConst() {
        return constantValue;
    }

    public void setArgumentLabel(String label) {
        this.JEConstantLabel = label.trim().toUpperCase();
    }

    @Override
    public String getArgumentLabel() {
        return JEConstantLabel;
    }

    @Override
    protected String getOperationString(String variable) {
        return String.format("IF %s = %s GOTO %s", variable,
                getArgumentConst(), getArgumentLabel());
    }

    public String getArgumentConstName(){
        return argConstName;
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
        List<SInstruction> expanded = new ArrayList<>();
        String V = this.getSVariable();
        String z1 = context.freshVar();
        String L1 = context.freshLabel();
        String L = this.getArgumentLabel();
        int K = Integer.parseInt(this.getArgumentConst()); // if exception, check validator

        expanded.add(new AssignmentInstruction(z1, this.getSLabel(), V));
        for(int i =0; i < K; i++){
            expanded.add(new JumpZeroInstruction(z1, "", L1));
            expanded.add(new DecreaseInstruction(z1, ""));
        }

        expanded.add(new JumpNotZeroInstruction(z1, "", L1));
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
        int constValue = Integer.parseInt(getArgumentConst());
        int value = context.getVariables().computeIfAbsent(var, k -> 0);

        if(value == constValue){
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
