package meka.classifiers.multilabel.meta;
//Java imports:
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

//MEKA imports:
import meka.classifiers.multilabel.meta.gaautomlc.core.MetaIndividualGA;
import meka.classifiers.multilabel.AbstractMultiLabelClassifier;
import meka.classifiers.multilabel.CC;
import meka.classifiers.multilabel.meta.gaautomlc.core.IntermediateResults4GA;
import meka.core.MLUtils;
import meka.classifiers.multilabel.meta.gaautomlc.core.xmlparser.Allele;
import meka.classifiers.multilabel.meta.gaautomlc.core.xmlparser.XMLAlgorithmHandler;
import meka.classifiers.multilabel.meta.gaautomlc.core.xmlparser.XMLGeneHandler;
import meka.core.OptionUtils;
import meka.classifiers.multilabel.meta.util.EvolutionaryUtil;
import meka.classifiers.multilabel.meta.util.DataUtil;
//Mulan imports:
import mulan.data.InvalidDataFormatException;
import mulan.data.IterativeStratification;
import mulan.data.MultiLabelInstances;
//EpochX imports:
import org.epochx.tools.random.MersenneTwisterFast;
//WEKA imports:
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;

/**
 * GAAutoMLC.java - A method for selecting and configuring multi-label
 * classification (MLC) algorithm in the MEKA software 
 * (version with multifidelity).
 *
 * GA-Auto-MLC uses a genetic algorithm aiming to find the most suitable MLC 
 * algorithm for a given dataset of interest
 *
 * @author Alex G. C. de Sa (alexgcsa@gmail.com)
 */
public class GAAutoMLC extends AbstractMultiLabelClassifier {

    //For serialization.
    private static final long serialVersionUID = -427074159411195910L;
    
    //The selected MLC algorithm.
    protected Classifier bestMLCalgorithm;    

    //Crossover rate. 
    protected double m_crossoverRate = 0.90;
    
    //Mutation rate. 
    protected double m_mutationRate = 0.10;
    
    //Number of generations. 
    protected int m_numberOfGenerations = 2;
    
    //Number of generations to resample the data. 
    protected int m_resample = 5;
    
    //Number of generations defined as fitness convergencence to reinit the population. 
    protected int m_reinit = 10;

    //The generation value to start to analyze the convergence  in terms of generation (default: 20)
    protected int m_convergenceGen = 20;
    
    //Size of the population.
    protected int m_populationSize = 15;
    
    //Size of the tournament.
    protected int m_tournamentSize = 2;  
    
    //Size of the elitism.
    protected int m_elitismSize = 1;   
    
    //Init of the number of threads.
    protected int m_numberOfThreads = 1;
    
    //Seed for random number.
    protected long m_seed = 11321;
    
    //The directory to save the results. 
    protected String m_savingDirectory = "~/";    
    
    //Training directory.
    protected String m_trainingDirectory = "training.arff";
    //Testing directory.
    protected String m_testingDirectory = "testing.arff";
    
    // Init of the fold.
    protected int m_foldInit = 0;    
    
    //A Template for Problem Transformations.
    protected Instances m_InstancesTemplate;
    
    //Timeout limit in seconds for each algorithm.
    protected int m_algorithmTimeLimit = 10;
    
    //The name of the experiment, which is useful to define the name of the folders and files.
    protected String m_experimentName = "experimentABC";
    
    //To decide if the process will be guided by generations or time
    protected boolean m_anytime = false;
    
    //The time limit for running Auto-MEKA_GGP (in minutes). 
    protected int m_generaltimeLimit = 10;
    
    //The step in the general time limit  to save intermediate results (in minutes). 
    protected int m_stepTimeLimit = 1;
    
    //The defined grammar (i.e., 0: Minimal, 1: Medium, 2: Large).
    protected int m_searchSpaceMode = 2;
    
    //Java directory.
    protected String m_javaDir = "java";
    
    //Multi-fidelity approaches.    
    //0: Exponential Attribute Selection, Polynomial Instance Selection
    //1: Polynomial Attribute Selection,  Polynomial Instance Selection
    //2: No Attribute Selection, Polynomial Instance Selection
    //3: Exponential Attribute Selection, No Instance Selection
    //4: Polynomial Attribute Selection, No Instance Selection
    //5: No Attribute Selection, No Instance Selection
    //>5: 5: No Attribute Selection, No Instance Selection
    protected int m_multifidelityMode = 5;        

    //A XML file containing the algorithms search space to build a new algorithm.
    protected File m_XMLAlgorithmsFile = null;

    /**
     * Constructor for GA-Auto-MLC.
     * @param argv the arguments of the class.
     */
    public GAAutoMLC(String[] argv) {
        super();
        resetOptions();
        this.bestMLCalgorithm = new CC();

        for (int i = 0; i < argv.length; i++) {
            if (argv[i].equals("-t")) {
                i++;
                m_trainingDirectory = argv[i];
            } else if (argv[i].equals("-T")) {
                i++;
                m_testingDirectory = argv[i];
            }
        }
    }
    
    /**
     * Reset options to default values
     */
    private void resetOptions() {
        this.m_seed = 1;
        this.m_tournamentSize = 2;
        this.m_populationSize = 13;
        this.m_numberOfGenerations = 3;
        this.m_resample = -1;
        this.m_mutationRate = 0.2;
        this.m_crossoverRate = 0.8;
        this.m_foldInit = 0;
        this.m_numberOfThreads = 1;
        this.m_experimentName = "Experiment-ABC";
        this.m_elitismSize = 5;
        this.m_trainingDirectory = "training.arff";
        this.m_testingDirectory = "testing.arff";
        this.m_algorithmTimeLimit = 10;
        this.m_generaltimeLimit = 1;
        this.m_anytime = true;
        this.m_savingDirectory = "~/";
        this.m_XMLAlgorithmsFile = new File(System.getProperty("user.dir"));
    }    
    
//########################################################################################################################## 
//###########################################CLASSIFIER OPTIONS#############################################################
//########################################################################################################################## 
   
    /**
     * The name of the method.
     * @return the name of the method.
     */
    @Override
    public String toString() {
	return "GA-Auto-MLC";
    }    
    
    
    /**
     * Parses a given list of options. 
     * @param options to be parsed.
     * @throws Exception 
     */ 
    @Override
    public void setOptions (String[] options)  throws Exception {
        super.setOptions(options);
        setTournamentSize(OptionUtils.parse(options, "K", 2 ));
        setElitismSize(OptionUtils.parse(options, "V", 1 ));
        setPopulationSize(OptionUtils.parse(options, "P", 10));
        setNumberOfGenerations(OptionUtils.parse(options, "G", 2 ));
        setResample(OptionUtils.parse(options, "R", -1));
        setReinit(OptionUtils.parse(options, "E", -1));  
        setConvergenceGen(OptionUtils.parse(options, "Eg", 20));
        setMutationRate(OptionUtils.parse(options, "M", 0.10));
        setCrossoverRate(OptionUtils.parse(options, "X", 0.90));
        setNumberOfThreads(OptionUtils.parse(options, "N", 1));
        setSeed(OptionUtils.parse(options, "H", 11321));    
        setFoldInit(OptionUtils.parse(options, "Y", 0));
        setAlgorithmTimeLimit(OptionUtils.parse(options, "L", 60));
        setExperimentName(OptionUtils.parse(options, "W", "ExperimentABC"));        
        setAnytime(Utils.getFlag("C", options));         
        setGeneraltimeLimit(OptionUtils.parse(options, "B", 10));
        setStepTimeLimit(OptionUtils.parse(options, "Bs", 1));        
        setSavingDirectory(OptionUtils.parse(options, "D", "~/"));   
        setJavaDir(OptionUtils.parse(options, "JavaDir", "java"));
        setMultifidelityMode(OptionUtils.parse(options, "MM", 5));
        setSearchSpaceMode(OptionUtils.parse(options, "O", 2));
        setXMLAlgorithmsFile(OptionUtils.parse(options, "A", "useDir"));
    }    
    
