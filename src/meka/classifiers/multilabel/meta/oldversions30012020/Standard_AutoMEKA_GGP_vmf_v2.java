/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package meka.classifiers.multilabel.meta.oldversions30012020;
//Java imports:
import meka.classifiers.multilabel.meta.util.EvolutionaryUtil;
import meka.classifiers.multilabel.meta.util.DataUtil;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.*;
//MEKA imports:
import meka.classifiers.multilabel.AbstractMultiLabelClassifier;
import meka.classifiers.multilabel.CC;
import meka.classifiers.multilabel.MultiLabelClassifier;
import meka.classifiers.multilabel.meta.automekaggp.core.MetaIndividualGGP;
import meka.classifiers.multilabel.meta.automekaggp.core.GrammarDefinitions;
import meka.classifiers.multilabel.meta.automekaggp.core.IntermediateResults4Standard;
import meka.core.MLUtils;
import meka.core.OptionUtils;
//WEKA imports:
import weka.core.*;
import weka.classifiers.*;
//EpochX imports:
import org.epochx.tools.grammar.*;
import org.epochx.representation.CandidateProgram;
import org.epochx.tools.random.MersenneTwisterFast;
import org.epochx.gr.op.init.RampedHalfAndHalfInitialiser;
import org.epochx.gr.representation.GRCandidateProgram;



/**
 * Standard_AutoMEKA_GGP_vmf.java - A method for selecting and configuring multi-label 
 * classification (MLC) algorithm in the MEKA software
 * (version with multifidelity - vmf).
 * 
 * Auto-MEKA uses a grammar-based genetic programming approach (from EpochX)
 * aiming to find the most suitable MLC algorithm for a given dataset of 
 * interest
 *
 * @author Alex G. C. de Sa (alexgcsa@dcc.ufmg.br)
 */
public class Standard_AutoMEKA_GGP_vmf_v2 extends AbstractMultiLabelClassifier implements MultiLabelClassifier {
    
    //For serialization.
    private static final long serialVersionUID = -1875298821884012336L;
    
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
    
    //seed for random number.
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
    
    //The defined search space (i.e., 0: Minimal, 1: Medium, 2: Large).
    protected int m_searchSpaceMode = 2;
    
    //Java directory.
    protected String m_javaDir = "java";
    
    //0: Exponential Attribute Selection, Polynomial Instance Selection
    //1: Polynomial Attribute Selection,  Polynomial Instance Selection
    //2: No Attribute Selection, Polynomial Instance Selection
    //3: Exponential Attribute Selection, No Instance Selection
    //4: Polynomial Attribute Selection, No Instance Selection
    //5: No Attribute Selection, No Instance Selection
    //>5: 5: No Attribute Selection, No Instance Selection
    protected int m_multifidelityMode = 5;     
    


