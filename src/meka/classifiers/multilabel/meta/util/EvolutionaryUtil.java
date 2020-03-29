/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meka.classifiers.multilabel.meta.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import meka.classifiers.multilabel.meta.automekaggp.core.GrammarDefinitions;
import meka.classifiers.multilabel.meta.automekaggp.core.MetaIndividualGGP;
import meka.classifiers.multilabel.meta.gaautomlc.core.MetaIndividualGA;
import meka.classifiers.multilabel.meta.gaautomlc.core.xmlparser.Allele;
import meka.classifiers.multilabel.meta.gaautomlc.core.xmlparser.XMLAlgorithmHandler;
import meka.classifiers.multilabel.meta.gaautomlc.core.xmlparser.XMLGeneHandler;
import org.epochx.gr.op.crossover.CrossoverOutput;
import org.epochx.gr.op.crossover.WhighamCrossover;
import org.epochx.gr.op.init.GrowInitialiser;
import org.epochx.gr.op.init.RampedHalfAndHalfInitialiser;
import org.epochx.gr.op.mutation.WhighamMutation;
import org.epochx.gr.representation.GRCandidateProgram;
import org.epochx.representation.CandidateProgram;
import org.epochx.tools.grammar.Grammar;
import org.epochx.tools.random.MersenneTwisterFast;

/**
 *
 * @author alexgcsa
 */
public class EvolutionaryUtil {
    

    /**
     * Reinit potulation for GGP.
     * @param popSize
     * @param grammar
     * @param usedSeedReinit
     * @param nLabels
     * @param nAttributes
     * @param grammarMode
     * @return 
     */
    public static ArrayList<CandidateProgram> reInitPopulationGGP(int popSize, 
                                                              Grammar grammar, 
                                                              long usedSeedReinit, 
                                                              int nLabels, 
                                                              int nAttributes, 
                                                              int grammarMode
                                                             ){
        
        MersenneTwisterFast rng = new MersenneTwisterFast(usedSeedReinit);
        
        boolean acceptDuplicates = false;
        if(grammarMode==0){
            acceptDuplicates = true;
        }
        //Initialiser and groups by species.
        RampedHalfAndHalfInitialiser initPop = new RampedHalfAndHalfInitialiser(rng, grammar, popSize, 2, 100, acceptDuplicates, nLabels, nAttributes);
        ArrayList<CandidateProgram> population = new ArrayList<CandidateProgram>(initPop.getInitialPopulation());
        
        return population;        
    }
    
    /**
     * Reinit potulation for GA.
     * @param popSize
     * @param usedSeedReinit
     * @param xmlAlgorithmHandler
     * @param geneSize
     * @return 
     * @throws java.lang.Exception 
     */
    public static ArrayList<MetaIndividualGA> reInitPopulationGA(int popSize, 
                                                              long usedSeedReinit, 
                                                              XMLAlgorithmHandler xmlAlgorithmHandler,
                                                              int geneSize,
                                                              int nAttributes,
                                                              int nLabels,
                                                              int numberOfThreads
                                                             ) throws Exception{
        
        MersenneTwisterFast rng = new MersenneTwisterFast(usedSeedReinit);
        ArrayList<MetaIndividualGA> population = new ArrayList<MetaIndividualGA>();
//        ArrayList<XMLGeneHandler> xmlGeneHandlerList = new ArrayList<XMLGeneHandler>();  
//        ArrayList<Double> doubleIDList = new ArrayList<Double>();        
        for (int init = 0; init < popSize; init++) {
            double doubleID = rng.nextDouble();
//            doubleIDList.add(doubleID);
            int fileID = (int) ((xmlAlgorithmHandler.getAlgorithmsFiles().size()) * doubleID);
            XMLGeneHandler xmlGeneHandler = new XMLGeneHandler(new File(xmlAlgorithmHandler.getAlgorithmsFiles().get(fileID)), nAttributes, nLabels);
            
            Allele genes = xmlGeneHandler.getGenes();

            double[] randomChromossome = generateRandomChromosome(geneSize, rng, doubleID);
            population.add(new MetaIndividualGA(randomChromossome, genes, 0.0));                  
//            xmlGeneHandlerList.add(xmlGeneHandler);

        }
//        XMLGeneHandlerUtil_remove xmlGeneUtil= new XMLGeneHandlerUtil_remove();
//       
//        ArrayList<XMLGeneHandler> xmlGeneHandlerListFinal = new ArrayList<XMLGeneHandler>(xmlGeneUtil.performParallelParsing(xmlGeneHandlerList, numberOfThreads));           
//        for (int init = 0; init < popSize; init++) {
// 
//        }        
        
         
            
            

        
        return population;        
    }
    
    /**
     * Random chromosome to the initial population.
     * @param size
     * @param rnd
     * @param firstValue
     * @return 
     */
    public static double[] generateRandomChromosome(int size, MersenneTwisterFast rnd, double firstValue) {
        double[] chromosome = new double[size + 1];
        chromosome[0] = firstValue;
        for (int i = 1; i <= size; i++) {
            chromosome[i] = rnd.nextDouble();
        }
        return chromosome;
    }    
    
    
    
    /**
     * 
     * @param phenotypeHistory
     * @param generation
     * @param reinit
     * @param convergenceGen
     * @return 
     */
    public static boolean testForReinitialization(String[] phenotypeHistory, 
                                                  int generation, 
                                                  int reinit, 
                                                  int convergenceGen
                                                 ){
        boolean doReinitialization = true;
        
        if(reinit <= 0){
            return false;
        }
        
        if(generation >= convergenceGen){
            for(int i=0; i<phenotypeHistory.length-1; i++){
                if(!phenotypeHistory[i].equals(phenotypeHistory[i+1])){
                    doReinitialization = false;                    
                }
            }     
        }else{
           return false;
        }
        
   
        return doReinitialization;
    } 
        
    
    /**
     * 
     * @param phenotypeHistory
     * @param specie
     * @param generation
     * @param reinit
     * @param convergenceGen
     * @return 
     */
    public static boolean testForReinitializationForSpecies(String[][] phenotypeHistory, 
                                                            int specie, 
                                                            int generation, 
                                                            int reinit, 
                                                            int convergenceGen
                                                           ){
        boolean doReinitialization = true;
        
        if(reinit <= 0){
            return false;
        }
        
        if(generation >= convergenceGen){
            for(int i=0; i<phenotypeHistory.length-1; i++){
                if(!phenotypeHistory[i][specie].equals(phenotypeHistory[i+1][specie])){
                    doReinitialization = false;
                }
            }     
        }else{
           return false;
        }
        
   
        return doReinitialization;
    }
    
