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

        TensorflowModel tfModel = TensorflowModel.getInstance();

        float[] smo = tfModel.getPredictionVector(Helper.double2FloatRow(image.imageVector));
        answer.cnnResult = Helper.getMaxInd(smo);

        return answer;
    }
}
