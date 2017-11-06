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

        float[] preparedImage = Helper.double2FloatRow(image.imageVector);

        TensorflowModel tfModel = new TensorflowModel("/tmp/model");

        float[] smo = tfModel.getPredictionVector(preparedImage, false);
        answer.cnnResult = Helper.getMaxInd(smo);

        TensorflowModel tfModel2 = new TensorflowModel("/tmp/model2");

        float[] smo2 = tfModel2.getPredictionVector(preparedImage, true);
        answer.cnnExpResult = Helper.getMaxInd(smo2);

        TensorflowModel tfModel3 = new TensorflowModel("/tmp/model3_stn");

        float[] smo3 = tfModel3.getPredictionVector(preparedImage, true);
        answer.cnnSTNResult = Helper.getMaxInd(smo3);

        return answer;
    }
}
