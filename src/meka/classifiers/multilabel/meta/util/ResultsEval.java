/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package meka.classifiers.multilabel.meta.util;

import meka.classifiers.multilabel.meta.automekaggp.core.AbstractResults;

/**
 * Class that has the structure to save part of the results -- multi-label classification metrics..
 * @author alexgcsa
 */
public class ResultsEval extends AbstractResults{

 

    public ResultsEval() {
        accuracy_Training = 0.0;
        accuracy_Evaluation = 0.0;

        hammingScore_Training = 0.0;
        hammingScore_Evaluation = 0.0;

        exactMatch_Training = 0.0;
        exactMatch_Evaluation = 0.0;

        jaccardDistance_Training = 0.0;
        jaccardDistance_Evaluation = 0.0;

        hammingLoss_Training = 0.0;
        hammingLoss_Evaluation = 0.0;

        zeroOneLoss_Training = 0.0;
        zeroOneLoss_Evaluation = 0.0;

        harmonicScore_Training = 0.0;
        harmonicScore_Evaluation = 0.0;

        oneError_Training = 0.0;
        oneError_Evaluation = 0.0;

        rankLoss_Training = 0.0;
        rankLoss_Evaluation = 0.0;

        avgPrecision_Training = 0.0;
        avgPrecision_Evaluation = 0.0;

        microPrecision_Training = 0.0;
        microPrecision_Evaluation = 0.0;

        microRecall_Training = 0.0;
        microRecall_Evaluation = 0.0;

        macroPrecision_Training = 0.0;
        macroPrecision_Evaluation = 0.0;

        macroRecall_Training = 0.0;
        macroRecall_Evaluation = 0.0;

        f1MicroAveraged_Training = 0.0;
        f1MicroAveraged_Evaluation = 0.0;

        f1MacroAveragedExample_Training = 0.0;
        f1MacroAveragedExample_Evaluation = 0.0;

        f1MacroAveragedLabel_Training = 0.0;
        f1MacroAveragedLabel_Evaluation = 0.0;

        aurcMacroAveraged_Training = 0.0;
        aurcMacroAveraged_Evaluation = 0.0;

        aurocMacroAveraged_Training = 0.0;
        aurocMacroAveraged_Evaluation = 0.0;

        emptyLabelvectorsPredicted_Training = 0.0;
        emptyLabelvectorsPredicted_Evaluation = 0.0;

        labelCardinalityPredicted_Training = 0.0;
        labelCardinalityPredicted_Evaluation = 0.0;

        levenshteinDistance_Training = 0.0;
        levenshteinDistance_Evaluation = 0.0;

        labelCardinalityDifference_Training = 0.0;
        labelCardinalityDifference_Evaluation = 0.0;
        
        completeEvaluation = false;

    }

