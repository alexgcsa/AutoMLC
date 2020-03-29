/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meka.classifiers.multilabel.meta.automekaggp.core;

import org.epochx.representation.CandidateProgram;

/**
 *
 * @author alexgcsa
 */
public class InterXOverHist {
    
    private CandidateProgram indBefore;
    private CandidateProgram indAfter;
    private String chosenNT;
    private String subtree1;
    private String subtree2;
    private double fitnessBefore;
    private double fitnessAfter;

    public InterXOverHist(CandidateProgram indBefore, CandidateProgram indAfter, String chosenNT, String subtree1, String subtree2, double fitnessBefore, double fitnessAfter) {
        this.indBefore = indBefore;
        this.indAfter = indAfter;
        this.chosenNT = chosenNT;
        this.subtree1 = subtree1;
        this.subtree2 = subtree2;
        this.fitnessBefore = fitnessBefore;
        this.fitnessAfter = fitnessAfter;
    }

    public String getSubtree1() {
        return subtree1;
    }

    public String getSubtree2() {
        return subtree2;
    }

    public double getFitnessBefore() {
        return fitnessBefore;
    }

    public double getFitnessAfter() {
        return fitnessAfter;
    }

    public CandidateProgram getIndBefore() {
        return indBefore;
    }

    public CandidateProgram getIndAfter() {
        return indAfter;
    }

    public String getChosenNT() {
        return chosenNT;
    }

    public void setIndBefore(CandidateProgram indBefore) {
        this.indBefore = indBefore;
    }

    public void setIndAfter(CandidateProgram indAfter) {
        this.indAfter = indAfter;
    }

    public void setChosenNT(String chosenNT) {
        this.chosenNT = chosenNT;
    }

    public void setSubtree1(String subtree1) {
        this.subtree1 = subtree1;
    }

    public void setSubtree2(String subtree2) {
        this.subtree2 = subtree2;
    }

    public void setFitnessBefore(double fitnessBefore) {
        this.fitnessBefore = fitnessBefore;
    }

    public void setFitnessAfter(double fitnessAfter) {
        this.fitnessAfter = fitnessAfter;
    }
    
    
    
}
