/*
*    This program is free software; you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation; either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program; if not, write to the Free Software
*    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/

package meka.classifiers.multilabel.meta;

import meka.classifiers.multilabel.meta.oldversions30012020.*;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.AttributeSelection;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.converters.ArffSaver;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.TechnicalInformation;
import weka.core.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.net.URLDecoder;
import java.nio.file.Paths;

import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import meka.classifiers.multilabel.AbstractMultiLabelClassifier;
import meka.classifiers.multilabel.meta.automekabocompact.core.AlgorithmEvaluation;
import meka.classifiers.multilabel.meta.automekabocompact.core.AutoMLCAlgorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import meka.classifiers.multilabel.meta.automekabocompact.core.Experiment;
import meka.classifiers.multilabel.meta.automekabocompact.core.ExperimentConstructor;
import meka.classifiers.multilabel.meta.automekabocompact.core.Util;
import meka.classifiers.multilabel.meta.automekabocompact.core.Configuration;
import meka.classifiers.multilabel.meta.automekabocompact.core.ConfigurationCollection;
import meka.classifiers.multilabel.meta.automekabocompact.core.HandleAlgorithm;
import meka.classifiers.multilabel.meta.automekabocompact.core.HandleFiles;
import meka.classifiers.multilabel.meta.util.Results;
import meka.core.MLUtils;



/**
 * AutoMEKA_BO_Large.java - A method for selecting and configuring multi-label 
 * classification (MLC) algorithm in the MEKA software
 * (version with multifidelity)
 * (version for the search space Minimal and Medium).
 * 
 * 
 * Auto-MEKA_BO uses a Bayesian otimization method (SMAC: Sequential Model-based
 * Algorithm Configuration), aiming to find the most suitable MLC algorithm for 
 * a given dataset of interest.
 * 
 * This code is based on Auto-WEKA, which was developed by Lars Kotthoff.
 *
 * @author Alex G. C. de Sa (alexgcsa@gmail.com)
 */
public class AutoMEKA_BO_MinimalMedium extends AbstractClassifier{

    /** For serialization. */
    static final long serialVersionUID = 2907034203562786373L;

    /** For logging Auto-MEKA's output. */
    final Logger log = LoggerFactory.getLogger(AutoMEKA_BO_MinimalMedium.class);

    /** Default time limit for Auto-MEKA. */
    static final int DEFAULT_TIME_LIMIT = 1;
    /** Default memory limit for classifiers. */
    static final int DEFAULT_MEM_LIMIT = 1024;
    /** Default */
    static final int DEFAULT_N_BEST = 1;

    
    /** Internal evaluation method. */
    static enum Resampling {
        CrossValidation,
        MultiLevel,
        RandomSubSampling,
        TerminationHoldout
    }
    /** Default evaluation method. */
    static final Resampling DEFAULT_RESAMPLING = Resampling.RandomSubSampling;

    /** Available metrics. */
    static enum Metric {
        errorRate,
        fMeasure,
        hammingLoss,
        rankingLoss,
        exactMatch,
        qualityMetric
    }
    /** Metrics to maximise. */
    static final Metric[] metricsToMax = {
        Metric.fMeasure,
        Metric.exactMatch,
        Metric.qualityMetric
    };
    /** Default evaluation metric. */
    static final Metric DEFAULT_METRIC = Metric.fMeasure;

    /** Default arguments for the different evaluation methods. */
    static final Map<Resampling, String> resamplingArgsMap;
    static {
        resamplingArgsMap = new HashMap<Resampling, String>();
        resamplingArgsMap.put(Resampling.CrossValidation, "numFolds=10");
        resamplingArgsMap.put(Resampling.MultiLevel, "numLevels=2[$]meka.classifiers.multilabel.meta.automekabo.core.instancegenerators.CrossValidation[$]numFolds=10");
        resamplingArgsMap.put(Resampling.RandomSubSampling, "numSamples=1:percent=66");
    }
    /** Arguments for the default evaluation method. */
    static final String DEFAULT_RESAMPLING_ARGS = resamplingArgsMap.get(DEFAULT_RESAMPLING);

    /** Default additional arguments for Auto-MEKA. */
    static final String DEFAULT_EXTRA_ARGS = "initialIncumbent=DEFAULT:acq-func=EI";

    /** The path for the sorted best configurations **/
    public static final String configurationRankingPath = "ConfigurationLogging" + File.separator + "configuration_ranking.xml";
    /** The path for the log with the hashcodes for the configs we have **/
    public static final String configurationHashSetPath = "ConfigurationLogging" + File.separator + "configuration_hashes.txt";
    /** The path for the directory with the configuration data and score **/
    public static final String configurationInfoDirPath = "ConfigurationLogging" + File.separator + "configurations/";


    /** The chosen classifier. */
    protected Classifier classifier;
    /** The chosen attribute selection method. */
    protected AttributeSelection as;

    /** The class of the chosen classifier. */
    protected String classifierClass;
    /** The arguments of the chosen classifier. */
    protected String[] classifierArgs;
    /** The class of the chosen attribute search method. */
    protected String attributeSearchClass;
    /** The arguments of the chosen attribute search method. */
    protected String[] attributeSearchArgs;
    /** The class of the chosen attribute evaluation. */
    protected String attributeEvalClass;
    /** The arguments of the chosen attribute evaluation method. */
    protected String[] attributeEvalArgs;

    /** The paths to the internal Auto-MEKA files.*/
    protected static String[] msExperimentPaths;
    /** The internal name of the experiment. */
    protected static String expName = "Auto-MEKA";

    /** The random seed. */
    protected int seed = 456;
    /** The time limit for running Auto-MEKA. */
    protected int timeLimit = DEFAULT_TIME_LIMIT;
    /** The memory limit for running classifiers. */
    protected int memLimit = DEFAULT_MEM_LIMIT;

    /** The number of best configurations to return as output. */
    protected int nBestConfigs = DEFAULT_N_BEST;
    /** The best configurations. */
    protected ConfigurationCollection bestConfigsCollection;

    /** The internal evaluation method. */
    protected Resampling resampling = DEFAULT_RESAMPLING;
    /** The arguments to the evaluation method. */
    protected String resamplingArgs = DEFAULT_RESAMPLING_ARGS;
    /** The extra arguments for Auto-MEKA. */
    protected String extraArgs = DEFAULT_EXTRA_ARGS;

    /** The error metric. */
    protected Metric metric = DEFAULT_METRIC;

    /** The estimated metric values of the chosen methods for each parallel run. */
    protected double[] estimatedMetricValues;
    /** The names and parameters of the chosen methods for each parallel run. */
    protected String[] bestAlgorithms;
    protected String[] bestAlgorithmsParams; 
    /** The estimated metric value of the method chosen out of the parallel runs. */
    protected double estimatedMetricValue = -1;

    /** The evaluation for the best classifier. */
    protected Evaluation eval;

    /** The default number of parallel threads. */
    protected final int DEFAULT_PARALLEL_RUNS = 1;

    /** The number of parallel threads. */
    protected int parallelRuns = DEFAULT_PARALLEL_RUNS;
    
    /** The default fold to perform the task. */
    protected final int DEFAULT_FOLD = 0;    
    
    /** The fold to perform the task **/
    protected int fold = DEFAULT_FOLD;

    /** The time it took to train the final classifier. */
    protected double finalTrainTime = -1;

    private transient weka.gui.Logger wLog;

    /* Don't ask. */
    public int totalTried;
    
    protected final int DEFAULT_SEARCH_SPACE_MODE = 2;
    //The defined search space (i.e., 0: Minimal, 1: Medium, >2: Large).
    protected int searchSpaceMode = DEFAULT_SEARCH_SPACE_MODE;    
    
    
    /** Training directory. */
    protected String trainingDirectory = "";
    /** Testing directory. */
    protected String testingDirectory = "";
    
    /** The default complementar name of the file and directory to save the results. */
    protected String DEFAULT_NAME = "experimentABC"; 
    /** The complementar name of the file and directoryto save the results. */
    protected String name = DEFAULT_NAME;    
    
