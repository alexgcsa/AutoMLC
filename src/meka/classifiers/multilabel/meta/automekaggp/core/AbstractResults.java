/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meka.classifiers.multilabel.meta.automekaggp.core;

/**
 *
 * @author alexgcsa
 */
public class AbstractResults {
    
   
    protected String [] command;

    protected double accuracy_FullTraining;
    protected double accuracy_Test;
    protected double accuracy_Training;
    protected double accuracy_Validation;
    protected double accuracy_Evaluation;

    protected double hammingScore_FullTraining;
    protected double hammingScore_Test;
    protected double hammingScore_Training;
    protected double hammingScore_Validation;
    protected double hammingScore_Evaluation;

    protected double exactMatch_FullTraining;
    protected double exactMatch_Test;
    protected double exactMatch_Training;
    protected double exactMatch_Validation;
    protected double exactMatch_Evaluation;

    protected double jaccardDistance_FullTraining;
    protected double jaccardDistance_Test;
    protected double jaccardDistance_Training;
    protected double jaccardDistance_Validation;
    protected double jaccardDistance_Evaluation;

    protected double hammingLoss_FullTraining;
    protected double hammingLoss_Test;
    protected double hammingLoss_Training;
    protected double hammingLoss_Validation;
    protected double hammingLoss_Evaluation;

    protected double zeroOneLoss_FullTraining;
    protected double zeroOneLoss_Test;
    protected double zeroOneLoss_Training;
    protected double zeroOneLoss_Validation;
    protected double zeroOneLoss_Evaluation;

    protected double harmonicScore_FullTraining;
    protected double harmonicScore_Test;
    protected double harmonicScore_Training;
    protected double harmonicScore_Validation;
    protected double harmonicScore_Evaluation;

    protected double oneError_FullTraining;
    protected double oneError_Test;
    protected double oneError_Training;
    protected double oneError_Validation;
    protected double oneError_Evaluation;

    protected double rankLoss_FullTraining;
    protected double rankLoss_Test;
    protected double rankLoss_Training;
    protected double rankLoss_Validation;
    protected double rankLoss_Evaluation;

    protected double avgPrecision_FullTraining;
    protected double avgPrecision_Test;
    protected double avgPrecision_Training;
    protected double avgPrecision_Validation;
    protected double avgPrecision_Evaluation;

    protected double microPrecision_FullTraining;
    protected double microPrecision_Test;
    protected double microPrecision_Training;
    protected double microPrecision_Validation;
    protected double microPrecision_Evaluation;

    protected double microRecall_FullTraining;
    protected double microRecall_Test;
    protected double microRecall_Training;
    protected double microRecall_Validation;
    protected double microRecall_Evaluation;

    protected double macroPrecision_FullTraining;
    protected double macroPrecision_Test;
    protected double macroPrecision_Training;
    protected double macroPrecision_Validation;
    protected double macroPrecision_Evaluation;

    protected double macroRecall_FullTraining;
    protected double macroRecall_Test;
    protected double macroRecall_Training;
    protected double macroRecall_Validation;
    protected double macroRecall_Evaluation;

    protected double f1MicroAveraged_FullTraining;
    protected double f1MicroAveraged_Test;
    protected double f1MicroAveraged_Training;
    protected double f1MicroAveraged_Validation;
    protected double f1MicroAveraged_Evaluation;
    

    protected double f1MacroAveragedExample_FullTraining;
    protected double f1MacroAveragedExample_Test;
    protected double f1MacroAveragedExample_Training;
    protected double f1MacroAveragedExample_Validation;
    protected double f1MacroAveragedExample_Evaluation;

    protected double f1MacroAveragedLabel_FullTraining;
    protected double f1MacroAveragedLabel_Test;
    protected double f1MacroAveragedLabel_Training;
    protected double f1MacroAveragedLabel_Validation;
    protected double f1MacroAveragedLabel_Evaluation;

    protected double aurcMacroAveraged_FullTraining;
    protected double aurcMacroAveraged_Test;
    protected double aurcMacroAveraged_Training;
    protected double aurcMacroAveraged_Validation;
    protected double aurcMacroAveraged_Evaluation;

    protected double aurocMacroAveraged_FullTraining;
    protected double aurocMacroAveraged_Test;
    protected double aurocMacroAveraged_Training;
    protected double aurocMacroAveraged_Validation;
    protected double aurocMacroAveraged_Evaluation;