   /**
    * Gets the current settings of Auto-MEKA.
    * @return an array of strings suitable for passing to setOptions()
    */
    @Override
    public String[] getOptions(){
	List<String> result = new ArrayList<String>();
        OptionUtils.add(result, super.getOptions());
        OptionUtils.add(result, "K", this.getTournamentSize());
        OptionUtils.add(result, "V", this.getElitismSize());
        OptionUtils.add(result, "P", this.getPopulationSize());
        OptionUtils.add(result, "G", this.getNumberGenerations());
        OptionUtils.add(result, "R", this.getResample());
        OptionUtils.add(result, "E", this.getReinit());   
        OptionUtils.add(result, "Eg", this.getConvergenceGen());
        OptionUtils.add(result, "M", this.getMutationRate());
        OptionUtils.add(result, "X", this.getCrossoverRate());
        OptionUtils.add(result, "N", this.getNumberOfThreads());
        OptionUtils.add(result, "H", this.getSeed());
        OptionUtils.add(result, "Y", this.getFoldInit());
        OptionUtils.add(result, "L", this.getAlgorithmTimeLimit()); 
        OptionUtils.add(result, "W", this.getExperimentName());
        OptionUtils.add(result, "B", this.getGeneraltimeLimit());
        OptionUtils.add(result, "Bs", this.getStepTimeLimit()); 
        OptionUtils.add(result, "D", this.getSavingDirectory());
        OptionUtils.add(result, "JavaDir", this.getJavaDir());
        OptionUtils.add(result, "MM", this.getMultiFidelityMode());        
        OptionUtils.add(result, "O", this.getSearchSpaceMode());
        OptionUtils.add(result, "A", this.get_XMLAlgorithmsFile());
        if(this.getAnytime()){
            result.add("-C");
        } 
	
	return OptionUtils.toArray(result);
    }    


    /**
    * Returns an enumeration describing the available options.
    * @return an enumeration of all the available options.
    *
    **/    
    @Override
    @SuppressWarnings("unchecked")
    public Enumeration<Option> listOptions () {        
        Vector<Option> new_options = new Vector<Option>();  
        
        new_options.addElement(new Option("\n" + crossoverRateTipText(), "X", 1, "-X <value>"));
        new_options.addElement(new Option("\t" + elitismSizeTipText(), "V", 1, "-V <value>"));  
        new_options.addElement(new Option("\t" + foldInitTipText(), "Y", 1, "-Y <value>"));          
        new_options.addElement(new Option("\t" + mutationRateTipText(), "M", 1, "-M <value>"));       
        new_options.addElement(new Option("\t" + numberOfGenerationsTipText(), "G", 1, "-G <value>"));           
        new_options.addElement(new Option("\t" + numberOfThreadsTipText(), "N", 1, "-N <value>"));  
        new_options.addElement(new Option("\t" + populationSizeTipText(), "P", 1, "-P <value>"));
        new_options.addElement(new Option("\t" + resampleTipText() ,"R", 1, "-R <value>")); 
        new_options.addElement(new Option("\t" + reinitTipText() ,"E", 1, "-E <value>"));  
        new_options.addElement(new Option("\t" + convergenceGenTipText() ,"Eg", 1, "-Eg <value>")); 
        new_options.addElement(new Option("\t" + seedTipText(), "H", 1, "-H <value>"));
        new_options.addElement(new Option("\t" + timeoutLimitTipText(), "L", 1, "-L <value>"));            
        new_options.addElement(new Option("\t" + tournamentSizeTipText(), "K", 1, "-K <value>"));         
        new_options.addElement(new Option("\t" + experimentNameTipText() , "W", 1, "-W <value>"));  
        new_options.addElement(new Option("\t" + anytimeTipText() , "C", 0, "-Z"));
        new_options.addElement(new Option("\t" + generalTimeLimitTipText() , "B", 1, "-B <value>"));
        new_options.addElement(new Option("\t" + generalTimeLimitTipText() , "Bs", 1, "-Bs <value>"));
        new_options.addElement(new Option("\t" + savingDirectoryTipText(), "D", 1, "-D <value>"));
        new_options.addElement(new Option("\t" + searchSpaceModeTipText(), "O", 1, "-O <value>"));
        new_options.addElement(new Option("\t" + XMLAlgorithmsFileTipText(), "A", 1, "-A <value>"));
        OptionUtils.add(new_options, super.listOptions());      
        
        return OptionUtils.toEnumeration(new_options);
    } 
    
//########################################################################################################################## 
//###########################################TIP TEXTS - GUI################################################################
//########################################################################################################################## 

