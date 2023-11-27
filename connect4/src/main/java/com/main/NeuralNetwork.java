    package com.main;
    /* -------------------------------------------------
     * 
     * HUGE THANKS TO THIS VIDEO
     * https://www.youtube.com/watch?v=-WjKICvAOsY&t=142s
     *
     *
     *--------------------------------------------------*/

    //Backwards Propigating neural network
    public class NeuralNetwork
    {
        enum function
        {
            sigmoid,
            hardSigmoid,
            reLu,
            customRelu,
            customFunction
        }

        private function functionType = function.customRelu;

        public float[][] values;
        public float[][] biases;
        public float[][][] weights;

        public float[][] desiredValues;
        public float[][] biasesSmudge;
        public float[][][] weightsSmudge;

        private final float WeightDecay = 0.0f;
        private final float LearningRate = 0.005f;
        private final float WeightMultiplier = 0f;

        int[] structure;

        //builds a new neural network
        public NeuralNetwork(int[] structure )
        {
            this.structure = structure;
            values = new float[structure.length][];
            desiredValues = new float[structure.length][];
            biases = new float[structure.length][];
            biasesSmudge = new float[structure.length][];
            weights = new float[structure.length - 1][][];
            weightsSmudge = new float[structure.length - 1][][];

            for (int i = 0; i < structure.length; i++)
            {
                values[i] = new float[structure[i]];
                desiredValues[i] = new float[structure[i]];
                biases[i] = new float[structure[i]];
                biasesSmudge[i] = new float[structure[i]];
            }

            for (int i = 0; i < structure.length - 1; i++)
            {
                weights[i] = new float[values[i + 1].length][];
                weightsSmudge[i] = new float[values[i + 1].length][];
                for (int j = 0; j < weights[i].length; j++)
                {
                    weights[i][j] = new float[values[i].length];
                    weightsSmudge[i][j] = new float[values[i].length];
                    
                    for (int k = 0; k < weights[i][j].length; k++)
                    {
                        weights[i][j][k] = (float)((Math.random()- .5) * WeightMultiplier);
                    }
                }
            }
        }

        //builds a new network using data saved from a previous network
        public NeuralNetwork( int[] structure , float[][] biasFile , float[][][] weightFile)
        {
            values = new float[structure.length][];
            desiredValues = new float[structure.length][];
            biasesSmudge = new float[structure.length][];
            weightsSmudge = new float[structure.length - 1][][];
            biases = biasFile;
            weights = weightFile;

            for (int i = 0; i < structure.length; i++)
            {
                values[i] = new float[structure[i]];
                desiredValues[i] = new float[structure[i]];
                biasesSmudge[i] = new float[structure[i]];
            }

            for (int i = 0; i < structure.length - 1; i++)
            {
                weightsSmudge[i] = new float[values[i + 1].length][];
                for (int j = 0; j < weights[i].length; j++)
                {
                    weightsSmudge[i][j] = new float[values[i].length];
                }
            }

        }

        public float[] Test(float[] input)
        {
            for (int i = 0; i < values[0].length; i++) values[0][i] = input[i];

            for (int i = 1; i < values.length; i++)
                for (int j = 0; j < values[i].length; j++)
                {
                    values[i][j] = ActivationFunction(sum(values[i - 1], weights[i - 1][j]) + biases[i][j]);
                    desiredValues[i][j] = values[i][j];
                }

            return values[values.length - 1];
        }

        private float sum( float[] l1, float[] l2){
            float total = 0;
            for( int i = 0; i < l1.length; i ++){
                total += l1[i] + l2[i];
            }
            return total;
        }

        /*private static float dot(float[] values , float[] weights)
        {
            float sum = 0;
            for( int i = 0; i  < values.length; i++)
            {
                sum += values[i] * weights[i];
            }
            return sum;
        }*/

        private float ActivationFunction(float x)
        {
            switch( functionType )
            {
                case sigmoid:
                    x = Sigmoid(x);
                    break;
                case hardSigmoid:
                    x = HardSigmoid(x);
                    break;
                case reLu:
                    x = ReLu(x);
                    break;
                case customRelu:
                    x = CustomReLu(x);
                    break;
                case customFunction:
                    x = customFunc(x);
                    break;
            }
            return x;
        }

        private static float Sigmoid(float x){ 
            return (1f / (1f + (float)Math.exp(x))) - .5f;
        } 

        private static float CustomReLu(float x)
        {
            if (x > 0f) return 2f*x;
            return  -x;
        }

        private static float CustomReLuDerivative( float x)
        {
            if (x > 0) return 2f;
            return -1;
        }

        private static float customFunc(float x) {
            if( x>= -1 && x<= 1) return x; 
            else if ( x>= -1 ) return -1;
            else return 1;
        }

        private static float customFuncDerivative(float x) {
            if( x>= -1 && x<= 1) return 1;
            else return 0; 
        }

        private static float HardSigmoid(float x)
        {
            if (x < -2.5f)
                return 0;
            if (x > 2.5f)
                return 1;
            return 0.2f * x + 0.5f;
        }

        private static float HardSigmoidDerivative( float x)
        {
            if (x < -2.5f)
                return 0;
            if (x > 2.5f)
                return 0;
            return .2f;
        }

        public void Train(float[][] trainingInputs, float[][] trainingOutputs)
        {
            for (int i = 0; i < trainingInputs.length; i++)
            {
                Test(trainingInputs[i]);

                for (int j = 0; j < desiredValues[desiredValues.length - 1].length; j++)
                    desiredValues[desiredValues.length - 1][j] = trainingOutputs[i][j];

                for (int j = values.length - 1; j >= 1; j--)
                {
                    for (int k = 0; k < values[j].length; k++)
                    {
                        float biasSmudge = ActivationFunctionDerivative(values[j][k]) *
                                         (desiredValues[j][k] - values[j][k]);
                        biasesSmudge[j][k] += biasSmudge;

                        for (int l = 0; l < values[j - 1].length; l++)
                        {
                            float weightSmudge = values[j - 1][l] * biasSmudge;
                            weightsSmudge[j - 1][k][l] += weightSmudge;

                            float valueSmudge = weights[j - 1][k][l] * biasSmudge;
                            desiredValues[j - 1][l] += valueSmudge;
                        }
                    }
                }
            }

            for (int i = values.length - 1; i >= 1; i--)
            {
                for (int j = 0; j < values[i].length; j++)
                {
                    biases[i][j] += biasesSmudge[i][j] * LearningRate;
                    biases[i][j] *= 1 - WeightDecay;
                    biasesSmudge[i][j] = 0;

                    for (int k = 0; k < values[i - 1].length; k++)
                    {
                        weights[i - 1][j][k] += weightsSmudge[i - 1][j][k] * LearningRate;
                        weights[i - 1][j][k] *= 1 - WeightDecay;
                        weightsSmudge[i - 1][j][k] = 0;
                    }

                    desiredValues[i][j] = 0;
                }
            }
        }

        private static float SigmoidDerivative(float x){
            return x * (1 - x);
        }

        private static float ReLu( float V)
        {
            if (V > 0)
            {
                return V + 2;
            }
            return 2;
        }

        public static float ReLuDerivative( float V)
        {
            if( V > 0)
            {
                return 1f;
            }
            return 0f;
        }

        private float ActivationFunctionDerivative(float x)
        {
            switch (functionType)
            {
                case sigmoid:
                    x = SigmoidDerivative(x);
                    break;
                case hardSigmoid:
                    x = HardSigmoidDerivative(x);
                    break;
                case reLu:
                    x = ReLuDerivative(x);
                    break;
                case customRelu:
                    x = CustomReLuDerivative(x);
                    break;
                case customFunction:
                    x = customFuncDerivative(x);
                    break;
            }
            return x;
        }

    }