    protected double emptyLabelvectorsPredicted_FullTraining;
    protected double emptyLabelvectorsPredicted_Test;
    protected double emptyLabelvectorsPredicted_Training;
    protected double emptyLabelvectorsPredicted_Validation;
    protected double emptyLabelvectorsPredicted_Evaluation;

    protected double labelCardinalityPredicted_FullTraining;
    protected double labelCardinalityPredicted_Test;
    protected double labelCardinalityPredicted_Training;
    protected double labelCardinalityPredicted_Validation;
    protected double labelCardinalityPredicted_Evaluation;

    protected double levenshteinDistance_FullTraining;
    protected double levenshteinDistance_Test;
    protected double levenshteinDistance_Training;
    protected double levenshteinDistance_Validation;
    protected double levenshteinDistance_Evaluation;

    protected double labelCardinalityDifference_FullTraining;
    protected double labelCardinalityDifference_Test;
    protected double labelCardinalityDifference_Training;
    protected double labelCardinalityDifference_Validation; 
    protected double labelCardinalityDifference_Evaluation;
    
    protected boolean completeEvaluation;

    public String[] getCommand() {
        return command;
    }

    public double getAccuracy_FullTraining() {
        return accuracy_FullTraining;
    }

    public double getAccuracy_Test() {
        return accuracy_Test;
    }

    public double getAccuracy_Training() {
        return accuracy_Training;
    }

    public double getAccuracy_Validation() {
        return accuracy_Validation;
    }
    
    public double getAccuracy_Evaluation() {
        return accuracy_Evaluation;
    }    

    public double getHammingScore_FullTraining() {
        return hammingScore_FullTraining;
    }

    public double getHammingScore_Test() {
        return hammingScore_Test;
    }

    public double getHammingScore_Training() {
        return hammingScore_Training;
    }

    public double getHammingScore_Validation() {
        return hammingScore_Validation;
    }
    
    public double getHammingScore_Evaluation() {
        return hammingScore_Evaluation;
    }    

    public double getExactMatch_FullTraining() {
        return exactMatch_FullTraining;
    }

    public double getExactMatch_Test() {
        return exactMatch_Test;
    }

    public double getExactMatch_Training() {
        return exactMatch_Training;
    }

    public double getExactMatch_Validation() {
        return exactMatch_Validation;
    }
    
    public double getExactMatch_Evaluation() {
        return exactMatch_Evaluation;
    }    

    public double getJaccardDistance_FullTraining() {
        return jaccardDistance_FullTraining;
    }

    public double getJaccardDistance_Test() {
        return jaccardDistance_Test;
    }

    public double getJaccardDistance_Training() {
        return jaccardDistance_Training;
    }

    public double getJaccardDistance_Validation() {
        return jaccardDistance_Validation;
    }
    
    public double getJaccardDistance_Evaluation() {
        return jaccardDistance_Training;
    }    

    public double getHammingLoss_FullTraining() {
        return hammingLoss_FullTraining;
    }

    public double getHammingLoss_Test() {
        return hammingLoss_Test;
    }

    public double getHammingLoss_Training() {
        return hammingLoss_Training;
    }

    public double getHammingLoss_Validation() {
        return hammingLoss_Validation;
    }
    
    public double getHammingLoss_Evaluation() {
        return hammingLoss_Evaluation;
    } 

    public double getZeroOneLoss_FullTraining() {
        return zeroOneLoss_FullTraining;
    }

    public double getZeroOneLoss_Test() {
        return zeroOneLoss_Test;
    }

    public double getZeroOneLoss_Training() {
        return zeroOneLoss_Training;
    }

    public double getZeroOneLoss_Validation() {
        return zeroOneLoss_Validation;
    }
    
    public double getZeroOneLoss_Evaluation() {
        return zeroOneLoss_Evaluation;
    }    

    public double getHarmonicScore_FullTraining() {
        return harmonicScore_FullTraining;
    }

    public double getHarmonicScore_Test() {
        return harmonicScore_Test;
    }

    public double getHarmonicScore_Training() {
        return harmonicScore_Training;
    }

    public double getHarmonicScore_Validation() {
        return harmonicScore_Validation;
    }
    
    public double getHarmonicScore_Evaluation() {
        return harmonicScore_Evaluation;
    }    

    public double getOneError_FullTraining() {
        return oneError_FullTraining;
    }

    public double getOneError_Test() {
        return oneError_Test;
    }

    public double getOneError_Training() {
        return oneError_Training;
    }

    public double getOneError_Validation() {
        return oneError_Validation;
    }
    
    public double getOneError_Evaluation() {
        return oneError_Evaluation;
    }    

