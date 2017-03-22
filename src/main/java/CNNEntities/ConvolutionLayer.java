package CNNEntities;

import java.util.Vector;

public class ConvolutionLayer extends BaseLayer {
    public Vector<float[][]> kernels;
    public Vector<float[][]> kernelsDelta;
    public int[][] connectionsMap;
    public int stride;
    public int kernelSize;
    public int kernelAmount;
}