    /**
     * It applies mutation operator on an individual
     * @param individual to be mutated.
     * @param rnd to define the random generator.
     * @param n_labels
     * @param attributes the number of attributes of the dataset

     * @return a mutated individual, which is a candidate program.
     */
    public static CandidateProgram mutateConservative(CandidateProgram individual, 
                                          MersenneTwisterFast rnd,
                                          int n_labels, 
                                          int attributes
                                         ){

        double randomVar = rnd.nextDouble();
        CandidateProgram mchild = individual.clone();
//        if (randomVar < mutationRate) {
        WhighamMutation mutation = new WhighamMutation(rnd, n_labels, attributes);
        mchild = mutation.mutate(individual);
//        }        
        
        return mchild;
    }       
    
    /**
     * It applies mutation operator on an individual
     * @param individual to be mutated.
     * @param rnd to define the random generator.
     * @param n_labels
     * @param attributes the number of attributes of the dataset
     * @param mutationRate
     * @return a mutated individual, which is a candidate program.
     */
    public static CandidateProgram mutate(CandidateProgram individual, 
                                          MersenneTwisterFast rnd,
                                          int n_labels, 
                                          int attributes, 
                                          double mutationRate
                                         ){

        double randomVar = rnd.nextDouble();
        CandidateProgram mchild = individual.clone();
        if (randomVar < mutationRate) {
            WhighamMutation mutation = new WhighamMutation(rnd, n_labels, attributes);
            mchild = mutation.mutate(individual);
        }        
        
        return mchild;
    }   
    
    /**
     * It applies mutation operator on an individual
     * @param individual to be mutated.
     * @param rnd to define the random generator.
     * @param n_labels the number of labels of the dataset.
     * @param n_attributes the number of attributes of the dataset
     * @param specie the current specie to be mutated
     * @param grammarMode
     * @return a mutated individual, which is a candidate program.
     */
    public static CandidateProgram mutateFromSpeciesConservative(CandidateProgram individual, 
                                    MersenneTwisterFast rnd,
                                    int n_labels, 
                                    int n_attributes, 
                                    int specie,
                                    int grammarMode
                                   ){

        CandidateProgram mchild = individual.clone();       
        WhighamMutation mutation = new WhighamMutation(rnd, n_labels, n_attributes);

        GrammarDefinitions grammars = new GrammarDefinitions();
        Grammar grammar = new Grammar(grammars.getGrammarDefinition(specie, grammarMode));
        GrowInitialiser grow = new GrowInitialiser(rnd, grammar, 1, 100, false, n_labels, n_attributes);
        

        mchild = mutation.mutate(individual, grammar, grow);
        System.err.println("####Mutation child: "+ mchild.toString());

        return mchild;
    }    
    
    /**
     * It applies mutation operator on an individual
     * @param individual to be mutated.
     * @param rnd to define the random generator.
     * @param n_labels the number of labels of the dataset.
     * @param n_attributes the number of attributes of the dataset
     * @param specie the current specie to be mutated
     * @param mutationRate
     * @param grammarMode
     * @return a mutated individual, which is a candidate program.
     */
    public static CandidateProgram mutateFromSpecies(CandidateProgram individual, 
                                    MersenneTwisterFast rnd,
                                    int n_labels, 
                                    int n_attributes, 
                                    int specie,
                                    double mutationRate,
                                    int grammarMode
                                   ){
        
        double randomVar = rnd.nextDouble();
        CandidateProgram mchild = individual.clone();
//        do{
//            System.out.println("Mutating...");
        if (randomVar < mutationRate) {
            WhighamMutation mutation = new WhighamMutation(rnd, n_labels, n_attributes);

            GrammarDefinitions grammars = new GrammarDefinitions();
            Grammar grammar = new Grammar(grammars.getGrammarDefinition(specie, grammarMode));
            GrowInitialiser grow = new GrowInitialiser(rnd, grammar, 1, 100, false, n_labels, n_attributes);

            mchild = mutation.mutate(individual, grammar, grow);
//                child_str = mchild.toString();
//                System.out.println("ind_str: "+ind_str);
//                System.out.println("child_str: "+child_str);
        }
//            localSeed++;
//        }while(mchild==null);
        return mchild;
    }
    
    /**
     * It performs intra-specie crossover.
     * @param population the population of individuals for one specific specie
     * @param individual1 the first individual to perform crossover on.
     * @param individual2 the first individual to perform crossover on.
     * @param rnd to define the random generator.
     * @param tournamentSize
     * @return an array of individuals that suffered intra-specie crossover.
     * @throws Exception 
     */
    public static CandidateProgram[] crossover(ArrayList<CandidateProgram> population,                                                    
                                               CandidateProgram individual1, 
                                               CandidateProgram individual2, 
                                               StringBuilder intraXOverBuffer,
                                               MersenneTwisterFast rnd,
                                               double crossoverRate,
                                               int tournamentSize
                                              ) throws Exception{
        
        double randomVar = rnd.nextDouble();
        CandidateProgram[] xoverprograms = new CandidateProgram[2]; 
        CrossoverOutput xoverResult = null;
         
         
        if (randomVar < crossoverRate) {
            WhighamCrossover xover = new WhighamCrossover(rnd);
            CandidateProgram parent1 = individual1.clone();
            CandidateProgram parent2 = individual2.clone();
           

            do {
                xoverResult = xover.standardCrossover(parent1.clone(), parent2.clone());
                if (xoverResult == null) {
//                System.out.println("Choosing another parent2");
                    parent2 = EvolutionaryUtil.getParentFromTournamentGGP(population, rnd, tournamentSize);
                }
            } while (xoverResult == null);

            intraXOverBuffer.append(xoverResult.getSubtree1().getGrammarRule().getName()).append(";");
            intraXOverBuffer.append(xoverResult.getSubtree2().toString()).append(";");
            intraXOverBuffer.append(xoverResult.getSubtree1().toString()).append(";");
            intraXOverBuffer.append(parent1.toString()).append(";");
            intraXOverBuffer.append(parent2).append(";");
            intraXOverBuffer.append(xoverResult.getChildren()[0]).append(";");
            intraXOverBuffer.append(xoverResult.getChildren()[1]).append("\n");
            
            xoverprograms[0] = xoverResult.getChildren()[0];
            xoverprograms[1] = xoverResult.getChildren()[1];
        for (CandidateProgram child : xoverprograms) {
            ((GRCandidateProgram) child).setIntraSpecieXOver(true);
            ((GRCandidateProgram) child).setParent1Fitness(((GRCandidateProgram) parent1).getFitnessValue());
            ((GRCandidateProgram) child).setParent2Fitness(((GRCandidateProgram) parent2).getFitnessValue());
            ((GRCandidateProgram) child).setParent1Phenotype( ((GRCandidateProgram) parent1).toString() );
            ((GRCandidateProgram) child).setParent2Phenotype( ((GRCandidateProgram) parent2).toString() );
        }            

        }else{
             xoverprograms[0] = individual1.clone();
             xoverprograms[1] = individual2.clone();
        }
        
        
        
        return xoverprograms;
    }        
    