    public double getRankLoss_FullTraining() {
        return rankLoss_FullTraining;
    }

    public double getRankLoss_Test() {
        return rankLoss_Test;
    }

    public double getRankLoss_Training() {
        return rankLoss_Training;
    }

    public double getRankLoss_Validation() {
        return rankLoss_Validation;
    }

    public double getRankLoss_Evaluation() {
        return rankLoss_Evaluation;
    }
    
    public double getAvgPrecision_FullTraining() {
        return avgPrecision_FullTraining;
    }

    public double getAvgPrecision_Test() {
        return avgPrecision_Test;
    }

    public double getAvgPrecision_Training() {
        return avgPrecision_Training;
    }

    public double getAvgPrecision_Validation() {
        return avgPrecision_Validation;
    }
    
    public double getAvgPrecision_Evaluation() {
        return avgPrecision_Evaluation;
    }    

    public double getMicroPrecision_FullTraining() {
        return microPrecision_FullTraining;
    }

    public double getMicroPrecision_Test() {
        return microPrecision_Test;
    }

    public double getMicroPrecision_Training() {
        return microPrecision_Training;
    }

    public double getMicroPrecision_Validation() {
        return microPrecision_Validation;
    }

    public double getMicroPrecision_Evaluation() {
        return microPrecision_Evaluation;
    }    
    
    public double getMicroRecall_FullTraining() {
        return microRecall_FullTraining;
    }

    public double getMicroRecall_Test() {
        return microRecall_Test;
    }

    public double getMicroRecall_Training() {
        return microRecall_Training;
    }

    public double getMicroRecall_Validation() {
        return microRecall_Validation;
    }
    
    public double getMicroRecall_Evaluation() {
        return microRecall_Evaluation;
    }    

    public double getMacroPrecision_FullTraining() {
        return macroPrecision_FullTraining;
    }

    public double getMacroPrecision_Test() {
        return macroPrecision_Test;
    }

    public double getMacroPrecision_Training() {
        return macroPrecision_Training;
    }

    public double getMacroPrecision_Validation() {
        return macroPrecision_Validation;
    }
    

    public double getMacroPrecision_Evaluation() {
        return macroPrecision_Evaluation;
    }    

    public double getMacroRecall_FullTraining() {
        return macroRecall_FullTraining;
    }

    public double getMacroRecall_Test() {
        return macroRecall_Test;
    }

    public double getMacroRecall_Training() {
        return macroRecall_Training;
    }

    public double getMacroRecall_Validation() {
        return macroRecall_Validation;
    }

    public double getMacroRecall_Evaluation() {
        return macroRecall_Evaluation;
    }
    
    public double getF1MicroAveraged_FullTraining() {
        return f1MicroAveraged_FullTraining;
    }

    public double getF1MicroAveraged_Test() {
        return f1MicroAveraged_Test;
    }

    public double getF1MicroAveraged_Training() {
        return f1MicroAveraged_Training;
    }

    public double getF1MicroAveraged_Validation() {
        return f1MicroAveraged_Validation;
    }
    
    public double getF1MicroAveraged_Evaluation() {
        return f1MicroAveraged_Evaluation;
    }    

    public double getF1MacroAveragedExample_FullTraining() {
        return f1MacroAveragedExample_FullTraining;
    }

    public double getF1MacroAveragedExample_Test() {
        return f1MacroAveragedExample_Test;
    }

    public double getF1MacroAveragedExample_Training() {
        return f1MacroAveragedExample_Training;
    }

    public double getF1MacroAveragedExample_Validation() {
        return f1MacroAveragedExample_Validation;
    }

    public double getF1MacroAveragedExample_Evaluation() {
        return f1MacroAveragedExample_Evaluation;
    }
        
    public double getF1MacroAveragedLabel_FullTraining() {
        return f1MacroAveragedLabel_FullTraining;
    }

    public double getF1MacroAveragedLabel_Test() {
        return f1MacroAveragedLabel_Test;
    }

    public double getF1MacroAveragedLabel_Training() {
        return f1MacroAveragedLabel_Training;
    }

    public double getF1MacroAveragedLabel_Validation() {
        return f1MacroAveragedLabel_Validation;
    }
    
    public double getF1MacroAveragedLabel_Evaluation() {
        return f1MacroAveragedLabel_Evaluation;
    }    

    public double getAurcMacroAveraged_FullTraining() {
        return aurcMacroAveraged_FullTraining;
    }

    public double getAurcMacroAveraged_Test() {
        return aurcMacroAveraged_Test;
    }

