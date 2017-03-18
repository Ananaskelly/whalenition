package recognition;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ImageProcess {

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
        Imgproc.dilate(buffer, buffer,
                Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(7,7)));
        Imgproc.threshold( buffer, buffer, 150, 255, Imgproc.THRESH_BINARY);

        Mat labels = new Mat();
        Mat stat = new Mat();
        Mat centroids = new Mat();

        Imgproc.connectedComponentsWithStats(buffer, labels, stat, centroids);

        int maxHeight = 0;
        int tx = -1; int ty = -1; int bx = 0; int by = 0;
        int heightR = 0; int widthR = 0;

        for( int i = 1; i < stat.rows(); i++ )
        {
            int[] currentStat = new int[5];
            stat.get(i, 0, currentStat);
            if (currentStat[3] > maxHeight){
                tx = currentStat[0];
                ty = currentStat[1];
                bx = currentStat[0] + currentStat[2];
                by = currentStat[1] + currentStat[3];
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

        int newH = heightR;
        if (newH > 20*0.7){
            newH = 20;
        }
        int newW = (int)Math.round((((double)newH)/heightR)*widthR);
        if (newW >= 32) {
            newW = 26;
            newH = Math.max((int)Math.round((((double)newW)/widthR)*heightR), 4);
        }
        if (newH <= 0){
            newH = 1;
        }
        if (newW <= 0){
            newW = 1;
        }
        croppedDigit = resize(croppedDigit, newW , newH);
        Imgproc.GaussianBlur(croppedDigit, croppedDigit, new Size(3,3), 1);

        Mat blank = new Mat(28, 28, CvType.CV_8UC1, new Scalar(0,0,0));
        int biasX = (int)Math.floor(28*0.5 - newW*0.5);
        int biasY = (int)Math.floor((28*0.5 - newH*0.5));
        Rect place = new Rect(biasX, biasY, newW, newH);
        Mat submat = blank.submat(place);
        croppedDigit.copyTo(submat);
        return blank;
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
