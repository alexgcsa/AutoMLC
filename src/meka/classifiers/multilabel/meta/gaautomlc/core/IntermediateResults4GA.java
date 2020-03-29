/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meka.classifiers.multilabel.meta.gaautomlc.core;

import meka.classifiers.multilabel.meta.automekaggp.core.*;
import java.util.ArrayList;
import org.epochx.representation.CandidateProgram;

/**
 *
 * @author alexgcsa
 */
public class IntermediateResults4GA extends AbstractIntermediateResults4GA{
    
    protected ArrayList<MetaIndividualGA> population;
    protected StringBuilder loggingBuffer;

    public IntermediateResults4GA(long intermediateBudget, 
                               ArrayList<MetaIndividualGA> population, 
                               ArrayList<MetaIndividualGA> bestOfTheReinitializations, 
                               long usedSeedResample, 
                               int nLabels, 
                               StringBuilder generationBuffer, 
                               StringBuilder convergenceBuffer, 
                               int numbOfEval, long startTime, 
                               long searchTime, int actualGeneration, 
                               StringBuilder loggingBuffer, 
                               int numbOfReinit, 
                               boolean saveLogFiles,
                               int nFoldsToLearn,
                               int nFoldsToValid) {
        
        this.intermediateBudget = intermediateBudget;
        this.population = population;
        this.bestOfTheReinitializations = bestOfTheReinitializations;
        this.usedSeedResample = usedSeedResample;
        this.nLabels = nLabels;
        this.generationBuffer = generationBuffer;
        this.convergenceBuffer = convergenceBuffer;
        this.numbOfEval = numbOfEval;
        this.startTime = startTime;
        this.searchTime = searchTime;
        this.actualGeneration = actualGeneration;
        this.loggingBuffer = loggingBuffer;
        this.numbOfReinit = numbOfReinit;
        this.saveLogFiles = saveLogFiles;
        this.nFoldsToLearn = nFoldsToLearn;
        this.nFoldsToValid = nFoldsToValid;
    }


    public ArrayList<MetaIndividualGA> getPopulation() {
        return population;
    }
    
    public void setPopulation(ArrayList<MetaIndividualGA> population) {
        this.population = population;
    }    
    
    public StringBuilder getLoggingBuffer() {
        return loggingBuffer;
    }

    public void setLoggingBuffer(StringBuilder loggingBuffer) {
        this.loggingBuffer = loggingBuffer;
    }

}
