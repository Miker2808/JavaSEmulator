package engine.instruction;

import engine.SVariable.SVariable;
import engine.execution.ExecutionContext;
import engine.expander.ExpansionContext;
import engine.validator.InstructionValidator;

import java.util.ArrayList;
import java.util.List;

public class JumpEqualFunctionInstruction extends QuoteInstruction {
    protected final String argumentLabelName = "JEFunctionLabel";
    protected String JEFunctionLabel;

    public JumpEqualFunctionInstruction(SInstruction base) {
        super(base);
        setType("synthetic");
        setInstructionName(InstructionName.JUMP_EQUAL_FUNCTION);
        setArgumentLabel(getArgument(argumentLabelName));
        setFunctionArguments(getArgument(argFunctionArgumentsName));
        setFunctionName(getArgument(argFunctionName));
    }

    JumpEqualFunctionInstruction(JumpEqualFunctionInstruction other) {
        super(other);
        setInstructionName(getInstructionName());
        setType(other.getType());
        setCycles(other.getCycles());
        setDegree(other.getDegree());
        setFunctionName(other.getFunctionName());
        setFunctionArguments(other.getFunctionArguments());
        setArgumentLabel(other.getArgumentLabel());

    }

    @Override
    public JumpEqualFunctionInstruction copy() {
        return new JumpEqualFunctionInstruction(this);
    }

    @Override
    public void validate(InstructionValidator validator) throws InvalidInstructionException {
        validator.validate(this);
    }


    @Override
    public String getInstructionString() {
        if(getFunctionArguments().isBlank()){
            return String.format("IF %s <- (%s) GOTO %s", getSVariable(), getFunctionName(), getArgumentLabel());
        }
        return String.format("IF %s = (%s,%s) GOTO %s", getSVariable(), getFunctionName(), getFunctionArguments(), getArgumentLabel());
    }

    @Override
    public void setArgumentLabel(String label) {
        this.JEFunctionLabel = label.trim().toUpperCase();
    }

    @Override
    public String getArgumentLabel() {
        return JEFunctionLabel;
    }

    @Override
    public int getCycles(){
        return 6;
    }

    @Override
    public List<SInstruction> expand(ExpansionContext context, int line) {
        List<SInstruction> expanded = new ArrayList<>();
        String V = this.getSVariable();
        String z1 = context.freshVar();
        String L = this.getArgumentLabel();

        expanded.add(new QuoteInstruction(z1, getSLabel(), getFunctionName(), getFunctionArguments()));
        expanded.add(new JumpEqualVariableInstruction(V, "", z1, L));

        for(SInstruction instr : expanded){
            instr.setParentLine(line);
            instr.setParent(this);
        }

        return expanded;
    }

    @Override
    public void execute(ExecutionContext context){
        ExecutionContext result = runFunction(getFunctionName(), getFunctionArguments(), context);

        //String var = this.getSVariable();
        //int varValue = context.getVariables().computeIfAbsent(var, k -> 0);
        //int value = result.getVariables().get("y");

        SVariable var = this.getSVariableS();
        int varValue = context.getVariableValue(var);
        int value = result.getVariableValue(new SVariable("y"));

        String argLabel = this.getArgumentLabel();

        if(varValue == value){
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

        context.increaseCycles(result.getCycles() + getCycles());

    }





}