    public double getAurcMacroAveraged_Training() {
        return aurcMacroAveraged_Training;
    }

    public double getAurcMacroAveraged_Validation() {
        return aurcMacroAveraged_Validation;
    }
    
    public double getAurcMacroAveraged_Evaluation() {
        return aurcMacroAveraged_Evaluation;
    } 
    
    public double getAurocMacroAveraged_FullTraining() {
        return aurocMacroAveraged_FullTraining;
    }

    public double getAurocMacroAveraged_Test() {
        return aurocMacroAveraged_Test;
    }

    public double getAurocMacroAveraged_Training() {
        return aurocMacroAveraged_Training;
    }

    public double getAurocMacroAveraged_Validation() {
        return aurocMacroAveraged_Validation;
    }
    
    public double getAurocMacroAveraged_Evaluation() {
        return aurocMacroAveraged_Evaluation;
    }    
    
    public double getEmptyLabelvectorsPredicted_FullTraining() {
        return emptyLabelvectorsPredicted_FullTraining;
    }

    public double getEmptyLabelvectorsPredicted_Test() {
        return emptyLabelvectorsPredicted_Test;
    }

    public double getEmptyLabelvectorsPredicted_Training() {
        return emptyLabelvectorsPredicted_Training;
    }

    public double getEmptyLabelvectorsPredicted_Validation() {
        return emptyLabelvectorsPredicted_Validation;
    }
    
    public double getEmptyLabelvectorsPredicted_Evaluation() {
        return emptyLabelvectorsPredicted_Evaluation;
    }       

    public double getLabelCardinalityPredicted_FullTraining() {
        return labelCardinalityPredicted_FullTraining;
    }

    public double getLabelCardinalityPredicted_Test() {
        return labelCardinalityPredicted_Test;
    }

    public double getLabelCardinalityPredicted_Training() {
        return labelCardinalityPredicted_Training;
    }

    public double getLabelCardinalityPredicted_Validation() {
        return labelCardinalityPredicted_Validation;
    }
    
    public double getLabelCardinalityPredicted_Evaluation() {
        return labelCardinalityPredicted_Evaluation;
    }    

    public double getLevenshteinDistance_FullTraining() {
        return levenshteinDistance_FullTraining;
    }

    public double getLevenshteinDistance_Test() {
        return levenshteinDistance_Test;
    }

    public double getLevenshteinDistance_Training() {
        return levenshteinDistance_Training;
    }

    public double getLevenshteinDistance_Validation() {
        return levenshteinDistance_Validation;
    }
    
    public double getLevenshteinDistance_Evaluation() {
        return levenshteinDistance_Evaluation;
    }    

    public double getLabelCardinalityDifference_FullTraining() {
        return labelCardinalityDifference_FullTraining;
    }

    public double getLabelCardinalityDifference_Test() {
        return labelCardinalityDifference_Test;
    }

    public double getLabelCardinalityDifference_Training() {
        return labelCardinalityDifference_Training;
    }

    public double getLabelCardinalityDifference_Validation() {
        return labelCardinalityDifference_Validation;
    }

    public double getLabelCardinalityDifference_Evaluation() {
        return labelCardinalityDifference_Evaluation;
    }
    public boolean isCompleteEvaluation() {
        return completeEvaluation;
    }    

    public void setCommand(String[] command) {
        this.command = command;
    }

    public void setAccuracy_FullTraining(double accuracy_FullTraining) {
        this.accuracy_FullTraining = accuracy_FullTraining;
    }

    public void setAccuracy_Test(double accuracy_Test) {
        this.accuracy_Test = accuracy_Test;
    }

    public void setAccuracy_Training(double accuracy_Training) {
        this.accuracy_Training = accuracy_Training;
    }

    public void setAccuracy_Validation(double accuracy_Validation) {
        this.accuracy_Validation = accuracy_Validation;
    }

    public void setAccuracy_Evaluation(double accuracy_Evaluation) {
        this.accuracy_Evaluation = accuracy_Evaluation;
    }
  
    public void setHammingScore_FullTraining(double hammingScore_FullTraining) {
        this.hammingScore_FullTraining = hammingScore_FullTraining;
    }

    public void setHammingScore_Test(double hammingScore_Test) {
        this.hammingScore_Test = hammingScore_Test;
    }

    public void setHammingScore_Training(double hammingScore_Training) {
        this.hammingScore_Training = hammingScore_Training;
    }

    public void setHammingScore_Validation(double hammingScore_Validation) {
        this.hammingScore_Validation = hammingScore_Validation;
    }
    