    public ResultsEval(String [] command, boolean completeEvaluation, double accuracy_Training, double accuracy_Evaluation, double hammingScore_Training, double hammingScore_Evaluation, double exactMatch_Training, double exactMatch_Evaluation, double jaccardDistance_Training, double jaccardDistance_Evaluation, double hammingLoss_Training, double hammingLoss_Evaluation, double zeroOneLoss_Training, double zeroOneLoss_Evaluation, double harmonicScore_Training, double harmonicScore_Evaluation, double oneError_Training, double oneError_Evaluation, double rankLoss_Training, double rankLoss_Evaluation, double avgPrecision_Training, double avgPrecision_Evaluation, double microPrecision_Training, double microPrecision_Evaluation, double microRecall_Training, double microRecall_Evaluation, double macroPrecision_Training, double macroPrecision_Evaluation, double macroRecall_Training, double macroRecall_Evaluation, double f1MicroAveraged_Training, double f1MicroAveraged_Evaluation, double f1MacroAveragedExample_Training, double f1MacroAveragedExample_Evaluation, double f1MacroAveragedLabel_Training, double f1MacroAveragedLabel_Evaluation, double aurcMacroAveraged_Training, double aurcMacroAveraged_Evaluation, double aurocMacroAveraged_Training, double aurocMacroAveraged_Evaluation, double emptyLabelvectorsPredicted_Training, double emptyLabelvectorsPredicted_Evaluation, double labelCardinalityPredicted_Training, double labelCardinalityPredicted_Evaluation, double levenshteinDistance_Training, double levenshteinDistance_Evaluation, double labelCardinalityDifference_Training, double labelCardinalityDifference_Evaluation) {
        this.command = command;
        this.completeEvaluation = completeEvaluation;
        this.accuracy_Training = accuracy_Training;
        this.accuracy_Evaluation = accuracy_Evaluation;
        this.hammingScore_Training = hammingScore_Training;
        this.hammingScore_Evaluation = hammingScore_Evaluation;
        this.exactMatch_Training = exactMatch_Training;
        this.exactMatch_Evaluation = exactMatch_Evaluation;
        this.jaccardDistance_Training = jaccardDistance_Training;
        this.jaccardDistance_Evaluation = jaccardDistance_Evaluation;
        this.hammingLoss_Training = hammingLoss_Training;
        this.hammingLoss_Evaluation = hammingLoss_Evaluation;
        this.zeroOneLoss_Training = zeroOneLoss_Training;
        this.zeroOneLoss_Evaluation = zeroOneLoss_Evaluation;
        this.harmonicScore_Training = harmonicScore_Training;
        this.harmonicScore_Evaluation = harmonicScore_Evaluation;
        this.oneError_Training = oneError_Training;
        this.oneError_Evaluation = oneError_Evaluation;
        this.rankLoss_Training = rankLoss_Training;
        this.rankLoss_Evaluation = rankLoss_Evaluation;
        this.avgPrecision_Training = avgPrecision_Training;
        this.avgPrecision_Evaluation = avgPrecision_Evaluation;
        this.microPrecision_Training = microPrecision_Training;
        this.microPrecision_Evaluation = microPrecision_Evaluation;
        this.microRecall_Training = microRecall_Training;
        this.microRecall_Evaluation = microRecall_Evaluation;
        this.macroPrecision_Training = macroPrecision_Training;
        this.macroPrecision_Evaluation = macroPrecision_Evaluation;
        this.macroRecall_Training = macroRecall_Training;
        this.macroRecall_Evaluation = macroRecall_Evaluation;
        this.f1MicroAveraged_Training = f1MicroAveraged_Training;
        this.f1MicroAveraged_Evaluation = f1MicroAveraged_Evaluation;
        this.f1MacroAveragedExample_Training = f1MacroAveragedExample_Training;
        this.f1MacroAveragedExample_Evaluation = f1MacroAveragedExample_Evaluation;
        this.f1MacroAveragedLabel_Training = f1MacroAveragedLabel_Training;
        this.f1MacroAveragedLabel_Evaluation = f1MacroAveragedLabel_Evaluation;
        this.aurcMacroAveraged_Training = aurcMacroAveraged_Training;
        this.aurcMacroAveraged_Evaluation = aurcMacroAveraged_Evaluation;
        this.aurocMacroAveraged_Training = aurocMacroAveraged_Training;
        this.aurocMacroAveraged_Evaluation = aurocMacroAveraged_Evaluation;
        this.emptyLabelvectorsPredicted_Training = emptyLabelvectorsPredicted_Training;
        this.emptyLabelvectorsPredicted_Evaluation = emptyLabelvectorsPredicted_Evaluation;
        this.labelCardinalityPredicted_Training = labelCardinalityPredicted_Training;
        this.labelCardinalityPredicted_Evaluation = labelCardinalityPredicted_Evaluation;
        this.levenshteinDistance_Training = levenshteinDistance_Training;
        this.levenshteinDistance_Evaluation = levenshteinDistance_Evaluation;
        this.labelCardinalityDifference_Training = labelCardinalityDifference_Training;
        this.labelCardinalityDifference_Evaluation = labelCardinalityDifference_Evaluation;
    }
 
   
    


}