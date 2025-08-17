package engine;

import jakarta.xml.bind.annotation.XmlEnumValue;

public enum InstructionName {
    @XmlEnumValue("INCREASE") INCREASE("INCREASE"),
    @XmlEnumValue("DECREASE") DECREASE("DECREASE"),
    @XmlEnumValue("JUMP_NOT_ZERO") JUMP_NOT_ZERO("JUMP_NOT_ZERO"),
    @XmlEnumValue("NEUTRAL") NEUTRAL("NEUTRAL"),
    @XmlEnumValue("ZERO_VARIABLE") ZERO_VARIABLE("ZERO_VARIABLE"),
    @XmlEnumValue("GOTO_LABEL") GOTO_LABEL("GOTO_LABEL"),
    @XmlEnumValue("ASSIGNMENT") ASSIGNMENT("ASSIGNMENT"),
    @XmlEnumValue("CONSTANT_ASSIGNMENT") CONSTANT_ASSIGNMENT("CONSTANT_ASSIGNMENT"),
    @XmlEnumValue("JUMP_ZERO") JUMP_ZERO("JUMP_ZERO"),
    @XmlEnumValue("JUMP_EQUAL_CONSTANT") JUMP_EQUAL_CONSTANT("JUMP_EQUAL_CONSTANT"),
    @XmlEnumValue("JUMP_EQUAL_VARIABLE") JUMP_EQUAL_VARIABLE("JUMP_EQUAL_VARIABLE"),
    @XmlEnumValue("QUOTE") QUOTE("QUOTE"),
    @XmlEnumValue("JUMP_EQUAL_FUNCTION") JUMP_EQUAL_FUNCTION("JUMP_EQUAL_FUNCTION"),
    UNSUPPORTED("UNSUPPORTED");

    private final String text;


    InstructionName(String text) {
        this.text = text;
    }

    public String toString(){
        return text;
    }

    public static InstructionName fromString(String name) {
        for (InstructionName instr : InstructionName.values()) {
            if (instr.name().equalsIgnoreCase(name)) { // case-insensitive match
                return instr;
            }
        }
        return UNSUPPORTED; // or return a default value like ADD
    }
}
