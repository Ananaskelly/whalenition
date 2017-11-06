package recognition;

import org.tensorflow.Output;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

public class TensorflowModel {

    private SavedModelBundle modelBundle;

    public TensorflowModel(String dirName){
        modelBundle = SavedModelBundle.load(System.getProperty("user.dir") + dirName, "serve");
    }

    public int getPrediction(float[] input){
        Session session = modelBundle.session();
        long[] shape = new long[] {28, 28};
        long[] shape1 = new long[] {10};
        FloatBuffer buffer = FloatBuffer.wrap(input);
        Tensor inputTensor = Tensor.create(shape, buffer);
        FloatBuffer buffer1 = FloatBuffer.allocate(10);
        Tensor outputTensor = Tensor.create(shape1, buffer1);
        LongBuffer newBuffer = LongBuffer.allocate(10);
        Tensor out = session.runner().feed("x", inputTensor).feed("y", outputTensor).fetch("answer42").run().get(0);
        out.writeTo(newBuffer);
        return (int)newBuffer.get(0);
    }

    public float[] getPredictionVector(float[] input, boolean hasPhase){
        Session session = modelBundle.session();

        long[] shape = new long[] {1, 28*28};
        long[] shape1 = new long[] {1, 10};

        FloatBuffer buffer = FloatBuffer.wrap(input);
        Tensor inputTensor = Tensor.create(shape, buffer);

        FloatBuffer buffer1 = FloatBuffer.allocate(10);
        Tensor outputTensor = Tensor.create(shape1, buffer1);

        FloatBuffer newBuffer = FloatBuffer.allocate(10);
        Tensor out;
        if (hasPhase) {

            Tensor phaseTensor = Tensor.create(Boolean.FALSE);

            out = session.runner().feed("x", inputTensor)
                    .feed("y", outputTensor)
                    .feed("phase", phaseTensor)
                    .fetch("softmax")
                    .run()
                    .get(0);
        } else {
            out = session.runner().feed("x", inputTensor)
                                     .feed("y", outputTensor)
                                     .fetch("softmax")
                                     .run()
                                     .get(0);
        }
        out.writeTo(newBuffer);
        return newBuffer.array();
    }
}
