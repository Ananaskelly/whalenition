package recognition;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ImageLoader {

    /**
     *
     * @param file image to load
     * @return {BufferedImage} image
     */
    static public Image load(File file) {
        Image image = new Image();
        Mat original = Imgcodecs.imread(file.getAbsolutePath(), Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Mat prepared = ImageProcess.prepare(original);

        System.out.print(prepared.dump());
        image.imageMat = prepared;
        image.imageVector = ImageProcess.Mat2DoubleVector(prepared);

        return image;
    }
}
