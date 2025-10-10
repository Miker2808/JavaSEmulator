package DTOConverter;

import Storage.UserInstance;
import dto.UserStatDTO;

public class UserStatDTOConverter {

    public static UserStatDTO toDTO(String username, UserInstance userInstance) {
        UserStatDTO dto = new UserStatDTO();
        dto.username = username;
        dto.avail_credits = userInstance.getCreditsAvailable();
        dto.credits_spent = userInstance.getCreditsUsed();
        dto.num_runs = userInstance.getTotalRuns();
        dto.num_uploaded_programs = userInstance.getNumProgramsUploaded();
        dto.num_uploaded_functions = userInstance.getNumFunctionsUploaded();
        return dto;
    }
}
