package engine.validator;

import java.util.ArrayList;
import java.util.List;

public class FunctionArgumentsValidator {
    private List<String> availableFunctions;

    FunctionArgumentsValidator(List<String> availableFunctions){
        this.availableFunctions = availableFunctions;
    }

    public static boolean isValidVariable(String str) {
        return str.matches("^(y|[xz][1-9][0-9]*)$");
    }

    public static boolean enclosedInParenthesis(String str){
        return str.matches("^\\(.+\\)$");
    }

    public static boolean functionNoArgs(String str){
        return str.matches("^\\([A-Za-z0-9_]+\\)$");
    }

    public boolean isFunctionAvailable(String functionName){
        for(String availableFunction : availableFunctions){
            if(availableFunction.equals(functionName)){
                return true;
            }
        }
        return false;
    }

    public void validateArguments(String str) throws InvalidFunctionException{
        if(str.isEmpty()){
            return;
        }

        List<String> argsList = splitTopLevel(str);

        for(String arg : argsList){
            arg = arg.trim();
            // if of type "(Func1)"
            if(functionNoArgs(arg)){
                String func = arg.substring(1, arg.length()-1);
                if(!isFunctionAvailable(func)){
                    throw new InvalidFunctionException(String.format("Function '%s' does not exist", func));
                }
            }
            // if of type "(Func1,x1,x2)"
            else if(enclosedInParenthesis(arg)){
                String func = getFunctionName(arg);
                if(!isFunctionAvailable(func)){
                    throw new InvalidFunctionException(String.format("Function '%s' does not exist", func));
                }

                String sub_args = getArguments(arg);
                validateArguments(sub_args);
            }
            else{
                if(isFunctionAvailable(arg)){
                    // by task requirements, its treated as a basic invalid format, the message is extra.
                    throw new InvalidFunctionException(String.format("Function %s with no arguments must be enclosed in parenthesis: (%s)", arg, arg));
                }
                else if(!isValidVariable(arg)) {
                    throw new InvalidFunctionException(String.format("Invalid argument format: %s", arg));
                }

            }

        }
    }

    public static String getFunctionName(String input) {
        if(functionNoArgs(input)){
            return input.substring(1, input.length()-1).trim();
        }

        int openParen = input.indexOf(',');
        return input.substring(1, openParen).trim();
    }

    public static String getArguments(String input) {
        int openParen = input.indexOf(',');
        int closeParen = input.lastIndexOf(')');
        return input.substring(openParen + 1, closeParen).trim();
    }

    public static List<String> splitTopLevel(String input) {
        List<String> result = new ArrayList<>();
        int parenCount = 0;
        int lastSplit = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '(') {
                parenCount++;
            } else if (c == ')') {
                parenCount--;
            } else if (c == ',' && parenCount == 0) {
                // top-level comma, split here
                result.add(input.substring(lastSplit, i).trim());
                lastSplit = i + 1;
            }
        }

        // add last segment
        result.add(input.substring(lastSplit).trim());
        return result;
    }

    /*
    public static void main(String[] args) {
        List<String> validFunctions = new ArrayList<>(List.of("Func1", "Func2", "Func3", "Func4"));
        String input = "(Func1,z1,x2),(Func2,(Func3,x3,y,(Func4,z2))),z5,(Func2)";


        FunctionArgumentsValidator funcValidator = new FunctionArgumentsValidator(validFunctions);
        try {
            funcValidator.validateArguments(input);
            System.out.println("All good");
        }catch (InvalidFunctionException e){
            System.out.println(e.getMessage());
        }
    }

     */
}
