package CNNEntities;

import java.util.Vector;

public class FullyConnectedLayer extends BaseLayer {
    public Vector<Vector<Double>> weights;
    public Vector<Vector<Double>> weightsDelta;
    public int in;
    public int out;
}