    public Standard_AutoMEKA_GGP_vmf_v2(String [] argv) {
        
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
  
 //  ########################################################################################################################## 
 //  ###########################################CLASSIFIER OPTIONS#############################################################
 //  ########################################################################################################################## 
   
    /**
     * The name of the method.
     * @return the name of the method.
     */
    @Override
    public String toString() {
		return "Auto-MEKA";
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
        OptionUtils.add(new_options, super.listOptions());      
        
        return OptionUtils.toEnumeration(new_options);
    } 
    
 // ########################################################################################################################## 
 // ###########################################TIP TEXTS - GUI################################################################
 // ########################################################################################################################## 

    /**
     * Description of Auto-MEKA to display in the GUI.
     * @return	the description of Auto-MEKA in the GUI.
     */
    @Override
    public String globalInfo() {
        return "Auto-MEKA: A method for selecting and configuring multi-label "
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
//    /**
//     * Tip text for the grammar directory.
//     * @return the tip text for the grammar directory.
//     */      
//    public String grammarDirectoryTipText(){
//        return "The directory containing the grammar file, ie, the multi-label classification search space.";
//    }
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
    
 // ########################################################################################################################## 
 // ###########################################GETTERS######################################################################## 
 // ########################################################################################################################## 
 

    
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
//    /**
//     * Getter for the grammar directory.
//     * @return the grammar directory.
//     */ 
//    public String getGrammarDirectory() {
//        return m_grammarDirectory;
//    }
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
    
    public String getJavaDir(){
        return m_javaDir;
    }
    /**
     * Getter for the search space mode (i.e., 0: Minimal, 1: Medium, >=2:Large).
     * @return the grammar mode.
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
//    /**
//     * Setter for the grammar directory containing the multi-label search space..
//     * @param grammarDirectory the actual value for the grammar directory.
//     */
//    public void setGrammarDirectory(String grammarDirectory) {
//        this.m_grammarDirectory = grammarDirectory;
//    }

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

    
   
 /*  ########################################################################################################################## 
     ###########################################CLASSIFIERS METHODS############################################################ 
     ########################################################################################################################## 
 */        
    
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
    }

    /**
     * It runs the training (evolution) of Auto-MEKA and produces the final classification
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

              
//        long genTimeLimitMilSec = this.getGeneraltimeLimit() * 60 * 1000;
        
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
        
        //String builder to save information about convergence.
        StringBuilder xOverBuffer = new StringBuilder();
        StringBuilder xOverBuffer_aux = new StringBuilder();        
        
        //Training and validation directories.
        String[] learningANDvalidationDataDir = new String[2];
        
        //Variables to measure the worst, the best, the average fitness, the fitness' standard deviation values.
        double worstFitness = 0.0;
        double bestFitness = 0.0;
        double avgFitness = 0.0; 
        double stdevFitness = 0.0;
        
        final StringBuilder interBuffer_case1 = new StringBuilder();
        final StringBuilder interBuffer_case2 = new StringBuilder();
        final StringBuilder interBuffer_case3 = new StringBuilder();
        final StringBuilder interBuffer_case4 = new StringBuilder();
        final StringBuilder interBuffer_case5 = new StringBuilder();        
        
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
        
        
        //It is used to save the best of the generations.
        LinkedList<CandidateProgram> bestOfTheGenerations = new LinkedList<CandidateProgram>();
        //It is used to save the best individuals before reinitialization.
        ArrayList<CandidateProgram> bestOfTheReinitializations = new ArrayList<CandidateProgram>();
            
        // It defines the grammar.
        GrammarDefinitions grammarDef = new GrammarDefinitions();
        Grammar grammar = new Grammar(grammarDef.getGrammarDefinition(7, this.getSearchSpaceMode()));
        
        
        boolean acceptDuplicates = false;
        if (this.getSearchSpaceMode() == 0) {
            acceptDuplicates = true;
        }    
        //For initializing the population.
        RampedHalfAndHalfInitialiser initPop = new RampedHalfAndHalfInitialiser(rng, grammar, this.getPopulationSize(), 2, 100, acceptDuplicates, nLabels, nAttributes);
//        GrowInitialiser initPop = new GrowInitialiser(rng, grammar, this.getPopulationSize(), 50, false, n_labels, n_attributes);
        //It creates the initial population (zero) in accordance to the grammar. 
        ArrayList<CandidateProgram> population = new ArrayList<CandidateProgram>(initPop.getInitialPopulation());

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
            System.out.println("Polynomial Instance Selection");
            learningANDvalidationDataDir = DataUtil.splitDataInAStratifiedWay(usedSeedResample, nFoldsToLearn, nFoldsToValid, nLabels, intemBudgets[posInt]);  
        }else if ((this.getMultiFidelityMode() == 3) || (this.getMultiFidelityMode() == 4) || (this.getMultiFidelityMode() == 5)) {
            System.out.println("No Instance Selection");
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
        
        System.out.println("Seed:"+this.getSeed() +"#--#Fold:" + this.getFoldInit()+"#--#GGP:" + this.getSearchSpaceMode() + "#--#ExecBudget:" + this.getGeneraltimeLimit() +"min#--#AlgBudget:" + this.getAlgorithmTimeLimit()+"s");        
        System.out.println();
        //Helper population.
        ArrayList<CandidateProgram> populationAux = null;
        ArrayList<CandidateProgram> finalPopulation = null;
//        long timeSpentOnTest = 0;
        
        ArrayList<IntermediateResults4Standard> intResults = new ArrayList<IntermediateResults4Standard>();
        IntermediateResults4Standard ir = null;        
        double fitness = 0.0;
        double oldFitness = 0.0; 
        double diff = 0.0;
        
        //It executes for a number of generations. 
        for (generation = 0; generation <= this.getNumberGenerations(); generation++) { 
            oldFitness = fitness;
            fitness = 0.0;
            
            System.out.println("#Generation: "+generation);
            System.out.println("Size of saving map: "+saveCompTime.size());
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
                bestOfTheReinitializations.add(populationAux.get(populationAux.size() - 1));
                generationAux=0;
                //And update the population.
                populationAux = EvolutionaryUtil.reInitPopulationGGP(this.getPopulationSize(), grammar, usedSeedReinit, nLabels, nAttributes, this.getSearchSpaceMode());
            } else {
                populationAux = new ArrayList<CandidateProgram>(population);
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
            

            //##########################################################################################
            //PAREI AQUI
            
            //##########################################################################################
            System.out.println("Evaluating individuals...");  
            //It evaluates the individuals.
            MetaIndividualGGP.evaluateIndividuals(populationAux, learningANDvalidationDataDir[0], learningANDvalidationDataDir[1], this.getNumberOfThreads(), this.getSeed(), saveCompTime, this.getAlgorithmTimeLimit(), this.getExperimentName(), this.getJavaDir());
            //It sorts the individuals given the fitness value. 
            Collections.sort(populationAux);
            
            for (CandidateProgram cp : populationAux) {
                GRCandidateProgram gcp = (GRCandidateProgram) cp;
                this.addToLogBuffer(gcp, interBuffer_case1, interBuffer_case2, interBuffer_case3, interBuffer_case4, interBuffer_case5);

            }
                                
            
//            for(CandidateProgram cp : populationAux){
//                GRCandidateProgram gcp = (GRCandidateProgram) cp;
//                System.out.println(gcp.toString() + "#" + gcp.getFitnessValue());
//            }
            xOverBuffer.append(xOverBuffer_aux.toString());
            xOverBuffer_aux = new StringBuilder();       
            
            //It updates the current time to verify if the timeout was achieved.
            currentAnyTime = System.nanoTime();
            diffAnyTime =  (currentAnyTime - startTime)/1000000;
            
            //It tests if the timeout was reached or if we can continue with the generational process. 
            //We run at least the first generation
            if( ( ((diffAnyTime <= searchTimeBudgetMilSec) || (generation==0)) && (this.getAnytime()) ) || (!this.getAnytime()) ){
                finalPopulation = new ArrayList<CandidateProgram>();
                finalPopulation.addAll(populationAux);
                //For logging the statistics.
                searchTime = diffAnyTime;
                numbOfEval+= this.getPopulationSize();
                actualGeneration = generation;                              
                bestFitness = ((GRCandidateProgram) populationAux.get(populationAux.size()-1)).getFitnessValue();
                worstFitness = ((GRCandidateProgram) populationAux.get(0)).getFitnessValue();
                avgFitness = EvolutionaryUtil.getAvgFitnessGGP(populationAux);     
                fitness = avgFitness;
                stdevFitness = EvolutionaryUtil.getPopStdDevGGP(populationAux, avgFitness);
                long currentTime = System.nanoTime();
                long diffTime =  (currentTime - startTime)/1000000;                  
                
                log = diffTime + ";" + generation + ";" + worstFitness + ";" + avgFitness + ";" + bestFitness + ";"+ stdevFitness + "\n";
                loggingBuffer.append(log);   
                
                for(int p=1; p<=8; p++){
                    //Saving the p bests of the generations.
                    bestOfTheGenerations.addLast(populationAux.get(populationAux.size()-p));   
                }
                
                //Printing the population and the individuals' fitness values.
                for(CandidateProgram cp : populationAux){
                    GRCandidateProgram gcp = (GRCandidateProgram) cp;
                    String gcp_grammar = gcp.toString();
                    if(!saveCompTime.containsKey(gcp_grammar)){
                        saveCompTime.put(gcp_grammar, gcp.getFitnessValue());
                    }                 
                    log = gcp_grammar + "#" + gcp.getFitnessValue() + "\n";
                    generationBuffer.append(log);     
                }
                //It tracks the phenotype's history of the best individual. 
                if (this.getReinit() > 0) {
                    //It defines the convergence criterion through fitness history.
                    for (int i = 0; i < phenotypeHistory.length; i++) {
                        if ((phenotypeHistory.length - i - 1) != 0) {
                            phenotypeHistory[phenotypeHistory.length - i - 1] = phenotypeHistory[phenotypeHistory.length - i - 2];
                        } else {
                            phenotypeHistory[phenotypeHistory.length - i - 1] = ((GRCandidateProgram) populationAux.get(populationAux.size() - 1)).toString();
                        }
                        log = "#pos: " + (phenotypeHistory.length - i - 1) + "--phenotypeHistory: " + phenotypeHistory[phenotypeHistory.length - i - 1]+ "\n";
//                        System.out.print(log);
                        convergenceBuffer.append(log);
                    }
                }
                
                diff = fitness - oldFitness;
                if(generation != 0){
                    xOverBuffer_aux.append(fitness).append("\n").append(diff).append("\n\n");
                }
                                
                
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
                            ir = new IntermediateResults4Standard(intemBudgets[posInt], populationAux, bestOfTheReinitializations, usedSeedResample, nLabels, generationBuffer, convergenceBuffer, numbOfEval,startTime,
                                                                  searchTime, actualGeneration, loggingBuffer, numbOfReinit, false, nFoldsToLearn, nFoldsToValid);
                        } else if ((this.getMultiFidelityMode() == 3) || (this.getMultiFidelityMode() == 4) || (this.getMultiFidelityMode() == 5)) {
                            ir = new IntermediateResults4Standard(intemBudgets[posInt], populationAux,bestOfTheReinitializations,usedSeedResample,nLabels,generationBuffer,convergenceBuffer,numbOfEval,startTime,
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
           
            population = this.operateOverPopulation(populationAux, rng, nLabels, nAttributes, fitness, xOverBuffer_aux);

            System.gc();
            generationAux++;
        } //The end of the evolutionary process. 
        xOverBuffer.append(avgFitness).append("\n").append(0.0).append("\n");

        if ((this.getMultiFidelityMode() == 0) || (this.getMultiFidelityMode() == 1) || (this.getMultiFidelityMode() == 2)) {
            ir = new IntermediateResults4Standard(intemBudgets[posInt], populationAux, bestOfTheReinitializations, usedSeedResample, nLabels, generationBuffer, convergenceBuffer, numbOfEval, startTime,
                    searchTime, actualGeneration, loggingBuffer, numbOfReinit, true, nFoldsToLearn, nFoldsToValid);
        } else if ((this.getMultiFidelityMode() == 3) || (this.getMultiFidelityMode() == 4) || (this.getMultiFidelityMode() == 5)) {
            ir = new IntermediateResults4Standard(intemBudgets[posInt], populationAux, bestOfTheReinitializations, usedSeedResample, nLabels, generationBuffer, convergenceBuffer, numbOfEval, startTime,
                    searchTime, actualGeneration, loggingBuffer, numbOfReinit, true, nFoldsToValid, nFoldsToLearn);
        }

        intResults.add(ir);

        System.out.println("\n\nPrinting results by time budget...");
        long newSeedToResample = this.getSeed();
        for(IntermediateResults4Standard i : intResults){
            newSeedToResample += 1000;
            System.out.println((i.getIntermediateBudget()/60000) + "min:");
            this.saveResults(i.getIntermediateBudget(), i.getPopulation(), i.getBestOfTheReinitializations(), newSeedToResample, i.getnLabels(), i.getGenerationBuffer(), i.getConvergenceBuffer(), i.getNumbOfEval(),
                             i.getStartTime(), i.getSearchTime(), i.getActualGeneration(), i.getLoggingBuffer(), i.getNumbOfReinit(), i.isSaveLogFiles(), i.getNFoldsToLearn(), i.getNFoldsToValid(), xOverBuffer);
            System.out.println();
        }        
        
        DataUtil.savingLog(interBuffer_case3, "LogCase3_OnlyXOverInra", this.getGeneraltimeLimit(), this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit()); 
        DataUtil.savingLog(interBuffer_case4, "LogCase4_XOverInraAndMutation", this.getGeneraltimeLimit(), this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit()); 
        DataUtil.savingLog(interBuffer_case5, "LogCase5_OnlyMutation", this.getGeneraltimeLimit(), this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit()); 
        
        DataUtil.removeUnnecessaryFiles(intemBudgets, this.getExperimentName());
        
//        this.saveResults(genTimeLimitMilSec, finalPopulation, bestOfTheReinitializations, usedSeedResample, nLabels, generationBuffer, convergenceBuffer, numbOfEval, startTime, searchTime, actualGeneration, loggingBuffer, numbOfReinit, true, algsInTheEnsemble, algsInTheEnsemble8, timeSpentOnTest, totalEnsembleTime);
//        this.removeUnnecessaryFiles();
        
        System.gc();       
            
        date = new Date();
        dateFormat = new SimpleDateFormat(strDateFormat);
        formattedDate = dateFormat.format(date);      
        System.out.println(formattedDate);              
        System.exit(1);
        
    }
    
    public void addToLogBuffer(GRCandidateProgram gcp,  final StringBuilder interBuffer_case1,  
                              final StringBuilder interBuffer_case2,  final StringBuilder interBuffer_case3,
                               final StringBuilder interBuffer_case4,  final StringBuilder interBuffer_case5){
        String extraLog = gcp.getParent1Phenotype() + ";" + gcp.getParent2Phenotype() + ";" + gcp.getParent1Fitness() + ";" + gcp.getParent2Fitness() + ";" + ((gcp.getParent1Fitness() + gcp.getParent2Fitness()) / 2) + ";" + gcp.getFitnessValue() + "\n";
        
        if((gcp.isInterSpecieXOver()) && (!gcp.isMutation())){
            interBuffer_case1.append(extraLog);
        }else if((gcp.isInterSpecieXOver()) && (gcp.isMutation())){ 
            interBuffer_case2.append(extraLog);
        }else if((gcp.isIntraSpecieXOver()) && (!gcp.isMutation())){ 
            interBuffer_case3.append(extraLog);
        }else if((gcp.isIntraSpecieXOver()) && (gcp.isMutation())){ 
            interBuffer_case4.append(extraLog);
        }else if((!gcp.isInterSpecieXOver()) && (!gcp.isIntraSpecieXOver()) && (gcp.isMutation())){
            interBuffer_case5.append(extraLog);
        } 
        
    }    
    
    
    public void saveResults(long budget, ArrayList<CandidateProgram> population, final ArrayList<CandidateProgram> bestOfTheReinitializations, 
                            long usedSeedResample, int nLabels, final StringBuilder generationBuffer, final StringBuilder convergenceBuffer, int numbOfEval, long startTime, 
                            long searchTime, int actualGeneration,  StringBuilder loggingBuffer, int numbOfReinit, boolean saveLogFiles, int nFoldsToLearn, int nFoldsToValid, StringBuilder xOverBuffer) throws Exception{
      
        if (population != null) {
                bestOfTheReinitializations.add(population.get(population.size() - 1));

        }
        //It resamples again to check the best individual in the whole evolutionary process.
        long newSeedToResample =  usedSeedResample;
        String[] learningANDvalidationDataDir = DataUtil.splitDataInAStratifiedWay(newSeedToResample, nFoldsToLearn, nFoldsToValid, nLabels, budget);
        //It chooses among the best individuals of all generations.
        ArrayList<CandidateProgram> m_bestAlgorithms = new ArrayList<CandidateProgram>(EvolutionaryUtil.getBestAlgorithmsGGP(generationBuffer, bestOfTheReinitializations, newSeedToResample, learningANDvalidationDataDir, this.getNumberOfThreads(), this.getSeed(), this.getAlgorithmTimeLimit(), this.getExperimentName(), this.getJavaDir()));
        
//        CandidateProgram m_bestAlgorithm = this.chooseAmongBestAlgorithms(generationBuffer, bestOfTheReinitializations, newSeedToResample, learningANDvalidationDataDir);
        int newNumbOfEval = numbOfEval+bestOfTheReinitializations.size();
        
        
        //Measuring the elapsed time to run the GGP.
        long endTime = System.nanoTime();        
        long differenceTime = (endTime - startTime)/1000000;; 
        System.gc();
        //Saving the results...
        EvolutionaryUtil.savingMLCResultsGGP(budget, generationBuffer, m_bestAlgorithms, searchTime, differenceTime, actualGeneration, learningANDvalidationDataDir, newNumbOfEval, numbOfReinit,
                                 this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit(), this.getTrainingDirectory(), this.getTestingDirectory(), this.getJavaDir());    
        
  
        generationBuffer.append("==============================================\n");
        //And the log of each generation.
        
        if (saveLogFiles) {
            EvolutionaryUtil.savingFitnessLog(loggingBuffer, budget, this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit(), false);

            DataUtil.savingLog(generationBuffer, "LogGenerations", budget, this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit());
            DataUtil.savingLog(convergenceBuffer, "LogConvergence", budget, this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit());
            DataUtil.savingLog(xOverBuffer, "LogXOver", budget, this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit());
        }
        
        System.gc(); 
    }        
    

 
    
    public ArrayList<CandidateProgram> operateOverPopulation(ArrayList<CandidateProgram> populationAux, MersenneTwisterFast rng, int n_labels, int n_attributes, double oldFitness, StringBuilder xOverBuffer) throws Exception {
        
        ArrayList<CandidateProgram> population = new ArrayList<CandidateProgram>();
        xOverBuffer.append("").append(oldFitness).append("\n");
        

        for (int e = 0; e < this.getElitismSize(); e++) {
            population.add(populationAux.get(populationAux.size() - 1 - e));
        }

        int pop = this.getElitismSize();      
        //It fulfills the population considering the individuals from elitism.
        while (pop < this.getPopulationSize()) {
            //Tournament.
            CandidateProgram parent1 = EvolutionaryUtil.getParentFromTournamentGGP(populationAux, rng, this.getTournamentSize());
            CandidateProgram parent2 = EvolutionaryUtil.getParentFromTournamentGGP(populationAux, rng, this.getTournamentSize());

            //The returned individuals from crossover operation.
            CandidateProgram[] xChilds = EvolutionaryUtil.crossover(populationAux, parent1, parent2, xOverBuffer, rng, this.getCrossoverRate(), this.getTournamentSize());
            CandidateProgram child1 = xChilds[0];
            CandidateProgram child2 = xChilds[1];

            //And probabilistic mutation.
            double randomVar = rng.nextDouble();
            CandidateProgram mchild1 = null;
            CandidateProgram mchild2 = null;
            if(randomVar < this.getMutationRate()){
                mchild1 = EvolutionaryUtil.mutateConservative(child1, rng, n_labels, n_attributes);
                ((GRCandidateProgram) mchild1).setMutation(true);
                ((GRCandidateProgram) mchild1).setParent1Fitness(((GRCandidateProgram) mchild1).getFitnessValue());
                ((GRCandidateProgram) mchild1).setParent1Phenotype(parent1.toString());        
                mchild2 = EvolutionaryUtil.mutateConservative(child2, rng, n_labels, n_attributes);
                ((GRCandidateProgram) mchild2).setMutation(true);
                ((GRCandidateProgram) mchild2).setParent1Fitness(((GRCandidateProgram) mchild2).getFitnessValue());
                ((GRCandidateProgram) mchild2).setParent1Phenotype(parent2.toString());                 
            }else{
                mchild1 = child1.clone();
                mchild2 = child2.clone();
            }


            population.add(mchild1);
            pop++;
            if (pop < this.getPopulationSize()) {
                population.add(mchild2);
                pop++;
            }
        }

        //Reset the individuals.
        for (CandidateProgram cp : population) {
            ((GRCandidateProgram) cp).reset();
        }	
     
        return population;
    }    

    

   /**
    * It calculates the probability distribution for a test instance.
    * @param x the test instance to be evaluated by the best algorithm..
    * @return the distribution for the test instance.
    * @throws Exception 
    */
   @Override
   public double[] distributionForInstance(Instance x) throws Exception {                
        double p[] = new double[x.classIndex()];
        p = this.bestMLCalgorithm.distributionForInstance(x);            
        return p;
    }


    /**
     * The main method to run AutoMEKA_GGP.
     * @param args the arguments of the method.
     */
    public static void main(String args[]) {
        AbstractMultiLabelClassifier.runClassifier(new Standard_AutoMEKA_GGP_vmf_v2(args), args);
    }
}