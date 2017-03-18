package recognition;

import whalenition.FilesStorage;

import java.io.File;

public class Recognizer {

    public static RecognizerAnswer run(FilesStorage storage){
        RecognizerAnswer answer = new RecognizerAnswer();
        File lastLoadedPic = storage.getLast().get().toFile();

        Image image = ImageLoader.load(lastLoadedPic);

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

        return answer;
    }
}