    /**
     * Description of GA-AutoMLC to display in the GUI.
     * @return	the description of GA-Auto-MLC in the GUI.
     */
    @Override
    public String globalInfo() {
        return "GA-Auto-MLC: A method for selecting and configuring multi-label "
                + "classification algorithm in the MEKA software";
    }    
    /**
     * Tip text for the crossover rate.
     * @return the tip text for the crossover rate.
     */
    public String crossoverRateTipText() {
        return "The crossover rate to be used in the evolutionary process.";
    }
    /**
     * Tip text for the elitism size.
     * @return the tip text for the elitism size.
     */   
    public String elitismSizeTipText() {
        return "The size of the elitism to be used in the evolutionary process.";
    }
    /**
     * Tip text for the fold init.
     * @return the tip text for the fold init.
     */     
    public String foldInitTipText(){
        return "The fold to init the evolutionary process, ie, the part of the dataset to search for the best algorithm.";
    }
    /**
     * Tip text for the grammar directory.
     * @return the tip text for the grammar directory.
     */      
    public String savingDirectoryTipText(){
        return "The directory to save the results of the search process.";
    }    
    /**
     * Tip text for the mutation rate.
     * @return the tip text for the mutation rate.
     */     
    public String mutationRateTipText() {
        return "The mutation rate to be used in the evolutionary process.";
    }
    /**
     * Tip text for the number of generations.
     * @return the tip text for the number of generations.
     */     
    public String numberOfGenerationsTipText() {
        return "The number of generations to be used in the evolutionary process.";
    }
    /**
     * Tip text for the number of threads.
     * @return the tip text for the number of threads.
     */      
    public String numberOfThreadsTipText() {
        return "The number of threads to evaluate the individuals (algorithms).";
    }
    /**
     * Tip text for the population size.
     * @return the tip text for the population size.
     */      
    public String populationSizeTipText() {
        return "The size of the population (number of individuals) in the evolutionary process.";
    } 
    /**
     * Tip text for the reinit value.
     * @return the tip text for the reinit value.
     */     
    public String reinitTipText() {
        return "It reinits the population in each E iterations (values lower than or equal to zero mean that no reinitilization of the population will be performed).";
    }   
    /**
     * Tip text for the convergence generation value.
     * @return the tip text for the convergence generation value.
     */     
    public String convergenceGenTipText() {
        return "The value to start looking for convergence in terms of generation.";
    }     
    /**
     * Tip text for the resample value.
     * @return the tip text for the resample value.
     */     
    public String resampleTipText() {
        return "It resamples learning and validation sets in each R iterations (values lower than or equal to zero mean that no resampling will be performed).";
    }           
    /**
     * Tip text for the random seed.
     * @return the tip text for the random seed.
     */      
    public String seedTipText() {
        return "The seed for random number generator.";
    }  
    /**
     * Tip text for the testing directory.
     * @return the tip text for the testing directory.
     */       
    public String testingDirectoryTipText() {
        return "The directory for the testing data.";
    }   
    /**
     * Tip text for the timeout limit.
     * @return the tip text for the timeout limit (in seconds).
     */     
    public String timeoutLimitTipText() {
        return "The time budget in seconds for each individual (algorithm) to be executed.";
    }   
    /**
     * Tip text for the tournament size.
     * @return the tip text for the tournament size.
     */     
    public String tournamentSizeTipText() {
        return "The size of the tournament in the evolutionary process.";
    } 
    /**
     * Tip text for the training directory.
     * @return the tip text for the training directory.
     */     
    public String trainingDirectoryTipText() {
        return "The directory for the training data.";
    }    
    /**
     * Tip text for the experiment name.
     * @return the tip text for the name of the experiment.
     */     
    public String experimentNameTipText() {
        return "The name of the experiment, which is useful to define the name of the folders and files.";
    } 
    /**
     * Tip text for the anytime behavior.
     * @return the tip text for the anytime behavior.
     */     
    public String anytimeTipText() {
        return "If true, it defines the anytime behavior, instead of the generational.";
    }  
    /**
     * Tip text for the general time limit.
     * @return the tip text for the general time limit.
     */     
    public String generalTimeLimitTipText() {
        return "It defines the general time limit (used only if anytime is set to true).";
    }   
    /**
     * Tip text for the step to get intermediate results (in minutes).
     * @return the tip text for the step to get intermediate results.
     */     
    public String stepTimeLimitTipText() {
        return "It defines the the step to get intermediate results (in minutes and used only if anytime is set to true).";
    }
    /**
     * Tip text for the search space mode.
     * @return the tip text for the search space mode.
     */     
    public String searchSpaceModeTipText() {
        return "It defines the search space mode, i.e., which search space the GGP will use to guide its search.";
    }    
    /**
     * Tip text for the XML algorithms file.
     * @return the tip text for the 
     */
    public String XMLAlgorithmsFileTipText(){
        return "It defines the directory of the file that contains where is located the search spaces.";
    }
    
//########################################################################################################################## 
//###########################################GETTERS######################################################################## 
//##########################################################################################################################     
    
    /**
     * Getter for the fold init.
     * @return the fold init.
     */
    public int getFoldInit() {
        return m_foldInit;
    }
    /**
     * Getter for the crossover rate.
     * @return the crossover rate.
     */
    public double getCrossoverRate() {
        return m_crossoverRate;
    }    
    /**
     * Getter for the mutation rate.
     * @return the mutation rate.
     */    
    public double getMutationRate() {
        return m_mutationRate;
    }
    /**
     * Getter for the number of generations.
     * @return the number of generations.
     */    
    public int getNumberGenerations() {
        return m_numberOfGenerations;
    }
    /**
     * Getter for resampling in R iterations.
     * @return the value of resampling, ie, the number of generations to wait
     *         to apply the resample.
     */ 
    public int getResample() {
        return m_resample;
    }
    /**
     * Getter for reinit in E iterations.
     * @return the value of reinit, ie, the number of generations to wait
     *         to start the population again
     */ 
    public int getReinit() {
        return m_reinit;
    }        
    /**
     * Getter for the convergence generation, i.e., the value to start looking for
     * convergence in terms of generation.
     * @return the value of convergence generation.
     */ 
    public int getConvergenceGen() {
        return m_convergenceGen;
    }     
    /**
     * Getter for the population size.
     * @return the population size.
     */ 
    public int getPopulationSize() {
        return m_populationSize;
    }
    /**
     * Getter for the tournament size.
     * @return the tournament size.
     */ 
    public int getTournamentSize() {
        return m_tournamentSize;
    }
    /**
     * Getter for the elitism size.
     * @return the elitism size.
     */ 
    public int getElitismSize() {
        return m_elitismSize;
    }    
    /**
     * Getter for the number of threads.
     * @return the number of threads.
     */ 
    public int getNumberOfThreads() {
        return m_numberOfThreads;
    }
    /**
     * Getter for the random seed.
     * @return the number of threads.
     */ 
    public long getSeed() {
        return m_seed;
    }
    /**
     * Getter for the saving directory.
     * @return the saving directory.
     */ 
    public String getSavingDirectory() {
        return m_savingDirectory;
    }
    /**
     * Getter for the training directory.
     * @return the training directory.
     */ 
    public String getTrainingDirectory() {
        return m_trainingDirectory;
    }
    /**
     * Getter for the testing directory.
     * @return the testing directory.
     */ 
    public String getTestingDirectory() {
        return m_testingDirectory;
    }
    /**
     * Getter for the timeout limit (in seconds) for each individual.
     * @return the timeout limit (in seconds) for each individual.
     */ 
    public int getAlgorithmTimeLimit() {
        return m_algorithmTimeLimit;
    }    
    /**
     * Getter for the name of the experiment.
     * @return the experiment name.
     */
    public String getExperimentName(){
        return m_experimentName;
    }    
    /**
     * Getter for the anytime behavior.
     * @return the type of behavior:anytime or generational.
     */
    public boolean getAnytime() {
        return m_anytime;
    }
    /**
     * Getter for the general timeout (in minutes).
     * @return the general timeout (in minutes).
     */
    public int getGeneraltimeLimit() {
        return m_generaltimeLimit;
    }
    /**
     * Getter for the step to get intermediate results (in minutes).
     * @return the step to get intermediate results (in minutes).
     */
    public int getStepTimeLimit() {
        return m_stepTimeLimit;
    }
    /**
     * Getter for the search space mode (i.e., 0: Minimal, 1: Medium, >=2:Large).
     * @return the search space mode.
     */
    public int getSearchSpaceMode() {
        return m_searchSpaceMode;
    }        
    /**
     * Getter for the multi-fidelity approach.
     * @return the grammar mode.
     */
    public int getMultiFidelityMode() {
        return m_multifidelityMode;
    } 
    /**
     * Getter for the directory of the XML file.
     * @return the directory of the XML file.
     */
    public String get_XMLAlgorithmsFile() {
        return m_XMLAlgorithmsFile.getAbsolutePath();
    }
    /**
     * Getter for the Java directory.
     * @return the appropriate Java directory.
     */
    public String getJavaDir(){
        return m_javaDir;
    }    
    
    
//########################################################################################################################## 
//###########################################SETTERS######################################################################## 
//##########################################################################################################################   

