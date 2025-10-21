package DTOConverter;


import dto.SProgramViewStatsDTO;
import engine.SProgramView;


public class SProgramDTOConverter {


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
