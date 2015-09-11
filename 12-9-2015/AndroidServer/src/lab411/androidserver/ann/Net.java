package lab411.androidserver.ann;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import lab411.androidserver.features.Features;
import lab411.androidserver.features.MyVector;
import lab411.androidserver.file.MyFile;

import android.os.Environment;

public class Net {
	static double LEARNING_RATE = 0.005;
	static double MOMENTUM = 0.05;
    public static int nInput, nHidden, nOutput;

    //neurons
    public static double[] inputNeurons;
    public static double[] hiddenNeurons;
    public static double[] outputNeurons;

    //weights
    public static double[][] wInputHidden;
    public static double[][] wHiddenOutput;

    //change to weights
    public static double[][] deltaInputHidden;
    public static double[][] deltaHiddenOutput;

    //error gradients
    public static double[] hiddenErrorGradients;
    public static double[] outputErrorGradients;

    //learning parameters
    public static double learningRate;
    public static double momentum;
    
    public static void initNet(int nI, int nH, int nO)
    {
        nInput = nI;
        nHidden = nH;
        nOutput = nO;

        inputNeurons = new double[nInput];
        for (int i = 0; i < nInput; i++)
        {
            inputNeurons[i] = 0;
        }
        hiddenNeurons = new double[nHidden];
        for (int i = 0; i < nHidden; i++)
        {
            hiddenNeurons[i] = 0;
        }
        outputNeurons = new double[nOutput];
        for (int i = 0; i < nOutput; i++)
        {
            outputNeurons[i] = 0;
        }
        wInputHidden = new double[nInput][];
        for (int i = 0; i < nInput; i++)
        {
            wInputHidden[i] = new double[nHidden];
            for (int j = 0; j < nHidden; j++)
            {
                wInputHidden[i][j] = 0;
            }
        }
        wHiddenOutput = new double[nHidden][];
        for (int i = 0; i < nHidden; i++)
        {
            wHiddenOutput[i] = new double[nOutput];
            for (int j = 0; j < nOutput; j++)
            {
                wHiddenOutput[i][j] = 0;
            }
        }

        deltaInputHidden = new double[nInput][];
        for (int i = 0; i < nInput; i++)
        {
            deltaInputHidden[i] = new double[nHidden];
            for (int j = 0; j < nHidden; j++)
            {
                deltaInputHidden[i][j] = 0;
            }
        }
        deltaHiddenOutput = new double[nHidden][];
        for (int i = 0; i < nHidden; i++)
        {
            deltaHiddenOutput[i] = new double[nOutput];
            for (int j = 0; j < nOutput; j++)
            {
                deltaHiddenOutput[i][j] = 0;
            }
        }

        hiddenErrorGradients = new double[nHidden];
        for (int i = 0; i < nHidden; i++)
        {
            hiddenErrorGradients[i] = 0;
        }
        outputErrorGradients = new double[nOutput];
        for (int i = 0; i < nOutput; i++)
        {
            outputErrorGradients[i] = 0;
        }
        learningRate = LEARNING_RATE;
        momentum = MOMENTUM;
    }
    public static void initializeWeights()
    {
        Random random = new Random();
        for (int i = 0; i < nInput; i++)
        {
            for (int j = 0; j < nHidden; j++)
            {
            	wInputHidden[i][j] = random.nextDouble()*0.4 - 0.2;
            }
        }
        for (int i = 0; i < nHidden; i++)
        {
            for (int j = 0; j < nOutput; j++)
            {
            	wHiddenOutput[i][j] = random.nextDouble()*0.4 - 0.2;
            }
        }
    }
    public static void setLearningParameters(double lr, double m)
    {
    	learningRate = lr;
    	momentum = m;
    }
    public static double activationFunction(double x)
    {
        return (double)(1.0 / (1 + Math.exp(-x)));
    }
    public static void feedForward(double[] inputs)
    {
        for (int i = 0; i < nInput; i++)
        {
        	inputNeurons[i] = inputs[i];
        }
        for (int i = 0; i < nHidden; i++)
        {
        	hiddenNeurons[i] = 0;
            for (int j = 0; j < nInput; j++)
            {
            	hiddenNeurons[i] += inputNeurons[j] * wInputHidden[j][i];
            }
            hiddenNeurons[i] = activationFunction(hiddenNeurons[i]);
        }
        for (int i = 0; i < nOutput; i++)
        {
        	outputNeurons[i] = 0;
            for (int j = 0; j < nHidden; j++)
            {
            	outputNeurons[i] += hiddenNeurons[j] * wHiddenOutput[j][i];
            }
            outputNeurons[i] = activationFunction(outputNeurons[i]);
        }
    }
    public static void backpropagate(double[] desiredValues)
    {
        double sum;
        for (int i = 0; i < nOutput; i++)
        {
        	outputErrorGradients[i] = outputNeurons[i] * (1 - outputNeurons[i]) * (desiredValues[i] - outputNeurons[i]);
            for (int j = 0; j < nHidden; j++)
            {
            	deltaHiddenOutput[j][i] = learningRate * hiddenNeurons[j] * outputErrorGradients[i] + momentum * deltaHiddenOutput[j][i];
            }
        }
        for (int i = 0; i < nHidden; i++)
        {
            sum = 0;
            for (int j = 0; j < nOutput; j++)
            {
                sum += outputErrorGradients[j] * wHiddenOutput[i][j];
            }
            hiddenErrorGradients[i] = hiddenNeurons[i] * (1 - hiddenNeurons[i]) * sum;
            for (int j = 0; j < nInput; j++)
            {
            	deltaInputHidden[j][i] = learningRate * inputNeurons[j] * hiddenErrorGradients[i] + momentum * deltaInputHidden[j][i];
            }
        }
    }
    public static void updateWeights()
    {
        for (int i = 0; i < nInput; i++)
        {
            for (int j = 0; j < nHidden; j++)
            {
            	wInputHidden[i][j] += deltaInputHidden[i][j];
            }
        }
        for (int i = 0; i < nHidden; i++)
        {
            for (int j = 0; j < nOutput; j++)
            {
            	wHiddenOutput[i][j] += deltaHiddenOutput[i][j];
            }
        }
    }
    public static void disorder(Random random)
    {
        int a1, a2;
        int tmp;
        for (int i = 0; i < 2 * Irea.irea.length; i++)
        {
            a1 = random.nextInt(Irea.irea.length);
            do
            {
                a2 = random.nextInt(Irea.irea.length);
            } while (a1 == a2);
            tmp = Irea.irea[a1];
            Irea.irea[a1] = Irea.irea[a2];
            Irea.irea[a2] = tmp;
        }
    }
    public static void trainNetworks(MyVector[] vec, int loop)
    {
        Irea.setIrea(vec.length);
        Random random = new Random();

        for (int i = 0; i < loop; i++)
        {
            disorder(random);
            for (int j = 0; j < vec.length; j++)
            {
                feedForward(vec[Irea.irea[j]].pattern);
                backpropagate(vec[Irea.irea[j]].target);
                updateWeights();
            }
        }
    }
    public static double testAccuracy(MyVector[] vec)
    {
        double incorrectResults = 0;
        for (int i = 0; i < vec.length; i++)
        {
            feedForward(vec[i].pattern);
            getRoundedOutputValue();
            if (compare(outputNeurons, vec[i].target))
                incorrectResults++;
        }
        return (double)incorrectResults / vec.length;
    }
    public static void getRoundedOutputValue()
    {
        for (int i = 0; i < nOutput; i++)
        {
            if (outputNeurons[i] < 0.05)
                outputNeurons[i] = 0.0;
            else if (outputNeurons[i] > 0.95)
                outputNeurons[i] = 1.0;
            else
                outputNeurons[i] = -1.0;
        }
    }
    public static boolean compare(double[] a, double[] b)
    {
        if (a.length != b.length)
            return false;
        else
        {
            for (int i = 0; i < a.length; i++)
            {
                if (a[i] != b[i])
                    return false;
            }
            return true;
        }
    }
    public static String detect(double[] input)
    {
        double[] w = new double[2];
        w[0] = 0;
        w[1] = 0;
        double[] s1 = new double[2];
        s1[0] = 0;
        s1[1] = 1;
        double[] s2 = new double[2];
        s2[0] = 1;
        s2[1] = 0;
        double[] s3 = new double[2];
        s3[0] = 1;
        s3[1] = 1;
        double[] extract = Features.extract(input);
        feedForward(extract);
        getRoundedOutputValue();
        if (compare(outputNeurons, w))
            return "wake";
        else if (compare(outputNeurons, s1))
            return "stage1";
        else if (compare(outputNeurons, s2))
            return "stage2";
        else if (compare(outputNeurons, s3))
            return "stage3";
        else
            return "fail";
    }
    
