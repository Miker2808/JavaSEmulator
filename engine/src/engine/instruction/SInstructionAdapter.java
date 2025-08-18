package engine.instruction;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class SInstructionAdapter extends XmlAdapter<SInstruction, SInstruction> {
    @Override
    public SInstruction unmarshal(SInstruction v) throws Exception {
        if (v == null) return null;

        InstructionName name = v.getInstructionName();
        if (name == null) name = InstructionName.UNSUPPORTED;

        switch (name) {
            case INCREASE: return new IncreaseInstruction(v);
            case DECREASE: return new DecreaseInstruction(v);
            case JUMP_NOT_ZERO: return new JumpNotZeroInstruction(v);
            case NEUTRAL: return new NeutralInstruction(v);
            case ZERO_VARIABLE: return new ZeroVariableInstruction(v);
            case GOTO_LABEL: return new GotoLabelInstruction(v);
            case ASSIGNMENT: return new AssignmentInstruction(v);
            case CONSTANT_ASSIGNMENT: return new ConstantAssignmentInstruction(v);
            case JUMP_ZERO: return new JumpZeroInstruction(v);
            case JUMP_EQUAL_CONSTANT: return new JumpEqualConstantInstruction(v);
            case JUMP_EQUAL_VARIABLE: return new JumpEqualVariableInstruction(v);
            case QUOTE: return new QuoteInstruction(v);
            case JUMP_EQUAL_FUNCTION: return new JumpEqualFunctionInstruction(v);
            case UNSUPPORTED:
            default:
                return new UnsupportedInstruction(v);
        }
    }

    @Override
    public SInstruction marshal(SInstruction v) throws Exception {
        return v; // Usually, just return the same object for marshalling
    }
}
