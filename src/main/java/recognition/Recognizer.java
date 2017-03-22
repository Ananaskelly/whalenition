package recognition;

import org.opencv.core.Mat;
import whalenition.FilesStorage;

import java.io.File;

import static recognition.Helper.double2Float;

public class Recognizer {

    public static RecognizerAnswer run(FilesStorage storage){
        RecognizerAnswer answer = new RecognizerAnswer();
        File lastLoadedPic = storage.getLast().get().toFile();

        Image image = ImageLoader.load(lastLoadedPic);

        // MLP
        MLP.run(image.imageVector, MLP.weights);

        int maxInd = 0;
        double maxEl = 0.0;

        for (int j = 0; j < MLP.layersOutput.lastElement().length; j++) {
            if (MLP.layersOutput.lastElement()[j] > maxEl) {
                maxEl = MLP.layersOutput.lastElement()[j];
                maxInd = j;
            }
        }
        answer.mlpResult = maxInd;

        /*CNN cnn = new CNN();
        cnn.setPath("/convMaps4/");
        cnn.createModel(System.getProperty("user.dir") + "/cnn_model_mnist.txt");

        cnn.setInitialFeature(double2Float(image.imageVector));
        cnn.feedforward();

        int maxIndCC = 0;
        double maxElCC = 0.0;
        for (int k = 0; k < cnn.features.lastElement().size(); k++) {
            if(cnn.features.lastElement().get(k)[0][0] > maxElCC) {
                maxElCC = cnn.features.lastElement().get(k)[0][0];
                maxIndCC = k;
            }
        }
        answer.cnnResult = maxIndCC;*/

        return answer;
    }
}