    public void setHammingScore_Evaluation(double hammingScore_Evaluation) {
        this.hammingScore_Evaluation = hammingScore_Evaluation;
    }


    public void setExactMatch_FullTraining(double exactMatch_FullTraining) {
        this.exactMatch_FullTraining = exactMatch_FullTraining;
    }

    public void setExactMatch_Test(double exactMatch_Test) {
        this.exactMatch_Test = exactMatch_Test;
    }

    public void setExactMatch_Training(double exactMatch_Training) {
        this.exactMatch_Training = exactMatch_Training;
    }

    public void setExactMatch_Validation(double exactMatch_Validation) {
        this.exactMatch_Validation = exactMatch_Validation;
    }
    
    public void setExactMatch_Evaluation(double exactMatch_Evaluation) {
        this.exactMatch_Evaluation = exactMatch_Evaluation;
    }        

    public void setJaccardDistance_FullTraining(double jaccardDistance_FullTraining) {
        this.jaccardDistance_FullTraining = jaccardDistance_FullTraining;
    }

    public void setJaccardDistance_Test(double jaccardDistance_Test) {
        this.jaccardDistance_Test = jaccardDistance_Test;
    }

    public void setJaccardDistance_Training(double jaccardDistance_Training) {
        this.jaccardDistance_Training = jaccardDistance_Training;
    }

    public void setJaccardDistance_Validation(double jaccardDistance_Validation) {
        this.jaccardDistance_Validation = jaccardDistance_Validation;
    }
    
    public void setJaccardDistance_Evaluation(double jaccardDistance_Evaluation) {
        this.jaccardDistance_Evaluation = jaccardDistance_Evaluation;
    }

    public void setHammingLoss_FullTraining(double hammingLoss_FullTraining) {
        this.hammingLoss_FullTraining = hammingLoss_FullTraining;
    }

    public void setHammingLoss_Test(double hammingLoss_Test) {
        this.hammingLoss_Test = hammingLoss_Test;
    }

    public void setHammingLoss_Training(double hammingLoss_Training) {
        this.hammingLoss_Training = hammingLoss_Training;
    }

    public void setHammingLoss_Validation(double hammingLoss_Validation) {
        this.hammingLoss_Validation = hammingLoss_Validation;
    }

    public void setHammingLoss_Evaluation(double hammingLoss_Evaluation) {
        this.hammingLoss_Evaluation = hammingLoss_Evaluation;
    } 

    public void setZeroOneLoss_FullTraining(double zeroOneLoss_FullTraining) {
        this.zeroOneLoss_FullTraining = zeroOneLoss_FullTraining;
    }

    public void setZeroOneLoss_Test(double zeroOneLoss_Test) {
        this.zeroOneLoss_Test = zeroOneLoss_Test;
    }

    public void setZeroOneLoss_Training(double zeroOneLoss_Training) {
        this.zeroOneLoss_Training = zeroOneLoss_Training;
    }

    public void setZeroOneLoss_Validation(double zeroOneLoss_Validation) {
        this.zeroOneLoss_Validation = zeroOneLoss_Validation;
    }

    public void setZeroOneLoss_Evaluation(double zeroOneLoss_Evaluation) {
        this.zeroOneLoss_Evaluation = zeroOneLoss_Evaluation;
    }        

    public void setHarmonicScore_FullTraining(double harmonicScore_FullTraining) {
        this.harmonicScore_FullTraining = harmonicScore_FullTraining;
    }

    public void setHarmonicScore_Test(double harmonicScore_Test) {
        this.harmonicScore_Test = harmonicScore_Test;
    }

    public void setHarmonicScore_Training(double harmonicScore_Training) {
        this.harmonicScore_Training = harmonicScore_Training;
    }

    public void setHarmonicScore_Validation(double harmonicScore_Validation) {
        this.harmonicScore_Validation = harmonicScore_Validation;
    }
    
    public void setHarmonicScore_Evaluation(double harmonicScore_Evaluation) {
        this.harmonicScore_Evaluation = harmonicScore_Evaluation;
    } 

    public void setOneError_FullTraining(double oneError_FullTraining) {
        this.oneError_FullTraining = oneError_FullTraining;
    }

    public void setOneError_Test(double oneError_Test) {
        this.oneError_Test = oneError_Test;
    }

    public void setOneError_Training(double oneError_Training) {
        this.oneError_Training = oneError_Training;
    }

    public void setOneError_Validation(double oneError_Validation) {
        this.oneError_Validation = oneError_Validation;
    }

