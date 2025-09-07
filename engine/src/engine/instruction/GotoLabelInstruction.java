package engine.instruction;

import engine.execution.ExecutionContext;
import engine.expander.ExpansionContext;
import engine.validator.InstructionValidator;

import java.util.ArrayList;
import java.util.List;

public class GotoLabelInstruction extends SInstruction {
    private String gotoLabel;
    private final String argName = "gotoLabel";

    public GotoLabelInstruction(SInstruction base) {
        super(base);

        this.setCycles(1);
        this.setType("synthetic");
        this.setDegree(1);
        this.setArgumentLabel(getArgument(argName));
    }

    public GotoLabelInstruction( String label, String gotoLabel) {
        super();
        this.setInstructionName(InstructionName.GOTO_LABEL);
        this.setSVariable("");
        this.setSLabel(label);
        this.setArgumentLabel(gotoLabel);
        this.setCycles(1);
        this.setType("synthetic");
        this.setDegree(1);
    }

    @Override
    public String getInstructionString() {
        return String.format("GOTO %s", getArgumentLabel());
    }

    public void setArgumentLabel(String label) {
        this.gotoLabel = label.trim().toUpperCase();
    }

    @Override
    public String getArgumentLabel(){
        return gotoLabel;
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
        String var = context.freshVar();
        String label = this.getArgumentLabel();

        expanded.add(new IncreaseInstruction(var, this.getSLabel()));
        expanded.add(new JumpNotZeroInstruction(var, "", label));

        for(SInstruction instr : expanded){
            instr.setParentLine(line);
            instr.setParent(this);
        }

        return expanded;
    }

    @Override
    public void execute(ExecutionContext context){
        String argLabel = this.getArgumentLabel();

        if(argLabel.equals("EXIT")){
            context.setExit(true);
        }
        else{
            context.setPC(context.getLabelLine(argLabel));
        }

        context.increaseCycles(getCycles());
    }

}
