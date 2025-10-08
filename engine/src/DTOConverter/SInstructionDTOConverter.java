package DTOConverter;

import engine.instruction.SInstruction;
import dto.SInstructionDTO;

public class SInstructionDTOConverter {

    public static SInstructionDTO convertToDTO(SInstruction instruction){
        SInstructionDTO dto = new SInstructionDTO();
        dto.name = instruction.getName();
        dto.instructionString = instruction.getInstructionString();
        dto.degree = instruction.getDegree();
        dto.cycles = instruction.getCycles();
        dto.cyclesStr = instruction.getCyclesStr();
        dto.line = instruction.getLine();
        dto.credits = instruction.getCredits();
        dto.generation = instruction.getGenStr();
        dto.typeShort = instruction.getTypeShort();
        dto.sLabel = instruction.getSLabel();
        dto.sVariable = instruction.getSVariable();
        return dto;
    }

}
