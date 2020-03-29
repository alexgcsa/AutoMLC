package meka.classifiers.multilabel.meta.automekabo.core;

import java.io.BufferedReader;
import weka.classifiers.Evaluation;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.evaluation.output.prediction.CSV;
import weka.core.Instances;
import weka.core.Instance;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;
import weka.attributeSelection.AttributeSelection;
import java.util.Map;
import java.util.Arrays;
import meka.classifiers.multilabel.AbstractMultiLabelClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static meka.classifiers.multilabel.meta.AutoMEKA_BO_Large.configurationRankingPath;
import static meka.classifiers.multilabel.meta.AutoMEKA_BO_Large.configurationInfoDirPath;
import static meka.classifiers.multilabel.meta.AutoMEKA_BO_Large.configurationHashSetPath;
import weka.classifiers.Classifier;


/**
 * Class that is responsible for actually running a WEKA classifier from start to finish using the Auto-WEKA argument format.
 *
 * Note that this class can leak memory epically bad if it 'terminates' the classifier, so this should always be called in
 * a sub process from your main work to prevent memouts
 */
public class ClassifierRunner
{
    final Logger log = LoggerFactory.getLogger(ClassifierRunner.class);

    //private ParameterRegularizer mRegularizer = null;
    private InstanceGenerator mInstanceGenerator = null;
    private boolean mTestOnly = false;
    private boolean mDisableOutput = false;
    private java.io.PrintStream mSavedOutput = null;
    private String mPredictionsFileName = null;
    private Properties mProps;

    /**
     * Prepares a runner with the specified properties.
     *
     * Importantly, you must define 'instanceGenerator' and 'datasetString', while optional properties are 'verbose', 'onlyTest' and 'disableOutput'
     *
     * @param props Properties to set.
     */
    public ClassifierRunner(Properties props)
    {
        mProps = props;
        //Get the instnace generator
        mInstanceGenerator = InstanceGenerator.create(props.getProperty("instanceGenerator"), props.getProperty("datasetString"));
        //Get the regularizer - experimental to the point of not working
        //mRegularizer = ParameterRegularizer.create(props.getProperty("regularizer"), props.getProperty("regularizerParameterFileName"), props.getProperty("regularizerParams"));

        mTestOnly = Boolean.valueOf(props.getProperty("onlyTest", "false"));
        mDisableOutput = Boolean.valueOf(props.getProperty("disableOutput", "false"));
        mPredictionsFileName = props.getProperty("predictionsFileName", null);
    }

    /*
     * Kind of a hack, since this lets us look at what instances we should be running
     */
    public InstanceGenerator getInstanceGenerator(){
        return mInstanceGenerator;
    }

    /** Wrapper method on the runner thread so we can be doubly sure we terminate when we should */
    private class RunnerThread extends WorkerThread
    {
        private String instanceStr;
        private String resultMetric;
        private float timeout;
        private String mSeed;
        private List<String> args;
        public ClassifierResult result;

        public RunnerThread(String _instanceStr, String _resultMetric, float _timeout, String _mSeed, List<String> _args)
        {
            instanceStr = _instanceStr;
            resultMetric = _resultMetric;
            timeout = _timeout;
            mSeed = _mSeed;
            args = _args;
        }
        protected void doWork() throws Exception
        {
            result = _run(instanceStr, resultMetric, timeout, mSeed, args);
        }

        protected String getOpName()
        {
            return "Main Thread";
        }
    }

    /**
     * Public interface to running a classifier specified in the Auto-WEKA format of arguments to generate a classifier result
     * @param instanceStr The string describing the instances.
     * @param resultMetric The metric to use.
     * @param timeout The timeout.
     * @param mSeed The random seed.
     * @param args The list of arguments.
     * @return The evaluation result.
     */
    public ClassifierResult run(String instanceStr, String resultMetric, float timeout, String mSeed, List<String> args)
    {
        java.io.PrintStream stderr = System.err;
        System.setErr(System.out);

        RunnerThread runner = new RunnerThread(instanceStr, resultMetric, timeout, mSeed, args);
        float time = runner.runWorker(timeout * 2.05f);
        System.setErr(stderr);
        if(runner.getException() != null)
//            throw (RuntimeException)runner.getException();
            System.out.println(runner.getException().toString());
        if(runner.terminated())
        {
            ClassifierResult res = new ClassifierResult(resultMetric);
            res.setTrainingTime(time);
        }

        return runner.result;
    }

