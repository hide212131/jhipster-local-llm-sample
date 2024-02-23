package com.mycompany.myapp.web.api;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.random.RandomDataGenerator;

public class RandomProjection {

    private RealMatrix randomMatrix;

    public RandomProjection(int originalDimension, int reducedDimension) {
        RandomDataGenerator randomData = new RandomDataGenerator();
        double[][] randomMatrixData = new double[reducedDimension][originalDimension];

        for (int i = 0; i < reducedDimension; i++) {
            for (int j = 0; j < originalDimension; j++) {
                // generate N(0,1) random number
                randomMatrixData[i][j] = randomData.nextGaussian(0, 1);
            }
        }

        this.randomMatrix = new Array2DRowRealMatrix(randomMatrixData);
    }

    public RealMatrix project(RealMatrix originalData) {
        return this.randomMatrix.multiply(originalData.transpose()).transpose();
    }
}