    /**
     * Setter for the name of the experiment..
     * @param experimentName the actual value for the experiment name.
     */
    public void setExperimentName(String experimentName){
        this.m_experimentName = experimentName;
    }
    /**
     * Setter for the fold init.
     * @param foldInit the actual value for the fold init.
     */
    public void setFoldInit(int foldInit) {
        this.m_foldInit = foldInit;
    }
    /**
     * Setter for the crossover rate.
     * @param crossoverRate the actual value for the crossover rate.
     */
    public void setCrossoverRate(double crossoverRate) {
        this.m_crossoverRate = crossoverRate;
    }
    /**
     * Setter for the mutation rate.
     * @param mutationRate the actual value for the mutation rate.
     */
    public void setMutationRate(double mutationRate) {
        this.m_mutationRate = mutationRate;
    }
    /**
     * Setter for the number of generations.
     * @param numberOfGenerations the actual value for the number of generations.
     */
    public void setNumberOfGenerations(int numberOfGenerations) {
        this.m_numberOfGenerations = numberOfGenerations;
    }
    /**
     * Setter for the resample value.
     * @param resample the actual value for the resample.
     */
    public void setResample(int resample) {
        this.m_resample = resample;
    }
    /**
     * Setter for the reinit value.
     * @param reinit the actual value for the reinit.
     */
    public void setReinit(int reinit) {
        this.m_reinit = reinit;
    }    
    /**
     * Setter for the convergence generation value.
     * @param convergenceGen the actual value for the convergence generation.
     */
    public void setConvergenceGen(int convergenceGen) {
        this.m_convergenceGen = convergenceGen;
    }     
    /**
     * Setter for the population size.
     * @param populationSize the actual value for the population size.
     */
    public void setPopulationSize(int populationSize) {
        this.m_populationSize = populationSize;
    }
    /**
     * Setter for the tournament size.
     * @param tournamentSize the actual value for the tournament size.
     */
    public void setTournamentSize(int tournamentSize) {
        this.m_tournamentSize = tournamentSize;
    }
    /**
     * Setter for the elitism size.
     * @param elitismSize the actual value for the elistism size.
     */
    public void setElitismSize(int elitismSize) {
        this.m_elitismSize = elitismSize;
    }
    /**
     * Setter for the number of threads.
     * @param numberOfThreads the actual value for the number of threads.
     */
    public void setNumberOfThreads(int numberOfThreads) {
        this.m_numberOfThreads = numberOfThreads;
    }
    /**
     * Setter for random seed.
     * @param seed the actual value for the random seed.
     */
    public void setSeed(long seed) {
        if(seed >= 0){
            this.m_seed = seed;
        }else{
            this.m_seed = 123;
        }        
    }
    /**
     * Setter for directory to save the results.
     * @param savingDirectory the actual value for the saving directory.
     */
    public void setSavingDirectory(String savingDirectory) {
        this.m_savingDirectory = savingDirectory;
    }    

    /**
     * Setter for the timeout limit (in seconds).
     * @param algorithmTimeLimit the actual value timeout limit (in seconds) for each individual.
     */
    public void setAlgorithmTimeLimit(int algorithmTimeLimit) {
        this.m_algorithmTimeLimit = algorithmTimeLimit;
    }    
    /**
     * Setter for the anytime behavior.
     * @param anytime it decides if the anytime behavior will be performed instead of the generational..
     */
    public void setAnytime(boolean anytime) {
        this.m_anytime = anytime;
    }     
    /**
     * Setter for the general timeout limit (in minutes).
     * @param generaltimeLimit the actual value timeout limit (in seconds) for the whole evolutionary process.
     */
    public void setGeneraltimeLimit(int generaltimeLimit) {
        this.m_generaltimeLimit = generaltimeLimit;
    }    
    /**
     * //The step in the general time limit  to save intermediate results (in minutes)
     * @param stepTimeLimit the actual value of the step in the general time limit  to save intermediate results (in minutes).
     */
    public void setStepTimeLimit(int stepTimeLimit) {
        this.m_stepTimeLimit = stepTimeLimit;
    }     
    /**
     * The search space mode of the algorithm
     * @param searchSpaceMode the search space mode for the algorithm (i.e., 0: Minimal, 1: Medium, >=2:Large) 
     */
    public void setSearchSpaceMode(int searchSpaceMode){
            this.m_searchSpaceMode = searchSpaceMode;                 
    }   
    /**
     * Setter for the Java directory.
     * @param javaDir the Java directory.
     */    
    public void setJavaDir(String javaDir){
        this.m_javaDir = javaDir;
    }
    /**
     * Setter for multifidelity approach.
     * @param multifidelityMode the actual mode for the multifidelity mode.
     */
    public void setMultifidelityMode(int multifidelityMode) {
        if(multifidelityMode > 5){
            this.m_multifidelityMode = 0;
        }else{
            this.m_multifidelityMode = multifidelityMode;
        }        
    }
    /**
     * Setter for the XML file, which has the MLC algorithms.
     * @param dir the directory of the file 
     */
    public void setXMLAlgorithmsFile(String dir) {
        if (dir == null || dir.equals("") || dir.equals(" ")) {
             m_XMLAlgorithmsFile = new File(System.getProperty("user.dir"));
        }

        m_XMLAlgorithmsFile = new File(dir);
    }    
   
//########################################################################################################################## 
//###########################################CLASSIFIERS METHODS############################################################ 
//########################################################################################################################## 

    /**
     * It builds Auto-WEKA, by selecting and configuring the MLC algorithms
     * for this data.
     * 
     * @param data - The instances to be used.
     * @throws Exception - To inform any exception in the code.
     */
    @Override
    public void buildClassifier(Instances data) throws Exception {
        train(data);
        System.exit(1);
    }