    /*
     * Have a pre-trained classifier and want to get another set of testing data out of it? Use this
     */
    public ClassifierResult evaluateClassifierOnTesting(AbstractMultiLabelClassifier classifier, String instanceStr, String resultMetric, float evaluateClassifierOnInstances)
    {
        ClassifierResult res = new ClassifierResult(resultMetric);
        res.setClassifier(classifier);
        Instances instances = mInstanceGenerator.getTestingFromParams(instanceStr);
        _evaluateClassifierOnInstances(classifier, res, instances, evaluateClassifierOnInstances,null,null);

        return res;
    }

    /*
     * Do the actual run of a classifier for AS, Training and Test
     */
    private ClassifierResult _run(String instanceStr, String resultMetric, float timeout, String mSeed, List<String> args) throws Exception
    {       
        //The first arg contains stuff we need to pass to the instance generator
        Instances training = mInstanceGenerator.getTrainingFromParams(instanceStr);
        Instances testing  = mInstanceGenerator.getTestingFromParams(instanceStr);

        //Next, start into the arguments that are for the actual classifier
        WekaArgumentConverter.Arguments wekaArgs = WekaArgumentConverter.convert(args);
        Map<String, String> propertyMap = wekaArgs.propertyMap;
        Map<String, List<String>> argMap = wekaArgs.argMap;

        //Build a result with the appropriate fields
        ClassifierResult res = new ClassifierResult(resultMetric);


        //Now work on the actual classifier
        String targetClassifierName = propertyMap.get("targetclass");

        if(targetClassifierName == null || targetClassifierName.isEmpty())
        {
            throw new RuntimeException("No target classifier name specified!");
        }

        //Compute the regularization penalty
        float regPenalty = 0;
        res.setRegularizationPenalty(regPenalty);

        //Get one of these classifiers
        String[] argsArray = argMap.get("classifier").toArray(new String[0]);
        String[] argsArraySaved = argMap.get("classifier").toArray(new String[0]);
        String path = System.getProperty("user.dir") +  File.separator;   
//        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$algorithm: "+targetClassifierName);
//        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$algorithm's parameters: "+Arrays.toString(argsArray));

        double raw = (-1.00) * this.evaluateAlgorithm(targetClassifierName, argsArray, "Learning.arff", "Validation.arff", Integer.parseInt(mSeed), (int) timeout, "testABC", path);
        res._setRawScore(raw);
        res.setCompleted(true);
        enableOutput();
        
        // write out configuration info
        log.info("{};{};{};{};{};{};{};{}",
        targetClassifierName, argsArraySaved,
        instanceStr, res.getRawScore());

        log.debug("Num Training: {}, num testing: {}", training.numInstances(), testing.numInstances());
        return res;
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
        
    
    
    /**
     * It evaluates the algorithm, represented by an indivividual.
     * @param individual the individual/algorithm to be evaluated.
     * @param learningSet the learning set to construct the model on it.
     * @param validationSet the validation set to evaluate the produced model.
     * @param seed the random seed.
     * @param timeoutLimit timeout limit for each evaluated algorithm.
     * @param experimentName the name of the experiment.
     * @return the fitness value.
     * @throws IOException
     * @throws Exception 
     */    
    public double evaluateAlgorithm(String algorithm, String[] options, String learningSet, String validationSet, long seed, int timeoutLimit, String experimentName, String path) throws IOException, Exception{
        double fitnessValue = 0.0;
        HandleAlgorithm hAlg = new HandleAlgorithm();
        String[] fullCommandAux = hAlg.generateAlgorith(timeoutLimit, algorithm, options, path, learningSet, validationSet);
        String[] fullCommand = clearCommand(fullCommandAux);
//        System.out.println(Arrays.toString(commandLine));
//        System.out.println(Arrays.toString(fullCommand));
        
        /** It creates the process. **/
        ProcessBuilder pb = new ProcessBuilder(fullCommand);
        pb.redirectErrorStream(true);

        /** It starts the process. **/
        Process proc = pb.start();
        
        /** It reads the process's output. **/
        String line;             
        BufferedReader in = new BufferedReader(new InputStreamReader(
        proc.getInputStream())); 
        StringBuilder sf = new StringBuilder();
        
        /** The metrics of the fitness formula. **/
        double outValidF1 = 0.0;
        double outValidEM = 0.0;        
        double outValidHL = 1.0;
        double outValidRL = 1.0;
        
        /** Boolean variables to control the generated output for the algorithm.. **/
        boolean validF1 = false;        
        boolean validEM = false;
        boolean validHL = false;        
        boolean validRL = false;  
        
        /** It sets the values of the metrics, if possible. **/
        while ((line = in.readLine()) != null) {
//            System.out.println("**********************************************line: "+line);
             sf.append(line).append("\n");
           if(line.startsWith("Exact-match-test")){
                outValidEM = Double.parseDouble(line.split("=")[1]);
                validEM = true;
            }else if(line.startsWith("Hamming-loss-test")){
                outValidHL = Double.parseDouble(line.split("=")[1]);
                validHL = true;
            }else if(line.startsWith("Rank-loss-test")){
                outValidRL= Double.parseDouble(line.split("=")[1]);
                validRL = true;
            }else if(line.startsWith("F1-macro-averaged-by-label-test")){
                outValidF1= Double.parseDouble(line.split("=")[1]);
                validF1 = true;
            }
        }
        
        /** The fitness is only defined if all the metrics were calculated. **/
        if(validEM && validHL && validRL && validF1){
            double fitness = outValidEM + (1.0 - outValidHL) + (1.0 - outValidRL) + outValidF1;
            fitness = fitness/4.0;
            fitnessValue = fitness;        
        }else{
           /** Otherwise, a file showing the issue is created or appended**/
           String buffer = sf.toString();
           BufferedWriter bfw = null;
           new File("./results-"+ experimentName).mkdir();
           fitnessValue = 0.000001;
           if(buffer.isEmpty()){ 
                /** File for complexity issues, ie, the algorithm did not finish with the given timeout limit. **/
                bfw = new BufferedWriter(new FileWriter("results-"+ experimentName +"/TimeoutIssues-"+experimentName+"-"+seed+".csv", true));
                bfw.write("Complexity issues in the algorithm: "+algorithm + ";" + Arrays.toString(options) + "\n");     
           }else{   
                /** File for general issues with the executed algorithm. **/
                bfw = new BufferedWriter(new FileWriter("results-"+ experimentName +"/GeneralIssues-"+experimentName+"-"+seed+".csv", true));
                bfw.write("General issues in the algorithm: "+algorithm + ";" + Arrays.toString(options) + "\n"); 
                bfw.write(buffer + "\n");
           }
           bfw.write("=====================================================\n");
           sf = null; 
           bfw.close(); 
        }       
        
        in.close();
        /** Clean-up the process. **/
        proc.destroy();
        System.gc();
        
        BufferedWriter bw = new BufferedWriter(new FileWriter("trajetory.txt", true));
        bw.write(System.currentTimeMillis() +";"+ algorithm + ";" + Arrays.toString(options) + ";" + fitnessValue + "\n" );
        bw.close();
        
        /** It returns the fitness value. **/
        return fitnessValue;
    }    

    /*
     * Internal method that performs the evaluation of a classifier on a bunch of instances
     *
     * If true, then the training was good, otherwise it failed
     */
    private boolean _evaluateClassifierOnInstances(AbstractMultiLabelClassifier classifier, ClassifierResult res, Instances instances, float timeout,List<String> args,String instanceStr)
    {

        Evaluation eval = null;
        try
        {
            eval = new Evaluation(instances);
            EvaluatorThread evalThread = new EvaluatorThread(eval, classifier, instances, mPredictionsFileName);

            disableOutput();
            float evalTime = evalThread.runWorker(timeout);
            enableOutput();
            res.setEvaluationTime(evalTime);

            if(evalThread.getException() != null) {
                throw evalThread.getException();
            }

            log.debug("Completed evaluation on {}/{} instances.", (instances.numInstances() - eval.unclassified()), instances.numInstances());

            //Make sure that if we terminated the eval, we crap out accordingly
            res.setCompleted(!evalThread.terminated());

            res.setPercentEvaluated(100.0f*(float)(1.0f - eval.unclassified() / instances.numInstances()));
            log.debug("Percent evaluated: {}", res.getPercentEvaluated());
            //Check to make sure we evaluated enough data (and if we should log it)
            if(res.getPercentEvaluated() < 100)
            {
                res.setCompleted(false);
                log.debug("Evaluated less than 100% of the data.");
            }
            else if(!evalThread.terminated())
            {
                //We're good, we can safely report this value
                res.setScoreFromEval(eval, instances);
                saveConfiguration(res,args,instanceStr);
            }
        } catch(Exception e) {
            log.debug("Evaluating classifier failed: {}", e.getMessage(), e);
            res.setCompleted(false);
            res.setMemOut(e.getCause() instanceof OutOfMemoryError);
            return false;
        }
        log.trace(eval.toSummaryString("\nResults\n======\n", false));
        try
        {
            log.trace(eval.toMatrixString());
        }catch(Exception e)
        {
            //throw new RuntimeException("Failed to get confusion matrix", e);
        }
        log.debug(res.getDescription());

        return true;
    }

    protected void saveConfiguration(ClassifierResult res,List<String> args, String instanceStr){
      //Checking if we're doing this logging for this run of autoweka
      File sortedLog = new File(configurationRankingPath);
      if (!sortedLog.exists()){
        return;
      }

      //Setting up some basic stuff
      Configuration ciConfig = new Configuration(args);
      int ciHash             = ciConfig.hashCode();
      String ciFilename      = configurationInfoDirPath+ciHash+".xml";
      File ciFile            = new File(ciFilename);
      String configIndex     = configurationHashSetPath;

      //Computing Score and fold ID
      Properties pInstanceString = Util.parsePropertyString(instanceStr);
      int ciFold     = Integer.parseInt(pInstanceString.getProperty("fold", "-1"));
      double ciScore = res.getScore();

      //Updating the configuration data
      ciConfig.setEvaluationValues(ciScore,ciFold);

      if (ciFile.exists()){
        Configuration ciConfigFull = Configuration.fromXML(ciFilename,Configuration.class); //Find a faster way w/o IOs?
        ciConfigFull.mergeWith(ciConfig);
        ciConfigFull.toXML(ciFilename);
      }else{
        Util.initializeFile(ciFilename);
        ciConfig.toXML(ciFilename);
      }

      //Updating the configuration list
      try{
          BufferedWriter fp = new BufferedWriter(new FileWriter(configurationHashSetPath,true));//true for appending
          fp.write(ciHash+",");
          fp.flush();
          fp.close();
      }catch(IOException e){
          throw new RuntimeException("Couldn't write to configIndex");
      }

    }


    protected void disableOutput()
    {
        if(!mDisableOutput) return;
        mSavedOutput = System.out;
        System.setOut(new Util.NullPrintStream());
    }

    protected void enableOutput()
    {
        if(!mDisableOutput) return;
        System.setOut(mSavedOutput);
    }

    class BuilderThread extends WorkerThread
    {
        private AbstractClassifier mClassifier;
        private Instances mTrainInstances;

        public BuilderThread(AbstractClassifier cls, Instances inst)
        {
            mClassifier = cls;
            mTrainInstances = inst;
        }

        protected void doWork() throws Exception
        {
            mClassifier.buildClassifier(mTrainInstances);
        }

        protected String getOpName()
        {
            return "Training of classifier";
        }
    }

    public static class EvaluatorThread extends WorkerThread
    {
        private AbstractClassifier mClassifier;
        private Instances mInstances;
        private Evaluation mEval;
        private String mPredictionsFile;

        public EvaluatorThread(Evaluation ev, AbstractClassifier cls, Instances inst)
        {
            this(ev, cls, inst, null);
        }

        public EvaluatorThread(Evaluation ev, AbstractClassifier cls, Instances inst, String predictionsFile)
        {
            mEval = ev;
            mClassifier = cls;
            mInstances = inst;
            mPredictionsFile = predictionsFile;
        }
        protected void doWork() throws Exception
        {
            CSV out = null;
            StringBuffer buffer = null;
            if(mPredictionsFile != null){
                out = new CSV();
                buffer = new StringBuffer();
                out.setBuffer(buffer);
                out.setHeader(mInstances);
                out.setOutputDistribution(true);
                out.printHeader();
                mEval.evaluateModel(mClassifier, mInstances, out);
                out.printFooter();
                try{
                    BufferedWriter fp = new BufferedWriter(new FileWriter(mPredictionsFile));
                    fp.write(buffer.toString());
                    fp.flush();
                    fp.close();
                }catch(IOException e){
                    throw new RuntimeException(e);
                }
            } else {
                for (Instance instance : mInstances) {
                    mEval.evaluateModelOnceAndRecordPrediction(mClassifier, instance);
                }
            }
        }

        protected String getOpName()
        {
            return "Evaluation of classifier";
        }
    }

    class AttributeSelectorThread extends WorkerThread
    {
        private AttributeSelection mSelection;
        private Instances mInstances;

        public AttributeSelectorThread(AttributeSelection selection, Instances inst)
        {
            mInstances = inst;
            mSelection = selection;
        }

        protected void doWork() throws Exception
        {
            //Go do some training
            mSelection.SelectAttributes(mInstances);
        }

        protected String getOpName()
        {
            return "Attribute selection";
        }
    }
}