    /** The default directory to save the results and logs. */
    protected String DEFAULT_SAVING_DIR = ""; 
    /** The directory to save the results and logs. */
    protected String savingDir = DEFAULT_SAVING_DIR;       
    
    ProcessBuilder [] pb;
    Process [] mProc;
    BufferedReader[] reader;
    Thread [] tst;
    
    

    /**
     * Main method for testing this class.
     *
     * @param argv should contain command line options (see setOptions)
     */
    public static void main(String[] argv) {
        // this always succeeds...
        AbstractMultiLabelClassifier.runClassifier(new AutoMEKA_BO_MinimalMedium(argv), argv);
    }

    /** Constructs a new AutoMEKA_GGP Classifier.
     * @param argv */
    public AutoMEKA_BO_MinimalMedium(String [] argv) {
        classifier = null;
        classifierClass = null;
        classifierArgs = null;
        attributeSearchClass = null;
        attributeSearchArgs = new String[0];
        attributeEvalClass = null;
        attributeEvalArgs = new String[0];
        wLog = null;
        for(int i=0; i < argv.length; i++){
            if(argv[i].equals("-t")){
                i++;
                trainingDirectory = argv[i];
            }else if(argv[i].equals("-T")){
                i++;
                testingDirectory = argv[i];
            }
        }

        totalTried = 0;

        // work around broken XML parsers
        Properties props = System.getProperties();
        props.setProperty("org.xml.sax.parser", "com.sun.org.apache.xerces.internal.parsers.SAXParser");
        props.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        props.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
    }
    
  


    /**
    * Find the best classifier, arguments, and attribute selection for the data.
    *
    * @param is the training data to be used for selecting and tuning the
    * classifier.
    * @throws Exception if the classifier could not be built successfully.
    */
    public void buildClassifier(Instances is) throws Exception { 

       /** For logging. **/
       this.createParamsDir();
       long startTime = System.currentTimeMillis();
        /** Number of labels: */
        String[] dataOpts = MLUtils.getDatasetOptions(is);
        int n_labels = Integer.parseInt(dataOpts[dataOpts.length - 1]);
        if(n_labels < 0){
            n_labels *= -1;
        }        

        estimatedMetricValues = new double[parallelRuns];
        bestAlgorithms = new String[parallelRuns]; 
        bestAlgorithmsParams = new String[parallelRuns]; 
        msExperimentPaths = new String[parallelRuns];
        
        pb = new ProcessBuilder[parallelRuns];
        mProc = new Process[parallelRuns];
        reader= new BufferedReader[parallelRuns];   
        tst = new Thread[parallelRuns];
        ArrayList<String> dirToDelete = new ArrayList<String>();        
        
        for(int i = 0; i < parallelRuns; i++) {
            estimatedMetricValues[i] = -1;
            //msExperimentPaths[i] = Files.createTempDirectory(Paths.get("/home/alexgcsa"), "automeka").toString() + File.separator;
            msExperimentPaths[i] = Files.createTempDirectory( "automeka").toString() + File.separator;
            Experiment exp = new Experiment();
            exp.name = expName;
            exp.seed = seed;

            exp.resultMetric = metric.toString();

            Properties props = Util.parsePropertyString("type=trainTestArff:testArff=__dummy__");
            ArffSaver saver = new ArffSaver();
            saver.setInstances(is);
            File fp = new File(msExperimentPaths[i] + expName + File.separator + expName + ".arff");
            saver.setFile(fp);
            saver.writeBatch();
            props.setProperty("trainArff", URLDecoder.decode(fp.getAbsolutePath()));
            props.setProperty("classIndex", String.valueOf(is.classIndex()));
            exp.datasetString = Util.propertiesToString(props);
            exp.instanceGenerator = "meka.classifiers.multilabel.meta.automekabocompact.core.instancegenerators." + String.valueOf(resampling);
            exp.instanceGeneratorArgs = "seed=" + (seed + 1) + ":" + resamplingArgs + ":seed=" + (seed + i);
            exp.attributeSelection = false;

            exp.attributeSelectionTimeout = timeLimit * 1;
            exp.tunerTimeout = timeLimit * 50;
            exp.trainTimeout = timeLimit * 5;

            exp.memory = memLimit + "m";
            exp.extraPropsString = extraArgs;

            //Setup all the extra args
            List<String> args = new LinkedList<String>();
            args.add("-experimentpath");
            args.add(msExperimentPaths[i]);
            //Make the thing
            dirToDelete.add(msExperimentPaths[i]);
            ExperimentConstructor.buildSingle("meka.classifiers.multilabel.meta.automekabocompact.core.smac.SMACExperimentConstructor", exp, args, fold, n_labels);
            
            if(nBestConfigs > 1) {
                String temporaryDirPath = msExperimentPaths[i] + expName + File.separator; //TODO make this a global
                
                Util.makePath(temporaryDirPath + configurationInfoDirPath);
                Util.initializeFile(temporaryDirPath + configurationRankingPath);
                Util.initializeFile(temporaryDirPath + configurationHashSetPath);
            }
        }
        
        final String javaExecutable = meka.classifiers.multilabel.meta.automekabo.core.Util.getJavaExecutable();

        if(!(new File(javaExecutable)).isFile() && // Windows...
          !(new File(javaExecutable + ".exe")).isFile()) {
            throw new Exception("Java executable could not be found. Please refer to \"Known Issues\" in the Auto-MEKA manual.");
        }

        Thread[] workers = new Thread[parallelRuns];

        for(int i = 0; i < parallelRuns; i++) {
            final int index = i;            
            workers[i] = new Thread(new Runnable() {
                public void run() {
                    Process mProc = null;
                    try {
                        long novelSeed = seed*seed*seed*seed;
                        ProcessBuilder pb = new ProcessBuilder(javaExecutable, "-Xmx128m", "-cp", meka.classifiers.multilabel.meta.automekabo.core.Util.getAbsoluteClasspath(), "meka.classifiers.multilabel.meta.automekabocompact.core.tools.ExperimentRunner", msExperimentPaths[index] + expName, "" + (novelSeed + index));
                        
                        pb.redirectErrorStream(true);

                        mProc = pb.start();

                        Thread killerHook = new meka.classifiers.multilabel.meta.automekabo.core.Util.ProcessKillerShutdownHook(mProc);
                        Runtime.getRuntime().addShutdownHook(killerHook);

                        BufferedReader reader = new BufferedReader(new InputStreamReader(mProc.getInputStream()));
                        String line;
                        Pattern p = Pattern.compile(".*Estimated mean quality of final incumbent config .* on test set: (-?[0-9.]+).*");
                        Pattern pint = Pattern.compile(".*mean quality of.*: (-?[0-9E.]+);.*");
                        int tried = 0;
                        double bestMetricValue = -1;
                        boolean offlineValidation = false;
                        while((line = reader.readLine()) != null) {
//                            System.out.println("******************"+line);
                            Matcher m = p.matcher(line);
                            
                            if(m.matches()) {
                                estimatedMetricValues[index] = Double.parseDouble(m.group(1));
                                if(Arrays.asList(metricsToMax).contains(metric)) {
                                    estimatedMetricValues[index] *= -1;
                                }
                            }
                            m = pint.matcher(line);
                            if(m.matches()) {
                                bestMetricValue = Double.parseDouble(m.group(1));
                                if(Arrays.asList(metricsToMax).contains(metric)) {
                                    bestMetricValue *= -1;
                                }
                            }
                            // fix nested logging...
                            if(line.matches(".*DEBUG.*") || line.matches(".*Variance is less than.*")) {
                                log.debug(line);
                            } else if(line.matches(".*INFO.*")) {
                                if(line.matches(".*ClassifierRunner.*")) {
                                    tried++;
                                    totalTried++;
                                    if(wLog != null) {
                                        String msg = "Thread " + index + ": performed " + tried + " evaluations, estimated " + metric + " " + bestMetricValue + "...";
                                        wLog.statusMessage(msg);
                                        if(tried % 10 == 0)
                                            wLog.logMessage(msg);
                                    }
                                    if(offlineValidation){
                                         String [] OutputParser = line.split(" - ");
                                         String [] MLCAlgorithmParser = OutputParser[OutputParser.length - 1].split(";");
                                         bestAlgorithms[index] = MLCAlgorithmParser[0];
                                         bestAlgorithmsParams[index] = MLCAlgorithmParser[1];
                                    }
                                }else if(line.matches(".*Now starting offline validation*.")){
                                    offlineValidation = true;
                                }
                                log.info(line);
                            } else if(line.matches(".*WARN.*")) {
                                log.warn(line);
                            } else if(line.matches(".*ERROR.*")) {
                                log.error(line);
                            } else {
                                log.info(line);
                            }
                            if(Thread.currentThread().isInterrupted()) {
                                mProc.destroy();
                                break;
                            }
                        }
                        Runtime.getRuntime().removeShutdownHook(killerHook);
                    } catch (Exception e) {
                        if(mProc != null) mProc.destroy();
                        log.error(e.getMessage(), e);
                    }
                } 
            });
            workers[i].start();
        }
        try {
            for(int i = 0; i < parallelRuns; i++) {
                workers[i].join();
            }
        } catch(InterruptedException e) {
            for(int i = 0; i < parallelRuns; i++) {
                workers[i].interrupt();
            }
            throw new InterruptedException("Auto-MEKA_BO run interrupted!");
        }
        
    
        
        String path = msExperimentPaths[0] + expName + File.separator;
        AutoMLCAlgorithm best = null;
        if(parallelRuns == 1){
            best = new AutoMLCAlgorithm(bestAlgorithms[0], bestAlgorithmsParams[0].substring(1, bestAlgorithmsParams[0].length()-1).split(", "));
        }else{
            best = this.chooseAmongSMACRuns(n_labels);         
        }

        System.out.println("Algorithm" + ":::::::::::" + best.getMLCAlg());
        System.out.println("Algorithm's Params" + ":::::::::::" + Arrays.toString(best.getMLCAlgParams()));
        
        String [] best_hps = best.getMLCAlgParams().clone();
        String [] final_best_hps = this.clearCommand(best_hps);
        best.setMLCAlgParams(final_best_hps);
        System.out.println("Final Algorithm's Params" + ":::::::::::" + Arrays.toString(best.getMLCAlgParams()));           
        
        /** Measuring the elapsed time to run the GGP. **/
        long endTime = System.currentTimeMillis();
        long differenceTime = (endTime - startTime) / 1000; 
        
        this.savingMLCResults(best, differenceTime, path);
        
        
        this.savingLogs();
        System.out.println("---------------");
        for(String f : dirToDelete){
            System.out.println("Deleting:"+f);
            this.deleteDir(new File(f));
        }        
        this.deleteDir(new File("params/"));
        
        System.exit(1);           


//        
//        System.out.println("final algorithm: "+classifierClass);
//        System.out.println("final params: "+classifierArgs.toString());
//
//        classifier = AbstractClassifier.forName(classifierClass, classifierArgs.clone());
//
//        long startTime = System.currentTimeMillis();
//        is = as.reduceDimensionality(is);
//        classifier.buildClassifier(is);
//        long stopTime = System.currentTimeMillis();
//        finalTrainTime = (stopTime - startTime) / 1000.0;
//
//        eval = new Evaluation(is);
//        eval.evaluateModel(classifier, is);
    }
    