    /**
     * It applies crossover operator on two individuals
     * @param population
     * @param individual1 the first parent to suffer crossover.      
     * @param individual2 the second parent to suffer crossover.
     * @param rnd
     * @param crossoverRate
     * @param tournamentSize
     * @return the individuals that suffered crossover.
     * @throws java.lang.Exception
     */
    public static CandidateProgram[] crossover(ArrayList<CandidateProgram> population, 
                                               CandidateProgram individual1, 
                                               CandidateProgram individual2, 
                                               MersenneTwisterFast rnd,
                                               double crossoverRate,
                                               int tournamentSize
                                              ) throws Exception{
       
        CandidateProgram parent1 = individual1.clone();
        CandidateProgram parent2 = individual2.clone();
  
        CandidateProgram[] xoverprograms = null;        
        double randomVar = rnd.nextDouble();
       
        
        if (randomVar < crossoverRate) {
            WhighamCrossover xover = new WhighamCrossover(rnd);            
            
            do{
                xoverprograms = xover.crossover(parent1.clone(), parent2.clone());
                if(xoverprograms==null){
                    
                    parent2 = EvolutionaryUtil.getParentFromTournamentGGP(population, rnd, tournamentSize);
                }              
            }while(xoverprograms==null);           
        }else{
             xoverprograms = new CandidateProgram[2];
             xoverprograms[0] = parent1.clone();
             xoverprograms[1] = parent2.clone();
        }      
        return xoverprograms;
    }
    
    
    /**
     * It applies crossover operator on two individuals
     * @param popGroupedBySpecie
     * @param specie the actual specie to apply intra-specie crossover
     * @param individual1 the first parent to suffer crossover.      
     * @param individual2 the second parent to suffer crossover.
     * @param rnd to define the random generator.
     * @param interXOverBuffer
     * @param crossoverRate
     * @param intraInterCrossoverRate
     * @param tournamentSize
     * @return the individuals that suffered crossover.
     * @throws java.lang.Exception
     */
    public static CandidateProgram[] generalSpecieCrossover(ArrayList<CandidateProgram>[] popGroupedBySpecie, 
                                                            int specie, 
                                                            CandidateProgram individual1, 
                                                            CandidateProgram individual2, 
                                                            MersenneTwisterFast rnd, 
                                                            StringBuilder interXOverBuffer,
                                                            StringBuilder intraXOverBuffer,
                                                            double crossoverRate,
                                                            double intraInterCrossoverRate,
                                                            int tournamentSize
                                                           ) throws Exception{
       
        CandidateProgram[] xoverprograms = null;        
        double randomVar = rnd.nextDouble();
        //Crossover:
        if (randomVar < crossoverRate) {            
            double randomVar2 = rnd.nextDouble();  
            if(randomVar2 < intraInterCrossoverRate){
                //Inter-specie crossover:
                xoverprograms = new CandidateProgram[2];
                CandidateProgram[] xOverResult1 = new CandidateProgram[2];
                CandidateProgram[] xOverResult2 = new CandidateProgram[2];
                
                xOverResult1 = EvolutionaryUtil.interCrossover(popGroupedBySpecie, specie, individual1, rnd, interXOverBuffer, tournamentSize, 0);
                xOverResult2 = EvolutionaryUtil.interCrossover(popGroupedBySpecie, specie, individual2, rnd, interXOverBuffer, tournamentSize, 0);
                
                double randomVar3 = rnd.nextDouble();
                if(randomVar3 >= 0.0 && randomVar3 < 0.25){
                   xoverprograms[0] =  xOverResult1[0];
                   xoverprograms[1] =  xOverResult2[0];
                }else if(randomVar3 >= 0.25 && randomVar3 < 0.50){
                   xoverprograms[0] =  xOverResult1[0];
                   xoverprograms[1] =  xOverResult2[1];
                }else if(randomVar3 >= 0.50 && randomVar3 < 0.75){
                   xoverprograms[0] =  xOverResult1[1];
                   xoverprograms[1] =  xOverResult2[0];
                }else if(randomVar3 >= 0.75){
                   xoverprograms[0] =  xOverResult1[1];
                   xoverprograms[1] =  xOverResult2[1];
                }
                
                
                 
            }else{
                //Intra-specie crossover:
                xoverprograms = EvolutionaryUtil.intraCrossover(popGroupedBySpecie[specie], specie, individual1, individual2, intraXOverBuffer, rnd, tournamentSize);              
            }         
        }else{
             xoverprograms = new CandidateProgram[2];
             xoverprograms[0] = individual1.clone();
             xoverprograms[1] = individual2.clone();
        } 
        
        return xoverprograms;
    }
    
