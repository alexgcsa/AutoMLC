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
package meka.classifiers.multilabel.meta.tests;
//Java imports:
import meka.classifiers.multilabel.meta.util.EvolutionaryUtil;
import meka.classifiers.multilabel.meta.util.Util;
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
import meka.classifiers.multilabel.meta.automekaggp.core.IntermediateResults4Speciation;
import meka.classifiers.multilabel.meta.automekaggp.core.UptatedMetaIndividualGGP;
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
 * AutoMEKA_GGP.java - A method for selecting and configuring multi-label 
 classification (MLC) algorithm in the MEKA software.
 * 
 * Auto-MEKA uses a grammar-based genetic programming approach (from EpochX)
 * aiming to find the most suitable MLC algorithm for a given dataset of 
 * interest
 *
 * @author Alex G. C. de Sa (alexgcsa@dcc.ufmg.br)
 */
public class AutoMEKA_SpGGP_tst3 extends AbstractMultiLabelClassifier implements MultiLabelClassifier {
    
    //For serialization.
    private static final long serialVersionUID = -1875298821884012336L;
    
    //The selected MLC algorithm. 
    protected Classifier bestMLCalgorithm;
   
    //Crossover rate. 
    protected double m_crossoverRate = 0.90;
    
    //Intra/Interspecie crossover rate. 
    protected double m_intraInterCrossoverRate = 0.90;
    
    //Mutation rate. 
    protected double m_mutationRate = 0.10;
    
    //Number of generations. 
    protected int m_numberOfGenerations = 2;
    
    //Number of generations to resample the data. 
    protected int m_resample = 5;
    
    //Number of generations defined as fitness convergencence to reinit the population. 
    protected int m_reinit = 10;    
    //The generation value to start to analyze the convergence in terms of generation (default: 20).
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
    protected int m_generaltimeLimit = 1;
    
    //The step in the general time limit  to save intermediate results (in minutes). 
    protected int m_stepTimeLimit = 1;
    
    //The defined search space (i.e., 0: Minimal, 1: Medium, 2: Large).
    protected int m_searchSpaceMode = 2;
    
    //Number of species.
    protected int m_numberOfSpecies = 8;
      
    //Java directory.
    protected String m_javaDir = "java";
    
    //Multi-fidelity approaches:
    //0: Exponential Attribute Selection, Polynomial Instance Selection
    //1: Polynomial Attribute Selection,  Polynomial Instance Selection
    //2: No Attribute Selection, Polynomial Instance Selection
    //3: Exponential Attribute Selection, No Instance Selection
    //4: Polynomial Attribute Selection, No Instance Selection
    //5: No Attribute Selection, No Instance Selection
    //>5: 5: No Attribute Selection, No Instance Selection
    protected int m_multifidelityMode = 5; 
    
    //Options for the fitness:
    //0: (EM + (1.0 - HL) + (1.0 - RL) + FM)/4;
    //1: (0.1 * EM) + (0.4 * (1.0 - HL)) + (0.1* (1.0 - RL)) + (0.4 * F1))/4;
    //2: ((0.1 * EM) + (0.35 * (1.0 - HL)) + (0.2 * AvgPrec) + (0.35 * F1))/4;
    //3:  1.0 - HL;
    protected int m_fitnessOption = 0;
    
    
    
