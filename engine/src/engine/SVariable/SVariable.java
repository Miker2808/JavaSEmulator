package engine.SVariable;

public class SVariable {
    private int number = 0;
    private char type = 0;

    public SVariable(String variable){
        if(!variable.isBlank()) {
            type = variable.charAt(0);
            if(type != 'y') {
                number = Integer.parseInt(variable.substring(1));
            }
        }
    }

    public SVariable(SVariable variable){
        type = variable.type;
        number = variable.number;
    }

    public int getNumber(){
        return number;
    }

    public char getType(){
        return type;
    }

    public String toString(){
        if(type == 'y'){
            return "y";
        }
        return String.format("%c%d",type, number);
    }
}
