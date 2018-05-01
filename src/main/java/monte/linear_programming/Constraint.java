package monte.linear_programming;

public class Constraint {
    public String leftSide;
    public String rightSide;
    public boolean gt;

    public Constraint(String leftSide, String rightSide, boolean gt) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
        this.gt = gt;
    }

    public Constraint() {
    }

    @Override
    public String toString() {
        if( gt ){
            return leftSide + " \\geq " + rightSide;
        }
        return leftSide + " \\leq " + rightSide;
    }
}
