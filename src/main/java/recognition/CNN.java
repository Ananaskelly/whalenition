package recognition;

import CNNEntities.*;
import org.jblas.FloatMatrix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collections;
import java.util.Vector;

/**
 *
 * Cnn (maybe)
 */
public class CNN {

    /* layer types */
    static public final String CONV = "convolution";
    static public final String SUBS = "subsampling";
    static public final String FC = "fullyconnected";

    /* activation functions */
    static public final String TANH = "tanh";
    static public final String SIGMOID = "sigmoid";
    static public final String RELU = "relu";
    static public final String SOFTMAX = "softmax";
    static public final String LEAKYRELU = "LeakyReLU";
    static public final String SOFTPLUS = "softplus";

    /* layers */
    public Vector<Layer> layers;

    public Vector<LayerDelta> layersDelta;

    /* features */
    public Vector<Vector<float[][]>> features;

    /* learning rate */
    private float learningRate;

    /* regularization */
    private boolean regularizationMode = false;

    /* regularization coefficient*/
    private double regularizationParam = 0;

    /* activate momentum */
    private boolean MOMENTUM_MODE = false;
    private double momentum = 0.1;

    /* batch settings */
    private boolean BATCH_MODE = false;
    private int batchSize = 10;
    private int currentIndex = 0;

    /* bias settings */
    private boolean BIAS_MODE = false;
    public Vector<double[]> bias;
    public float[] biasVal;
    private float[] biasGrad;
    private float[] input;

    /* strange staff */
    int convCounter = 0;
    String path;

    /**
     *
     * Constructor
     */
    public CNN(){
        layers = new Vector<>();
    }

