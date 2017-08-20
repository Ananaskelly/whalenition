package recognition;

import CNNEntities.Winner;

public class Helper {

    public static float[][] getSubmatrix(float[][] input, int y, int x, int height, int width){
        float[][] result = new float[height][width];
        int c = 0;
        for (int i = y; i < (y + height); i ++){
            int c1 = 0;
            for (int j = x; j < (x+width); j++){
                result[c][c1] = input[i][j];
                c1++;
            }
            c++;
        }

        return result;
    }

    public static float[][] matrixSum(float[][] m1, float[][] m2){
        float[][] res = new float[m1.length][m1[0].length];
        for (int i = 0; i < m1.length; i++){
            for (int j = 0; j < m1[0].length; j++){
                res[i][j] = m1[i][j] + m2[i][j];
            }
        }
        return res;
    }

    public static float convolution(float[][] m1, float[][] m2){
        float answer = 0;
        for (int i = 0; i < m1.length; i++){
            for (int j = 0; j < m1[0].length; j++){
                answer += m1[i][j]*m2[i][j];
            }
        }
        return answer;
    }

    public static Winner getMax(float[][] input){
        Winner winner = new Winner();
        float max = -Float.MAX_VALUE;
        int indexI = 0;
        int indexJ = 0;
        for (int i = 0; i < input.length; i++){
            for (int j = 0; j < input[0].length; j++ ){
                if (input[i][j] > max){
                    max = input[i][j];
                    indexI = i;
                    indexJ = j;
                }
            }
        }
        winner.value = max;
        winner.indexI = indexI;
        winner.indexJ = indexJ;
        return winner;
    }

    public static float[][] double2Float(double[] in){
        float m[][] = new float[28][28];
        int c = 0;
        for (int i = 0; i < 28; i++ ){
            for (int j = 0; j < 28; j++){
                m[i][j] = (float)in[c];
                c++;
            }
        }
        return m;
    }

    public static float[] double2FloatRow(double[] in){
        float m[] = new float[in.length];

        for (int i = 0; i < in.length; i++ ){
            m[i] = (float)in[i];
        }
        return m;
    }

    public static int getMaxInd(float[] arr){
       int maxInd = 0;
       float maxVal = - Float.MAX_VALUE;
       for (int i = 0; i < arr.length; i++){
           if (maxVal < arr[i]){
               maxVal = arr[i];
               maxInd = i;
           }
       }

       return maxInd;
    }

}