    /**
     * It runs the training (evolution) of GA-Auto-MLC and produces the final classification
     * algorithm to be evaluated
     *
     * @param data - The instances to be used.
     * @throws Exception  - To inform any exception in the code.
     */
    private void train(Instances data) throws Exception {
        Date date = new Date();
        String strDateFormat = "hh:mm:ss a";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate = dateFormat.format(date);
        System.out.println(formattedDate); 
        
        //Starting the process...
        long startTime = System.nanoTime();        
        //For logging the actual generation and the search time.        
        int actualGeneration = -1;
        long searchTime = -1;
        //Number of evaluations.
        int numbOfEval = 0;
        //Number of reinitializations.
        int numbOfReinit = 0;    
        
        long searchTimeBudgetMilSec = 0;        
        searchTimeBudgetMilSec = (long) (this.getGeneraltimeLimit() * 60 * 1000 * 0.85);

        //To define the intermediate time budges:
        long[] intemBudgetsAux = null;

        long[] intemBudgets = null;
       
        if (this.getStepTimeLimit() > 0) {
            long step = (long) (this.getStepTimeLimit() * 60 * 1000);
            long intermediateBudget = 0;
            int sizeOfTheArray = (int) (this.getGeneraltimeLimit() / this.getStepTimeLimit()) + 1;
            intemBudgetsAux = new long[sizeOfTheArray];
            intemBudgets = new long[sizeOfTheArray];
            for (int i = 0; i < sizeOfTheArray; i++) {
                intermediateBudget = intermediateBudget + step;
                intemBudgetsAux[i] = (long) (intermediateBudget * 0.85);
                intemBudgets[i] = intermediateBudget;
            }
        }
        //position in the array of intermediate time budgets.
        int posInt = 0;
        
        //Used to calculate de diff time.        
        long currentAnyTime = 0;
        long diffAnyTime = 0;
        //String to save the current log.
        String log = "";        
        //Boolean for reinitialization tests.
        boolean testForReinit = true;        
        
        //String Buffer to save the results.
        StringBuilder loggingBuffer = new StringBuilder();
        //String Buider to save the whole information about each generation.
        StringBuilder generationBuffer = new StringBuilder();  
        //String builder to save information about convergence.
        StringBuilder convergenceBuffer = new StringBuilder();    
        
    
        
        //Training and validation directories.
        String[] learningANDvalidationDataDir = new String[2];
        
        //Variables to measure the worst, the best, the average fitness, the fitness' standard deviation values.
        double worstFitness = 0.0;
        double bestFitness = 0.0;
        double avgFitness = 0.0; 
        double stdevFitness = 0.0;
        
        //Map to save computational time.
        HashMap<String,Double> saveCompTime = new HashMap<String,Double>();
        
        //Number of attributes and labels. 
        String[] dataOpts = MLUtils.getDatasetOptions(data);        
        int nLabels = Integer.parseInt(dataOpts[dataOpts.length - 1]);
        if(nLabels < 0){
            nLabels *= -1;
        }         
        int nAttributes = data.numAttributes() - data.classIndex();
        int steps = (this.getGeneraltimeLimit()/this.getStepTimeLimit()); 
//        int steps = originalSteps;
        
        int nFoldsToLearn = 1;
        int nFoldsToValid = steps;
        int nAttributesToKeep = nAttributes;
        
        if((this.getMultiFidelityMode()==2) || (this.getMultiFidelityMode()==5) ){
            System.out.println("No Feature Selection.");
            DataUtil.noFeatureSelection(nLabels, intemBudgets[posInt], this.getTrainingDirectory());
        }else if((this.getMultiFidelityMode()==0) || (this.getMultiFidelityMode()==3) ){
            System.out.println("Exponential Feature Selection.");
            nAttributesToKeep = DataUtil.getAggressivelyMultFidAttributes(nAttributes, steps);
            DataUtil.featureSelection(nAttributesToKeep, nLabels, intemBudgets[posInt], this.getTrainingDirectory());
        }else if((this.getMultiFidelityMode()==1) || (this.getMultiFidelityMode()==4) ){
            System.out.println("Polynomial Feature Selection.");
            nAttributesToKeep = DataUtil.getNonAggressivelyMultFidAttributes(nAttributes, steps);
            DataUtil.featureSelection(nAttributesToKeep, nLabels, intemBudgets[posInt], this.getTrainingDirectory());            
        }
        
        //It is used to change the seed, when resampling is performed.
        long usedSeedResample = 0 + this.getSeed();  
        //It is used to change the seed, when reinitializing the poputlation.
        long usedSeedReinit = 0 + this.getSeed();
        
        //It defines the random number generator.
        MersenneTwisterFast rng = new MersenneTwisterFast(usedSeedReinit); 
        
        //Reset the statistics and set the seed
        MetaIndividualGA.resetStatistics(true);
        MetaIndividualGA.setRnd(rng);        
        
        
        // Test to see if the XML file path was set
        if (!m_XMLAlgorithmsFile.isFile()) {
            System.err.println("No XML file");
            System.exit(1);
        }       
        
        //It handles the search space of algorithms
        XMLAlgorithmHandler xmlAlgorithmHandler = new XMLAlgorithmHandler(m_XMLAlgorithmsFile);        

        //It is used to save the best of the generations.
        LinkedList<MetaIndividualGA> bestOfTheGenerations = new LinkedList<MetaIndividualGA>();
        //It is used to save the best individuals before reinitialization.
        ArrayList<MetaIndividualGA> bestOfTheReinitializations = new ArrayList<MetaIndividualGA>();        
        
        //For initializing the population.
        ArrayList<MetaIndividualGA> population = new ArrayList<MetaIndividualGA>();
        
        //The size of the gene;
        int geneSize = -1;
        
        //It calibrates the size of the gene in terms of the size of the search space.
        switch (this.m_searchSpaceMode) {
            case 0:
                geneSize = 12;
                break;
            case 1:
                geneSize = 15;
                break;
            case 2:
                geneSize = 25;
                break;   
            default:
                geneSize = 25;
                break;
        }

       ArrayList<String> xmlDir = new ArrayList<String>();
       
       for(int init =0; init < this.getPopulationSize(); init++){
            double doubleID = rng.nextDouble();
            int fileID = (int) ((xmlAlgorithmHandler.getAlgorithmsFiles().size()) * doubleID);

            XMLGeneHandler xmlGeneHandler = new XMLGeneHandler(new File(xmlAlgorithmHandler.getAlgorithmsFiles().get(fileID)), nAttributes, nLabels);
            String dir = xmlGeneHandler.getXMLPath().getAbsolutePath();
            if(!xmlDir.contains(dir)){
                xmlDir.add(dir);
            }          
            Allele genes = xmlGeneHandler.getGenes();
            double[] randomChromossome = EvolutionaryUtil.generateRandomChromosome(geneSize, rng, doubleID);
            population.add(new MetaIndividualGA(randomChromossome, genes, 0.0));            
       }
       
        System.gc();
        //Phenotype history, to check about convergence.
        String[] phenotypeHistory = null;
        if (this.getReinit() > 0) {
            phenotypeHistory = new String[this.getReinit()];
            for (int i = 0; i < phenotypeHistory.length; i++) {
                    phenotypeHistory[i] = "";
            }            
        }               
        
        //Determining the instance in accordance to the multi-fidelity mode.
        if ((this.getMultiFidelityMode() == 0) || (this.getMultiFidelityMode() == 1) || (this.getMultiFidelityMode() == 2)) {
            System.out.println("Polynomial Instance Selection.");
            learningANDvalidationDataDir = DataUtil.splitDataInAStratifiedWay(usedSeedResample, nFoldsToLearn, nFoldsToValid, nLabels, intemBudgets[posInt]);  
        }else if ((this.getMultiFidelityMode() == 3) || (this.getMultiFidelityMode() == 4) || (this.getMultiFidelityMode() == 5)) {
            System.out.println("No Instance Selection.");
            learningANDvalidationDataDir = DataUtil.splitDataInAStratifiedWay(usedSeedResample, nFoldsToValid, nFoldsToLearn, nLabels, intemBudgets[posInt]);   
        }
        usedSeedResample++;      
        
        //Generation iterator.
        int generation = 0;
        int generationAux = 0;
        //If it is using anytime, the number of generations is infinitive.
        if(this.getAnytime()){            
            this.setNumberOfGenerations(Integer.MAX_VALUE);
        }        

        
        System.out.println("Seed:"+this.getSeed() +"#--#Fold:" + this.getFoldInit()+"#--#GA:" + this.getSearchSpaceMode() + "#--#ExecBudget:" + this.getGeneraltimeLimit() +"min#--#AlgBudget:" + this.getAlgorithmTimeLimit()+"s");        
        System.out.println();
        //Helper population.
        ArrayList<MetaIndividualGA> populationAux = null;
        ArrayList<MetaIndividualGA> populationTemp = null;
        ArrayList<MetaIndividualGA> finalPopulation = null;
        
        ArrayList<IntermediateResults4GA> intResults = new ArrayList<IntermediateResults4GA>();
        IntermediateResults4GA ir = null;        
        double fitness = 0.0;
        double oldFitness = 0.0; 
        double diff = 0.0;        
        System.gc();
        
        //It executes for a number of generations. 
        for (generation = 0; generation <= this.getNumberGenerations(); generation++) { 
            oldFitness = fitness;
            fitness = 0.0;
            
            System.out.println("#Generation: "+generation);
            generationBuffer.append("#Generation: ").append(generation).append("\n");
            convergenceBuffer.append("#Generation: ").append(generation).append("\n");
            
            //Test for population reinitializaiton and Copy/update individuals.
            testForReinit = EvolutionaryUtil.testForReinitialization(phenotypeHistory, generationAux, this.getReinit(), this.getConvergenceGen());
            
            if (testForReinit) {
                usedSeedReinit++;
                numbOfReinit++;            
                System.out.println("#Reinitialization at the generation "+generation);
                convergenceBuffer.append("#Reinitialization at the generation ").append(generation).append("\n"); 
                //Save the best of the specie before reinitialization.
                bestOfTheReinitializations.add(populationTemp.get(populationTemp.size() - 1));
                populationTemp=null;
                generationAux=0;
                //And update the population.
                populationAux = null;
                System.gc();
                populationAux = EvolutionaryUtil.reInitPopulationGA(this.getPopulationSize(), usedSeedReinit, xmlAlgorithmHandler, geneSize, nAttributes, nLabels, this.getNumberOfThreads());
            } else {
                System.gc();
                populationAux = null;
                populationAux = new ArrayList<MetaIndividualGA>(population);
            } 
            
            //Resampling data every m_resample generations. 
            if ((this.getResample() > 0) && (generation % this.getResample() == 0) && (generation > 0)) {
                if ((this.getMultiFidelityMode() == 0) || (this.getMultiFidelityMode() == 1) || (this.getMultiFidelityMode() == 2)) {
                    learningANDvalidationDataDir = DataUtil.splitDataInAStratifiedWay(usedSeedResample, nFoldsToLearn, nFoldsToValid, nLabels, intemBudgets[posInt]);
                } else if ((this.getMultiFidelityMode() == 3) || (this.getMultiFidelityMode() == 4) || (this.getMultiFidelityMode() == 5)) {
                    learningANDvalidationDataDir = DataUtil.splitDataInAStratifiedWay(usedSeedResample, nFoldsToValid, nFoldsToLearn, nLabels, intemBudgets[posInt]);
                }
//                learningANDvalidationDataDir = splitDataInAStratifiedWay(usedSeedResample, nFoldsToLearn, nFoldsToValid, nLabels, intemBudgets[posInt]);
                saveCompTime = new HashMap<String,Double>();
                usedSeedResample++;
            }              
            
            System.out.println("Evaluating...");
            //It evaluates the individuals.
            MetaIndividualGA.evaluateIndividuals(populationAux, learningANDvalidationDataDir[0], learningANDvalidationDataDir[1], this.getNumberOfThreads(), 
                                                 this.getSeed(), this.getAlgorithmTimeLimit(), saveCompTime, this.getExperimentName(), this.getJavaDir());
            //It sorts the individuals given the fitness value. 
            Collections.sort(populationAux);
           
            //Printing the population
            for(MetaIndividualGA mi_ga : populationAux){
                System.out.println(mi_ga.getM_individualInString() + "#" + mi_ga.getFitnessValue());
            }
            
        
            //It updates the current time to verify if the timeout was achieved.
            currentAnyTime = System.nanoTime();
            diffAnyTime =  (currentAnyTime - startTime)/1000000;
            

            //It tests if the timeout was reached or if we can continue with the generational process. 
            //We run at least the first generation
            if( ( ((diffAnyTime <= searchTimeBudgetMilSec) || (generation==0)) && (this.getAnytime()) ) || (!this.getAnytime()) ){
                finalPopulation = new ArrayList<MetaIndividualGA>();
                finalPopulation.addAll(populationAux);
                //For logging the statistics.
                searchTime = diffAnyTime;
                numbOfEval+= this.getPopulationSize();
                actualGeneration = generation;                              
                bestFitness = ((MetaIndividualGA) populationAux.get(populationAux.size()-1)).getFitnessValue();
                worstFitness = ((MetaIndividualGA) populationAux.get(0)).getFitnessValue();
                avgFitness = EvolutionaryUtil.getAvgFitnessGA(populationAux);     
                fitness = avgFitness;
                stdevFitness = EvolutionaryUtil.getPopStdDevGA(populationAux, avgFitness);
                long currentTime = System.nanoTime();
                long diffTime =  (currentTime - startTime)/1000000;                  
                
                log = diffTime + ";" + generation + ";" + worstFitness + ";" + avgFitness + ";" + bestFitness + ";"+ stdevFitness + "\n";
                loggingBuffer.append(log);   
                
                for(int p=1; p<=8; p++){
                    //Saving the p bests of the generations.
                    bestOfTheGenerations.addLast(populationAux.get(populationAux.size()-p));   
                }
                
                //Saving computational time:
                for(MetaIndividualGA mi_ga : populationAux){
                    String ga_rep = mi_ga.getM_individualInString();
                    if(!saveCompTime.containsKey(ga_rep)){
                        saveCompTime.put(ga_rep, mi_ga.getFitnessValue());
                    }                 
                    log = ga_rep + "#" + mi_ga.getFitnessValue() + "\n";
                    generationBuffer.append(log);     
                }
                //It tracks the phenotype's history of the best individual. 
                if (this.getReinit() > 0) {
                    //It defines the convergence criterion through fitness history.
                    for (int i = 0; i < phenotypeHistory.length; i++) {
                        if ((phenotypeHistory.length - i - 1) != 0) {
                            phenotypeHistory[phenotypeHistory.length - i - 1] = phenotypeHistory[phenotypeHistory.length - i - 2];
                        } else {
                            phenotypeHistory[phenotypeHistory.length - i - 1] = (populationAux.get(populationAux.size() - 1)).getM_individualInString();
                        }
                        log = "#pos: " + (phenotypeHistory.length - i - 1) + "--phenotypeHistory: " + phenotypeHistory[phenotypeHistory.length - i - 1]+ "\n";
//                        System.out.print(log);
                        convergenceBuffer.append(log);
                    }
                }
                
                diff = fitness - oldFitness;

            }

           //It cheaks if the evolutionary process reached the end by time budget. 
            if(diffAnyTime > searchTimeBudgetMilSec){
                break;
            }



            if (this.getStepTimeLimit() > 0) {
                if (posInt < (intemBudgetsAux.length - 1)) {
                    if ((diffAnyTime == intemBudgetsAux[posInt]) || (diffAnyTime > intemBudgetsAux[posInt] && diffAnyTime < intemBudgetsAux[posInt + 1])) {
                        System.out.println("Saving intermediate results for time budget "+ (intemBudgetsAux[posInt]/60000));

                        date = new Date();
                        dateFormat = new SimpleDateFormat(strDateFormat);
                        formattedDate = dateFormat.format(date);
                        System.out.println(formattedDate);
                        
                     
                        
                        long intemediateStartTime = System.nanoTime();
                        
                       if ((this.getMultiFidelityMode() == 0) || (this.getMultiFidelityMode() == 1) || (this.getMultiFidelityMode() == 2)) {
                            ir = new IntermediateResults4GA(intemBudgets[posInt], populationAux, bestOfTheReinitializations, usedSeedResample, nLabels, generationBuffer, convergenceBuffer, numbOfEval,startTime,
                                                                  searchTime, actualGeneration, loggingBuffer, numbOfReinit, false, nFoldsToLearn, nFoldsToValid);
                        } else if ((this.getMultiFidelityMode() == 3) || (this.getMultiFidelityMode() == 4) || (this.getMultiFidelityMode() == 5)) {
                            ir = new IntermediateResults4GA(intemBudgets[posInt], populationAux,bestOfTheReinitializations,usedSeedResample,nLabels,generationBuffer,convergenceBuffer,numbOfEval,startTime,
                                                                  searchTime, actualGeneration, loggingBuffer, numbOfReinit, false, nFoldsToValid, nFoldsToLearn);
                        }                         
                        
                        intResults.add(ir);
                        
                        long intemediateEndTime = System.nanoTime();
                        long intemediateDiffTime = (intemediateEndTime - intemediateStartTime) / 1000000;
                        
                        
                        for (int i = posInt + 1; i < intemBudgetsAux.length; i++) {
                            intemBudgetsAux[i] += intemediateDiffTime;
                        }
                        searchTimeBudgetMilSec += intemediateDiffTime;
                        posInt++;

                        
                        if ((this.getMultiFidelityMode() == 2) || (this.getMultiFidelityMode() == 5)) {
                            DataUtil.noFeatureSelection(nLabels, intemBudgets[posInt], this.getTrainingDirectory());
                        } else if ((this.getMultiFidelityMode() == 0) || (this.getMultiFidelityMode() == 3)) {
                            steps--;
                            nAttributesToKeep = DataUtil.getAggressivelyMultFidAttributes(nAttributes, steps);
                            DataUtil.featureSelection(nAttributesToKeep, nLabels, intemBudgets[posInt], this.getTrainingDirectory());
                            saveCompTime = new HashMap<String,Double>();
                            usedSeedResample++; 
                        } else if ((this.getMultiFidelityMode() == 1) || (this.getMultiFidelityMode() == 4)) {
                            steps--;
                            nAttributesToKeep = DataUtil.getNonAggressivelyMultFidAttributes(nAttributes, steps);
                            DataUtil.featureSelection(nAttributesToKeep, nLabels, intemBudgets[posInt], this.getTrainingDirectory());
                            saveCompTime = new HashMap<String,Double>();
                            usedSeedResample++; 
                        }
                        
                        
                        
                        if ((this.getMultiFidelityMode() == 0) || (this.getMultiFidelityMode() == 1) || (this.getMultiFidelityMode() == 2)) {
                           nFoldsToLearn++;
                           nFoldsToValid--;
                           learningANDvalidationDataDir = DataUtil.splitDataInAStratifiedWay(usedSeedResample, nFoldsToLearn, nFoldsToValid, nLabels, intemBudgets[posInt]);
                        } else if ((this.getMultiFidelityMode() == 3) || (this.getMultiFidelityMode() == 4) || (this.getMultiFidelityMode() == 5)) {
                            learningANDvalidationDataDir = DataUtil.splitDataInAStratifiedWay(usedSeedResample, nFoldsToValid, nFoldsToLearn, nLabels, intemBudgets[posInt]);
                        }
                                                  
                    }
                }
            }            
            populationTemp = new ArrayList<MetaIndividualGA>(populationAux);
            
            population = new ArrayList<MetaIndividualGA>(this.operateOverPopulation(populationAux, rng, xmlAlgorithmHandler, nAttributes, nLabels));

            generationAux++; 
            System.gc();
        }
        
        if ((this.getMultiFidelityMode() == 0) || (this.getMultiFidelityMode() == 1) || (this.getMultiFidelityMode() == 2)) {
            ir = new IntermediateResults4GA(intemBudgets[posInt], populationAux, bestOfTheReinitializations, usedSeedResample, nLabels, generationBuffer, convergenceBuffer, numbOfEval, startTime,
                    searchTime, actualGeneration, loggingBuffer, numbOfReinit, true, nFoldsToLearn, nFoldsToValid);
        } else if ((this.getMultiFidelityMode() == 3) || (this.getMultiFidelityMode() == 4) || (this.getMultiFidelityMode() == 5)) {
            ir = new IntermediateResults4GA(intemBudgets[posInt], populationAux, bestOfTheReinitializations, usedSeedResample, nLabels, generationBuffer, convergenceBuffer, numbOfEval, startTime,
                    searchTime, actualGeneration, loggingBuffer, numbOfReinit, true, nFoldsToValid, nFoldsToLearn);
        }
        intResults.add(ir);  
        
       
        System.out.println("\n\nPrinting results by time budget...");
        long newSeedToResample = this.getSeed();
        for(IntermediateResults4GA i : intResults){
            newSeedToResample += 1000;
            System.out.println((i.getIntermediateBudget()/60000) + "min:");
            this.saveResults(i.getIntermediateBudget(), i.getPopulation(), i.getBestOfTheReinitializations(), newSeedToResample, i.getnLabels(), i.getGenerationBuffer(), i.getConvergenceBuffer(), i.getNumbOfEval(),
                             i.getStartTime(), i.getSearchTime(), i.getActualGeneration(), i.getLoggingBuffer(), i.getNumbOfReinit(), i.isSaveLogFiles(), i.getNFoldsToLearn(), i.getNFoldsToValid());
            System.out.println();
        }           

        
        DataUtil.removeUnnecessaryFiles(intemBudgets, this.getExperimentName());        
        DataUtil.removeFiles(xmlDir);
        
        System.gc();       
            
        date = new Date();
        dateFormat = new SimpleDateFormat(strDateFormat);
        formattedDate = dateFormat.format(date);      
        System.out.println(formattedDate);              
        System.exit(1);
    }

    