    /**
     * It performs intra-specie crossover.
     * @param population the population of individuals for one specific specie
     * @param individual1 the first individual to perform crossover on.
     * @param individual2 the first individual to perform crossover on.
     * @param rnd to define the random generator.
     * @param tournamentSize
     * @return an array of individuals that suffered intra-specie crossover.
     * @throws Exception 
     */
    public static CandidateProgram[] intraCrossover_old(ArrayList<CandidateProgram> population,
                                                    int specie,
                                                    CandidateProgram individual1, 
                                                    CandidateProgram individual2, 
                                                    StringBuilder intraXOverBuffer,
                                                    MersenneTwisterFast rnd,
                                                    int tournamentSize
                                                   ) throws Exception{
        WhighamCrossover xover = new WhighamCrossover(rnd);
        CandidateProgram parent1 = individual1.clone();
        CandidateProgram parent2 = individual2.clone(); 
        CrossoverOutput xoverResult = null;
        intraXOverBuffer.append(specie).append(";");
        
        do{
            xoverResult = xover.standardCrossover(parent1.clone(), parent2.clone());
            if( xoverResult==null){         
//                System.out.println("Choosing another parent2");
                parent2 = EvolutionaryUtil.getParentFromTournamentGGP(population, rnd, tournamentSize);
            }              
        }while( xoverResult==null); 
        
        
        intraXOverBuffer.append(xoverResult.getSubtree1().getGrammarRule().getName()).append(";");
        intraXOverBuffer.append(xoverResult.getSubtree2().toString()).append(";");
        intraXOverBuffer.append(xoverResult.getSubtree1().toString()).append(";");
        intraXOverBuffer.append(parent1.toString()).append(";");
        intraXOverBuffer.append(parent2).append(";");
        intraXOverBuffer.append(xoverResult.getChildren()[0]).append(";");
        intraXOverBuffer.append(xoverResult.getChildren()[1]).append("\n");        
        
        return xoverResult.getChildren();
    }    
    
    /**
     * It performs intra-specie crossover.
     * @param population the population of individuals for one specific specie
     * @param individual1 the first individual to perform crossover on.
     * @param individual2 the first individual to perform crossover on.
     * @param rnd to define the random generator.
     * @param tournamentSize
     * @return an array of individuals that suffered intra-specie crossover.
     * @throws Exception 
     */
    public static CandidateProgram[] intraCrossover(ArrayList<CandidateProgram> population,
                                                    int specie,
                                                    CandidateProgram individual1, 
                                                    CandidateProgram individual2, 
                                                    StringBuilder intraXOverBuffer,
                                                    MersenneTwisterFast rnd,
                                                    int tournamentSize
                                                   ) throws Exception{
        WhighamCrossover xover = new WhighamCrossover(rnd);
        CandidateProgram parent1 = individual1.clone();
        CandidateProgram parent2 = individual2.clone(); 
        CrossoverOutput xoverResult = null;
        intraXOverBuffer.append(specie).append(";");
        
        do{
            xoverResult = xover.standardCrossover(parent1.clone(), parent2.clone());

            if( xoverResult==null){         
//                System.out.println("Choosing another parent2");
                parent2 = EvolutionaryUtil.getParentFromTournamentGGP(population, rnd, tournamentSize);
            }              
        }while( xoverResult==null); 
        
        
        intraXOverBuffer.append(xoverResult.getSubtree1().getGrammarRule().getName()).append(";");
        intraXOverBuffer.append(xoverResult.getSubtree2().toString()).append(";");
        intraXOverBuffer.append(xoverResult.getSubtree1().toString()).append(";");
        intraXOverBuffer.append(parent1.toString()).append(";");
        intraXOverBuffer.append(parent2).append(";");
        intraXOverBuffer.append(xoverResult.getChildren()[0]).append(";");
        intraXOverBuffer.append(xoverResult.getChildren()[1]).append("\n"); 
        
        CandidateProgram[] children = new CandidateProgram[xoverResult.getChildren().length];
        children[0] = xoverResult.getChildren()[0];
        children[1] = xoverResult.getChildren()[1];
        for (CandidateProgram child : children) {
            ((GRCandidateProgram) child).setIntraSpecieXOver(true);
            ((GRCandidateProgram) child).setParent1Fitness(((GRCandidateProgram) parent1).getFitnessValue());
            ((GRCandidateProgram) child).setParent2Fitness(((GRCandidateProgram) parent2).getFitnessValue());
            ((GRCandidateProgram) child).setParent1Phenotype( ((GRCandidateProgram) parent1).toString() );
            ((GRCandidateProgram) child).setParent2Phenotype( ((GRCandidateProgram) parent2).toString() );
        }

        
        
        return children;
    }      
    
    /**
     * It performs inter-specie crossover.
     * @param popGroupedBySpecie
     * @param specie
     * @param individual the first individual to perform crossover on.
     * @param rnd to define the random generator.
     * @param interXOverBuffer
     * @param tournamentSize
     * @return an array of individuals that suffered inter-specie crossover.
     * @throws Exception 
     */
    public static CandidateProgram[] interCrossover_old(ArrayList<CandidateProgram>[] popGroupedBySpecie, 
                                                  int specie, 
                                                  CandidateProgram individual, 
                                                  MersenneTwisterFast rnd, 
                                                  StringBuilder interXOverBuffer,
                                                  int tournamentSize,
                                                  int option
                                                 ) throws Exception{
        WhighamCrossover xover = new WhighamCrossover(rnd);
        CandidateProgram[] xoverprograms = null;
        CrossoverOutput xoverResult = null;
        CandidateProgram parent = individual.clone();   
//        System.out.println("Parent1: "+ ((GRCandidateProgram) parent).toString());
        

        int[] ex = {specie};
        int interSpecie = EvolutionaryUtil.getRandomWithExclusion(rnd, 0, popGroupedBySpecie.length-1, ex);
        CandidateProgram interParent = EvolutionaryUtil.getParentFromTournamentGGP(popGroupedBySpecie[interSpecie], rnd, tournamentSize);
        
        interXOverBuffer.append(specie).append(";").append(interSpecie).append(";");
//        System.out.println("Parent2: "+ ((GRCandidateProgram) interParent).toString());        
        do{
            xoverResult = xover.crossoverInterSpecie(parent.clone(), interParent.clone());            
            if(xoverResult==null){    
//                System.out.println("Choosing another inter-parent");
                interParent = EvolutionaryUtil.getParentFromTournamentGGP(popGroupedBySpecie[interSpecie], rnd, tournamentSize);
            }              
        }while(xoverResult==null);

        xoverprograms = xoverResult.getChildren();
        CandidateProgram [] xOverChild = new CandidateProgram[2];
//        if(rnd.nextDouble() <= 0.5){
        xOverChild[0] = xoverprograms[0];
//            }else{
        xOverChild[1] = xoverprograms[1];  
//        }
        interXOverBuffer.append(xoverResult.getSubtree1().getGrammarRule().getName()).append(";");
        interXOverBuffer.append(xoverResult.getSubtree2().toString()).append(";");
        interXOverBuffer.append(xoverResult.getSubtree1().toString()).append(";");
        interXOverBuffer.append(parent.toString()).append(";");
        interXOverBuffer.append(interParent).append(";");
        interXOverBuffer.append(xOverChild[option].toString()).append("\n");
        
//        System.out.println("Child" + i + ": "+((GRCandidateProgram) xOverChild).toString());
       
        ((GRCandidateProgram) xOverChild[option]).setSpecie(specie);
//        System.out.println("------------------------------------------------------------------------------");
        return xOverChild;
    }  
    