    public String[] clearCommand(String[] commandLine){
        int count = this.countOccurences(commandLine);
        String [] fCommandLine = this.correctString(commandLine);
        
        String [] output = null;
        int j = 0;
        int i = 0;
        
        if(count < 2){
            output = fCommandLine.clone();
            return output;
        }else if(count==2){
            output = new String[fCommandLine.length-2];
            boolean threshold = false;
            while (i < commandLine.length){
                if((fCommandLine[i].equals("-threshold") && !threshold)){
                    output[j] = fCommandLine[i];
                    i++; j++;
                    output[j] = fCommandLine[i];                      
                    i++; j++;      
                    threshold = true;
                }else if((fCommandLine[i].equals("-threshold") && threshold)){
                    i++; 
                    i++;        
                }else {
                    output[j] = fCommandLine[i];
                    i++; j++;                    
                } 
            }    

        }
    return output;

    }
    
    public String[] correctString(String[] commandLine){
        String [] output = new String[commandLine.length];
        for (int i = 0; i < commandLine.length; i++) {
            if (commandLine[i] != null) {
                if (commandLine[i].startsWith("PCut1")) {
                    output[i] = "PCut1";
                } else if (commandLine[i].startsWith("PCutL")) {
                    output[i] = "PCutL";
                } else {
                    output[i] = commandLine[i];
                }
            }
        }
        return output;
    }    
        