    public ArrayList<MetaIndividualGA> operateOverPopulation(ArrayList<MetaIndividualGA> populationAux, MersenneTwisterFast rng, XMLAlgorithmHandler xmlAlgorithmHandler, int nAttributes, int nLabels) throws Exception {

        ArrayList<MetaIndividualGA> population = new ArrayList<MetaIndividualGA>();
        //Elistism:
        for (int e = 0; e < this.getElitismSize(); e++) {
            int p = populationAux.size() - 1 - e;
            population.add(new MetaIndividualGA(populationAux.get(p).getGeneticCode(), populationAux.get(p).getM_genome(), populationAux.get(p).getFitnessValue()));
        }

        int pop = this.getElitismSize();
        //It fulfills the population considering the individuals from elitism.
        while (pop < this.getPopulationSize()) {
            MetaIndividualGA parent1 = EvolutionaryUtil.getParentFromTournamentGA(populationAux, rng, this.getTournamentSize());
            MetaIndividualGA parent2 = EvolutionaryUtil.getParentFromTournamentGA(populationAux, rng, this.getTournamentSize());            

            double[] child1 = parent1.getGeneticCode();
            double[] child2 = parent2.getGeneticCode();
            
                        
            double randomVar = rng.nextDouble();

            // Probabilistic crossover
            if (randomVar < m_crossoverRate) {
                //Executa cross over sobre os dois individuos:										
                int[] mask = EvolutionaryUtil.generateCrossoverMask4GA(parent1.getM_genomeSize(), rng);

                for (int k = 0; k < mask.length; k++) {
                    if (mask[k] == 1) {
                        double aux = child1[k];
                        child1[k] = child2[k];
                        child2[k] = aux;
                    }
                }
            }

            // Mutation     
            randomVar = rng.nextDouble();
            if (randomVar < this.getMutationRate()) {            
                child1 = EvolutionaryUtil.mutation4GAConservative(child1, rng);
                child2 = EvolutionaryUtil.mutation4GAConservative(child2, rng);
            }
            
            //it re-defines the directory file:
            double doubleID1 = child1[0];
            int fileID1 = (int) ((xmlAlgorithmHandler.getAlgorithmsFiles().size()) * doubleID1);
            XMLGeneHandler xmlGeneHandler1 = new XMLGeneHandler(new File(xmlAlgorithmHandler.getAlgorithmsFiles().get(fileID1)), nAttributes, nLabels);
            Allele genes1 = xmlGeneHandler1.getGenes();

            double doubleID2 = child2[0];
            int fileID2 = (int) ((xmlAlgorithmHandler.getAlgorithmsFiles().size()) * doubleID2);
            XMLGeneHandler xmlGeneHandler2 = new XMLGeneHandler(new File(xmlAlgorithmHandler.getAlgorithmsFiles().get(fileID2)), nAttributes, nLabels);
            Allele genes2 = xmlGeneHandler2.getGenes();

            population.add(new MetaIndividualGA(child1, genes1, 0.0));
            pop++;
            if (pop < this.getPopulationSize()) {
                population.add(new MetaIndividualGA(child2, genes2, 0.0));
                pop++;
            }

        }

        //returning the new population:
        return population;
    }   
        