    /**
     * It performs inter-specie crossover.
     * @param popGroupedBySpecie
     * @param specie
     * @param individual the first individual to perform crossover on.
     * @param rnd to define the random generator.
     * @param interXOverBuffer
     * @param tournamentSize
     * @return an array of individuals that suffered inter-specie crossover.
     * @throws Exception 
     */
    public static CandidateProgram[] interCrossover(ArrayList<CandidateProgram>[] popGroupedBySpecie, 
                                                  int specie, 
                                                  CandidateProgram individual, 
                                                  MersenneTwisterFast rnd, 
                                                  StringBuilder interXOverBuffer,
                                                  int tournamentSize,
                                                  int option
                                                 ) throws Exception{
        WhighamCrossover xover = new WhighamCrossover(rnd);
        CandidateProgram[] xoverprograms = null;
        CrossoverOutput xoverResult = null;
        CandidateProgram parent = individual.clone();   
       

        int[] ex = {specie};
        int interSpecie = EvolutionaryUtil.getRandomWithExclusion(rnd, 0, popGroupedBySpecie.length-1, ex);
        CandidateProgram interParent = EvolutionaryUtil.getParentFromTournamentGGP(popGroupedBySpecie[interSpecie], rnd, tournamentSize);
        
        interXOverBuffer.append(specie).append(";").append(interSpecie).append(";");
//        System.out.println("Parent2: "+ ((GRCandidateProgram) interParent).toString());        
        do{
            xoverResult = xover.crossoverInterSpecie(parent.clone(), interParent.clone());            
            if(xoverResult==null){    
//                System.out.println("Choosing another inter-parent");
                interParent = EvolutionaryUtil.getParentFromTournamentGGP(popGroupedBySpecie[interSpecie], rnd, tournamentSize);
            }              
        }while(xoverResult==null);

        xoverprograms = xoverResult.getChildren();
        CandidateProgram [] xOverChild = new CandidateProgram[2];
//        if(rnd.nextDouble() <= 0.5){
        xOverChild[0] = xoverprograms[0];
//            }else{
        xOverChild[1] = xoverprograms[1];  
//        }
        interXOverBuffer.append(xoverResult.getSubtree1().getGrammarRule().getName()).append(";");
        interXOverBuffer.append(xoverResult.getSubtree2().toString()).append(";");
        interXOverBuffer.append(xoverResult.getSubtree1().toString()).append(";");
        interXOverBuffer.append(parent.toString()).append(";");
        interXOverBuffer.append(interParent).append(";");
        interXOverBuffer.append(xOverChild[option].toString()).append("\n");
        
//        System.out.println("Child" + i + ": "+((GRCandidateProgram) xOverChild).toString());
       
        ((GRCandidateProgram) xOverChild[option]).setSpecie(specie);
        
        
        CandidateProgram[] children = new CandidateProgram[xoverResult.getChildren().length];
        children[0] = xoverResult.getChildren()[0];
        children[1] = xoverResult.getChildren()[1];
        for (CandidateProgram child : children) {
            ((GRCandidateProgram) child).setInterSpecieXOver(true);
            ((GRCandidateProgram) child).setParent1Fitness(((GRCandidateProgram) parent).getFitnessValue());
            ((GRCandidateProgram) child).setParent2Fitness(((GRCandidateProgram) interParent).getFitnessValue());
            ((GRCandidateProgram) child).setParent1Phenotype( ((GRCandidateProgram) parent).toString() );
            ((GRCandidateProgram) child).setParent2Phenotype( ((GRCandidateProgram) interParent).toString() );
        }
        return children;
    }      
    
    
    /**
     * 
     * @param rnd
     * @param start
     * @param end
     * @param exclude
     * @return 
     */
    public static int getRandomWithExclusion(MersenneTwisterFast rnd, 
                                             int start, 
                                             int end, 
                                             int... exclude
                                            ) {
        int random = start + rnd.nextInt(end - start + 1 - exclude.length);
        for (int ex : exclude) {
            if (random < ex) {
                break;
            }
            random++;
        }
        return random;
    }
    
    
    /**
     * It gets a parent from the tournament for GGP-based AutoML methods.
     * @param population the population to select the individuals.
     * @param rnd to define the random generator.
     * @param tournamentSize
     * @return the selected candidate program (individual) from the tournament.
     * @throws Exception 
     */
    public static CandidateProgram getParentFromTournamentGGP(ArrayList<CandidateProgram> population, 
                                                           MersenneTwisterFast rnd, 
                                                           int tournamentSize                                               
                                                          ) throws Exception {
        
        ArrayList<CandidateProgram> candidates = new ArrayList<CandidateProgram>();
        for (int i = 0; i < tournamentSize; i++) {
            int index = (int) Math.round(rnd.nextDouble() * (population.size() - 1));
            candidates.add(population.get(index));
        }
        // The sorted k candidates.
        Collections.sort(candidates);

        return candidates.get(candidates.size() - 1);
    }
    