    public void setOneError_Evaluation(double oneError_Evaluation) {
        this.oneError_Evaluation = oneError_Evaluation;
    }
    
    public void setRankLoss_FullTraining(double rankLoss_FullTraining) {
        this.rankLoss_FullTraining = rankLoss_FullTraining;
    }

    public void setRankLoss_Test(double rankLoss_Test) {
        this.rankLoss_Test = rankLoss_Test;
    }

    public void setRankLoss_Training(double rankLoss_Training) {
        this.rankLoss_Training = rankLoss_Training;
    }

    public void setRankLoss_Validation(double rankLoss_Validation) {
        this.rankLoss_Validation = rankLoss_Validation;
    }

    public void setRankLoss_Evaluation(double rankLoss_Evaluation) {
        this.rankLoss_Evaluation = rankLoss_Evaluation;
    }
    
    public void setAvgPrecision_FullTraining(double avgPrecision_FullTraining) {
        this.avgPrecision_FullTraining = avgPrecision_FullTraining;
    }

    public void setAvgPrecision_Test(double avgPrecision_Test) {
        this.avgPrecision_Test = avgPrecision_Test;
    }

    public void setAvgPrecision_Training(double avgPrecision_Training) {
        this.avgPrecision_Training = avgPrecision_Training;
    }

    public void setAvgPrecision_Validation(double avgPrecision_Validation) {
        this.avgPrecision_Validation = avgPrecision_Validation;
    }


    public void setAvgPrecision_Evaluation(double avgPrecision_Evaluation) {
        this.avgPrecision_Evaluation = avgPrecision_Evaluation;
    }
    
    public void setMicroPrecision_FullTraining(double microPrecision_FullTraining) {
        this.microPrecision_FullTraining = microPrecision_FullTraining;
    }

    public void setMicroPrecision_Test(double microPrecision_Test) {
        this.microPrecision_Test = microPrecision_Test;
    }

    public void setMicroPrecision_Training(double microPrecision_Training) {
        this.microPrecision_Training = microPrecision_Training;
    }

    public void setMicroPrecision_Validation(double microPrecision_Validation) {
        this.microPrecision_Validation = microPrecision_Validation;
    }
    
    public void setMicroPrecision_Evaluation(double microPrecision_Evaluation) {
        this.microPrecision_Evaluation = microPrecision_Evaluation;
    }

    public void setMicroRecall_FullTraining(double microRecall_FullTraining) {
        this.microRecall_FullTraining = microRecall_FullTraining;
    }

    public void setMicroRecall_Test(double microRecall_Test) {
        this.microRecall_Test = microRecall_Test;
    }

    public void setMicroRecall_Training(double microRecall_Training) {
        this.microRecall_Training = microRecall_Training;
    }

    public void setMicroRecall_Validation(double microRecall_Validation) {
        this.microRecall_Validation = microRecall_Validation;
    }
    
    public void setMicroRecall_Evaluation(double microRecall_Evaluation) {
        this.microRecall_Evaluation = microRecall_Evaluation;
    }

    public void setMacroPrecision_FullTraining(double macroPrecision_FullTraining) {
        this.macroPrecision_FullTraining = macroPrecision_FullTraining;
    }

    public void setMacroPrecision_Test(double macroPrecision_Test) {
        this.macroPrecision_Test = macroPrecision_Test;
    }

    public void setMacroPrecision_Training(double macroPrecision_Training) {
        this.macroPrecision_Training = macroPrecision_Training;
    }

    public void setMacroPrecision_Validation(double macroPrecision_Validation) {
        this.macroPrecision_Validation = macroPrecision_Validation;
    }    

    public void setMacroPrecision_Evaluation(double macroPrecision_Evaluation) {
        this.macroPrecision_Evaluation = macroPrecision_Evaluation;
    }

    public void setMacroRecall_FullTraining(double macroRecall_FullTraining) {
        this.macroRecall_FullTraining = macroRecall_FullTraining;
    }

    public void setMacroRecall_Test(double macroRecall_Test) {
        this.macroRecall_Test = macroRecall_Test;
    }

    public void setMacroRecall_Training(double macroRecall_Training) {
        this.macroRecall_Training = macroRecall_Training;
    }

    public void setMacroRecall_Validation(double macroRecall_Validation) {
        this.macroRecall_Validation = macroRecall_Validation;
    }

    public void setMacroRecall_Evaluation(double macroRecall_Evaluation) {
        this.macroRecall_Evaluation = macroRecall_Evaluation;
    }

