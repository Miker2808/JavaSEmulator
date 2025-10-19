package ui.elements;

public class UtilityConverters {

    public static int romanToIntegerGen(String genStr){
        return switch (genStr) {
            case "I" -> 1;
            case "II" -> 2;
            case "III" -> 3;
            case "IV" -> 4;
            default -> throw new IllegalArgumentException("Only I to IV are supported");
        };
    }
}