    public void createModel(String path){
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line = br.readLine();
            while (line != null){
                String[] buffer;
                Layer newLayer = new Layer();
                switch (line){
                    case CONV:
                        ConvolutionLayer clayer = new ConvolutionLayer();
                        line = br.readLine();
                        buffer = line.split(" ");
                        clayer.kernelAmount = Integer.parseInt(buffer[0]);
                        clayer.kernelSize = Integer.parseInt(buffer[1]);
                        clayer.stride = Integer.parseInt(buffer[2]);
                        clayer.activation = buffer[3];
                        clayer.kernels = new Vector<>();
                        for (int i = 0; i < clayer.kernelAmount; i++){
                            float[][] kernel = new float[clayer.kernelSize][clayer.kernelSize];
                            for (int j = 0; j < clayer.kernelSize; j ++){
                                line = br.readLine();
                                buffer = line.split(" ");
                                for (int k = 0; k < clayer.kernelSize; k++){
                                    kernel[j][k] = Float.parseFloat(buffer[k]);
                                }
                            }
                            line = br.readLine();
                            clayer.kernels.add(kernel);
                        }
                        try {
                            BufferedReader br2 = new BufferedReader(new FileReader(System.getProperty("user.dir") + this.path +
                                    convCounter + ".txt"));
                            String line2 = br2.readLine();
                            String[] parsed = line2.split(" ");
                            int height = Integer.parseInt(parsed[0]);
                            int width = Integer.parseInt(parsed[1]);
                            line2 = br2.readLine();

                            int[][] map = new int[height][width];
                            int c = 0;
                            while (line2 != null){
                                parsed = line2.split(" ");
                                for (int i = 0; i < width; i++){
                                    map[c][i] = Integer.parseInt(parsed[i]);
                                }
                                c ++;
                                line2 = br2.readLine();
                            }
                            br2.close();
                            clayer.connectionsMap = map;
                            convCounter++;
                        } catch (Exception exp){
                            System.err.print(exp);
                        }
                        newLayer.type = CONV;
                        newLayer.layerInfo = clayer;
                        break;
                    case SUBS:
                        SubsamplingLayer slayer = new SubsamplingLayer();
                        line = br.readLine();
                        buffer = line.split(" ");
                        slayer.size = Integer.parseInt(buffer[0]);
                        newLayer.type = SUBS;
                        newLayer.layerInfo = slayer;
                        break;
                    case FC:
                        FullyConnectedLayer fclayer = new FullyConnectedLayer();
                        line = br.readLine();
                        buffer = line.split(" ");
                        fclayer.in = Integer.parseInt(buffer[0]);
                        fclayer.out = Integer.parseInt(buffer[1]);
                        fclayer.activation = buffer[2];
                        fclayer.weights = new Vector<>();
                        for (int i = 0; i < fclayer.in; i++){
                            Vector<Double> neuron = new Vector<>();
                            line = br.readLine();
                            if (i == 180 ){
                                System.out.println();
                            }
                            buffer = line.split(" ");
                            for (int j = 0; j < fclayer.out; j++){
                                neuron.add(Double.parseDouble(buffer[j]));
                            }
                            fclayer.weights.add(neuron);
                        }
                        newLayer.type = FC;
                        newLayer.layerInfo = fclayer;
                }
                layers.add(newLayer);
                line = br.readLine();
            }
        } catch (Exception exp){
            System.err.print(exp);
        }
    }

    /**
     * Initialize features vector
     *
     * @param input input for neural network
     */
    public void setInitialFeature(float[][] input){
        features = new Vector<>();
        Vector<float[][]> initial = new Vector<>();
        initial.add(input);
        features.add(initial);
    }

    public void feedforward(){
        for (int i = 0; i < layers.size(); i++){
            switch (layers.get(i).type){
                case CONV:
                    convolution((ConvolutionLayer)layers.get(i).layerInfo, i);
                    break;
                case SUBS:
                    subsampling((SubsamplingLayer)layers.get(i).layerInfo, i);
                    break;
                case FC:
                    fullyConnected((FullyConnectedLayer)layers.get(i).layerInfo, i);
                    break;

            }
        }
    }

    /**
     * Perform convolution feedforward case;
     *
     * @param layer current layer information
     * @param currentFeatureIndex index of current layer
     */
    private void convolution(ConvolutionLayer layer, int currentFeatureIndex) {
        int oldHeight = features.get(currentFeatureIndex).get(0).length;
        int oldWidth = features.get(currentFeatureIndex).get(0)[0].length;
        int nextFeatureIndex = currentFeatureIndex + 1;
        // for all kernels
       // AparapiValidConvolution conv2D;
        Vector<float[][]> nextLayerFeatures = new Vector<>();
        for (int i = 0; i < layer.kernels.size(); i++) {
            int newH = oldHeight - layer.kernels.get(i).length + 1;
            int newW = oldWidth - layer.kernels.get(i)[0].length + 1;
            float[][] feature = new float[newH][newW];
            FloatMatrix m1 = new FloatMatrix(feature);
            for (int j = 0; j < layer.connectionsMap.length; j++) {
                if (layer.connectionsMap[j][i] == 1) {

                    FloatMatrix m2 = new FloatMatrix(convolutionFeatureKernel(features.get(currentFeatureIndex).get(j),
                            rotate180(layer.kernels.get(i)), layer.stride));
                    m1 = m1.add(m2);
                }
            }
            feature = m1.toArray2();
            for (int k = 0; k < feature.length; k++){
                for (int q = 0; q < feature[0].length; q++){
                    switch (layer.activation) {
                        case SIGMOID:
                            feature[k][q] = (float)sigmoid(feature[k][q]);
                            break;
                        case TANH:
                            feature[k][q] = (float)tanh(feature[k][q]);
                            break;
                        case RELU:
                            feature[k][q] = (float)relu(feature[k][q]);
                            break;
                        case LEAKYRELU:
                            feature[k][q] = (float)leakyReLU(feature[k][q]);
                            break;
                        case SOFTPLUS:
                            feature[k][q] = (float)softPlus(feature[k][q]);
                    }
                }
            }
            nextLayerFeatures.add(feature);
        }
        features.add(nextFeatureIndex,nextLayerFeatures);
    }

    /**
     * Convolution between matrix and kernel
     *
     * @param input input matrix
     * @param kernel kernel
     * @param stride layer stride
     * @return
     */
    private float[][] convolutionFeatureKernel(float[][] input, float[][] kernel, int stride){
        int height = input.length - kernel.length + 1;
        int width = input[0].length - kernel[0].length + 1;

        int kernelHeight = kernel.length;
        int kernelWidth = kernel[0].length;

        float[][] result = new float[height][width];
        for (int i = 0; i < height; i += stride){
            for (int j = 0; j < width; j += stride){
                result[i][j] = Helper.convolution(Helper.getSubmatrix(input, i, j, kernelHeight, kernelWidth), kernel);
            }
        }

        return result;
    }

    /**
     * Perform subsampling
     *
     * @param layer layer information
     * @param currentFeatureIndex index of current layer
     */
    private void subsampling(SubsamplingLayer layer, int currentFeatureIndex){
        int step = layer.size;
        int nextFeatureIndex = currentFeatureIndex + 1;
        layer.indexies = new Vector<>();
        Vector<float[][]> nextLayerFeatures = new Vector<>();
        for (int i = 0; i < features.get(currentFeatureIndex).size(); i++){
            nextLayerFeatures.add(subsampleMatrix(features.get(currentFeatureIndex).get(i), step, layer));
        }
        features.add(nextFeatureIndex, nextLayerFeatures);
    }

    /**
     *
     * @param input input matrix
     * @param step step for subsample
     * @return {double[][]} resulted matrix
     */
    private float[][] subsampleMatrix(float[][] input, int step, SubsamplingLayer layer){
        int height = input.length / step;
        int width = input[0].length / step;
        float[][] result = new float[height][width];
        int c = 0;
        Vector<Index> pairs = new Vector<>();
        for (int i = 0; i < input.length; i += step){
            int c1 = 0;
            for (int j = 0; j < input[0].length; j += step){
                Winner currentWinner = Helper.getMax(Helper.getSubmatrix(input, i, j, step, step));
                result[c][c1] = currentWinner.value;
                Index ind = new Index();
                ind.i = currentWinner.indexI + i;
                ind.j = currentWinner.indexJ + j;
                ind.newI = c;
                ind.newJ = c1;
                pairs.add(ind);
                c1++;
            }
            c++;
        }
        Collections.sort(pairs);
        layer.indexies.add(pairs);
        return result;
    }

    /**
     *
     * @param layer layer information
     * @param currentFeatureIndex index of current layer
     */
    private void fullyConnected(FullyConnectedLayer layer, int currentFeatureIndex){
        int nextFeatureIndex = currentFeatureIndex + 1;
        Vector<float[][]> nextLayerFeature = new Vector<>();

        if (layer.activation.equals(SOFTMAX)){
            features.add(nextFeatureIndex, softMAX(layer.weights, features.get(currentFeatureIndex), layer.out));
            return;
        }
        for (int i = 0; i < layer.out; i++){
            float sum = 0;
            for (int j = 0; j < layer.in; j++){
                sum += features.get(currentFeatureIndex).get(j)[0][0]*layer.weights.get(j).get(i);
            }

            switch (layer.activation) {
                case SIGMOID:
                    sum = (float)sigmoid(sum);
                    break;
                case TANH:
                    sum = (float)tanh(sum);
                    break;
                case RELU:
                    sum = (float)relu(sum);
                    break;
                case LEAKYRELU:
                    sum = (float)leakyReLU(sum);
                    break;
                case SOFTPLUS:
                    sum = (float)softPlus(sum);
            }
            float[][] feature = new float[][]{{sum}};
            nextLayerFeature.add(feature);
        }
        features.add(nextFeatureIndex, nextLayerFeature);
    }


    private Vector<float[][]> softMAX(Vector<Vector<Double>> weights, Vector<float[][]> currentInput, int out){
        Vector<float[][]> output = new Vector<>();
        // for all neuron in layer
        float summator = 0;
        for (int i = 0; i < out; i++) {
            double sum = 0;
            for (int j = 0; j < currentInput.size(); j++){
                /* get j-neuron with i-weight */
                sum += currentInput.get(j)[0][0]*weights.get(j).get(i);
            }

            //output[i] = 1/((1 + Math.exp(-1*sum)));
            summator += Math.exp(sum);
            output.add(new float[][]{{(float)Math.exp(sum)}});
        }
        for (int i = 0; i < output.size(); i++){
            output.get(i)[0][0] /= summator;
        }

        return output;
    }

    private float[][] rotate180(float[][] input){
        float[][] result = new float[input.length][input[0].length];

        int c = 0;
        for (int i = input.length - 1; i >= 0; i --){
            int c1 = 0;
            for (int j = input[0].length - 1; j >= 0; j--){
                result[c][c1] = input[i][j];
                c1 ++;
            }
            c++;
        }

        return result;
    }

    private float[][] expandMatrix(float[][] input, int border){
        float[][] result = new float[input.length + border*2][input[0].length + border*2];
        for (int i = 0; i < result.length; i++){
            for (int j = 0; j < result[0].length; j++){
                if ( i >= border && i < (input.length + border) && j >= border && j < (input[0].length + border)){
                    result[i][j] = input[i - border][j - border];
                } else {
                    result[i][j] = 0;
                }
            }
        }
        return result;
    }


    public void setPath(String path){
        this.path = path;
    }

    /**
     * Perform sigmoid activation function
     *
     * @param value current neuron output
     * @return {double}
     */
    private double sigmoid(double value){
        return 1/(1 + Math.exp(-1*value));
    }

    /**
     * Perform tanh activation function
     *
     * @param value current neuron output
     * @return {double}
     */
    private double tanh(double value){
        return Math.tanh(value);
    }

    /**
     * Perform relu activation function
     *
     * @param value current neuron output
     * @return {double}
     */
    private double relu(double value) {
        return Math.max(0,value);
    }

    /**
     * Perform LeakyReLU activation function
     *
     * @param value current neuron output
     * @return {double}
     */
    private double leakyReLU(double value){
        return (value < 0) ? value*0.3 : value;
    }

    /**
     * Perform SoftPlus activation function
     *
     * @param value current neuron output
     * @return {double}
     */
    private double softPlus(double value){
        return Math.log(1 + Math.exp(value));
    }

    /**
     * Perform sigmoid derivative
     *
     * @param value point
     * @return {double}
     */
    private double sigmoid_pDer(double value){
        return value*(1 - value);
    }

    /**
     * Perform tanh derivative
     *
     * @param value point
     * @return {double}
     */
    private double tanh_pDer(double value){
        return (1 - Math.pow(value,2));
    }

    /**
     * Perform relu derivative
     *
     * @param value point
     * @return {double}
     */
    private double relu_pDer(double value) {
        return (value > 0) ? 1.0 : 0.0;
    }

    /**
     * Perform leakyReLU derivative
     *
     * @param value point
     * @return {double}
     */
    private double leakyReLU_pDer(double value) {
        return (value > 0) ? 1.0 : 0.3;
    }

    /**
     * Perform SoftPlus derivative
     *
     * @param value point
     * @return {double}
     */
    private double softPlus_pDer(double value) {
        return 1/(1 + Math.exp(-value));
    }

    /* SETTERS */

    public void setBIAS_MODE(boolean flag) {
        BIAS_MODE = flag;
    }

}