    public static void loadNetworks() throws IOException
    {
    	ArrayList<String> lines = MyFile.readAllFiles("lsdata/NETWORKS.txt");
        String[] tmp1 = lines.get(1).split("\t");
        nInput = Integer.parseInt(tmp1[0]);
        nHidden = Integer.parseInt(tmp1[1]);
        nOutput = Integer.parseInt(tmp1[2]);
        for (int i = 0; i < nInput; i++)
        {
            String[] tmp2 = lines.get(3 + i).trim().split("\t");
            for (int j = 0; j < nHidden; j++)
            {
                wInputHidden[i][j] = Double.parseDouble(tmp2[j]);
            }
        }
        for (int i = 0; i < nHidden; i++)
        {
            String[] tmp3 = lines.get(4 + nInput + i).trim().split("\t");
            for (int j = 0; j < nOutput; j++)
            {
                wHiddenOutput[i][j] = Double.parseDouble(tmp3[j]);
            }
        }
        String[] tmp4 = lines.get(5 + nInput + nHidden).split("\t");
        learningRate = Double.parseDouble(tmp4[0]);
        momentum = Double.parseDouble(tmp4[1]);
    }
    public static void saveNetworks() throws Exception 
    {
    	StringBuilder content = new StringBuilder();
    	content.append("nInput\tnHidden\tnOutput\n");
    	content.append(Integer.toString(nInput) + "\t" + Integer.toString(nHidden) + "\t" + Integer.toString(nOutput) + "\n");
    	content.append("Weights Input Hidden\n");
        for (int i = 0; i < nInput; i++)
        {
            for (int j = 0; j < nHidden; j++)
            {
            	content.append(Double.toString(wInputHidden[i][j]) + "\t");
            }
            content.append("\n");
        }
        content.append("Weights Hidden Output\n");
        for (int i = 0; i < nHidden; i++)
        {
            for (int j = 0; j < nOutput; j++)
            {
            	content.append(Double.toString(wHiddenOutput[i][j]) + "\t");
            }
            content.append("\n");
        }
        content.append("LearningRate\tMomentum\n");
        content.append(Double.toString(learningRate) + "\t" + Double.toString(momentum));
    	
        File sdcard = Environment.getExternalStorageDirectory();
		File fo = new File(sdcard, "lsdata/NETWORKS.txt");
        if (!fo.exists()) {
            fo.createNewFile();
        }
		FileWriter fw = new FileWriter(fo.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content.toString());
        bw.close();
        fw.close();
    }
}
