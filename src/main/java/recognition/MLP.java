package recognition;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class MLP {

    private static final String TANH = "tanh";
    private static final String SIGMOID = "sigmoid";

    /* weights */
    public static Vector<Vector<double []>> weights;

    /* output */
    public static Vector<double[]> layersOutput;

    /* AMOUNT OF LAYERS */
    private static int layersAmount;

    /* AMOUNT OF NEURON IN EVERY LAYER */
    private static int [] neuronLAmount;

    /* activation function */
    private static String activation = SIGMOID;
    /* bias settings */
    private static boolean BIAS_MODE = false;
    public static Vector<double[]> bias;
    public static double[] biasVal;

    public static void init(int value, int[] nerounsAmount){
        layersAmount = value;
        neuronLAmount = nerounsAmount;
    }

    public static void loadWeights(){
        weights = new Vector<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/output3.txt"));
            String line = br.readLine();
            while (line != null) {
                int currentLayer = 0;
                int amountOfWeights = neuronLAmount[currentLayer + 1];
                Vector<double[]> oneLayer = new Vector<>();
                while (!line.equals("end")){
                    double[] neuronWeights = new double[amountOfWeights];
                    String[] tokens = line.split(" ");
                    for (int i = 0; i < tokens.length; i++){
                        neuronWeights[i] = Double.parseDouble(tokens[i]);
                    }
                    oneLayer.add(neuronWeights);
                    line = br.readLine();
                }
                line = br.readLine();
                weights.add(oneLayer);
                currentLayer ++;
            }
            br.close();
            if (BIAS_MODE){
                br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/output2.txt"));
                line = br.readLine();
                if (line != null){
                    String[] tokens = line.split(" ");
                    for (int i = 0; i < layersAmount - 2; i++){
                        biasVal[i] = Double.parseDouble(tokens[i]);
                    }
                }
                line = br.readLine();
                int amount = neuronLAmount[1];
                while (line !=  null){
                    double[] layerB = new double[amount];
                    String[] tokens = line.split(" ");
                    for (int i = 0; i < amount; i++){
                        layerB[i] = Double.parseDouble(tokens[i]);
                    }
                    bias.add(layerB);
                    line = br.readLine();
                }
            }
        } catch (IOException exp){
            System.out.print(exp);
        }
    }
    /**
     *
     * @param initialInput input signal
     * @param weights current weights
     */
    public static void run(double[] initialInput, Vector<Vector<double[]>> weights){
        layersOutput = calculateLayersOutput(initialInput, weights);
    }
    /**
     *
     * @param initialInput input signal
     * @param w current weights
     * @return {Vector} current layers output
     */
    public static Vector<double[]> calculateLayersOutput(double[] initialInput, Vector<Vector<double[]>> w) {
        Vector<double[]> currentLayersOutput = new Vector<>();
        currentLayersOutput.add(initialInput);
        /*
        current input
         */
        double [] buffer = initialInput;
        /* for every layer */
        for (int i=0; i < layersAmount - 2; i++) {
            buffer = getLayerOutput(buffer, i+1, w);
            currentLayersOutput.add(buffer);
        }
        currentLayersOutput.add(softMAX(buffer));
        return currentLayersOutput;
    }

    /**
     *
     * @param input last layer
     * @param layersIndex current output layer index
     * @return double [] layer output
     */
    private static double [] getLayerOutput(double []input, int layersIndex, Vector<Vector<double[]>> w){
        double [] output = new double[neuronLAmount[layersIndex]];

        // for all neuron in layer
        for (int i = 0; i<neuronLAmount[layersIndex]; i++) {
            double sum = 0;
            for (int j = 0; j<neuronLAmount[layersIndex-1]; j++){
                /* get j-neuron with i-weight */
                sum += input[j]*w.get(layersIndex-1).get(j)[i];
            }

            // not supported
            if (layersIndex != 1 && BIAS_MODE) {
                sum += bias.get(layersIndex - 2)[i]*biasVal[layersIndex - 2];
            }

            switch (activation) {
                case SIGMOID:
                    output[i] = sigmoid(sum);
                    break;
                case TANH:
                    output[i] = tanh(sum);
            }
        }

        return output;
    }

    private static double[] softMAX(double[] buffer){
        double [] output = new double[neuronLAmount[layersAmount-1]];
        // for all neuron in layer
        double summator = 0;
        for (int i = 0; i<neuronLAmount[layersAmount-1]; i++) {
            double sum = 0;
            for (int j = 0; j<neuronLAmount[layersAmount-2]; j++){
                /* get j-neuron with i-weight */
                sum += buffer[j]*weights.get(layersAmount-2).get(j)[i];
            }
            if (BIAS_MODE) {
                sum += bias.lastElement()[i];
            }
            //output[i] = 1/((1 + Math.exp(-1*sum)));
            summator += Math.exp(sum);
            output[i] = Math.exp(sum);
        }

        for (int i=0; i<output.length; i++){
            output[i] /= summator;
        }

        return output;
    }
    /**
     * Perform sigmoid activation function
     *
     * @param value current neuron output
     * @return {double}
     */
    private static double sigmoid(double value){
        return 1/(1 + Math.exp(-1*value));
    }

    /**
     * Perform tanh activation function
     *
     * @param value current neuron output
     * @return {double}
     */
    private static double tanh(double value){
        return Math.tanh(value);
    }
    /* SETTERS */
    public static void setSIGMOID(){
        activation = SIGMOID;
    }
    public static void setTANH(){
        activation = TANH;
    }
    public void setBIAS_MODE(boolean flag) {
        BIAS_MODE = flag;
    }

}