    /**
     * It gets a parent from the tournament for GA-based AutoML methods.
     * @param population the population to select the individuals.
     * @param rnd to define the random generator.
     * @param tournamentSize
     * @return the selected candidate program (individual) from the tournament.
     * @throws Exception 
     */
    public static MetaIndividualGA getParentFromTournamentGA(ArrayList<MetaIndividualGA> population, 
                                                           MersenneTwisterFast rnd, 
                                                           int tournamentSize                                               
                                                          ) throws Exception {
       
        ArrayList<MetaIndividualGA> candidates = new ArrayList<MetaIndividualGA>();
        for (int i = 0; i < tournamentSize; i++) {
           int index =  (int) Math.round(rnd.nextDouble() * (population.size() - 1));
            candidates.add(population.get(index));
        }
        // The sorted k candidates.
        Collections.sort(candidates);

        return candidates.get(candidates.size() - 1);
    }   
    

    
    
    /**
     * 
     * @param generationBuffer
     * @param bests
     * @param usedSeed
     * @param learningANDvalidationDataDir
     * @param numOfThreads
     * @param seed
     * @param algorithmTimeLimit
     * @param experimentName
     * @param javaDir
     * @return
     * @throws Exception 
     */
    public static ArrayList<CandidateProgram> getBestAlgorithmsGGP(final StringBuilder generationBuffer, 
                                                                ArrayList<CandidateProgram> bests, 
                                                                long usedSeed, 
                                                                String [] learningANDvalidationDataDir,
                                                                int numOfThreads, 
                                                                long seed, 
                                                                int algorithmTimeLimit, 
                                                                String experimentName, 
                                                                String javaDir
                                                               ) throws Exception{
        
        HashMap<String, Double> saveCompTime = new HashMap<String, Double>(); 
        generationBuffer.append("==============================================\n");
        System.out.println("##Evaluating now the best ones...");
        generationBuffer.append("Evaluating the best ones...\n");  
        MetaIndividualGGP.evaluateIndividuals(bests, learningANDvalidationDataDir[0], learningANDvalidationDataDir[1], numOfThreads, seed, saveCompTime, algorithmTimeLimit, experimentName, javaDir);
        //It sorts the best of the bests.
        Collections.sort(bests);
        for(CandidateProgram e : bests){
            GRCandidateProgram gcp = (GRCandidateProgram) e;
            String gcp_grammar = gcp.toString();
            generationBuffer.append(gcp_grammar).append("#").append(gcp.getFitnessValue()).append("\n");     
        } 
        
        return bests;
    }
    
    
    
    /**
     * 
     * @param generationBuffer
     * @param bests
     * @param usedSeed
     * @param learningANDvalidationDataDir
     * @param numOfThreads
     * @param seed
     * @param algorithmTimeLimit
     * @param experimentName
     * @param javaDir
     * @return
     * @throws Exception 
     */
    public static ArrayList<MetaIndividualGA> getBestAlgorithmsGA(final StringBuilder generationBuffer, 
                                                                ArrayList<MetaIndividualGA> bests, 
                                                                long usedSeed, 
                                                                String [] learningANDvalidationDataDir,
                                                                int numOfThreads, 
                                                                long seed, 
                                                                int algorithmTimeLimit, 
                                                                String experimentName, 
                                                                String javaDir
                                                               ) throws Exception{
        
        HashMap<String, Double> saveCompTime = new HashMap<String, Double>(); 
        generationBuffer.append("==============================================\n");
        System.out.println("##Evaluating now the best ones...");
        generationBuffer.append("Evaluating the best ones...\n");  
        MetaIndividualGA.evaluateIndividuals(bests, learningANDvalidationDataDir[0], learningANDvalidationDataDir[1], numOfThreads, seed, algorithmTimeLimit, saveCompTime, experimentName, javaDir);
        //It sorts the best of the bests.
        Collections.sort(bests);
        for(MetaIndividualGA mi_ga : bests){
            generationBuffer.append(mi_ga.getM_individualInString()).append("#").append(mi_ga.getFitnessValue()).append("\n");     
        } 
        
        return bests;
    }    
    
    
    /**
     * It measures the average fitness of the population for GGP-based AutoML methods.
     * @param population of individuals that represent MLC algorithms.
     * @return the average fitness of the population.
     * @throws Exception 
     */    
    public static double getAvgFitnessGGP(ArrayList<CandidateProgram> population) throws Exception {
        double average = 0.0;
        int count = 0;
        for (int i = 0; i < population.size(); i++) {
            GRCandidateProgram grcp = (GRCandidateProgram) population.get(i);
//            if(grcp.getFitnessValue() != 0.0){
                average += grcp.getFitnessValue();
                count++;
//            }
        }
        average /= count;

        return average;
    }
    
    /**
     * It measures the average fitness of the population for GA-based AutoML methods.
     * @param population of individuals that represent MLC algorithms.
     * @return the average fitness of the population.
     * @throws Exception 
     */    
    public static double getAvgFitnessGA(ArrayList<MetaIndividualGA> population) throws Exception {
        double average = 0.0;
        int count = 0;
        for (int i = 0; i < population.size(); i++) {
            MetaIndividualGA m_ga = population.get(i);
//            if(grcp.getFitnessValue() != 0.0){
                average += m_ga.getFitnessValue();
                count++;
//            }
        }
        average /= count;

        return average;
    }    
    
    /**
     * It measures the standard deviation of the population for GGP-based AutoML methods.
     * @param population of individuals that represent MLC algorithms.
     * @param avg average of the fitness.
     * @return the fitness' standard deviation of the population.
     * @throws Exception 
     */        
    public static double getPopStdDevGGP(ArrayList<CandidateProgram> population, double avg) throws Exception {
         double sumSquare = 0.0;
        double fitness = 0.0;
        
        for (CandidateProgram cp : population){
            GRCandidateProgram grcp = (GRCandidateProgram) cp;
            fitness = grcp.getFitnessValue();
            sumSquare += Math.pow(fitness - avg, 2);
        }

        double meanOfDiffs = sumSquare/ ((double) (population.size()));
        
        return Math.sqrt(meanOfDiffs);        
    }
    
