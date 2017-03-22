package CNNEntities;

public class Index implements Comparable<Index>{
    public int i;
    public int j;
    public int newI;
    public int newJ;
    @Override
    public int compareTo(Index i2){
        if (this.i == i2.i && this.j == i2.j)
            return 0;
        if (this.i > i2.i || (this.i == i2.i && this.j > i2.j)) {
            return 1;
        }
        return -1;
    }
}
