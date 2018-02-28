package ch.idsia.evolution;

import java.util.Random;

public class SmarterMLP implements FA<double[], double[]>, Evolvable {

    private double[][] firstConnectionLayer;
    private double[][] secondConnectionLayer;
    private double[] hiddenNeurons; //hidden layer
    private double[] outputs; //output layer
    private double[] inputs; //input layer
    public double mutationMagnitude = 0.1; //severity multiplier applied to all mutations

    //initial values
    public static double mean = 0.0f; //initial mean
    public static double deviation = 0.1f; //initial deviation

    public static final Random random = new Random();
    public double learningRate = 0.01;

    public SmarterMLP(int numberOfInputs, int numberOfHidden, int numberOfOutputs)
    {

        firstConnectionLayer = new double[numberOfInputs][numberOfHidden];
        secondConnectionLayer = new double[numberOfHidden][numberOfOutputs];
        hiddenNeurons = new double[numberOfHidden];
        outputs = new double[numberOfOutputs];
        inputs = new double[numberOfInputs];
        initializeLayer(firstConnectionLayer);
        initializeLayer(secondConnectionLayer);
    }

    public SmarterMLP(double[][] firstConnectionLayer, double[][] secondConnectionLayer, int numberOfHidden,
               int numberOfOutputs)
    {
        this.firstConnectionLayer = firstConnectionLayer;
        this.secondConnectionLayer = secondConnectionLayer;
        inputs = new double[firstConnectionLayer.length];
        hiddenNeurons = new double[numberOfHidden];
        outputs = new double[numberOfOutputs];
    }
    
    //satisfy Evolvable implementation
    public void reset() {}

    protected void initializeLayer(double[][] layer)
    {
        for (int i = 0; i < layer.length; i++)
        {
            for (int j = 0; j < layer[i].length; j++)
            {
                layer[i][j] = (random.nextGaussian() * deviation + mean);
            }
        }
    }

    public SmarterMLP getNewInstance()
    {
        return new SmarterMLP(firstConnectionLayer.length, secondConnectionLayer.length, outputs.length);
    }

    public SmarterMLP copy()
    {
        SmarterMLP copy = new SmarterMLP(copy(firstConnectionLayer), copy(secondConnectionLayer),
                hiddenNeurons.length, outputs.length);
        copy.setMutationMagnitude(mutationMagnitude);
        return copy;
    }

    private double[][] copy(double[][] original)
    {
        double[][] copy = new double[original.length][original[0].length];
        for (int i = 0; i < original.length; i++)
        {
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }
        return copy;
    }
    
    public void mutate() {
    	mutate(firstConnectionLayer);
        mutate(secondConnectionLayer);
    }

    public void mutate(float newMutationMagnitude)
    {
    	if (newMutationMagnitude != -1) {
    		setMutationMagnitude(newMutationMagnitude);	
    	}
        mutate(firstConnectionLayer);
        mutate(secondConnectionLayer);
    }

    private void mutate(double[] array)
    {
        for (int i = 0; i < array.length; i++)
        {
            array[i] += random.nextGaussian() * mutationMagnitude;
        }
    }

    private void mutate(double[][] array)
    {
        for (double[] anArray : array)
        {
            mutate(anArray);
        }
    }

    public void psoRecombine(SmarterMLP last, SmarterMLP pBest, SmarterMLP gBest)
    {
        // Those numbers are supposed to be constants. Ask Maurice Clerc.
        final double ki = 0.729844;
        final double phi = 2.05;

        double phi1 = phi * random.nextDouble();
        double phi2 = phi * random.nextDouble();
        
        for (int i = 0; i < inputs.length; i++)
        {
            for (int j = 0; j < hiddenNeurons.length; j++)
            {
                firstConnectionLayer[i][j] = (double) (firstConnectionLayer[i][j] + ki * (firstConnectionLayer[i][j] - ((double[][]) (last.firstConnectionLayer))[i][j]
                        + phi1 * (((double[][]) (pBest.firstConnectionLayer))[i][j] - firstConnectionLayer[i][j])
                        + phi2 * (((double[][]) (gBest.firstConnectionLayer))[i][j] - firstConnectionLayer[i][j])));
            }
        }

        for (int i = 0; i < hiddenNeurons.length; i++)
        {
            for (int j = 0; j < outputs.length; j++)
            {
                secondConnectionLayer[i][j] = (double) (secondConnectionLayer[i][j] + ki * (secondConnectionLayer[i][j] - ((double[][]) (last.secondConnectionLayer))[i][j]
                        + phi1 * (((double[][]) (pBest.secondConnectionLayer))[i][j] - secondConnectionLayer[i][j])
                        + phi2 * (((double[][]) (gBest.secondConnectionLayer))[i][j] - secondConnectionLayer[i][j])));
            }
        }

    }

    public double[] approximate(double[] doubles)
    {
        return propagate(doubles);
    }

    public double[] propagate(double[] inputIn)
    {
        if (inputs != inputIn)
        {
            System.arraycopy(inputIn, 0, this.inputs, 0, inputIn.length);
        }
        if (inputIn.length < inputs.length)
            System.out.println("MLP: NOTE: only " + inputIn.length + " inputs out of " + inputs.length + " are used in the network");
        propagateOneStep(inputs, hiddenNeurons, firstConnectionLayer);
        tanh(hiddenNeurons);
        propagateOneStep(hiddenNeurons, outputs, secondConnectionLayer);
        tanh(outputs);

        return outputs;

    }

    private void propagateOneStep(double[] fromLayer, double[] toLayer, double[][] connections)
    {
    	//clear toLayer for fresh calculations
    	for (int i = 0; i < toLayer.length; toLayer[i] = 0, ++i);
    	
        for (int from = 0; from < fromLayer.length; from++)
        {
            for (int to = 0; to < toLayer.length; to++)
            {
                toLayer[to] += fromLayer[from] * connections[from][to];
                //System.out.println("From : " + from + " to: " + to + " :: " +toLayer[to] + "+=" +  fromLayer[from] + "*"+  connections[from][to]);
            }
        }
    }
    
    /**
     * calculate tanh for all values in the specified array
     * @param array: the array to use for calculations
     */
    private void tanh(double[] array) {
        for (int i = 0; i < array.length; array[i] = Math.tanh(array[i]), ++i);
    }

    /**
     * approximate the hyperbolic tangent of the specified number
     * @param num: the number with which to calculate
     * @return the hyperbolic tangent of num
     */
    private double dtanh(double num) { return (1 - (num * num)); }

    //getters and setters for mutation magnitude
    public double getMutationMagnitude() { return mutationMagnitude; }
    public void setMutationMagnitude(double mutationMagnitude) { this.mutationMagnitude = mutationMagnitude; }
}