    /**
     * It measures the standard deviation of the population for GA-based AutoML methods.
     * @param population of individuals that represent MLC algorithms.
     * @param avg average of the fitness.
     * @return the fitness' standard deviation of the population.
     * @throws Exception 
     */        
    public static double getPopStdDevGA(ArrayList<MetaIndividualGA> population, double avg) throws Exception {
        double sumSquare = 0.0;
        double fitness = 0.0;
        
        for (MetaIndividualGA mi_ga : population){
            fitness = mi_ga.getFitnessValue();
            sumSquare += Math.pow(fitness - avg, 2);
        }

        double meanOfDiffs = sumSquare/ ((double) (population.size()));
        
        return Math.sqrt(meanOfDiffs);        
    }    


    /**
     * It generates a file with the fitness curve. *
     * @param strB the buffer to save.
     * @param timeBudget the given time budget.
     * @param savingDirectory
     * @param experimentName
     * @param seed
     * @param foldInit
     * @throws java.io.IOException when there is any IO exception.
     */
    public static void savingFitnessLog(StringBuilder strB, 
                                        long timeBudget,
                                        String savingDirectory,
                                        String experimentName,
                                        long seed,
                                        int foldInit,
                                        boolean speciation
                                       ) throws IOException{
        String fileDir = savingDirectory + File.separator + "results-" + experimentName + File.separator + "LogFitness-" + experimentName +"-tb"+(timeBudget/60000) +"s"+ seed + "f" + foldInit + ".csv";
        File f = new File(fileDir);
        String firstLine = "";
        
        if(!f.exists()){
            if(speciation){
                firstLine = "specie;difftime;generation;worstFitness;avgFitness;bestFitness;stdevFitness\n";
            }else{
                firstLine = "difftime;generation;worstFitness;avgFitness;bestFitness;stdevFitness\n";
            }
            
        }        
        
        try {
            BufferedWriter bf = new BufferedWriter(new FileWriter(fileDir, true));
            bf.write(firstLine);
            bf.write(strB.toString());
            bf.close();
        } catch (IOException e) {
            System.out.println(e);
        }       
    }



