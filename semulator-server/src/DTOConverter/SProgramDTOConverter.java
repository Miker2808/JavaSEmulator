package DTOConverter;


import dto.SProgramViewDTO;
import dto.SProgramViewStatsDTO;
import engine.SInstructionsView;
import engine.SProgramView;
import engine.instruction.SInstruction;


public class SProgramDTOConverter {

    // TODO: this is incomplete, and just a barebone, used on execute screen
    public static SProgramViewDTO toExecuteDTO(SProgramView program){
        SProgramViewDTO dto = new SProgramViewDTO();

        SInstructionsView instructionsView = program.getInstructionsView();
        for(SInstruction instr : instructionsView.getAllInstructions()){
            dto.sInstructionsDTOs.add(SInstructionDTOConverter.convertToDTO(instr));
        }

        dto.name = program.getName();
        dto.userstring = program.getUserString();
        dto.type = (program.getProgramType() == SProgramView.ProgramType.FUNCTION) ? "FUNCTION" : "PROGRAM";
        dto.maxDegree = program.getInstructionsView().getMaxDegree();
        dto.uploader = program.getUploader();
        dto.average_credits_cost = program.getAverage_credits_cost();
        dto.numRuns = program.getNumRuns();

        return dto;
    }


    // used to show stats in dashboard
    public static SProgramViewStatsDTO toStatsDTO(SProgramView program){
        SProgramViewStatsDTO dto = new SProgramViewStatsDTO();
        dto.name = program.getName();
        dto.userstring = program.getUserString();
        dto.type = (program.getProgramType() == SProgramView.ProgramType.FUNCTION) ? "FUNCTION" : "PROGRAM";
        dto.maxDegree = program.getInstructionsView().getMaxDegree();
        dto.uploader = program.getUploader();
        dto.average_credits_cost = program.getAverage_credits_cost();
        dto.numRuns = program.getNumRuns();
        dto.num_instructions = program.getInstructionsView().size();
        dto.parentProgram = program.getParentProgram();
        return dto;
    }




}
