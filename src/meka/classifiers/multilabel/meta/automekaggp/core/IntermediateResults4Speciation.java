/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meka.classifiers.multilabel.meta.automekaggp.core;

import java.util.ArrayList;
import java.util.LinkedList;
import org.epochx.representation.CandidateProgram;

/**
 *
 * @author alexgcsa
 */
public class IntermediateResults4Speciation extends AbstractIntermediateResults{
    
    protected ArrayList<CandidateProgram>[] popGroupedBySpecie;
    protected StringBuilder[] loggingBuffer;


    public IntermediateResults4Speciation(long intermediateBudget, 
                               ArrayList<CandidateProgram>[] popGroupedBySpecie, 
                               ArrayList<CandidateProgram> bestOfTheReinitializations, 
                               long usedSeedResample, 
                               int nLabels, 
                               StringBuilder generationBuffer, 
                               StringBuilder convergenceBuffer, 
                               int numbOfEval, long startTime, 
                               long searchTime, int actualGeneration, 
                               StringBuilder[] loggingBuffer, 
                               int numbOfReinit, 
                               boolean saveLogFiles,
                               int nFoldsToLearn,
                               int nFoldsToValid) {
        
        this.intermediateBudget = intermediateBudget;
        this.popGroupedBySpecie = popGroupedBySpecie;
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
    
    
    public IntermediateResults4Speciation(long intermediateBudget, 
                               ArrayList<CandidateProgram>[] popGroupedBySpecie, 
                               ArrayList<CandidateProgram> bestOfTheReinitializations, 
                               LinkedList<CandidateProgram> bestOfTheGenerations, 
                               long usedSeedResample, 
                               int nLabels, 
                               StringBuilder generationBuffer, 
                               StringBuilder convergenceBuffer, 
                               int numbOfEval, long startTime, 
                               long searchTime, int actualGeneration, 
                               StringBuilder[] loggingBuffer, 
                               int numbOfReinit, 
                               boolean saveLogFiles,
                               int nFoldsToLearn,
                               int nFoldsToValid) {
        
        this.intermediateBudget = intermediateBudget;
        this.popGroupedBySpecie = popGroupedBySpecie;
        this.bestOfTheReinitializations = bestOfTheReinitializations;
        this.bestOfTheGenerations = bestOfTheGenerations;
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


    public ArrayList<CandidateProgram>[] getPopGroupedBySpecie() {
        return popGroupedBySpecie;
    }

    public StringBuilder[] getLoggingBuffer() {
        return loggingBuffer;
    }

    public void setPopGroupedBySpecie(ArrayList<CandidateProgram>[] popGroupedBySpecie) {
        this.popGroupedBySpecie = popGroupedBySpecie;
    }

    public void setLoggingBuffer(StringBuilder[] loggingBuffer) {
        this.loggingBuffer = loggingBuffer;
    }  
    
    
}
