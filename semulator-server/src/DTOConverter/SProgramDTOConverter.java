package DTOConverter;


import dto.SProgramViewDTO;
import engine.SInstructionsView;
import engine.SProgramView;
import engine.instruction.SInstruction;


public class SProgramDTOConverter {

    public static SProgramViewDTO toDTO(SProgramView program){
        SProgramViewDTO dto = new SProgramViewDTO();

        SInstructionsView instructionsView = program.getInstructionsView();
        for(SInstruction instr : instructionsView.getAllInstructions()){
            dto.sInstructionsDTOs.add(SInstructionDTOConverter.convertToDTO(instr));
        }
        dto.name = program.getName();
        dto.userstring = program.getUserString();
        dto.type = (program.getProgramType() == SProgramView.ProgramType.FUNCTION) ? "FUNCTION" : "PROGRAM";
        return dto;
    }


}
