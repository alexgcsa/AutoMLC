/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meka.classifiers.multilabel.meta.automekabo.core;

/**
 *
 * @author alexgcsa
 */
public class AutoMLCAlgorithm implements Cloneable, Comparable<AutoMLCAlgorithm> {
    private String MLCAlg;
    private String [] MLCAlgParams;
    private double score;

    public AutoMLCAlgorithm(String MLCAlg, String [] MLCAlgParams) {
        this.MLCAlg = MLCAlg;
        this.MLCAlgParams = MLCAlgParams;
        this.score = 0.0;
    }

    public String getMLCAlg() {
        return MLCAlg;
    }

    public String [] getMLCAlgParams() {
        return MLCAlgParams;
    }

    public double getScore() {
        return score;
    }

    public void setMLCAlg(String MLCAlg) {
        this.MLCAlg = MLCAlg;
    }

    public void setMLCAlgParams(String [] MLCAlgParams) {
        this.MLCAlgParams = MLCAlgParams;
    }

    public void setScore(double score) {
        this.score = score;
    }

    
    /**
     * Compares this program to another based upon fitness. Returns a negative
     * integer if this program has a worse (larger) fitness value, zero if they
     * have equal fitnesses and a positive integer if this program has a better
     * (smaller) fitness value.
     *
     * @param o the <code>CandidateProgram</code> instance to compare against.
     * @return a negative integer, zero, or a positive integer if this program
     * has a worse, equal or better fitness than <code>o</code> respectively.
     */
    @Override
    public int compareTo(final AutoMLCAlgorithm o) {
        if (o == null) {
            throw new NullPointerException("cannot compare to null");
        }

        final double thisFitness = this.score;
        final double objFitness = o.score;

        if (thisFitness > objFitness) {
            return -1;
        } else if (thisFitness == objFitness) {
            return 0;
        } else {
            return 1;
        }
    }
    
    
    
}