    public void saveResults(long budget, ArrayList<MetaIndividualGA> population, final ArrayList<MetaIndividualGA> bestOfTheReinitializations, 
                            long usedSeedResample, int nLabels, final StringBuilder generationBuffer, final StringBuilder convergenceBuffer, int numbOfEval, long startTime, 
                            long searchTime, int actualGeneration,  StringBuilder loggingBuffer, int numbOfReinit, boolean saveLogFiles, int nFoldsToLearn, int nFoldsToValid) throws Exception{
      
        if (population != null) {
                bestOfTheReinitializations.add(population.get(population.size() - 1));

        }
        //It resamples again to check the best individual in the whole evolutionary process.
        long newSeedToResample =  usedSeedResample;
        String[] learningANDvalidationDataDir = DataUtil.splitDataInAStratifiedWay(newSeedToResample, nFoldsToLearn, nFoldsToValid, nLabels, budget);
        //It chooses among the best individuals of all generations.
        ArrayList<MetaIndividualGA> m_bestAlgorithms = new ArrayList<MetaIndividualGA>(EvolutionaryUtil.getBestAlgorithmsGA(generationBuffer, bestOfTheReinitializations, newSeedToResample, learningANDvalidationDataDir, this.getNumberOfThreads(), this.getSeed(), this.getAlgorithmTimeLimit(), this.getExperimentName(), this.getJavaDir()));
        
//        CandidateProgram m_bestAlgorithm = this.chooseAmongBestAlgorithms(generationBuffer, bestOfTheReinitializations, newSeedToResample, learningANDvalidationDataDir);
        int newNumbOfEval = numbOfEval+bestOfTheReinitializations.size();
        
        
        //Measuring the elapsed time to run the GGP.
        long endTime = System.nanoTime();        
        long differenceTime = (endTime - startTime)/1000000;; 
        //Saving the results...
        EvolutionaryUtil.savingMLCResultsGA(budget, generationBuffer, m_bestAlgorithms, searchTime, differenceTime, actualGeneration, learningANDvalidationDataDir, newNumbOfEval, numbOfReinit,
                                 this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit(), this.getTrainingDirectory(), this.getTestingDirectory(), this.getJavaDir());    
        
  
        generationBuffer.append("==============================================\n");
        //And the log of each generation.
        
        if (saveLogFiles) {
            EvolutionaryUtil.savingFitnessLog(loggingBuffer, budget, this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit(), false);

            DataUtil.savingLog(generationBuffer, "LogGenerations", budget, this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit());
            DataUtil.savingLog(convergenceBuffer, "LogConvergence", budget, this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit());
        }
        

    }   



