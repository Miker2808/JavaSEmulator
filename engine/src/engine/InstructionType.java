package engine;

import jakarta.xml.bind.annotation.XmlEnumValue;

public enum InstructionType {
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
    @XmlEnumValue("JUMP_EQUAL_FUNCTION") JUMP_EQUAL_FUNCTION("JUMP_EQUAL_FUNCTION");

    private final String text;

    InstructionType(String text) {
        this.text = text;
    }

    public String toString(){
        return text;
    }
}