    public void setF1MicroAveraged_FullTraining(double f1MicroAveraged_FullTraining) {
        this.f1MicroAveraged_FullTraining = f1MicroAveraged_FullTraining;
    }

    public void setF1MicroAveraged_Test(double f1MicroAveraged_Test) {
        this.f1MicroAveraged_Test = f1MicroAveraged_Test;
    }

    public void setF1MicroAveraged_Training(double f1MicroAveraged_Training) {
        this.f1MicroAveraged_Training = f1MicroAveraged_Training;
    }

    public void setF1MicroAveraged_Validation(double f1MicroAveraged_Validation) {
        this.f1MicroAveraged_Validation = f1MicroAveraged_Validation;
    }    

    public void setF1MicroAveraged_Evaluation(double f1MicroAveraged_Evaluation) {
        this.f1MicroAveraged_Evaluation = f1MicroAveraged_Evaluation;
    }         

    public void setF1MacroAveragedExample_FullTraining(double f1MacroAveragedExample_FullTraining) {
        this.f1MacroAveragedExample_FullTraining = f1MacroAveragedExample_FullTraining;
    }

    public void setF1MacroAveragedExample_Test(double f1MacroAveragedExample_Test) {
        this.f1MacroAveragedExample_Test = f1MacroAveragedExample_Test;
    }

    public void setF1MacroAveragedExample_Training(double f1MacroAveragedExample_Training) {
        this.f1MacroAveragedExample_Training = f1MacroAveragedExample_Training;
    }

    public void setF1MacroAveragedExample_Validation(double f1MacroAveragedExample_Validation) {
        this.f1MacroAveragedExample_Validation = f1MacroAveragedExample_Validation;
    }
    
    public void setF1MacroAveragedExample_Evaluation(double f1MacroAveragedExample_Evaluation) {
        this.f1MacroAveragedExample_Evaluation = f1MacroAveragedExample_Evaluation;
    }

    public void setF1MacroAveragedLabel_FullTraining(double f1MacroAveragedLabel_FullTraining) {
        this.f1MacroAveragedLabel_FullTraining = f1MacroAveragedLabel_FullTraining;
    }

    public void setF1MacroAveragedLabel_Test(double f1MacroAveragedLabel_Test) {
        this.f1MacroAveragedLabel_Test = f1MacroAveragedLabel_Test;
    }

    public void setF1MacroAveragedLabel_Training(double f1MacroAveragedLabel_Training) {
        this.f1MacroAveragedLabel_Training = f1MacroAveragedLabel_Training;
    }

    public void setF1MacroAveragedLabel_Validation(double f1MacroAveragedLabel_Validation) {
        this.f1MacroAveragedLabel_Validation = f1MacroAveragedLabel_Validation;
    }

    public void setF1MacroAveragedLabel_Evaluation(double f1MacroAveragedLabel_Evaluation) {
        this.f1MacroAveragedLabel_Evaluation = f1MacroAveragedLabel_Evaluation;
    }

    public void setAurcMacroAveraged_FullTraining(double aurcMacroAveraged_FullTraining) {
        this.aurcMacroAveraged_FullTraining = aurcMacroAveraged_FullTraining;
    }

    public void setAurcMacroAveraged_Test(double aurcMacroAveraged_Test) {
        this.aurcMacroAveraged_Test = aurcMacroAveraged_Test;
    }

    public void setAurcMacroAveraged_Training(double aurcMacroAveraged_Training) {
        this.aurcMacroAveraged_Training = aurcMacroAveraged_Training;
    }

    public void setAurcMacroAveraged_Validation(double aurcMacroAveraged_Validation) {
        this.aurcMacroAveraged_Validation = aurcMacroAveraged_Validation;
    }

    public void setAurcMacroAveraged_Evaluation(double aurcMacroAveraged_Evaluation) {
        this.aurcMacroAveraged_Evaluation = aurcMacroAveraged_Evaluation;
    }
    
    public void setAurocMacroAveraged_FullTraining(double aurocMacroAveraged_FullTraining) {
        this.aurocMacroAveraged_FullTraining = aurocMacroAveraged_FullTraining;
    }

    public void setAurocMacroAveraged_Test(double aurocMacroAveraged_Test) {
        this.aurocMacroAveraged_Test = aurocMacroAveraged_Test;
    }

    public void setAurocMacroAveraged_Training(double aurocMacroAveraged_Training) {
        this.aurocMacroAveraged_Training = aurocMacroAveraged_Training;
    }