    /**
     * It generates a file with the fitness curve.
     *
     *
     * @param strB the buffer to save.
     */
    public void savingLog(StringBuilder strB) throws IOException {
        BufferedWriter bf2 = new BufferedWriter(new FileWriter(this.getSavingDirectory() + File.separator + "results-" + this.getExperimentName() + File.separator + this.getFoldInit() + "Results-" + this.getExperimentName() + ".csv", true));
        try {
            bf2.write(strB.toString());
            bf2.newLine();
            bf2.write("############");
            bf2.newLine();
            bf2.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * It splits the training data (in a stratified way) into two subsets:
     * learning and validation. While the former is used to learn the model, the
     * latter is used to valid the produce model by that MLC algorithm. It uses
     * Mulan Java library to perform the stratified sampling.
     *
     * @param seed - The seed to sample the data.
     * @param fold - The fold to be sampled.
     * @param n_labels - The number of labels of the input dataset.
     * @return a string vector with the directories of the learning and
     * validation sets.
     */
    public String[] splitDataInAStratifiedWay(long seed, int fold, int n_labels) {
        try {
            String arffDir = this.getTrainingDirectory();
            MultiLabelInstances dataset = new MultiLabelInstances(arffDir, n_labels);

            IterativeStratification is = new IterativeStratification(seed);
            MultiLabelInstances[] folds = is.stratify(dataset, 5);

            /**
             * It creates the validation set. *
             */
            MultiLabelInstances validationData = new MultiLabelInstances(folds[fold].getDataSet(), dataset.getLabelsMetaData());
            validationData.getDataSet().setRelationName(dataset.getDataSet().relationName());

            Instances learningInst = null;
            boolean validFirstFold = true;

            for (int f = 0; f < folds.length; f++) {
                if (f != fold && validFirstFold) {
                    learningInst = new Instances(folds[f].getDataSet());
                    validFirstFold = false;
                } else if (f != fold) {
                    learningInst.addAll(folds[f].getDataSet());
                }
            }

            MultiLabelInstances learningData = new MultiLabelInstances(learningInst, dataset.getLabelsMetaData());
            learningData.getDataSet().setRelationName(dataset.getDataSet().relationName());

            try (FileWriter fLearning = new FileWriter("Learning-" + fold + ".arff", false)) {
                fLearning.write(learningData.getDataSet().toString());
                fLearning.close();
            }

            try (FileWriter fValidation = new FileWriter("Validation-" + fold + ".arff", false)) {
                fValidation.write(validationData.getDataSet().toString());
                fValidation.close();
            }

        } catch (InvalidDataFormatException ex) {
            System.err.println("Invalid data format exception: " + ex);
        } catch (IOException ex) {
            System.err.println("General exception: " + ex);
        }

        String[] learningANDvalidationData = new String[2];
        learningANDvalidationData[0] = "Learning-" + fold + ".arff";
        learningANDvalidationData[1] = "Validation-" + fold + ".arff";

        return learningANDvalidationData;
    }


    public static void main(String[] argv) {
        Locale.setDefault(Locale.US);
        runClassifier(new GAAutoMLC(argv), argv);
    }

    @Override
    public double[] distributionForInstance(Instance x) throws Exception {
        double p[] = new double[x.classIndex()];
        p = this.bestMLCalgorithm.distributionForInstance(x);
        return p;
    }

}