    /**
     * Constructor for Auto-MEKA_SpGGP.
     * @param argv the arguments of the class.
     */
    public AutoMEKA_SpGGP_tst3(String [] argv) {
        
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
        setIntraInterCrossoverRate(OptionUtils.parse(options, "Xo", 0.90));         
        setNumberOfThreads(OptionUtils.parse(options, "N", 1));
        setSeed(OptionUtils.parse(options, "H", 11321));     
        setFoldInit(OptionUtils.parse(options, "Y", 0));
        setAlgorithmTimeLimit(OptionUtils.parse(options, "L", 60));
        setExperimentName(OptionUtils.parse(options, "W", "ExperimentABC"));        
        setAnytime(Utils.getFlag("C", options));        
        setGeneraltimeLimit(OptionUtils.parse(options, "B", 10));
        setStepTimeLimit(OptionUtils.parse(options, "Bs", 1));           
        setSavingDirectory(OptionUtils.parse(options, "D", "~/"));
        setSearchSpaceMode(OptionUtils.parse(options, "O", 2));
        setJavaDir(OptionUtils.parse(options, "JavaDir", "java"));
        setMultifidelityMode(OptionUtils.parse(options, "MM", 5));
        setFitnessOption(OptionUtils.parse(options, "FO", 0));
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
        OptionUtils.add(result, "Xo", this.getIntraInterCrossoverRate());        
        OptionUtils.add(result, "N", this.getNumberOfThreads());
        OptionUtils.add(result, "H", this.getSeed());
//        OptionUtils.add(result, "A", this.getGrammarDirectory());
        OptionUtils.add(result, "Y", this.getFoldInit());
        OptionUtils.add(result, "L", this.getAlgorithmTimeLimit()); 
        OptionUtils.add(result, "W", this.getExperimentName());
        OptionUtils.add(result, "B", this.getGeneraltimeLimit()); 
        OptionUtils.add(result, "Bs", this.getStepTimeLimit());         
        OptionUtils.add(result, "D", this.getSavingDirectory());
        OptionUtils.add(result, "O", this.getSearchSpaceMode());
        OptionUtils.add(result, "JavaDir", this.getJavaDir());
        OptionUtils.add(result, "MM", this.getMultiFidelityMode());
        OptionUtils.add(result, "FO", this.getFitnessOption());
        
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
        new_options.addElement(new Option("\n" + intraInterCrossoverRateTipText(), "Xo", 1, "-Xo <value>"));               
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
     * Tip text for the intra/inter-specie crossover rate.
     * @return the tip text for the intra/inter-specie crossover rate.
     */
    public String intraInterCrossoverRateTipText() {
        return "The intra/inter-specie crossover rate to be used in the evolutionary process.";
    }      
    /**
     * Tip text for the elistism size.
     * @return the tip text for the elistism size.
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
     * Getter for the number of species.
     * @return the number of species.
     */
    public int getNumberOfSpecies() {
        return m_numberOfSpecies;
    }
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
     * Getter for the intra/inter-specie crossover rate.
     * @return the intra/inter-specie crossover rate.
     */
    public double getIntraInterCrossoverRate() {
        return m_intraInterCrossoverRate;
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
     * Getter for resampling in R iterations.
     * @return the value of resampling, ie, the number of generations to wait
     *         to apply the resample.
     */ 
    public int getResample() {
        return m_resample;
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
     * @return the grammar mode.
     */
    public int getSearchSpaceMode() {
        return m_searchSpaceMode;
    }
    
    /**
     * Getter for the java directory.
     * @return the java directory.
     */   
    public String getJavaDir(){
        return m_javaDir;
    }   
    
    /**
     * Getter for the multi-fidelity approach.
     * @return the grammar mode.
     */
    public int getMultiFidelityMode() {
        return m_multifidelityMode;
    }
    /**
     * Getter for the fitness options.
     * @return the fitness option.
     */
    public int getFitnessOption() {
        return m_fitnessOption;
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
     * Setter for the intra/inter-specie crossover rate.
     * @param intraInterCrossoverRate the actual value for the intra/inter-crossover rate.
     */
    public void setIntraInterCrossoverRate(double intraInterCrossoverRate) {
        this.m_intraInterCrossoverRate = intraInterCrossoverRate;
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
     * Setter for the resample value.
     * @param resample the actual value for the resample.
     */
    public void setResample(int resample) {
        this.m_resample = resample;
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
     * @param elitismSize the actual value for the elitism size.
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
     * The step in the general time limit  to save intermediate results (in minutes)
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

    /**
     * Setter for the fitness option.
     * @param fitnessOption the actual mode for the multifidelity mode.
     */
    public void setFitnessOption(int fitnessOption) {
            this.m_fitnessOption = fitnessOption;      
    
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
        
        String log = "";    
        boolean testForReinit = true;
       
        //String Buider to save the results.
        StringBuilder[] loggingBuffer = new StringBuilder[this.getNumberOfSpecies()];
        //String Buider to save the whole information about each generation.
        StringBuilder generationBuffer = new StringBuilder();
        //String builder to save information about convergence.
        StringBuilder convergenceBuffer = new StringBuilder();
        
        //String builder to save information about convergence.
        StringBuilder interXOverBuffer = new StringBuilder();
        StringBuilder interXOverBuffer_aux = new StringBuilder();        
        StringBuilder intraXOverBuffer = new StringBuilder();
        StringBuilder intraXOverBuffer_aux = new StringBuilder();  
        
        final StringBuilder interBuffer_case1 = new StringBuilder();
        final StringBuilder interBuffer_case2 = new StringBuilder();
        final StringBuilder interBuffer_case3 = new StringBuilder();
        final StringBuilder interBuffer_case4 = new StringBuilder();
        final StringBuilder interBuffer_case5 = new StringBuilder();
        
        //Variables to measure the worst, the best and the average fitness values.
        double[] worstFitness = new double[this.getNumberOfSpecies()];
        double[] bestFitness = new double[this.getNumberOfSpecies()];
        double[] avgFitness = new double[this.getNumberOfSpecies()]; 
        double[] stdevFitness = new double[this.getNumberOfSpecies()]; 
        
        
        //It is used to save the best of the generations.
        LinkedList<CandidateProgram> bestOfTheGenerations = new LinkedList<CandidateProgram>();       
         //It is used to save the best individuals (of each specie) before reinitialization.
        ArrayList<CandidateProgram> bestOfTheReinitializations = new ArrayList<CandidateProgram>();
        //Training and validation directories.
        String[] learningANDvalidationDataDir = new String[2];
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
            Util.noFeatureSelection(nLabels, intemBudgets[posInt], this.getTrainingDirectory());
        }else if((this.getMultiFidelityMode()==0) || (this.getMultiFidelityMode()==3) ){
            System.out.println("Exponential Feature Selection.");
            nAttributesToKeep = Util.getAggressivelyMultFidAttributes(nAttributes, steps);
            Util.featureSelection(nAttributesToKeep, nLabels, intemBudgets[posInt], this.getTrainingDirectory());
        }else if((this.getMultiFidelityMode()==1) || (this.getMultiFidelityMode()==4) ){
            System.out.println("Polynomial Feature Selection.");
            nAttributesToKeep = Util.getNonAggressivelyMultFidAttributes(nAttributes, steps);
            Util.featureSelection(nAttributesToKeep, nLabels, intemBudgets[posInt], this.getTrainingDirectory());            
        }
        

        //It is used to change the seed, when resampling is performed.
        long usedSeedResample = 0 + this.getSeed();  
        //It is used to change the seed, when reinitializing the poputlation.
        long usedSeedReinit = 0 + this.getSeed();
        //It defines the random number generator.
        MersenneTwisterFast rng = new MersenneTwisterFast(usedSeedReinit);
        
       

        //It defines the size of each group of specie.
        int groupSize = this.getPopulationSize()/this.getNumberOfSpecies();
//        System.out.println("groupsize:"+groupSize);
        
        //It defines the grammar.
        GrammarDefinitions grammarDef = new GrammarDefinitions();
        Grammar [] grammars = new Grammar[this.getNumberOfSpecies()];
        //And the initializer;
        RampedHalfAndHalfInitialiser [] initPops = new RampedHalfAndHalfInitialiser[this.getNumberOfSpecies()];
        
        boolean acceptDuplicates = false;
        //It defines the instantiation of the grammar:
        for(int s=0; s< this.getNumberOfSpecies(); s++){
            grammars[s] = new Grammar(grammarDef.getGrammarDefinition(s, this.getSearchSpaceMode())); 
            //Initialiser and groups by species.
            if(this.getSearchSpaceMode()==0){
                acceptDuplicates = true;
            }
            initPops[s] = new RampedHalfAndHalfInitialiser(rng, grammars[s], groupSize, 2, 100, acceptDuplicates, nLabels, nAttributesToKeep);            
       }
        

        ArrayList<CandidateProgram>[] popGroupedBySpecie = (ArrayList<CandidateProgram>[])new ArrayList[this.getNumberOfSpecies()];

        String[][] phenotypeHistory = null;
        if (this.getReinit() > 0) {
            phenotypeHistory = new String[this.getReinit()][this.getNumberOfSpecies()];
            for (int s = 0; s < this.getNumberOfSpecies(); s++) {
                for (int i = 0; i < phenotypeHistory.length; i++) {
                    phenotypeHistory[i][s] = "";
                }
            }
        }
        //Generation iterator.
        int generation = 0;
        int [] generationAux = new int[this.getNumberOfSpecies()];        
        //It creates the initial population (zero) in accordance to the grammar for each specie. 
        for(int s=0; s< this.getNumberOfSpecies(); s++){
//            System.out.println("Specie: "+s);
            popGroupedBySpecie[s] =  new ArrayList<CandidateProgram>(initPops[s].getInitialPopulation());

            worstFitness[s] = 0.0;
            bestFitness[s] = 0.0;
            avgFitness[s] = 0.0;  
            stdevFitness[s] = 0.0;
            //Buffer for logging in each specie.
            loggingBuffer[s] = new StringBuilder();
            generationAux[s] = 0;
        }
        
        //Initializing the specie in each individual.
        for(int s=0; s< this.getNumberOfSpecies(); s++){
            for(CandidateProgram cp : popGroupedBySpecie[s]){
                GRCandidateProgram grcp = (GRCandidateProgram) cp;
                grcp.setSpecie(s);
            }
        }
        
        //Determining the instance in accordance to the multi-fidelity mode.
        if ((this.getMultiFidelityMode() == 0) || (this.getMultiFidelityMode() == 1) || (this.getMultiFidelityMode() == 2)) {
            System.out.println("Polynomial Instance Selection");
            learningANDvalidationDataDir = Util.splitDataInAStratifiedWay(usedSeedResample, nFoldsToLearn, nFoldsToValid, nLabels, intemBudgets[posInt]);  
        }else if ((this.getMultiFidelityMode() == 3) || (this.getMultiFidelityMode() == 4) || (this.getMultiFidelityMode() == 5)) {
            System.out.println("No Instance Selection");
            learningANDvalidationDataDir = Util.splitDataInAStratifiedWay(usedSeedResample, nFoldsToValid, nFoldsToLearn, nLabels, intemBudgets[posInt]);   
        }
        usedSeedResample++;     
        

        //If it is using anytime, the number of generations is infinitive.
        if(this.getAnytime()){            
            this.setNumberOfGenerations(Integer.MAX_VALUE);
        }
        
        System.out.println("Seed:"+this.getSeed() +"#--#Fold:" + this.getFoldInit()+"#--#GGP:" + this.getSearchSpaceMode() + "#--#ExecBudget:" + this.getGeneraltimeLimit() +"min#--#AlgBudget:" + this.getAlgorithmTimeLimit()+"s");        
        System.out.println();
        
        ArrayList<CandidateProgram>[] popGroupedBySpecieAux = null;
        ArrayList<CandidateProgram>[] finalPopGroupedBySpecie = null;
        
        ArrayList<IntermediateResults4Speciation> intResults = new ArrayList<IntermediateResults4Speciation>();
        IntermediateResults4Speciation ir = null;
        
        double fitness = 0.0;
        double oldFitness = 0.0;
       
        
        
        //It executes for a number of generations. 
        for (generation = 0; generation <= this.getNumberGenerations(); generation++) { 
            oldFitness = fitness;
            fitness = 0.0;
            
            System.out.println("#Generation: "+generation);
            generationBuffer.append("#Generation: ").append(generation).append("\n");
            convergenceBuffer.append("#Generation: ").append(generation).append("\n");
//            System.out.println("Size of saving map: "+saveCompTime.size());
            
            //Helper population.
            popGroupedBySpecieAux = (ArrayList<CandidateProgram>[])new ArrayList[this.getNumberOfSpecies()];
            //Copy/update individuals.
            for (int s = 0; s < this.getNumberOfSpecies(); s++) {
                testForReinit =  EvolutionaryUtil.testForReinitializationForSpecies(phenotypeHistory, s, generationAux[s], this.getReinit(), this.getConvergenceGen());
                if(testForReinit){
                    usedSeedReinit++;
                    numbOfReinit++;
                    convergenceBuffer.append("#Reinitialization at generation: ").append(generation).append("\n");
                    System.out.println("Reinitializaion at the generation "+generation + " for the specie "+s);
                    convergenceBuffer.append("#Reinitialization of specie: ").append(s).append("\n");
                    //Save the best of the specie before reinitialization.
                    bestOfTheReinitializations.add(popGroupedBySpecie[s].get(popGroupedBySpecie[s].size()-1));
                    //And update the population.
                    popGroupedBySpecieAux[s] = EvolutionaryUtil.reInitPopulationGGP(groupSize, grammars[s], usedSeedReinit, nLabels, nAttributesToKeep, this.getSearchSpaceMode());                    
                    generationAux[s] = 0;
                    //setting the specie when performing reinitialization.
                    for (CandidateProgram cp : popGroupedBySpecieAux[s]) {
                        GRCandidateProgram gcp = (GRCandidateProgram) cp;
                        gcp.setSpecie(s);
                    }               
                }else{
                    popGroupedBySpecieAux[s] = new ArrayList<CandidateProgram>(popGroupedBySpecie[s]);
                }
            }            
            
            //Resampling data every m_resample generations. 
            if ((this.getResample() > 0) && (generation % this.getResample() == 0) && (generation > 0)) {
                if ((this.getMultiFidelityMode() == 0) || (this.getMultiFidelityMode() == 1) || (this.getMultiFidelityMode() == 2)) {
                    learningANDvalidationDataDir = Util.splitDataInAStratifiedWay(usedSeedResample, nFoldsToLearn, nFoldsToValid, nLabels, intemBudgets[posInt]);
                } else if ((this.getMultiFidelityMode() == 3) || (this.getMultiFidelityMode() == 4) || (this.getMultiFidelityMode() == 5)) {
                    learningANDvalidationDataDir = Util.splitDataInAStratifiedWay(usedSeedResample, nFoldsToValid, nFoldsToLearn, nLabels, intemBudgets[posInt]);
                }
//                learningANDvalidationDataDir = splitDataInAStratifiedWay(usedSeedResample, nFoldsToLearn, nFoldsToValid, nLabels, intemBudgets[posInt]);
                saveCompTime = new HashMap<String,Double>();
                usedSeedResample++;
            }          
            
            System.out.println("Evaluating individuals...");
            //It evaluates the individuals.
            UptatedMetaIndividualGGP.evaluateIndividualsFromSpecies(popGroupedBySpecieAux, learningANDvalidationDataDir[0], learningANDvalidationDataDir[1], this.getNumberOfThreads(), this.getSeed(), saveCompTime, this.getAlgorithmTimeLimit(), this.getExperimentName(), groupSize, this.getJavaDir(), this.getFitnessOption());
      
            
            intraXOverBuffer.append(intraXOverBuffer_aux.toString());
            intraXOverBuffer_aux = new StringBuilder(); 
            interXOverBuffer.append(interXOverBuffer_aux.toString());
            interXOverBuffer_aux = new StringBuilder(); 
            
            //It updates the current time to verify if the timeout was achieved.
            currentAnyTime = System.nanoTime();
            diffAnyTime =  (currentAnyTime - startTime)/1000000;
            
            //It tests if the timeout was reached or if we can continue with the generational process. 
            //We run at least the first generation
            if( ( ((diffAnyTime <= searchTimeBudgetMilSec) || (generation==0)) && (this.getAnytime()) ) || (!this.getAnytime()) ){
                //For logging the statistics.
                numbOfEval+= this.getPopulationSize();                
                searchTime = diffAnyTime;
                actualGeneration = generation;  
                finalPopGroupedBySpecie = (ArrayList<CandidateProgram>[])new ArrayList[this.getNumberOfSpecies()];
                long currentTime = System.nanoTime();
                long diffTime =  (currentTime - startTime)/1000000;                   
                
                for (int s = 0; s < this.getNumberOfSpecies(); s++) {
                    Collections.sort(popGroupedBySpecieAux[s]);
                    for (CandidateProgram cp : popGroupedBySpecieAux[s]) {
                        GRCandidateProgram gcp = (GRCandidateProgram) cp;
                        this.addToLogBuffer(s, gcp, interBuffer_case1, interBuffer_case2, interBuffer_case3, interBuffer_case4, interBuffer_case5);

                    }
                    
                    
                    
                    
                    finalPopGroupedBySpecie[s] = new ArrayList<CandidateProgram>();
                    finalPopGroupedBySpecie[s].addAll(popGroupedBySpecieAux[s]);
                    //For logging...
                    bestFitness[s] = ((GRCandidateProgram) popGroupedBySpecieAux[s].get(popGroupedBySpecieAux[s].size()-1)).getFitnessValue();
                    worstFitness[s] = ((GRCandidateProgram) popGroupedBySpecieAux[s].get(0)).getFitnessValue();
                    avgFitness[s] = EvolutionaryUtil.getAvgFitnessGGP(popGroupedBySpecieAux[s]); 
                    stdevFitness[s] = EvolutionaryUtil.getPopStdDevGGP(popGroupedBySpecieAux[s], avgFitness[s]);
   
                    
                    log = s  + ";" + diffTime + ";" + generation + ";" + worstFitness[s] + ";" + avgFitness[s] + ";" + bestFitness[s] + ";" + stdevFitness[s] + "\n";
//                    System.out.print(log);
                    loggingBuffer[s].append(log);
                    //Saving the best of the generations of each spece.
                    bestOfTheGenerations.addLast(popGroupedBySpecieAux[s].get(popGroupedBySpecieAux[s].size()-1));
                    fitness += avgFitness[s];
                    
                
                    //Print the evaluated individuals (with the fitness values) by specie.              
                    for (CandidateProgram cp : popGroupedBySpecieAux[s]) {
                        GRCandidateProgram gcp = (GRCandidateProgram) cp;
                        String gcp_grammar = gcp.toString();
                        //Save the evaluated individuals to save computational time.
                        if (!saveCompTime.containsKey(gcp_grammar)) {
                            saveCompTime.put(gcp_grammar, gcp.getFitnessValue());
                        }
                        log = gcp.getSpecie() + "#" + gcp_grammar + "#" + gcp.getFitnessValue() + "\n";
                        System.out.print(log);
                        generationBuffer.append(log);
                    }
                    
                    if (this.getReinit() > 0) {
                        //It defines the convergence criterion through fitness history.
                        for (int i = 0; i < phenotypeHistory.length; i++) {
                            if ((phenotypeHistory.length - i - 1) != 0) {
                                phenotypeHistory[phenotypeHistory.length - i - 1][s] = phenotypeHistory[phenotypeHistory.length - i - 2][s];
                            } else {
                                phenotypeHistory[phenotypeHistory.length - i - 1][s] = ((GRCandidateProgram) popGroupedBySpecieAux[s].get(popGroupedBySpecieAux[s].size() - 1)).toString();
                            }
                            log = "#pos: " + (phenotypeHistory.length - i - 1) + "--specie:" + s + "--phenotypeHistory: " + phenotypeHistory[phenotypeHistory.length - i - 1][s] + "\n";
                            convergenceBuffer.append(log);
                        }
                        convergenceBuffer.append("#######################");
                    }
                    
                } 
                
                fitness /= this.getNumberOfSpecies();
                double diff = fitness - oldFitness;
                if(generation != 0){
                    interXOverBuffer_aux.append(fitness).append("\n").append(diff).append("\n\n");
                    intraXOverBuffer_aux.append(fitness).append("\n").append(diff).append("\n\n");
                }
                
                
            }            
            if(diffAnyTime > searchTimeBudgetMilSec){
                break;
            }
            
            if (this.getStepTimeLimit() > 0) {
                if (posInt < (intemBudgetsAux.length - 1)) {
                    if ((diffAnyTime == intemBudgetsAux[posInt]) || (diffAnyTime > intemBudgetsAux[posInt] && diffAnyTime < intemBudgetsAux[posInt + 1])) {
                        System.out.println("Saving intermediate results for time budget "+(intemBudgets[posInt]/60000));
                        date = new Date();
                        dateFormat = new SimpleDateFormat(strDateFormat);
                        formattedDate = dateFormat.format(date);
                        System.out.println(formattedDate);
                        
                        long intemediateStartTime = System.nanoTime(); 
                        
                        System.out.println("bestOfTheGenerations size: "+bestOfTheGenerations.size());
                        if ((this.getMultiFidelityMode() == 0) || (this.getMultiFidelityMode() == 1) || (this.getMultiFidelityMode() == 2)) {
                            ir = new IntermediateResults4Speciation(intemBudgets[posInt], popGroupedBySpecieAux, bestOfTheReinitializations, new LinkedList<CandidateProgram>(bestOfTheGenerations), usedSeedResample, nLabels, generationBuffer, convergenceBuffer, numbOfEval,startTime,
                                                         searchTime, actualGeneration, loggingBuffer, numbOfReinit, false, nFoldsToLearn, nFoldsToValid);
                        } else if ((this.getMultiFidelityMode() == 3) || (this.getMultiFidelityMode() == 4) || (this.getMultiFidelityMode() == 5)) {
                            ir = new IntermediateResults4Speciation(intemBudgets[posInt], popGroupedBySpecieAux, bestOfTheReinitializations, new LinkedList<CandidateProgram>(bestOfTheGenerations), usedSeedResample,nLabels,generationBuffer,convergenceBuffer,numbOfEval,startTime,
                                                         searchTime, actualGeneration, loggingBuffer, numbOfReinit, false, nFoldsToValid, nFoldsToLearn);
                        }                     
                        
                        

                        intResults.add(ir);

//                        this.saveResults(intemBudgets[posInt], popGroupedBySpecieAux, bestOfTheReinitializations, usedSeedResample, nLabels, generationBuffer, convergenceBuffer, numbOfEval, startTime, searchTime, actualGeneration, loggingBuffer, numbOfReinit, false);
                        long intemediateEndTime = System.nanoTime();
                        long intemediateDiffTime = (intemediateEndTime - intemediateStartTime) / 1000000;

                        
                        for (int i = posInt + 1; i < intemBudgetsAux.length; i++) {
                            intemBudgetsAux[i] += intemediateDiffTime;
                        }
                        searchTimeBudgetMilSec += intemediateDiffTime;
                        posInt++;


                        if ((this.getMultiFidelityMode() == 2) || (this.getMultiFidelityMode() == 5)) {
                            Util.noFeatureSelection(nLabels, intemBudgets[posInt], this.getTrainingDirectory());
                        } else if ((this.getMultiFidelityMode() == 0) || (this.getMultiFidelityMode() == 3)) {
                            steps--;
                            nAttributesToKeep = Util.getAggressivelyMultFidAttributes(nAttributes, steps);
                            Util.featureSelection(nAttributesToKeep, nLabels, intemBudgets[posInt], this.getTrainingDirectory());
                            saveCompTime = new HashMap<String,Double>();
                            usedSeedResample++; 
                        } else if ((this.getMultiFidelityMode() == 1) || (this.getMultiFidelityMode() == 4)) {
                            steps--;
                            nAttributesToKeep = Util.getNonAggressivelyMultFidAttributes(nAttributes, steps);
                            Util.featureSelection(nAttributesToKeep, nLabels, intemBudgets[posInt], this.getTrainingDirectory());
                            saveCompTime = new HashMap<String,Double>();
                            usedSeedResample++; 
                        }
                        
                        
                        
                        if ((this.getMultiFidelityMode() == 0) || (this.getMultiFidelityMode() == 1) || (this.getMultiFidelityMode() == 2)) {
                           nFoldsToLearn++;
                           nFoldsToValid--;
                           learningANDvalidationDataDir = Util.splitDataInAStratifiedWay(usedSeedResample, nFoldsToLearn, nFoldsToValid, nLabels, intemBudgets[posInt]);
                        } else if ((this.getMultiFidelityMode() == 3) || (this.getMultiFidelityMode() == 4) || (this.getMultiFidelityMode() == 5)) {
                            learningANDvalidationDataDir = Util.splitDataInAStratifiedWay(usedSeedResample, nFoldsToValid, nFoldsToLearn, nLabels, intemBudgets[posInt]);
                        }
                 
                        

                    }
                }
            }            

            popGroupedBySpecie = this.operateOverSpecies(popGroupedBySpecieAux, rng, groupSize, nLabels, nAttributesToKeep, interXOverBuffer_aux, intraXOverBuffer_aux, fitness, usedSeedReinit);
            

            for(int s=0; s<this.getNumberOfSpecies(); s++){
                generationAux[s]= generationAux[s] + 1;
            }
            
        } //The end of the evolutionary process.        
        System.gc(); 
        interXOverBuffer.append(oldFitness).append("\n").append(0.0).append("\n");
        intraXOverBuffer.append(oldFitness).append("\n").append(0.0).append("\n");     
        
        if ((this.getMultiFidelityMode() == 0) || (this.getMultiFidelityMode() == 1) || (this.getMultiFidelityMode() == 2)) {
            ir = new IntermediateResults4Speciation(intemBudgets[posInt], popGroupedBySpecieAux, bestOfTheReinitializations, bestOfTheGenerations, usedSeedResample, nLabels, generationBuffer, convergenceBuffer, numbOfEval,startTime,
                                         searchTime, actualGeneration, loggingBuffer, numbOfReinit, true, nFoldsToLearn, nFoldsToValid);
        } else if ((this.getMultiFidelityMode() == 3) || (this.getMultiFidelityMode() == 4) || (this.getMultiFidelityMode() == 5)) {
            ir = new IntermediateResults4Speciation(intemBudgets[posInt], popGroupedBySpecieAux, bestOfTheReinitializations, bestOfTheGenerations, usedSeedResample, nLabels, generationBuffer, convergenceBuffer, numbOfEval,startTime,
                                         searchTime, actualGeneration, loggingBuffer, numbOfReinit, true, nFoldsToValid, nFoldsToLearn);
        } 

        intResults.add(ir);
        
        System.out.println("\n\nPrinting results by time budget...");
        long newSeedToResample = this.getSeed();
        for(IntermediateResults4Speciation i : intResults){
            System.out.println("Best of reinitialization size: "+i.getBestOfTheReinitializations().size());
            newSeedToResample += 1000;
            System.out.println((i.getIntermediateBudget()/60000) + "min:");
            this.saveResults(i.getIntermediateBudget(), i.getPopGroupedBySpecie(), i.getBestOfTheReinitializations(), newSeedToResample, i.getnLabels(), i.getGenerationBuffer(), i.getConvergenceBuffer(), i.getNumbOfEval(),
                             i.getStartTime(), i.getSearchTime(), i.getActualGeneration(), i.getLoggingBuffer(), i.getNumbOfReinit(), i.isSaveLogFiles(), interXOverBuffer, intraXOverBuffer, i.getNFoldsToLearn(), i.getNFoldsToValid());
            System.out.println();
            
        }
        
        this.saveAdditionalResults(intResults, newSeedToResample);
        
        System.gc();
        
        Util.savingLog(interBuffer_case1, "LogCase1_OnlyXOverInter", this.getGeneraltimeLimit(), this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit());   
        Util.savingLog(interBuffer_case2, "LogCase2_XOverInterAndMutation", this.getGeneraltimeLimit(), this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit()); 
        Util.savingLog(interBuffer_case3, "LogCase3_OnlyXOverInra", this.getGeneraltimeLimit(), this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit()); 
        Util.savingLog(interBuffer_case4, "LogCase4_XOverInraAndMutation", this.getGeneraltimeLimit(), this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit()); 
        Util.savingLog(interBuffer_case5, "LogCase5_OnlyMutation", this.getGeneraltimeLimit(), this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit()); 
        
        Util.removeUnnecessaryFiles(intemBudgets, this.getExperimentName());
        
        
        date = new Date();
        dateFormat = new SimpleDateFormat(strDateFormat);
        formattedDate = dateFormat.format(date);
        System.out.println(formattedDate);

        System.exit(1);       
    }
    
    public void saveAdditionalResults(ArrayList<IntermediateResults4Speciation> intResults, long newSeedToResample) throws Exception{
        long seed = newSeedToResample + 1000;
        System.out.println("Evaluating the best individuals of each generation...");
        ArrayList<CandidateProgram> bests = null;
        HashMap<String,Double> saveCompTime = new HashMap<String,Double>();
        
        for(IntermediateResults4Speciation i : intResults){
            bests = new ArrayList<CandidateProgram>(i.getBestOfTheGenerations());
            for(CandidateProgram cp : bests){
                GRCandidateProgram gcp = (GRCandidateProgram) cp;
                gcp.setFitnessValue(-1.00);
            }  
            System.out.println("Size of best individuals array: " + bests.size());
            UptatedMetaIndividualGGP.evaluateIndividuals(bests, this.getTrainingDirectory(), this.getTestingDirectory(), this.getNumberOfThreads(), seed, saveCompTime, this.getAlgorithmTimeLimit()*3, this.getExperimentName(), this.getJavaDir(), this.getFitnessOption());
            Collections.sort(bests);
            
            System.out.print("Timestamp: ");
            int timeStamp = (int) (i.getIntermediateBudget());
            System.out.println(timeStamp+ "min:");
            
            StringBuilder strB = new StringBuilder();
            for(CandidateProgram cp : bests){
                GRCandidateProgram gcp = (GRCandidateProgram) cp;
                String gcp_grammar = gcp.toString();
                strB.append(gcp_grammar).append(";;;").append(gcp.getFitnessValue()).append("\n");
                System.out.println(gcp_grammar + "###" + gcp.getFitnessValue());
                //Save the evaluated individuals to save computational time.
                if (!saveCompTime.containsKey(gcp_grammar)) {
                    saveCompTime.put(gcp_grammar, gcp.getFitnessValue());
                }                 
            }
            Util.savingLog(strB, "TestResultsLog", timeStamp, this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit());
            
                    
        }
        

        
        
    }
    
    public void addToLogBuffer(int specie, GRCandidateProgram gcp,  final StringBuilder interBuffer_case1,  
                              final StringBuilder interBuffer_case2,  final StringBuilder interBuffer_case3,
                               final StringBuilder interBuffer_case4,  final StringBuilder interBuffer_case5){
        String extraLog = specie + ";;" + gcp.getParent1Phenotype() + ";" + gcp.getParent2Phenotype() + ";" + gcp.getParent1Fitness() + ";" + gcp.getParent2Fitness() + ";" + ((gcp.getParent1Fitness() + gcp.getParent2Fitness()) / 2) + ";" + gcp.getFitnessValue() + "\n";
        
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
            
    
    
    
    public ArrayList<CandidateProgram>[] operateOverSpecies(ArrayList<CandidateProgram>[] popGroupedBySpecieAux, 
                                                            MersenneTwisterFast rng, 
                                                            int groupSize, 
                                                            int n_labels, 
                                                            int n_attributes, 
                                                            StringBuilder interXOverBuffer, 
                                                            StringBuilder intraXOverBuffer, 
                                                            double oldFitness,
                                                            long usedSeedReinit) throws Exception {
        
        ArrayList<CandidateProgram>[] popGroupedBySpecie = (ArrayList<CandidateProgram>[])new ArrayList[this.getNumberOfSpecies()];
        interXOverBuffer.append("").append(oldFitness).append("\n");
        intraXOverBuffer.append("").append(oldFitness).append("\n");
//        System.out.print("Operate over specie: ");
        for (int s = 0; s < this.getNumberOfSpecies(); s++) {
//            System.out.print(s + "; ");
            //It defines the new population, by adding firstly the best individuals from the size of the elitism. 
            popGroupedBySpecie[s] = new ArrayList<CandidateProgram>();
            
            for (int e = 0; e < this.getElitismSize(); e++) {
                popGroupedBySpecie[s].add(popGroupedBySpecieAux[s].get(popGroupedBySpecieAux[s].size() - 1 - e));
            }

            int pop = this.getElitismSize();


            //It fulfills the population considering the individuals from elitism.
            while (pop < groupSize) {
                //Tournament.
                CandidateProgram parent1 = EvolutionaryUtil.getParentFromTournamentGGP(popGroupedBySpecieAux[s], rng, this.getTournamentSize());
                CandidateProgram parent2 = EvolutionaryUtil.getParentFromTournamentGGP(popGroupedBySpecieAux[s], rng, this.getTournamentSize());
//                System.out.println("general crossover...");
                //The returned individuals from (intra/inter crossover operation.
                CandidateProgram [] xChilds = EvolutionaryUtil.generalSpecieCrossover(popGroupedBySpecieAux, s, parent1, parent2, rng, interXOverBuffer, intraXOverBuffer, this.getCrossoverRate(), this.getIntraInterCrossoverRate(), this.getTournamentSize());
               
                CandidateProgram child1 = xChilds[0];
                CandidateProgram child2 = xChilds[1];
                
//                 System.out.println("mutation...");
                //And probabilistic mutation.
                double randomVar = rng.nextDouble();
                
                CandidateProgram mchild1 = null;
                CandidateProgram mchild2 = null;
                
                if(randomVar < this.getMutationRate()){
                    mchild1 = EvolutionaryUtil.mutateFromSpeciesConservative(child1, rng, n_labels, n_attributes, s, this.getSearchSpaceMode());
                    ((GRCandidateProgram) mchild1).setMutation(true);
                    ((GRCandidateProgram) mchild1).setParent1Fitness( ((GRCandidateProgram) mchild1).getFitnessValue());
                    ((GRCandidateProgram) mchild1).setParent1Phenotype(parent1.toString());
                    mchild2 = EvolutionaryUtil.mutateFromSpeciesConservative(child2, rng, n_labels, n_attributes, s, this.getSearchSpaceMode());
                    ((GRCandidateProgram) mchild2).setMutation(true);
                    ((GRCandidateProgram) mchild2).setParent1Fitness(((GRCandidateProgram) mchild2).getFitnessValue());
                    ((GRCandidateProgram) mchild2).setParent1Phenotype(parent2.toString());                    
                    
                }else{
                    mchild1 = child1.clone();
                    mchild2 = child2.clone();
                }
            

                popGroupedBySpecie[s].add(mchild1);
                pop++;
                if (pop < groupSize) {
                    popGroupedBySpecie[s].add(mchild2);
                    pop++;
                }
            }
            //Reset the individuals.
            for (CandidateProgram cp : popGroupedBySpecie[s]) {
                ((GRCandidateProgram) cp).reset();
            }
            
//            System.out.println("\n\n\n\n\n\n\n\n\n\n\n_______________________________________________");
        }
        System.out.println("");
        return popGroupedBySpecie;
    }
        
    
    
    
    
    
    public void saveResults(long budget, ArrayList<CandidateProgram>[] popGroupedBySpecie, final ArrayList<CandidateProgram> bestOfTheReinitializations, 
                            long usedSeedResample, int nLabels, final StringBuilder generationBuffer, final StringBuilder convergenceBuffer, int numbOfEval, long startTime, 
                            long searchTime, int actualGeneration,  StringBuilder[] loggingBuffer, int numbOfReinit, boolean saveLogFiles, StringBuilder interXOverBuffer, StringBuilder intraXOverBuffer, int nFoldsToLearn, int nFoldsToValid) throws Exception{
      
        if (popGroupedBySpecie != null) {
            //Save the final best individuals of each specie.
            for (int s = 0; s < this.getNumberOfSpecies(); s++) {
                //Save the best of the specie s.
                bestOfTheReinitializations.add(popGroupedBySpecie[s].get(popGroupedBySpecie[s].size() - 1));
            }
        }
        //It resamples again to check the best individual in the whole evolutionary process.
        long newSeedToResample = usedSeedResample;
//        System.out.println(newSeedToResample);
        String[] learningANDvalidationDataDir = Util.splitDataInAStratifiedWay(newSeedToResample, nFoldsToLearn, nFoldsToValid, nLabels, budget);
        //It chooses among the best individuals of all generations.
        ArrayList<CandidateProgram> m_bestAlgorithms = new ArrayList<CandidateProgram>(EvolutionaryUtil.getBestAlgorithmsGGP(generationBuffer, bestOfTheReinitializations, newSeedToResample, learningANDvalidationDataDir, this.getNumberOfThreads(), this.getSeed(), this.getAlgorithmTimeLimit(), this.getExperimentName(), this.getJavaDir()));
        
//        CandidateProgram m_bestAlgorithm = this.chooseAmongBestAlgorithms(generationBuffer, bestOfTheReinitializations, newSeedToResample, learningANDvalidationDataDir);
        int newNumbOfEval = numbOfEval+bestOfTheReinitializations.size();
        
        
        //Measuring the elapsed time to run the GGP.
        long endTime = System.nanoTime();        
        long differenceTime = (endTime - startTime)/1000000;; 

        //Saving the results...
        EvolutionaryUtil.savingMLCResultsGGP(budget, generationBuffer, m_bestAlgorithms, searchTime, differenceTime, actualGeneration, learningANDvalidationDataDir, newNumbOfEval, numbOfReinit,
                                 this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit(), this.getTrainingDirectory(), this.getTestingDirectory(), this.getJavaDir());    
        
  
        generationBuffer.append("==============================================\n");
        //And the log of each generation.
        
        if (saveLogFiles) {
            String log = "";
            for (int s = 0; s < this.getNumberOfSpecies(); s++) {
                log = "#;" + "#;" + "#;" + "#;" + "\n";
                loggingBuffer[s].append(log);
                EvolutionaryUtil.savingFitnessLog(loggingBuffer[s], budget, this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit(), true);
            }
            Util.savingLog(generationBuffer, "LogGenerations", budget, this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit());
            Util.savingLog(convergenceBuffer, "LogConvergence", budget, this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit());
            Util.savingLog(interXOverBuffer, "LogInterXOver", budget, this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit());
            Util.savingLog(intraXOverBuffer, "LogIntraXOver", budget, this.getSavingDirectory(), this.getExperimentName(), this.getSeed(), this.getFoldInit());
        }
        
  
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
        AbstractMultiLabelClassifier.runClassifier(new AutoMEKA_SpGGP_tst3(args), args);
    }
}