    public int countOccurences(String[] commandLine){
        String search = "-threshold";
        int count = 0;
        for (int i = 0; i < commandLine.length; i++) {
            if (commandLine[i] != null) {
                if (commandLine[i].equals(search)) {
                    count++;
                }
                
                
            }
        }
        return count;
    }
             
    
    public void savingLogs() throws FileNotFoundException, IOException{
        StringBuffer logs = null;
        String line = "";
        BufferedReader br = null;
        BufferedWriter bw = null;
        new File(this.savingDir + "results-" + this.name + File.separator).mkdirs();
        logs = new StringBuffer();        
        for(int i=0; i < this.parallelRuns; i++){
            String filePath = msExperimentPaths[i] + expName + File.separator + "trajetory.txt";
            logs.append("###########THREAD ").append((i+1)).append("###########\n");
            br = new BufferedReader(new FileReader(filePath));
            
            while((line=br.readLine())!= null){
                logs.append(line).append("\n");
            } 
            br.close();
            System.gc();            
        }
        
        bw = new BufferedWriter(new FileWriter(this.savingDir + "results-" + this.name + File.separator + "trajectory.txt", true));
        bw.write(logs.toString());
        bw.close();
        
        logs = new StringBuffer();
        for(int i=0; i < this.parallelRuns; i++){
            String filePath = msExperimentPaths[i] + expName + File.separator + "out" + File.separator + "automeka" + File.separator + "traj-run-"+ (this.seed + i) +".txt";
            logs.append("###########THREAD ").append((i+1)).append("###########\n");
            br = new BufferedReader(new FileReader(filePath));
            
            while((line=br.readLine())!= null){
                logs.append(line).append("\n");
            } 
            br.close();
            System.gc();            
        }
        
        bw = new BufferedWriter(new FileWriter(this.savingDir + "results-" + this.name + File.separator + "traj-runs.txt", true));
        bw.write(logs.toString());
        bw.close();       
        logs = new StringBuffer();
        for(int i=0; i < this.parallelRuns; i++){
            String filePath = msExperimentPaths[i] + expName + File.separator + "out" + File.separator + "automeka" + File.separator + "log-warn"+ (this.seed + i) +".txt";
            logs.append("###########THREAD ").append((i+1)).append("###########\n");
            br = new BufferedReader(new FileReader(filePath));
            
            while((line=br.readLine())!= null){
                logs.append(line).append("\n");
            } 
            br.close();
            System.gc();            
        }
        
        bw = new BufferedWriter(new FileWriter(this.savingDir + "results-" + this.name + File.separator + "log-warn.txt", true));
        bw.write(logs.toString());
        bw.close();          
    }
    
    
    public void savingMLCResults(AutoMLCAlgorithm best, long differenceTime, String path) throws IOException, Exception {
        Results gr = AlgorithmEvaluation.testAlgorithm(best, path, this.trainingDirectory, this.testingDirectory, "Learning.arff", "Validation.arff", this.seed, Integer.MAX_VALUE);

        /**
         * It saves the results in specific files and folders. *
         */
        new File(this.savingDir + "results-" + this.name + File.separator).mkdirs();
        BufferedWriter bf0 = new BufferedWriter(new FileWriter(this.savingDir + "results-" + this.name + File.separator + this.getFold() + "Estatisticas-" + this.name + ".csv", true));
        BufferedWriter bf1 = new BufferedWriter(new FileWriter(this.savingDir + "results-" + this.name + File.separator + this.getFold() + "EstatisticasCompacto-" + this.name + ".csv", true));

        System.gc();

        /**
         * Generated algorithm and performance measures on learning, validation,
         * full-training and testing sets. 
        *
         */
        String algorithm = gr.getAlgorithm();
        double accuracy_FullTraining = gr.getAccuracy_FullTraining();
        double accuracy_Test = gr.getAccuracy_Test();
        double accuracy_Training = gr.getAccuracy_Training();
        double accuracy_Validation = gr.getAccuracy_Validation();

        double hammingScore_FullTraining = gr.getHammingScore_FullTraining();
        double hammingScore_Test = gr.getHammingScore_Test();
        double hammingScore_Training = gr.getHammingScore_Training();
        double hammingScore_Validation = gr.getHammingScore_Validation();

        double exactMatch_FullTraining = gr.getExactMatch_FullTraining();
        double exactMatch_Test = gr.getExactMatch_Test();
        double exactMatch_Training = gr.getExactMatch_Training();
        double exactMatch_Validation = gr.getExactMatch_Validation();

        double jaccardDistance_FullTraining = gr.getJaccardDistance_FullTraining();
        double jaccardDistance_Test = gr.getJaccardDistance_Test();
        double jaccardDistance_Training = gr.getJaccardDistance_Training();
        double jaccardDistance_Validation = gr.getJaccardDistance_Validation();

        double hammingLoss_FullTraining = gr.getHammingLoss_FullTraining();
        double hammingLoss_Test = gr.getHammingLoss_Test();
        double hammingLoss_Training = gr.getHammingLoss_Training();
        double hammingLoss_Validation = gr.getHammingLoss_Validation();

        double zeroOneLoss_FullTraining = gr.getZeroOneLoss_FullTraining();
        double zeroOneLoss_Test = gr.getZeroOneLoss_Test();
        double zeroOneLoss_Training = gr.getZeroOneLoss_Training();
        double zeroOneLoss_Validation = gr.getZeroOneLoss_Validation();

        double harmonicScore_FullTraining = gr.getHarmonicScore_FullTraining();
        double harmonicScore_Test = gr.getHarmonicScore_Test();
        double harmonicScore_Training = gr.getHarmonicScore_Training();
        double harmonicScore_Validation = gr.getHarmonicScore_Validation();

        double oneError_FullTraining = gr.getOneError_FullTraining();
        double oneError_Test = gr.getOneError_Test();
        double oneError_Training = gr.getOneError_Training();
        double oneError_Validation = gr.getOneError_Validation();

        double rankLoss_FullTraining = gr.getRankLoss_FullTraining();
        double rankLoss_Test = gr.getRankLoss_Test();
        double rankLoss_Training = gr.getRankLoss_Training();
        double rankLoss_Validation = gr.getRankLoss_Validation();

        double avgPrecision_FullTraining = gr.getAvgPrecision_FullTraining();
        double avgPrecision_Test = gr.getAvgPrecision_Test();
        double avgPrecision_Training = gr.getAvgPrecision_Training();
        double avgPrecision_Validation = gr.getAvgPrecision_Validation();

        double microPrecision_FullTraining = gr.getMicroPrecision_FullTraining();
        double microPrecision_Test = gr.getMicroPrecision_Test();
        double microPrecision_Training = gr.getMicroPrecision_Training();
        double microPrecision_Validation = gr.getMicroPrecision_Validation();

        double microRecall_FullTraining = gr.getMicroRecall_FullTraining();
        double microRecall_Test = gr.getMicroRecall_Test();
        double microRecall_Training = gr.getMicroRecall_Test();
        double microRecall_Validation = gr.getMicroRecall_Validation();

        double macroPrecision_FullTraining = gr.getMacroPrecision_FullTraining();
        double macroPrecision_Test = gr.getMacroPrecision_Test();
        double macroPrecision_Training = gr.getMacroPrecision_Training();
        double macroPrecision_Validation = gr.getMacroPrecision_Validation();

        double macroRecall_FullTraining = gr.getMacroRecall_FullTraining();
        double macroRecall_Test = gr.getMacroRecall_Test();
        double macroRecall_Training = gr.getMacroRecall_Test();
        double macroRecall_Validation = gr.getMacroRecall_Validation();

        double f1MicroAveraged_FullTraining = gr.getF1MicroAveraged_FullTraining();
        double f1MicroAveraged_Test = gr.getF1MicroAveraged_Test();
        double f1MicroAveraged_Training = gr.getF1MicroAveraged_Training();
        double f1MicroAveraged_Validation = gr.getF1MicroAveraged_Validation();

        double f1MacroAveragedExample_FullTraining = gr.getF1MacroAveragedExample_FullTraining();
        double f1MacroAveragedExample_Test = gr.getF1MacroAveragedExample_Test();
        double f1MacroAveragedExample_Training = gr.getF1MacroAveragedExample_Training();
        double f1MacroAveragedExample_Validation = gr.getF1MacroAveragedExample_Validation();;

        double f1MacroAveragedLabel_FullTraining = gr.getF1MacroAveragedLabel_FullTraining();
        double f1MacroAveragedLabel_Test = gr.getF1MacroAveragedLabel_Test();
        double f1MacroAveragedLabel_Training = gr.getF1MacroAveragedLabel_Training();
        double f1MacroAveragedLabel_Validation = gr.getF1MacroAveragedLabel_Validation();

        double aurcMacroAveraged_FullTraining = gr.getAurcMacroAveraged_FullTraining();
        double aurcMacroAveraged_Test = gr.getAurcMacroAveraged_Test();
        double aurcMacroAveraged_Training = gr.getAurcMacroAveraged_Training();
        double aurcMacroAveraged_Validation = gr.getAurcMacroAveraged_Validation();

        double aurocMacroAveraged_FullTraining = gr.getAurocMacroAveraged_FullTraining();
        double aurocMacroAveraged_Test = gr.getAurocMacroAveraged_Test();
        double aurocMacroAveraged_Training = gr.getAurocMacroAveraged_Training();
        double aurocMacroAveraged_Validation = gr.getAurocMacroAveraged_Validation();

        double emptyLabelvectorsPredicted_FullTraining = gr.getEmptyLabelvectorsPredicted_FullTraining();
        double emptyLabelvectorsPredicted_Test = gr.getEmptyLabelvectorsPredicted_Test();
        double emptyLabelvectorsPredicted_Training = gr.getEmptyLabelvectorsPredicted_Training();
        double emptyLabelvectorsPredicted_Validation = gr.getEmptyLabelvectorsPredicted_Validation();

        double labelCardinalityPredicted_FullTraining = gr.getLabelCardinalityPredicted_FullTraining();
        double labelCardinalityPredicted_Test = gr.getLabelCardinalityPredicted_Test();
        double labelCardinalityPredicted_Training = gr.getLabelCardinalityPredicted_Training();
        double labelCardinalityPredicted_Validation = gr.getLabelCardinalityPredicted_Validation();

        double levenshteinDistance_FullTraining = gr.getLevenshteinDistance_FullTraining();
        double levenshteinDistance_Test = gr.getLevenshteinDistance_Test();
        double levenshteinDistance_Training = gr.getLevenshteinDistance_Training();
        double levenshteinDistance_Validation = gr.getLevenshteinDistance_Validation();

        double labelCardinalityDifference_FullTraining = gr.getLabelCardinalityDifference_FullTraining();
        double labelCardinalityDifference_Test = gr.getLabelCardinalityDifference_Test();
        double labelCardinalityDifference_Training = gr.getLabelCardinalityDifference_Training();
        double labelCardinalityDifference_Validation = gr.getLabelCardinalityDifference_Validation();

        /**
         * It generates a file with all results. *
         */
        try {

            bf0.write(this.getSeed() + ";" + this.getFold() + ";" + this.totalTried + ";" + differenceTime + ";" + accuracy_FullTraining + ";" + accuracy_Test + ";" + accuracy_Training + ";" + accuracy_Validation + ";"
                    + hammingScore_FullTraining + ";" + hammingScore_Test + ";" + hammingScore_Training + ";" + hammingScore_Validation + ";"
                    + exactMatch_FullTraining + ";" + exactMatch_Test + ";" + exactMatch_Training + ";" + exactMatch_Validation + ";"
                    + jaccardDistance_FullTraining + ";" + jaccardDistance_Test + ";" + jaccardDistance_Training + ";" + jaccardDistance_Validation + ";"
                    + hammingLoss_FullTraining + ";" + hammingLoss_Test + ";" + hammingLoss_Training + ";" + hammingLoss_Validation + ";"
                    + zeroOneLoss_FullTraining + ";" + zeroOneLoss_Test + ";" + zeroOneLoss_Training + ";" + zeroOneLoss_Validation + ";"
                    + harmonicScore_FullTraining + ";" + harmonicScore_Test + ";" + harmonicScore_Training + ";" + harmonicScore_Validation + ";"
                    + oneError_FullTraining + ";" + oneError_Test + ";" + oneError_Training + ";" + oneError_Validation + ";"
                    + rankLoss_FullTraining + ";" + rankLoss_Test + ";" + rankLoss_Training + ";" + rankLoss_Validation + ";"
                    + avgPrecision_FullTraining + ";" + avgPrecision_Test + ";" + avgPrecision_Training + ";" + avgPrecision_Validation + ";"
                    + microPrecision_FullTraining + ";" + microPrecision_Test + ";" + microPrecision_Training + ";" + microPrecision_Validation + ";"
                    + microRecall_FullTraining + ";" + microRecall_Test + ";" + microRecall_Training + ";" + microRecall_Validation + ";"
                    + macroPrecision_FullTraining + ";" + macroPrecision_Test + ";" + macroPrecision_Training + ";" + macroPrecision_Validation + ";"
                    + macroRecall_FullTraining + ";" + macroRecall_Test + ";" + macroRecall_Training + ";" + macroRecall_Validation + ";"
                    + f1MicroAveraged_FullTraining + ";" + f1MicroAveraged_Test + ";" + f1MicroAveraged_Training + ";" + f1MicroAveraged_Validation + ";"
                    + f1MacroAveragedExample_FullTraining + ";" + f1MacroAveragedExample_Test + ";" + f1MacroAveragedExample_Training + ";" + f1MacroAveragedExample_Validation + ";"
                    + f1MacroAveragedLabel_FullTraining + ";" + f1MacroAveragedLabel_Test + ";" + f1MacroAveragedLabel_Training + ";" + f1MacroAveragedLabel_Validation + ";"
                    + aurcMacroAveraged_FullTraining + ";" + aurcMacroAveraged_Test + ";" + aurcMacroAveraged_Training + ";" + aurcMacroAveraged_Validation + ";"
                    + aurocMacroAveraged_FullTraining + ";" + aurocMacroAveraged_Test + ";" + aurocMacroAveraged_Training + ";" + aurocMacroAveraged_Validation + ";"
                    + emptyLabelvectorsPredicted_FullTraining + ";" + emptyLabelvectorsPredicted_Test + ";" + emptyLabelvectorsPredicted_Training + ";" + emptyLabelvectorsPredicted_Validation + ";"
                    + labelCardinalityPredicted_FullTraining + ";" + labelCardinalityPredicted_Test + ";" + labelCardinalityPredicted_Training + ";" + labelCardinalityPredicted_Validation + ";"
                    + levenshteinDistance_FullTraining + ";" + levenshteinDistance_Test + ";" + levenshteinDistance_Training + ";" + levenshteinDistance_Validation + ";"
                    + labelCardinalityDifference_FullTraining + ";" + labelCardinalityDifference_Test + ";" + labelCardinalityDifference_Training + ";" + labelCardinalityDifference_Validation
                    + ";" + ";" + algorithm);
            bf0.newLine();

            /**
             * It generates a file with the compacted results results. *
             */
            bf1.write(this.getSeed() + ";" + this.getFold() + ";" + this.totalTried + ";" + differenceTime + ";"
                    + exactMatch_FullTraining + ";" + exactMatch_Test + ";" + exactMatch_Training + ";" + exactMatch_Validation + ";"
                    + hammingLoss_FullTraining + ";" + hammingLoss_Test + ";" + hammingLoss_Training + ";" + hammingLoss_Validation + ";"
                    + rankLoss_FullTraining + ";" + rankLoss_Test + ";" + rankLoss_Training + ";" + rankLoss_Validation + ";"
                    + f1MacroAveragedLabel_FullTraining + ";" + f1MacroAveragedLabel_Test + ";" + f1MacroAveragedLabel_Training + ";" + f1MacroAveragedLabel_Validation
                    + ";" + ";" + algorithm);

            bf1.newLine();
        } catch (Exception e) {
            System.out.println(e);
        }
        bf0.close();
        bf1.close();
        
        System.out.println("############DONE - TESTING########");      
    }
    