     /**
     * It creates the files and saves the results for GGP-based AutoML methods.
     * @param budget
     * @param generationBuffer the buffer with all generation information.
     * @param m_bestAlgorithms
     * @param searchTime the time to search for the individuals.
     * @param differenceTime the total time.
     * @param actualGeneration the actual generation where the process stopped.
     * @param learningANDvalidationDataDir the directory of the learning and validation files.
     * @param numbOfEval the number of evaluations.
     * @param numbOfReinit the number of reinitializations.
     * @param savingDirectory
     * @param experimentName
     * @param foldInit
     * @param seed
     * @param trainingDirectory
     * @param testingDirectory
     * @param javaDir
     * @throws IOException
     * @throws Exception 
     */
    public static void savingMLCResultsGGP(long budget, 
                                        final StringBuilder generationBuffer, 
                                        ArrayList<CandidateProgram> m_bestAlgorithms, 
                                        long searchTime, 
                                        long differenceTime, 
                                        int actualGeneration, 
                                        String[] learningANDvalidationDataDir, 
                                        int numbOfEval, 
                                        int numbOfReinit,
                                        String savingDirectory,
                                        String experimentName,
                                        long seed,
                                        long foldInit,
                                        String trainingDirectory,
                                        String testingDirectory,
                                        String javaDir
                                       ) throws IOException, Exception {
           
//        System.out.println("###TESTING");
        generationBuffer.append("==============================================\n");
        generationBuffer.append("###TESTING\n");
        new File(savingDirectory).mkdir();
        new File(savingDirectory + File.separator + "results-"+ experimentName).mkdir();
        Results gr = null;
        String tag = "";
        int pos = m_bestAlgorithms.size();
        boolean completeEvaluation = false;
        ArrayList<String> alreadyEvaluated = new ArrayList<String>();
        String grammarName = "";
       
        while(!completeEvaluation){
            pos--;            
            if(pos==-1){
                grammarName = "threshold PCut1 CC RandomForest 100 0 0";
                gr = MetaIndividualGGP.testAlgorithm(generationBuffer, grammarName,
                        trainingDirectory, testingDirectory,
                        learningANDvalidationDataDir[0], learningANDvalidationDataDir[1],
                        seed, Integer.MAX_VALUE, javaDir);                
                break;
            }
            CandidateProgram m_bestAlgorithm = m_bestAlgorithms.get(pos);
            grammarName = ((GRCandidateProgram) m_bestAlgorithm).toString();
            if(alreadyEvaluated.contains(grammarName)){
                completeEvaluation = false;
                continue;
            }else{
                alreadyEvaluated.add(grammarName);
            }
            //It tests the best individual (MLC algorithm) for the input dataset.
            gr = MetaIndividualGGP.testAlgorithm(generationBuffer, grammarName,
                    trainingDirectory, testingDirectory,
                    learningANDvalidationDataDir[0], learningANDvalidationDataDir[1],
                    seed, 900, javaDir);
            completeEvaluation = gr.isCompleteEvaluation();
            
        }   
        
        
        tag = "Standard";
        //It saves the results in specific files and folders.
        BufferedWriter bf0 = new BufferedWriter(new FileWriter(savingDirectory + File.separator+"results-" + experimentName +File.separator+"Statistics"+tag+"-"  + experimentName +"-tb"+(budget/60000) +"s"+seed + "f" + foldInit + ".csv", true));
        BufferedWriter bf1 = new BufferedWriter(new FileWriter(savingDirectory +File.separator+"results-"+ experimentName +File.separator+ "StatisticsCompact"+tag+"-" + experimentName +"-tb"+(budget/60000) +"s"+seed + "f" + foldInit +".csv", true));
        
//        //Deleting the training and validation files.

        
//        System.gc();
        
        //Generated algorithm and performance measures on learning, validation, full-training and testing sets.
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
        
   
        //It generates a file with all results.
        try {

            bf0.write((budget/60000) + ";" + seed + ";" + foldInit + ";" + actualGeneration + ";"+ numbOfEval + ";"+ numbOfReinit + ";" + (differenceTime/60000) + ";" + (searchTime/60000) + ";" 
                    + accuracy_FullTraining + ";" + accuracy_Test + ";" + accuracy_Training + ";" + accuracy_Validation + ";"
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

            //It generates a file with the compacted results results.
            bf1.write((budget/60000) + ";" + seed + ";" + foldInit + ";" + actualGeneration + ";"+ numbOfEval + ";"+ numbOfReinit + ";" + (differenceTime/60000) + ";" + (searchTime/60000) + ";" 
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
        
        System.gc();        
    }    
    
    
     /**
     * It creates the files and saves the results.
     * @param budget
     * @param generationBuffer the buffer with all generation information.
     * @param m_bestAlgorithms
     * @param searchTime the time to search for the individuals.
     * @param differenceTime the total time.
     * @param actualGeneration the actual generation where the process stopped.
     * @param learningANDvalidationDataDir the directory of the learning and validation files.
     * @param numbOfEval the number of evaluations.
     * @param numbOfReinit the number of reinitializations.
     * @param savingDirectory
     * @param experimentName
     * @param foldInit
     * @param seed
     * @param trainingDirectory
     * @param testingDirectory
     * @param javaDir
     * @throws IOException
     * @throws Exception 
     */
    public static void savingMLCResultsGA(long budget, 
                                        final StringBuilder generationBuffer, 
                                        ArrayList<MetaIndividualGA> m_bestAlgorithms, 
                                        long searchTime, 
                                        long differenceTime, 
                                        int actualGeneration, 
                                        String[] learningANDvalidationDataDir, 
                                        int numbOfEval, 
                                        int numbOfReinit,
                                        String savingDirectory,
                                        String experimentName,
                                        long seed,
                                        long foldInit,
                                        String trainingDirectory,
                                        String testingDirectory,
                                        String javaDir
                                       ) throws IOException, Exception {
           
//        System.out.println("###TESTING");
        generationBuffer.append("==============================================\n");
        generationBuffer.append("###TESTING\n");
        new File(savingDirectory).mkdir();
        new File(savingDirectory + File.separator + "results-"+ experimentName).mkdir();
        Results gr = null;
        String tag = "";
        int pos = m_bestAlgorithms.size();
        boolean completeEvaluation = false;
        ArrayList<String> alreadyEvaluated = new ArrayList<String>();
        String algName = "";
       
        while(!completeEvaluation){
            pos--;            
            if(pos==-1){
                algName = "threshold PCut1 CC RandomForest 100 0 0";
                gr = MetaIndividualGGP.testAlgorithm(generationBuffer, algName,
                        trainingDirectory, testingDirectory,
                        learningANDvalidationDataDir[0], learningANDvalidationDataDir[1],
                        seed, Integer.MAX_VALUE, javaDir);                
                break;
            }
            MetaIndividualGA m_bestAlgorithm = m_bestAlgorithms.get(pos);
            algName = m_bestAlgorithm.getM_individualInString();
            if(alreadyEvaluated.contains(algName)){
                completeEvaluation = false;
                continue;
            }else{
                alreadyEvaluated.add(algName);
            }
            //It tests the best individual (MLC algorithm) for the input dataset.
            gr = MetaIndividualGA.testAlgorithm(m_bestAlgorithm, generationBuffer,
                    trainingDirectory, testingDirectory,
                    learningANDvalidationDataDir[0], learningANDvalidationDataDir[1],
                    seed, 900, javaDir);
            completeEvaluation = gr.isCompleteEvaluation();
            
        }   
        
        
        tag = "GA";
        //It saves the results in specific files and folders.
        BufferedWriter bf0 = new BufferedWriter(new FileWriter(savingDirectory + File.separator+"results-" + experimentName +File.separator+"Statistics"+tag+"-"  + experimentName +"-tb"+(budget/60000) +"s"+seed + "f" + foldInit + ".csv", true));
        BufferedWriter bf1 = new BufferedWriter(new FileWriter(savingDirectory +File.separator+"results-"+ experimentName +File.separator+ "StatisticsCompact"+tag+"-" + experimentName +"-tb"+(budget/60000) +"s"+seed + "f" + foldInit +".csv", true));
        
//        //Deleting the training and validation files.


        
        //Generated algorithm and performance measures on learning, validation, full-training and testing sets.
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
        
   
        //It generates a file with all results.
        try {

            bf0.write((budget/60000) + ";" + seed + ";" + foldInit + ";" + actualGeneration + ";"+ numbOfEval + ";"+ numbOfReinit + ";" + (differenceTime/60000) + ";" + (searchTime/60000) + ";" 
                    + accuracy_FullTraining + ";" + accuracy_Test + ";" + accuracy_Training + ";" + accuracy_Validation + ";"
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

            //It generates a file with the compacted results results.
            bf1.write((budget/60000) + ";" + seed + ";" + foldInit + ";" + actualGeneration + ";"+ numbOfEval + ";"+ numbOfReinit + ";" + (differenceTime/60000) + ";" + (searchTime/60000) + ";" 
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
        System.gc();
    }        


    //
    
    /**
     * Mutation operation is executed in a probabilistic fashion.
     * @param individual
     * @param rnd
     * @param mutationRate
     * @return 
     */
    public static double[] mutation4GAConservative(double[] individual, MersenneTwisterFast rnd) {
        int change = rnd.nextInt(individual.length);
        double [] newIndividual =  individual.clone();

        newIndividual[change] = rnd.nextDouble();

        return newIndividual;
    }    
    
    /**
     * Mutation operation is executed in a probabilistic fashion.
     * @param individual
     * @param rnd
     * @param mutationRate
     * @return 
     */
    public static double[] mutation4GA(double[] individual, MersenneTwisterFast rnd, double mutationRate) {
        int change = rnd.nextInt(individual.length);
        

        double randomVar = rnd.nextDouble();
        if (randomVar < mutationRate) {
            individual[change] = rnd.nextDouble();
        }
        return individual;
    }

    //
    
    /**
     * Generates a mask to make the cross over operation
     * @param size
     * @param rnd
     * @return 
     */
    public static int[] generateCrossoverMask4GA(int geneSize, MersenneTwisterFast rnd) {
        int[] mask = new int[geneSize];

        //Uniform distribution -- zero and ones:
        for (int i = 0; i < geneSize; i++) {
            if (rnd.nextDouble() < 0.5) {
                mask[i] = 0;
            } else {
                mask[i] = 1;
            }
        }
        return mask;
    }

    
}
