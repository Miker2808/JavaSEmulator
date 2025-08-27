package engine.expander;

public class ExpansionContext {
    private int nextVar;
    private int nextLabel;

    // tracks usage of free variables and labels
    public ExpansionContext(int maxVar, int maxLabel) {
        this.nextVar = maxVar + 1;
        this.nextLabel = maxLabel + 1;
    }

    public String freshVar() { return "z" + (nextVar++); }
    public String freshLabel() { return "L" + (nextLabel++); }
}
