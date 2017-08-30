package recognition;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageProcess {

    private static final String crROI = "crop_roi";
    private static final String crAll = "crop_all";

    private static String resizeMode = crROI;
    /**
     *
     * @param image input image
     * @return {Mat} centered image
     */
    public static Mat prepare(Mat image){

        int width = image.cols();
        int height = image.rows();
        Mat buffer = image.clone();

        if (image.channels() > 2) {
            Imgproc.cvtColor(image, buffer, Imgproc.COLOR_RGB2GRAY);
        }
        Core.bitwise_not(buffer,buffer);

        Mat labels = new Mat();
        Mat stat = new Mat();
        Mat centroids = new Mat();

        Imgproc.connectedComponentsWithStats(buffer, labels, stat, centroids);

        int maxHeight = 0;
        int tx = -1; int ty = -1;

        //int bx = 0; int by = 0;

        int heightR = 0; int widthR = 0;

        for( int i = 1; i < stat.rows(); i++ )
        {
            int[] currentStat = new int[5];
            stat.get(i, 0, currentStat);
            if (currentStat[3] > maxHeight){
                tx = currentStat[0];
                ty = currentStat[1];

                //bx = currentStat[0] + currentStat[2];
                //by = currentStat[1] + currentStat[3];

                heightR = currentStat[3];
                widthR = currentStat[2];
                maxHeight = currentStat[3];
            }
        }


        Rect rect = new Rect(tx,ty, widthR, heightR);


        if (tx < 0 || ty < 0){
            return null;
        }
        // I HOPE !!
        Mat croppedDigit = new Mat(buffer, rect);

        Mat blank;
        int biasX, biasY;
        if (resizeMode.equals(crROI)){
            int newH = heightR;
            if (newH > 20){
                newH = 20;
            }
            int newW = (int)Math.round((((double)newH)/heightR)*widthR);
            if (newW > 28) {
                newW = 20;
                newH = Math.max((int)Math.round((((double)newW)/widthR)*heightR), 4);
            }
            if (newH <= 0){
                newH = 1;
            }
            if (newW <= 0){
                newW = 1;
            }
            blank = new Mat(28, 28, CvType.CV_8UC1, new Scalar(0,0,0));
            croppedDigit = resize(croppedDigit, newW , newH);
            biasX = (int)Math.floor(28*0.5 - newW*0.5);
            biasY = (int)Math.floor((28*0.5 - newH*0.5));
            Rect place = new Rect(biasX, biasY, newW, newH);
            Mat submat = blank.submat(place);
            croppedDigit.copyTo(submat);
        } else {
            blank = new Mat(height, width, CvType.CV_8UC1, new Scalar(0,0,0));
            biasX = (int)Math.round(width*0.5 - widthR*0.5);
            biasY = (int)Math.round((height*0.5 - heightR*0.5)*0.9);
            Rect place = new Rect(biasX, biasY, widthR, heightR);
            Mat submat = blank.submat(place);
            croppedDigit.copyTo(submat);
            blank = resize(blank, 28,28);
        }

        return blank;
    }

    private  static Mat filtering(Mat input){
        Mat buffer = input.clone();
        Imgproc.dilate(buffer, buffer,
                Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(9,9)));
        Imgproc.GaussianBlur(buffer, buffer, new Size(13,13), 11);
        Imgproc.threshold( buffer, buffer, 150, 255, Imgproc.THRESH_BINARY);

        return buffer;
    }

    private static Mat resize(Mat originalSize, int w, int h){
        Mat resizedImage = new Mat();
        Size sz = new Size(w,h);
        Imgproc.resize( originalSize, resizedImage, sz );
        return resizedImage;
    }

    public static double[] Mat2DoubleVector(Mat original) {
        double[] dvec = new double[original.cols() * original.rows()];
        int c = 0;
        for (int i = 0; i < original.rows(); i ++){
            for (int j = 0; j < original.cols(); j++){
                double[] buffer = original.get(i, j);
                if (buffer[0] > 0) {
                    dvec[c] = buffer[0];
                } else {
                    dvec[c] = 0;
                }
                c++;
            }
        }
        return dvec;
    }

}
