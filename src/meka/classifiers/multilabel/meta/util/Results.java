/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package meka.classifiers.multilabel.meta.util;

import meka.classifiers.multilabel.meta.automekaggp.core.AbstractResults;

/**
 * Class that has the structure to save the results -- all multi-label classification metrics in all sets.
 * @author alexgcsa
 */
public class Results extends AbstractResults{
     protected String algorithm;

    public Results(String algorithm, String [] command, boolean completeEvaluation, int sizeOfTheEnsemble,
            double accuracy_FullTraining, double accuracy_Test, double accuracy_Training, double accuracy_Validation, 
            double hammingScore_FullTraining, double hammingScore_Test, double hammingScore_Training, double hammingScore_Validation, 
            double exactMatch_FullTraining, double exactMatch_Test, double exactMatch_Training, double exactMatch_Validation, 
            double jaccardDistance_FullTraining, double jaccardDistance_Test, double jaccardDistance_Training, double jaccardDistance_Validation, 
            double hammingLoss_FullTraining, double hammingLoss_Test, double hammingLoss_Training, double hammingLoss_Validation, 
            double zeroOneLoss_FullTraining, double zeroOneLoss_Test, double zeroOneLoss_Training, double zeroOneLoss_Validation, 
            double harmonicScore_FullTraining, double harmonicScore_Test, double harmonicScore_Training, double harmonicScore_Validation, 
            double oneError_FullTraining, double oneError_Test, double oneError_Training, double oneError_Validation, 
            double rankLoss_FullTraining,double rankLoss_Test, double rankLoss_Training, double rankLoss_Validation, 
            double avgPrecision_FullTraining, double avgPrecision_Test, double avgPrecision_Training, double avgPrecision_Validation, 
            double microPrecision_FullTraining, double microPrecision_Test, double microPrecision_Training, double microPrecision_Validation, 
            double microRecall_FullTraining, double microRecall_Test, double microRecall_Training, double microRecall_Validation, 
            double macroPrecision_FullTraining, double macroPrecision_Test, double macroPrecision_Training, double macroPrecision_Validation, 
            double macroRecall_FullTraining, double macroRecall_Test, double macroRecall_Training, double macroRecall_Validation, 
            double f1MicroAveraged_FullTraining, double f1MicroAveraged_Test, double f1MicroAveraged_Training, double f1MicroAveraged_Validation, 
            double f1MacroAveragedExample_FullTraining, double f1MacroAveragedExample_Test, double f1MacroAveragedExample_Training, double f1MacroAveragedExample_Validation, 
            double f1MacroAveragedLabel_FullTraining, double f1MacroAveragedLabel_Test, double f1MacroAveragedLabel_Training, double f1MacroAveragedLabel_Validation, 
            double aurcMacroAveraged_FullTraining, double aurcMacroAveraged_Test, double aurcMacroAveraged_Training, double aurcMacroAveraged_Validation, 
            double aurocMacroAveraged_FullTraining, double aurocMacroAveraged_Test, double aurocMacroAveraged_Training, double aurocMacroAveraged_Validation, 
            double emptyLabelvectorsPredicted_FullTraining, double emptyLabelvectorsPredicted_Test, double emptyLabelvectorsPredicted_Training, double emptyLabelvectorsPredicted_Validation, 
            double labelCardinalityPredicted_FullTraining, double labelCardinalityPredicted_Test, double labelCardinalityPredicted_Training, double labelCardinalityPredicted_Validation, 
            double levenshteinDistance_FullTraining, double levenshteinDistance_Test, double levenshteinDistance_Training, double levenshteinDistance_Validation, 
            double labelCardinalityDifference_FullTraining, double labelCardinalityDifference_Test, double labelCardinalityDifference_Training, double labelCardinalityDifference_Validation) {
        
        this.algorithm = algorithm;
        this.command = command;
        this.accuracy_FullTraining = accuracy_FullTraining;
        this.accuracy_Test = accuracy_Test;
        this.accuracy_Training = accuracy_Training;
        this.accuracy_Validation = accuracy_Validation;
        
        this.hammingScore_FullTraining = hammingScore_FullTraining;
        this.hammingScore_Test = hammingScore_Test;
        this.hammingScore_Training = hammingScore_Training;
        this.hammingScore_Validation = hammingScore_Validation;
        
        this.exactMatch_FullTraining = exactMatch_FullTraining;
        this.exactMatch_Test = exactMatch_Test;
        this.exactMatch_Training = exactMatch_Training;
        this.exactMatch_Validation = exactMatch_Validation;
        
        this.jaccardDistance_FullTraining = jaccardDistance_FullTraining;
        this.jaccardDistance_Test = jaccardDistance_Test;
        this.jaccardDistance_Training = jaccardDistance_Training;
        this.jaccardDistance_Validation = jaccardDistance_Validation;
        
        this.hammingLoss_FullTraining = hammingLoss_FullTraining;
        this.hammingLoss_Test = hammingLoss_Test;
        this.hammingLoss_Training = hammingLoss_Training;
        this.hammingLoss_Validation = hammingLoss_Validation;
        
        this.zeroOneLoss_FullTraining = zeroOneLoss_FullTraining;
        this.zeroOneLoss_Test = zeroOneLoss_Test;
        this.zeroOneLoss_Training = zeroOneLoss_Training;
        this.zeroOneLoss_Validation = zeroOneLoss_Validation;
        
        this.harmonicScore_FullTraining = harmonicScore_FullTraining;
        this.harmonicScore_Test = harmonicScore_Test;
        this.harmonicScore_Training = harmonicScore_Training;
        this.harmonicScore_Validation = harmonicScore_Validation;
        
        this.oneError_FullTraining = oneError_FullTraining;
        this.oneError_Test = oneError_Test;
        this.oneError_Training = oneError_Training;
        this.oneError_Validation = oneError_Validation;
        
        this.rankLoss_FullTraining = rankLoss_FullTraining;
        this.rankLoss_Test = rankLoss_Test;
        this.rankLoss_Training = rankLoss_Training;
        this.rankLoss_Validation = rankLoss_Validation;
        
        this.avgPrecision_FullTraining = avgPrecision_FullTraining;
        this.avgPrecision_Test = avgPrecision_Test;
        this.avgPrecision_Training = avgPrecision_Training;
        this.avgPrecision_Validation = avgPrecision_Validation;
        
        this.microPrecision_FullTraining = microPrecision_FullTraining;
        this.microPrecision_Test = microPrecision_Test;
        this.microPrecision_Training = microPrecision_Training;
        this.microPrecision_Validation = microPrecision_Validation;
        
        this.microRecall_FullTraining = microRecall_FullTraining;
        this.microRecall_Test = microRecall_Test;
        this.microRecall_Training = microRecall_Training;
        this.microRecall_Validation = microRecall_Validation;
        
        this.macroPrecision_FullTraining = macroPrecision_FullTraining;
        this.macroPrecision_Test = macroPrecision_Test;
        this.macroPrecision_Training = macroPrecision_Training;
        this.macroPrecision_Validation = macroPrecision_Validation;
        
        this.macroRecall_FullTraining = macroRecall_FullTraining;
        this.macroRecall_Test = macroRecall_Test;
        this.macroRecall_Training = macroRecall_Training;
        this.macroRecall_Validation = macroRecall_Validation;
        
        this.f1MicroAveraged_FullTraining = f1MicroAveraged_FullTraining;
        this.f1MicroAveraged_Test = f1MicroAveraged_Test;
        this.f1MicroAveraged_Training = f1MicroAveraged_Training;
        this.f1MicroAveraged_Validation = f1MicroAveraged_Validation;
        
        this.f1MacroAveragedExample_FullTraining = f1MacroAveragedExample_FullTraining;
        this.f1MacroAveragedExample_Test = f1MacroAveragedExample_Test;
        this.f1MacroAveragedExample_Training = f1MacroAveragedExample_Training;
        this.f1MacroAveragedExample_Validation = f1MacroAveragedExample_Validation;
        
        this.f1MacroAveragedLabel_FullTraining = f1MacroAveragedLabel_FullTraining;
        this.f1MacroAveragedLabel_Test = f1MacroAveragedLabel_Test;
        this.f1MacroAveragedLabel_Training = f1MacroAveragedLabel_Training;
        this.f1MacroAveragedLabel_Validation = f1MacroAveragedLabel_Validation;
        
        this.aurcMacroAveraged_FullTraining = aurcMacroAveraged_FullTraining;
        this.aurcMacroAveraged_Test = aurcMacroAveraged_Test;
        this.aurcMacroAveraged_Training = aurcMacroAveraged_Training;
        this.aurcMacroAveraged_Validation = aurcMacroAveraged_Validation;
        
        this.aurocMacroAveraged_FullTraining = aurocMacroAveraged_FullTraining;
        this.aurocMacroAveraged_Test = aurocMacroAveraged_Test;
        this.aurocMacroAveraged_Training = aurocMacroAveraged_Training;
        this.aurocMacroAveraged_Validation = aurocMacroAveraged_Validation;
        
        this.emptyLabelvectorsPredicted_FullTraining = emptyLabelvectorsPredicted_FullTraining;
        this.emptyLabelvectorsPredicted_Test = emptyLabelvectorsPredicted_Test;
        this.emptyLabelvectorsPredicted_Training = emptyLabelvectorsPredicted_Training;
        this.emptyLabelvectorsPredicted_Validation = emptyLabelvectorsPredicted_Validation;
        
        this.labelCardinalityPredicted_FullTraining = labelCardinalityPredicted_FullTraining;
        this.labelCardinalityPredicted_Test = labelCardinalityPredicted_Test;
        this.labelCardinalityPredicted_Training = labelCardinalityPredicted_Training;
        this.labelCardinalityPredicted_Validation = labelCardinalityPredicted_Validation;
        
        this.levenshteinDistance_FullTraining = levenshteinDistance_FullTraining;
        this.levenshteinDistance_Test = levenshteinDistance_Test;
        this.levenshteinDistance_Training = levenshteinDistance_Training;
        this.levenshteinDistance_Validation = levenshteinDistance_Validation;
        
        this.labelCardinalityDifference_FullTraining = labelCardinalityDifference_FullTraining;
        this.labelCardinalityDifference_Test = labelCardinalityDifference_Test;
        this.labelCardinalityDifference_Training = labelCardinalityDifference_Training;
        this.labelCardinalityDifference_Validation = labelCardinalityDifference_Validation;
        this.completeEvaluation = completeEvaluation;
        
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    
    
    
}
