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
public abstract class AbstractIntermediateResults {
    
    protected long intermediateBudget;

    protected ArrayList<CandidateProgram> bestOfTheReinitializations;
    protected LinkedList<CandidateProgram> bestOfTheGenerations;
    protected long usedSeedResample;
    protected int nLabels;
    protected StringBuilder generationBuffer;
    protected StringBuilder convergenceBuffer;
    protected  int numbOfEval;
    protected long startTime;
    protected long searchTime;
    protected int actualGeneration; 

    protected int numbOfReinit; 
    protected boolean saveLogFiles;
    protected int nFoldsToLearn;
    protected int nFoldsToValid;
      
    
    public long getIntermediateBudget() {
        return intermediateBudget;
    }



    public ArrayList<CandidateProgram> getBestOfTheReinitializations() {
        return bestOfTheReinitializations;
    }

    public long getUsedSeedResample() {
        return usedSeedResample;
    }

    public int getnLabels() {
        return nLabels;
    }

    public StringBuilder getGenerationBuffer() {
        return generationBuffer;
    }

    public StringBuilder getConvergenceBuffer() {
        return convergenceBuffer;
    }

    public int getNumbOfEval() {
        return numbOfEval;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getSearchTime() {
        return searchTime;
    }

    public int getActualGeneration() {
        return actualGeneration;
    }



    public int getNumbOfReinit() {
        return numbOfReinit;
    }

    public boolean isSaveLogFiles() {
        return saveLogFiles;
    }

    public int getNFoldsToLearn() {
        return nFoldsToLearn;
    }

    public int getNFoldsToValid() {
        return nFoldsToValid;
    }

    public LinkedList<CandidateProgram> getBestOfTheGenerations() {
        return bestOfTheGenerations;
    }    
       
    public void setIntermediateBudget(long intermediateBudget) {
        this.intermediateBudget = intermediateBudget;
    }



    public void setBestOfTheReinitializations(ArrayList<CandidateProgram> bestOfTheReinitializations) {
        this.bestOfTheReinitializations = bestOfTheReinitializations;
    }

    public void setUsedSeedResample(long usedSeedResample) {
        this.usedSeedResample = usedSeedResample;
    }

    public void setnLabels(int nLabels) {
        this.nLabels = nLabels;
    }

    public void setGenerationBuffer(StringBuilder generationBuffer) {
        this.generationBuffer = generationBuffer;
    }

    public void setConvergenceBuffer(StringBuilder convergenceBuffer) {
        this.convergenceBuffer = convergenceBuffer;
    }

    public void setNumbOfEval(int numbOfEval) {
        this.numbOfEval = numbOfEval;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setSearchTime(long searchTime) {
        this.searchTime = searchTime;
    }

    public void setActualGeneration(int actualGeneration) {
        this.actualGeneration = actualGeneration;
    }

    public void setNumbOfReinit(int numbOfReinit) {
        this.numbOfReinit = numbOfReinit;
    }

    public void setSaveLogFiles(boolean saveLogFiles) {
        this.saveLogFiles = saveLogFiles;
    }

    public void setNFoldsToLearn(int nFoldsToLearn) {
        this.nFoldsToLearn = nFoldsToLearn;
    }

    public void setNFoldsToValid(int nFoldsToValid) {
        this.nFoldsToValid = nFoldsToValid;
    }

    public void setBestOfTheGenerations(LinkedList<CandidateProgram> bestOfTheGenerations) {
        this.bestOfTheGenerations = bestOfTheGenerations;
    }

   
    
        
    
}