    public void createParamsDir() throws IOException {
        String source = "";
        System.out.println("search space mode: "+searchSpaceMode);
        switch (searchSpaceMode) {
            case 0:
                source = "paramsminimal";
                break;
            case 1:
                source = "paramsmedium";
                break;
            case 2:
                source = "paramslarge";
                break;
            default:
                source = "paramslarge";
                break;
        }
       
        File srcDir = new File(source);

        String destination = "params/";
        File destDir = new File(destination);
        this.copyFolder(srcDir, destDir);        
    }   

    /**
     * Code from 'how to do in Java': https://howtodoinjava.com/java/io/how-to-copy-directories-in-java/ 
     */
    public void copyFolder(File sourceFolder, File destinationFolder) throws IOException {
        //Check if sourceFolder is a directory or file
        //If sourceFolder is file; then copy the file directly to new location
        if (sourceFolder.isDirectory()) {
            //Verify if destinationFolder is already present; If not then create it
            if (!destinationFolder.exists()) {
                destinationFolder.mkdir();

            }

            //Get all files from source directory
            String files[] = sourceFolder.list();

            //Iterate over all files and copy them to destinationFolder one by one
            for (String file : files) {
                File srcFile = new File(sourceFolder, file);
                File destFile = new File(destinationFolder, file);

                //Recursive function call
                copyFolder(srcFile, destFile);
            }
        } else {
            //Copy the file content from one place to another
            Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
 
//        String [] commandLine = {"cp", "-R", "paramslarge/", "params/"};
//        ProcessBuilder pb = new ProcessBuilder(commandLine);
//        pb.redirectErrorStream(true);
//
//        //It starts the process. **/
//        Process proc = pb.start();
//        /* Clean-up */
//        proc.destroy();
//        System.gc();        
        
//    }    
    
    public void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (!Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f);
                }
            }
        }
        file.delete();
    }
          
    
    public AutoMLCAlgorithm chooseAmongSMACRuns(int n_labels) throws Exception{
        HandleFiles hf = new HandleFiles();
        
        String path = msExperimentPaths[0] + expName + File.separator;
        int newSeed = seed + parallelRuns + 1;
        
        hf.splitDataInAStratifiedWay(expName, path, newSeed, fold, n_labels);
        int trainLimit = timeLimit * 5;
        HandleAlgorithm ha = new HandleAlgorithm();
        ArrayList<AutoMLCAlgorithm> bests = new ArrayList<AutoMLCAlgorithm>();

        for(int i=0; i<parallelRuns; i++){            
            String [] options = bestAlgorithmsParams[i].substring(1, bestAlgorithmsParams[i].length()-1).split(", ");
            AutoMLCAlgorithm amlc = new AutoMLCAlgorithm(bestAlgorithms[i], options);
            bests.add(amlc);
//            System.out.println("options:: "+Arrays.toString(command));
        }
        
        ArrayList<AutoMLCAlgorithm> autoMLC = AlgorithmEvaluation.evaluateIndividuals(bests, parallelRuns, trainLimit, path, "Learning.arff", "Validation.arff");
        Collections.sort(autoMLC);
        
        for(AutoMLCAlgorithm a : autoMLC){
            System.out.println(a.getMLCAlg() + ";" + Arrays.toString(a.getMLCAlgParams()) + "--> " + a.getScore());
        }
        
        return autoMLC.get(0);
    }
    

    /**
    * Calculates the class membership for the given test instance.
    *
    * @param i the instance to be classified
    * @return predicted class
    * @throws Exception if instance could not be classified successfully
    */
    public double classifyInstance(Instance i) throws Exception {
        if(classifier == null) {
            throw new Exception("Auto-MEKA has not been run yet to get a model!");
        }
        i = as.reduceDimensionality(i);
        return classifier.classifyInstance(i);
    }

    /**
    * Calculates the class membership probabilities for the given test instance.
    *
    * @param i the instance to be classified
    * @return predicted class probability distribution
    * @throws Exception if instance could not be classified successfully.
    */
    public double[] distributionForInstance(Instance i) throws Exception {
        if(classifier == null) {
            throw new Exception("Auto-MEKA has not been run yet to get a model!");
        }
        i = as.reduceDimensionality(i);
        return classifier.distributionForInstance(i);
    }

    /**
     * Gets an enumeration describing the available options.
     *
     * @return an enumeration of all the available options.
     */
    @Override
    public Enumeration<Option> listOptions() {
        Vector<Option> result = new Vector<Option>();
        result.addElement(
            new Option("\tThe seed for the random number generator.\n" + "\t(default: " + seed + ")",
                "seed", 1, "-seed <seed>"));
        result.addElement(
            new Option("\tThe time limit for tuning in minutes (approximately).\n" + "\t(default: " + DEFAULT_TIME_LIMIT + ")",
                "timeLimit", 1, "-timeLimit <limit>"));
        result.addElement(
            new Option("\tThe memory limit for runs in MiB.\n" + "\t(default: " + DEFAULT_MEM_LIMIT + ")",
                "memLimit", 1, "-memLimit <limit>"));
        result.addElement(
            new Option("\tThe amount of best configurations to output.\n" + "\t(default: " + DEFAULT_N_BEST + ")",
                "nBestConfigs", 1, "-nBestConfigs <limit>"));
        result.addElement(
            new Option("\tThe metric to optimise.\n" + "\t(default: " + DEFAULT_METRIC + ")",
                "metric", 1, "-metric <metric>"));
        result.addElement(
            new Option("\tThe number of parallel runs. EXPERIMENTAL.\n" + "\t(default: " + DEFAULT_PARALLEL_RUNS + ")",
                "parallelRuns", 1, "-parallelRuns <runs>"));
        
        result.addElement(
            new Option("\tThe fold to perform the experiment.\n" + "\t(default: " + DEFAULT_FOLD + ")",
                "fold", 1, "-fold <runs>"));        
        //result.addElement(
        //    new Option("\tThe type of resampling used.\n" + "\t(default: " + String.valueOf(DEFAULT_RESAMPLING) + ")",
        //        "resampling", 1, "-resampling <resampling>"));
        //result.addElement(
        //    new Option("\tResampling arguments.\n" + "\t(default: " + DEFAULT_RESAMPLING_ARGS + ")",
        //        "resamplingArgs", 1, "-resamplingArgs <args>"));
        //result.addElement(
        //    new Option("\tExtra arguments.\n" + "\t(default: " + DEFAULT_EXTRA_ARGS + ")",
        //        "extraArgs", 1, "-extraArgs <args>"));

        Enumeration<Option> enu = super.listOptions();
        while (enu.hasMoreElements()) {
            result.addElement(enu.nextElement());
        }

        return result.elements();
    }

    /**
     * Returns the options of the current setup.
     *
     * @return the current options
     */
    @Override
    public String[] getOptions() {
        Vector<String> result = new Vector<String>();

        result.add("-seed");
        result.add("" + seed);
        result.add("-timeLimit");
        result.add("" + timeLimit);
        result.add("-memLimit");
        result.add("" + memLimit);
        result.add("-nBestConfigs");
        result.add("" + nBestConfigs);
        result.add("-metric");
        result.add("" + metric);
        result.add("-parallelRuns");
        result.add("" + parallelRuns);
        
        
        
        result.add("-fold");
        result.add("" + fold);
        result.add("-expName");
        result.add("" + name);    
        result.add("-savingDir");
        result.add("" + savingDir);          
        
        result.add("-searchSpaceMode");
        result.add("" + searchSpaceMode);        
        
        //result.add("-resampling");
        //result.add("" + resampling);
        //result.add("-resamplingArgs");
        //result.add("" + resamplingArgs);
        //result.add("-extraArgs");
        //result.add("" + extraArgs);

        Collections.addAll(result, super.getOptions());
        return result.toArray(new String[result.size()]);
    }

    /**
     * Set the options for the current setup.
     *
     * @param options the new options
     * @throws java.lang.Exception
     */
    @Override
    public void setOptions(String[] options) throws Exception {
        String tmpStr;

        tmpStr = Utils.getOption("seed", options);
        if (tmpStr.length() != 0) {
            seed = Integer.parseInt(tmpStr);
        }

        tmpStr = Utils.getOption("timeLimit", options);
        if (tmpStr.length() != 0) {
            timeLimit = Integer.parseInt(tmpStr);
        } else {
            timeLimit = DEFAULT_TIME_LIMIT;
        }

        tmpStr = Utils.getOption("memLimit", options);
        if (tmpStr.length() != 0) {
            memLimit = Integer.parseInt(tmpStr);
        } else {
            memLimit = DEFAULT_MEM_LIMIT;
        }

        tmpStr = Utils.getOption("nBestConfigs", options);
        if (tmpStr.length() != 0) {
            nBestConfigs = Integer.parseInt(tmpStr);
        } else {
            nBestConfigs = DEFAULT_N_BEST;
        }

        tmpStr = Utils.getOption("metric", options);
        if (tmpStr.length() != 0) {
            metric = Metric.valueOf(tmpStr);
        } else {
            metric = DEFAULT_METRIC;
        }

        tmpStr = Utils.getOption("parallelRuns", options);
        if (tmpStr.length() != 0) {
            parallelRuns = Integer.parseInt(tmpStr);
        } else {
            parallelRuns = DEFAULT_PARALLEL_RUNS;
        }
        
        tmpStr = Utils.getOption("fold", options);
        if (tmpStr.length() != 0) {
            fold = Integer.parseInt(tmpStr);
        } else {
            fold = DEFAULT_FOLD;
        }  
        
        tmpStr = Utils.getOption("expName", options);
        if (tmpStr.length() != 0) {
            name = tmpStr;
        } else {
            name = DEFAULT_NAME;
        }     
        
        tmpStr = Utils.getOption("savingDir", options);
        if (tmpStr.length() != 0) {
            savingDir = tmpStr;
        } else {
            savingDir = DEFAULT_SAVING_DIR;
        } 

        tmpStr = Utils.getOption("searchSpaceMode", options);
        if (tmpStr.length() != 0) {
            searchSpaceMode = Integer.parseInt(tmpStr);
        } else {
            searchSpaceMode = DEFAULT_SEARCH_SPACE_MODE;
        }   
        
        super.setOptions(options);
    }

    /**
     * Set the random seed.
     * @param s The random seed.
     */
    public void setSeed(int s) {
        seed = s;
    }

    /**
     * Get the random seed.
     * @return The random seed.
     */
    public int getSeed() {
        return seed;
    }

    /**
     * Returns the tip text for this property.
     * @return tip text for this property
     */
    public String seedTipText() {
        return "the seed for the random number generator (you do not usually need to change this)";
    }

    /**
     * Set the number of parallel runs.
     * @param n The number of parallel runs.
     */
    public void setParallelRuns(int n) {
        parallelRuns = n;
    }

    /**
     * Get the number of runs to do in parallel.
     * @return The number of parallel runs.
     */
    public int getParallelRuns() {
        return parallelRuns;
    }

    /**
     * Returns the tip text for this property.
     * @return tip text for this property
     */
    public String parallelRunsTipText() {
        return "the number of runs to perform in parallel EXPERIMENTAL";
    }

    /**
     * Set the metric.
     * @param m The metric.
     */
    public void setMetric(Metric m) {
        metric = m;
    }

    /**
     * Get the metric.
     * @return The metric.
     */
    public Metric getMetric() {
        return metric;
    }

    /**
     * Returns the tip text for this property.
     * @return tip text for this property
     */
    public String metricTipText() {
        return "the metric to optimise";
    }

    /**
     * Set the time limit.
     * @param tl The time limit in minutes.
     */
    public void setTimeLimit(int tl) {
        timeLimit = tl;
    }

    /**
     * Get the time limit.
     * @return The time limit in minutes.
     */
    public int getTimeLimit() {
        return timeLimit;
    }

    /**
     * Returns the tip text for this property.
     * @return tip text for this property
     */
    public String timeLimitTipText() {
        return "the time limit for tuning (in minutes)";
    }

    /**
     * Set the memory limit.
     * @param ml The memory limit in MiB.
     */
    public void setMemLimit(int ml) {
        memLimit = ml;
    }

    /**
     * Get the memory limit.
     * @return The memory limit in MiB.
     */
    public int getMemLimit() {
        return memLimit;
    }

    /**
     * Returns the tip text for this property.
     * @return tip text for this property
     */
    public String memLimitTipText() {
        return "the memory limit for runs (in MiB)";
    }

    /**
     * Set the amount of configurations that will be given as output
     * @param nbc The amount of best configurations desired by the user
     */
    public void setnBestConfigs(int nbc) {
        nBestConfigs = nbc;
    }

    /**
     * Get the memory limit.
     * @return The amount of best configurations that will be given as output
     */
    public int getnBestConfigs() {
        return nBestConfigs;
    }

    /**
     * Returns the tip text for this property.
     * @return tip text for this property
     */
    public String nBestConfigsTipText() {
        return "How many of the best configurations should be returned as output";
    }

    /**
     * Get the fold number.
     * @return The data fold number to perform the experiment
     */    
    public int getFold() {
        return fold;
    }

    /**
     * Set the fold number.
     * @param fold The fold number desired by the user
     */    
    public void setFold(int fold) {
        this.fold = fold;
    }

    public void setSearchSpaceMode(int searchSpaceMode) {
        this.searchSpaceMode = searchSpaceMode;
    }

    public int getSearchSpaceMode() {
        return searchSpaceMode;
    }
    
    
    
    

    //public void setResampling(Resampling r) {
    //    resampling = r;
    //    resamplingArgs = resamplingArgsMap.get(r);
    //}

    //public Resampling getResampling() {
    //    return resampling;
    //}

    ///**
    // * Returns the tip text for this property.
    // * @return tip text for this property
    // */
    //public String ResamplingTipText() {
    //    return "the type of resampling";
    //}

    //public void setResamplingArgs(String args) {
    //    resamplingArgs = args;
    //}

    //public String getResamplingArgs() {
    //    return resamplingArgs;
    //}

    ///**
    // * Returns the tip text for this property.
    // * @return tip text for this property
    // */
    //public String resamplingArgsTipText() {
    //    return "resampling arguments";
    //}

    //public void setExtraArgs(String args) {
    //    extraArgs = args;
    //}

    //public String getExtraArgs() {
    //    return extraArgs;
    //}

    ///**
    // * Returns the tip text for this property.
    // * @return tip text for this property
    // */
    //public String extraArgsTipText() {
    //    return "extra arguments";
    //}

    /** Set the WEKA logger.
     * Used for providing feedback during execution.
     *
     * @param log The logger.
     */
    public void setLog(weka.gui.Logger log) {
        this.wLog = log;
    }

    /**
     * Returns default capabilities of the classifier.
     *
     * @return      the capabilities of this classifier
     */
    public Capabilities getCapabilities() {
        Capabilities result = super.getCapabilities();
        result.disableAll();

        // attributes
        result.enable(Capability.NOMINAL_ATTRIBUTES);
        result.enable(Capability.NUMERIC_ATTRIBUTES);
        result.enable(Capability.DATE_ATTRIBUTES);
        result.enable(Capability.STRING_ATTRIBUTES);
        result.enable(Capability.RELATIONAL_ATTRIBUTES);
        result.enable(Capability.MISSING_VALUES);

        // class
        result.enable(Capability.NOMINAL_CLASS);
        result.enable(Capability.NUMERIC_CLASS);
        result.enable(Capability.DATE_CLASS);
        result.enable(Capability.MISSING_CLASS_VALUES);

        // instances
        result.setMinimumNumberInstances(1);

        return result;
    }

    /**
     * Returns an instance of a TechnicalInformation object, containing
     * detailed information about the technical background of this class,
     * e.g., paper reference or book this class is based on.
     *
     * @return the technical information about this class
     */
    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation result = new TechnicalInformation(Type.INPROCEEDINGS);
        result.setValue(Field.AUTHOR, "Chris Thornton, Frank Hutter, Holger Hoos, and Kevin Leyton-Brown");
        result.setValue(Field.YEAR, "2013");
        result.setValue(Field.TITLE, "Auto-WEKA: Combined Selection and Hyperparameter Optimization of Classifiaction Algorithms");
        result.setValue(Field.BOOKTITLE, "Proc. of KDD 2013");

        return result;
    }

    /**
     * This will return a string describing the classifier.
     * @return The string.
     */
    public String globalInfo() {
        return "Automatically finds the best model with its best parameter settings for a given dataset.\n\n"
            + "For more information see:\n\n"
            + getTechnicalInformation().toString();
    }

    /**
     * This will return a string describing the classifier.
     * @return The string.
     */
    public String toString() {
        String res = "best classifier: " + classifierClass + "\n" +
            "arguments: " + (classifierArgs != null ? Arrays.toString(classifierArgs) : "[]") + "\n" +
            "attribute search: " + attributeSearchClass + "\n" +
            "attribute search arguments: " + (attributeSearchArgs != null ? Arrays.toString(attributeSearchArgs) : "[]") + "\n" +
            "attribute evaluation: " + attributeEvalClass + "\n" +
            "attribute evaluation arguments: " + (attributeEvalArgs != null ? Arrays.toString(attributeEvalArgs) : "[]") + "\n" +
            "metric: " + metric + "\n" +
            "estimated " + metric + ": " + estimatedMetricValue + "\n" +
            "training time on evaluation dataset: " + finalTrainTime + " seconds\n\n";

        res += "You can use the chosen classifier in your own code as follows:\n\n";
        if(attributeSearchClass != null || attributeEvalClass != null) {
            res += "AttributeSelection as = new AttributeSelection();\n";
            if(attributeSearchClass != null) {
                res += "ASSearch asSearch = ASSearch.forName(\"" + attributeSearchClass + "\", new String[]{";
                if(attributeSearchArgs != null) {
                    String[] args = attributeSearchArgs.clone();
                    for(int i = 0; i < args.length; i++) {
                        res += "\"" + args[i] + "\"";
                        if(i < args.length - 1) res += ", ";
                    }
                }
                res += "});\n";
                res += "as.setSearch(asSearch);\n";
            }

            if(attributeEvalClass != null) {
                res += "ASEvaluation asEval = ASEvaluation.forName(\"" + attributeEvalClass + "\", new String[]{";
                if(attributeEvalArgs != null) {
                    String[] args = attributeEvalArgs.clone();
                    for(int i = 0; i < args.length; i++) {
                        res += "\"" + args[i] + "\"";
                        if(i < args.length - 1) res += ", ";
                    }
                }
                res += "});\n";
                res += "as.setEvaluator(asEval);\n";
            }
            res += "as.SelectAttributes(instances);\n";
            res += "instances = as.reduceDimensionality(instances);\n";
        }

        res += "Classifier classifier = AbstractClassifier.forName(\"" + classifierClass + "\", new String[]{";
        if(classifierArgs != null) {
            String[] args = classifierArgs.clone();
            for(int i = 0; i < args.length; i++) {
                res += "\"" + args[i] + "\"";
                if(i < args.length - 1) res += ", ";
            }
        }
        res += "});\n";
        res += "classifier.buildClassifier(instances);\n\n";

        try {
            res += eval.toSummaryString();
            res += "\n";
            res += eval.toMatrixString();
            res += "\n";
            res += eval.toClassDetailsString();
        } catch(Exception e) { /*TODO treat*/ }


        if(nBestConfigs > 1) {

            if(bestConfigsCollection==null){
                res += "\n\n------- BEST CONFIGURATIONS -------";
                res+= "\nEither your dataset is so large or the runtime is so short that we couldn't evaluate even a single fold";
                res+= "\nof your dataset within the given time constraints. Please, consider running Auto-MEKA for a longer time.";
            }else{
                List<Configuration> bccAL = bestConfigsCollection.asArrayList();
                int fullyEvaluatedAmt = bestConfigsCollection.getFullyEvaluatedAmt();
                int maxFoldEvaluationAmt = bccAL.get(0).getEvaluationAmount();

                res += "\n\n------- " + fullyEvaluatedAmt + " BEST CONFIGURATIONS -------";
                res += "\n\nThese are the " + fullyEvaluatedAmt + " best configurations, as ranked by SMAC";
                res += "\nPlease note that this list only contains configurations evaluated on at least "+maxFoldEvaluationAmt+" folds,";
                if(maxFoldEvaluationAmt<10){
                    res+= "\nWhich is less than 10 because that was the largest amount of folds we could evaluate for a single configuration";
                    res+= "\nunder the given time constraints. If you want us to evaluate more folds (recommended), or if you need more configurations,";
                    res +="\nplease consider running Auto-MEKA for a longer time.";
                }else{
                    res += "\nIf you need more configurations, please consider running Auto-MEKA for a longer time.";
                }
                for(int i = 0; i < fullyEvaluatedAmt; i++){
                    res += "\n\nConfiguration #" + (i + 1) + ":\nSMAC Score: " + bccAL.get(i).getAverageScore() + "\nArgument String:\n" + bccAL.get(i).getArgStrings();
                }
            }
            res+="\n\n----END OF CONFIGURATION RANKING----\n";
        }

        res += "\nTemporary run directories:\n";
        for(int i = 0; i < msExperimentPaths.length; i++) {
            res += msExperimentPaths[i] + "\n";
        }

        res += "\n\nFor better performance, try giving Auto-MEKA more time.\n";
        if(totalTried < 1000) {
            res += "Tried " + totalTried + " configurations; to get good results reliably you may need to allow for trying thousands of configurations.\n";
        }
        return res;
    }

    /**
     * Returns the metric value estimated during Auto-MEKA's internal evaluation.
     * @return The estimated metric value.
     */
    public double measureEstimatedMetricValue() {
        return estimatedMetricValue;
    }

    /**
    * Returns an enumeration of the additional measure names
    * @return an enumeration of the measure names
    */
    public Enumeration enumerateMeasures() {
        Vector newVector = new Vector(1);
        newVector.addElement("measureEstimatedMetricValue");
        return newVector.elements();
    }

    /**
    * Returns the value of the named measure
    * @param additionalMeasureName the name of the measure to query for its value
    * @return the value of the named measure
    * @throws IllegalArgumentException if the named measure is not supported
    */
    public double getMeasure(String additionalMeasureName) {
        if (additionalMeasureName.compareToIgnoreCase("measureEstimatedMetricValue") == 0) {
            return measureEstimatedMetricValue();
        } else {
            throw new IllegalArgumentException(additionalMeasureName
                    + " not supported (Auto-MEKA)");
        }
  }
    
    
  
    /**
     * A static class to process each individual.
     */
    private static class ProcessedIndividual extends Thread implements Runnable {

        protected String expName;
        protected long seed;
        protected int index;
        protected String javaExecutable;
        protected double[] estimatedMetricValues;
        protected Metric[] metricsToMax;
        protected Metric metric;
        protected String[] bestAlgorithms;
        protected String[] bestAlgorithmsParams;
        protected int totalTried;
        protected Logger log;
        protected weka.gui.Logger wLog;
        protected ProcessBuilder pb;
        protected Process mProc;
        protected BufferedReader reader;
        protected int id;
        protected Thread killerHook;
        

        public ProcessedIndividual(String expName, long seed, int index, String javaExecutable, double[] estimatedMetricValues, Metric[] metricsToMax, Metric metric, 
                                   String[] bestAlgorithms, String[] bestAlgorithmsParams, int totalTried, Logger log, weka.gui.Logger wLog, ProcessBuilder pb,
                                   Process mProc, BufferedReader reader, int id, Thread killerHook) {
            this.expName = expName;
            this.seed = seed;
            this.index = index;
            this.javaExecutable = javaExecutable;
            this.estimatedMetricValues = estimatedMetricValues;
            this.metricsToMax = metricsToMax;
            this.metric = metric;
            this.bestAlgorithms = bestAlgorithms;
            this.bestAlgorithmsParams = bestAlgorithmsParams;
            this.totalTried = totalTried;
            this.log = log;
            this.wLog = wLog;
            this.pb = pb;
            this.reader = reader;
            this.id = id;
        }
        
        

        


        
        public void run() {
            
            try {
                

                pb.redirectErrorStream(true);

                mProc = pb.start();
                
                if(id==0){
                    killerHook = new meka.classifiers.multilabel.meta.automekabo.core.Util.ProcessKillerShutdownHook(mProc);
                    Runtime.getRuntime().addShutdownHook(killerHook);                    
                }


                reader = new BufferedReader(new InputStreamReader(mProc.getInputStream()));
                String line;
                Pattern p = Pattern.compile(".*Estimated mean quality of final incumbent config .* on test set: (-?[0-9.]+).*");
                Pattern pint = Pattern.compile(".*mean quality of.*: (-?[0-9E.]+);.*");
                int tried = 0;
                double bestMetricValue = -1;
                boolean offlineValidation = false;
                
                
                while ((line = reader.readLine()) != null) {
//                            System.out.println("******************"+line);
                    Matcher m = p.matcher(line);

                    if (m.matches()) {
                        estimatedMetricValues[index] = Double.parseDouble(m.group(1));
                        if (Arrays.asList(metricsToMax).contains(metric)) {
                            estimatedMetricValues[index] *= -1;
                        }
                    }
                    m = pint.matcher(line);
                    if (m.matches()) {
                        bestMetricValue = Double.parseDouble(m.group(1));
                        if (Arrays.asList(metricsToMax).contains(metric)) {
                            bestMetricValue *= -1;
                        }
                    }
                    // fix nested logging...
                    if (line.matches(".*DEBUG.*") || line.matches(".*Variance is less than.*")) {
                        log.debug(line);
                    } else if (line.matches(".*INFO.*")) {
                        if (line.matches(".*ClassifierRunner.*")) {
                            tried++;
                            totalTried++;
                            if (wLog != null) {
                                String msg = "Thread " + index + ": performed " + tried + " evaluations, estimated " + metric + " " + bestMetricValue + "...";
                                wLog.statusMessage(msg);
                                if (tried % 10 == 0) {
                                    wLog.logMessage(msg);
                                }
                            }
                            if (offlineValidation) {
                                String[] OutputParser = line.split(" - ");
                                String[] MLCAlgorithmParser = OutputParser[OutputParser.length - 1].split(";");
                                bestAlgorithms[index] = MLCAlgorithmParser[0];
                                bestAlgorithmsParams[index] = MLCAlgorithmParser[1];
                            }
                        } else if (line.matches(".*Now starting offline validation*.")) {
                            offlineValidation = true;
                        }
                        log.info(line);
                    } else if (line.matches(".*WARN.*")) {
                        log.warn(line);
                    } else if (line.matches(".*ERROR.*")) {
                        log.error(line);
                    } else {
                        log.info(line);
                    }
                    if (Thread.currentThread().isInterrupted()) {
                        mProc.destroy();
                        break;
                    }
                }
                
                if(id==1){
                    Runtime.getRuntime().removeShutdownHook(killerHook);
                }
                
            } catch (Exception e) {
                if (mProc != null) {
                    mProc.destroy();
                }
                log.error(e.getMessage(), e);
            }
        }    

        
 
    }    
}
