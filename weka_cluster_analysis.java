import java.io.File;
import weka.core.Instances; 
import weka.core.converters.ConverterUtils.DataSource;
import weka.clusterers.SimpleKMeans;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.RemoveFolds;

// Arnar Ingi Gunnarsson
// Cluster analysis using WEKA - Data Mining 2016
public class assignment3 { 
 
    public static void main(String[] args) throws Exception { 
        Instances newData = DataSource.read("/Users/arnar/Documents/HI/gagnanam"
                + "/data_weka/"+"abalone.arff");  
      
 
        /*
        Remove remove = new Remove();
        remove.setAttributeIndices("");
        newData = removeAttributes(newData,remove);
        */
        
        // findSubsample(Instances newData,int numFolds,int setSeed)
        newData = findSubSample(newData,21,1);
        
        int sizeOfDataset = newData.numInstances();
        System.out.println("Number of instances in this dataset = "
        + sizeOfDataset+"\n" );
        
        // save file
        saveFileToDisk(newData);

        //checkSSEandSeed(newData,numSeedIterations,numClusters,SSELimit);
        checkSSEandSeed(newData,200,6,25); 
        
        int numberOfIterations = 5; 
        int seed = 7;
        int K = 6;
         
        Object[] kMeans = new SimpleKMeans[numberOfIterations];      
        int[] seedValue = new int[numberOfIterations];
        
        for (int i = 0; i<kMeans.length; i++) {
            SimpleKMeans model = new SimpleKMeans();
            model.setSeed(seed);
            seed = seed + 10;
            model.setNumClusters(K);
            model.buildClusterer(newData);
            kMeans[i]=model;
            seedValue[i]=model.getSeed();
        }
        
        for (int i = 0; i<kMeans.length; i++) {
            System.out.println("ITERATION "+(i+1));
            System.out.println("Seed = "+seedValue[i]);
            System.out.println(kMeans[i]);
        }
    }
    
    //Usage: checkSSEandSeed(newData,numSeedIterations,numClusters,SSELimit);
    //         This function finds how many SimpleKmeans cluster analysis 
    //         instances give an SSE under a chosen SSE limit for a chosen
    //         number of clusters and a chosen number of SimpleKMeans run,
    //         by iterating with the SEED variable stepwise from 0 to 
    //         numSeedIterations. 
    //         
    //         It prints out a list of (filtered) SEED values and the 
    //         corresponding SSE value under the specified limit.
    private static void checkSSEandSeed(
            Instances newData,
            int numSeedIterations,
            int numClusters,
            int SSELimit) 
            throws Exception {
        
        int i;
        int counter = 0;
        double[] SSE = new double[numSeedIterations];
        double[] filteredSSE;
        int[] seed;
           
        for (i = 0; i<numSeedIterations; i++) {
            SimpleKMeans model = new SimpleKMeans();
            model.setSeed(i);
            model.setNumClusters(numClusters);
            model.buildClusterer(newData);
            SSE[i] = model.getSquaredError();    
        }
        
        for (i = 0; i<numSeedIterations; i++) {
            if (SSE[i]<SSELimit) {
                counter++;    
            }
        }
        
        System.out.println("iterate seed from 0 to "+numSeedIterations+":");
        System.out.println(counter+" instances of SimpleKMeans with: "
                + "\nNr of clusters = "+ numClusters+ "\n"
                + "have an SSE value under "+ SSELimit+"\n");
        
        seed = new int[counter];
        filteredSSE = new double[counter];
        int k = 0;
        
        for (i = 0; i<numSeedIterations; i++) {
            if (SSE[i]<SSELimit) {
                seed[k] = i;
                filteredSSE[k]=SSE[i];
                k++;
                
            }    
        }
        
        System.out.println("Smallest SSE value is: "+getMinValue(SSE)
                   +"\nfor seed = " +getThe_i(SSE)+"\n");
        
        for (i = 0; i<counter; i++) {
            System.out.println(seed[i]+"  ;  "+filteredSSE[i]);
        }
    }
    
    // FINDS THE MINIMUM VALUE IN AN ARRAY OF TYPE DOULBE
    public static double getMinValue(double[] array) {
        double minValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < minValue) {
                minValue = array[i];
            }
        }
        return minValue;
    }
    
    // FINDS THE i-th SEAT IN THE ARRAY WHERE THE MINIMUM VALUE IS
    public static double getThe_i(double[] array) {
        double minValue = array[0];
        int i;
        for (i = 1; i < array.length; i++) {
            if (array[i] < minValue) {
                minValue = array[i];
            }
        }
        
        for(i = 1; i < array.length; i++) {
            if (minValue == array[i]) {
                break;
            }            
        }
        return i;  
    }
    
    // SAVES FILE TO DISK
    public static void saveFileToDisk(Instances newData) throws Exception {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(newData);
        saver.setFile(new File("/Users/arnar/desktop/data/prufa.arff"));
        saver.writeBatch(); 
        
    }
    
    // FIND SUBSAMPLE
    public static Instances findSubSample(
            Instances newData,
            int numFolds,
            int setSeed)
            throws Exception {
           
        RemoveFolds removeFol = new RemoveFolds();
        removeFol.setSeed(setSeed);
        removeFol.setNumFolds(numFolds);
        removeFol.setInputFormat(newData);
        newData = Filter.useFilter(newData, removeFol);
        return newData;   
    }
    
    public static Instances removeAttributes(
            Instances newData,
            Remove remove)
            throws Exception {
        remove.setInputFormat(newData);
        newData = Filter.useFilter(newData, remove);
        return newData;     
    }    
    
}
