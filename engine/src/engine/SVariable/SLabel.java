package engine.SVariable;

public class SLabel {
    private int number = 0;
    private char type = 0;

    public SLabel(String label){
        if(!label.isBlank()) {
            type = label.charAt(0);
            if(type != 'E') {
                number = Integer.parseInt(label.substring(1));
            }
        }
    }

    public SLabel(){
        type = 0;
        number = 0;
    }

    public SLabel(SLabel label){
        type = label.type;
        number = label.number;
    }

    public int getNumber(){
        return number;
    }

    public char getType(){
        return type;
    }

    public String toString(){
        if(type == 'L') {
            return String.format("%c%d", type, number);
        }
        else if(type == 'E') {
            return "EXIT";
        }
        else{
            return "";
        }
    }

    public boolean isExit(){
        return type == 'E';
    }
}