    public void setAurocMacroAveraged_Validation(double aurocMacroAveraged_Validation) {
        this.aurocMacroAveraged_Validation = aurocMacroAveraged_Validation;
    }

    public void setAurocMacroAveraged_Evaluation(double aurocMacroAveraged_Evaluation) {
        this.aurocMacroAveraged_Evaluation = aurocMacroAveraged_Evaluation;
    }        

    public void setEmptyLabelvectorsPredicted_FullTraining(double emptyLabelvectorsPredicted_FullTraining) {
        this.emptyLabelvectorsPredicted_FullTraining = emptyLabelvectorsPredicted_FullTraining;
    }

    public void setEmptyLabelvectorsPredicted_Test(double emptyLabelvectorsPredicted_Test) {
        this.emptyLabelvectorsPredicted_Test = emptyLabelvectorsPredicted_Test;
    }

    public void setEmptyLabelvectorsPredicted_Training(double emptyLabelvectorsPredicted_Training) {
        this.emptyLabelvectorsPredicted_Training = emptyLabelvectorsPredicted_Training;
    }

    public void setEmptyLabelvectorsPredicted_Validation(double emptyLabelvectorsPredicted_Validation) {
        this.emptyLabelvectorsPredicted_Validation = emptyLabelvectorsPredicted_Validation;
    }

    public void setEmptyLabelvectorsPredicted_Evaluation(double emptyLabelvectorsPredicted_Evaluation) {
        this.emptyLabelvectorsPredicted_Evaluation = emptyLabelvectorsPredicted_Evaluation;
    }    

    public void setLabelCardinalityPredicted_FullTraining(double labelCardinalityPredicted_FullTraining) {
        this.labelCardinalityPredicted_FullTraining = labelCardinalityPredicted_FullTraining;
    }

    public void setLabelCardinalityPredicted_Test(double labelCardinalityPredicted_Test) {
        this.labelCardinalityPredicted_Test = labelCardinalityPredicted_Test;
    }

    public void setLabelCardinalityPredicted_Training(double labelCardinalityPredicted_Training) {
        this.labelCardinalityPredicted_Training = labelCardinalityPredicted_Training;
    }

    public void setLabelCardinalityPredicted_Validation(double labelCardinalityPredicted_Validation) {
        this.labelCardinalityPredicted_Validation = labelCardinalityPredicted_Validation;
    }

    public void setLabelCardinalityPredicted_Evaluation(double labelCardinalityPredicted_Evaluation) {
        this.labelCardinalityPredicted_Evaluation = labelCardinalityPredicted_Evaluation;
    }  

    public void setLevenshteinDistance_FullTraining(double levenshteinDistance_FullTraining) {
        this.levenshteinDistance_FullTraining = levenshteinDistance_FullTraining;
    }

    public void setLevenshteinDistance_Test(double levenshteinDistance_Test) {
        this.levenshteinDistance_Test = levenshteinDistance_Test;
    }

    public void setLevenshteinDistance_Training(double levenshteinDistance_Training) {
        this.levenshteinDistance_Training = levenshteinDistance_Training;
    }

    public void setLevenshteinDistance_Validation(double levenshteinDistance_Validation) {
        this.levenshteinDistance_Validation = levenshteinDistance_Validation;
    }

    public void setLevenshteinDistance_Evaluation(double levenshteinDistance_Evaluation) {
        this.levenshteinDistance_Evaluation = levenshteinDistance_Evaluation;
    }

    public void setLabelCardinalityDifference_FullTraining(double labelCardinalityDifference_FullTraining) {
        this.labelCardinalityDifference_FullTraining = labelCardinalityDifference_FullTraining;
    }

    public void setLabelCardinalityDifference_Test(double labelCardinalityDifference_Test) {
        this.labelCardinalityDifference_Test = labelCardinalityDifference_Test;
    }

    public void setLabelCardinalityDifference_Training(double labelCardinalityDifference_Training) {
        this.labelCardinalityDifference_Training = labelCardinalityDifference_Training;
    }

    public void setLabelCardinalityDifference_Validation(double labelCardinalityDifference_Validation) {
        this.labelCardinalityDifference_Validation = labelCardinalityDifference_Validation;
    }    
    
    public void setLabelCardinalityDifference_Evaluation(double labelCardinalityDifference_Evaluation) {
        this.labelCardinalityDifference_Evaluation = labelCardinalityDifference_Evaluation;
    }   
    
    public void setCompleteEvaluation(boolean completeEvaluation) {
        this.completeEvaluation = completeEvaluation;
    }    
    
}



