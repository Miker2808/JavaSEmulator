package engine;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;



public class XMLValidator
{
    public static class InvalidXMLException extends Exception {
        public InvalidXMLException(String message) {
            super(message);
        }
    }

    /**
     * Validates that the given string is a valid path to an existing XML file.
     * @param filePathStr String path to check
     * @throws InvalidXMLException if validation fails
     */
    public static void validateXMLFile(String filePathStr) throws InvalidXMLException {
        Path path;

        String file_error_str = "Failed to load file: ";

        // 1. Check path format validity
        try {
            path = Paths.get(filePathStr);
        } catch (InvalidPathException e) {
            throw new InvalidXMLException(file_error_str + "Invalid path format" );
        }

        // 2. Check that file exists and is a file
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            throw new InvalidXMLException(file_error_str + "Path does not point to an existing file");
        }

        // 3. Check file extension
        String fileName = path.getFileName().toString().toLowerCase();
        if (!fileName.endsWith(".xml")) {
            throw new InvalidXMLException(file_error_str + "File is not an XML file (ends with .xml)");
        }
    }


}
