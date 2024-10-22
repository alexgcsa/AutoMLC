/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meka.classifiers.multilabel.meta.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import mulan.data.InvalidDataFormatException;
import mulan.data.IterativeStratification;
import mulan.data.LabelPowersetStratification;
import mulan.data.MultiLabelInstances;
import mulan.dimensionalityReduction.LabelPowersetAttributeEvaluator;
import mulan.dimensionalityReduction.Ranker;
import mulan.sampling.MLSOL;
import org.codehaus.plexus.util.FileUtils;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.GainRatioAttributeEval;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.RemoveFolds;

/**
 *
 * @author alexgcsa
 */
public class DataUtil {
    
    
    public static int getNonAggressivelyMultFidAttributes(int nAttributes, int steps) {
        if (steps <= 0) {
            return nAttributes;
        }

        int nAttributesToKeep = (int) (nAttributes / steps);
        if (nAttributesToKeep < 3) {
            nAttributesToKeep = nAttributes;
        }
        return nAttributesToKeep;
    } 
    
    
    public static int getAggressivelyMultFidAttributes(int nAttributes, int steps) {
        if (steps <= 0) {
            return nAttributes;
        }

        int div = (int) Math.pow(2, (steps - 1));
        int nAttributesToKeep = Math.round(nAttributes / div);
        if (nAttributesToKeep < 3) {
            nAttributesToKeep = (int) (nAttributes / steps);
            if (nAttributesToKeep < 3) {
                nAttributesToKeep = nAttributes;
            }
        }
        return nAttributesToKeep;
    }
    
    public static void noFeatureSelection(int nLabels, long timeBudget, String arffDir){
        try {
            MultiLabelInstances dataset = new MultiLabelInstances(arffDir, nLabels);
            
            try (FileWriter fTraining = new FileWriter("Training-" + timeBudget+".arff", false)) {
                fTraining.write(dataset.getDataSet().toString());
                fTraining.close();
            }            
            
         } catch (Exception ex) {
            System.err.println("Exception: "+ex);
        }
            
    } 
    
    /**
     * It selects attributes from the training set.    
     * Tt uses Mulan Java library to do it.
     * @param nAttributesToKeep - The number of attributes to keep in the dataset.
     * @param nLabels - The number of labels of the input dataset.
     * @param timeBudget - The current time budget.
     * 
     */
    public static void featureSelection(int nAttributesToKeep, 
                                 int nLabels, 
                                 long timeBudget, 
                                 String arffDir){
//        String arffDir = this.getTrainingDirectory(); 
        try {    
            MultiLabelInstances dataset = new MultiLabelInstances(arffDir, nLabels);
            ASEvaluation ase = new GainRatioAttributeEval();  
            LabelPowersetAttributeEvaluator ae = new LabelPowersetAttributeEvaluator(ase, dataset);
            //BinaryRelevanceAttributeEvaluator ae = new BinaryRelevanceAttributeEvaluator(ase, dataset, "max", "dl", "eval");
            Ranker r = new Ranker();
            int[] result = r.search(ae, dataset);
            
 
            int[] toKeep = new int[nAttributesToKeep + nLabels];
            System.arraycopy(result, 0, toKeep, 0, nAttributesToKeep);
            int[] labelIndices = dataset.getLabelIndices();
            System.arraycopy(labelIndices, 0, toKeep, nAttributesToKeep, dataset.getNumLabels());

            Remove filterRemove = new Remove();
            filterRemove.setAttributeIndicesArray(toKeep);
            filterRemove.setInvertSelection(true);
            filterRemove.setInputFormat(dataset.getDataSet());
            Instances filtered = Filter.useFilter(dataset.getDataSet(), filterRemove);
            MultiLabelInstances filteredDataset = new MultiLabelInstances(filtered, dataset.getLabelsMetaData());  
   
            Instances trainInst = filteredDataset.getDataSet();            
            MultiLabelInstances trainingData = new MultiLabelInstances(trainInst, dataset.getLabelsMetaData());
            trainingData.getDataSet().setRelationName(dataset.getDataSet().relationName());

            try (FileWriter fTraining = new FileWriter("Training-" + timeBudget+".arff", false)) {
                fTraining.write(trainingData.getDataSet().toString());
                fTraining.close();
            }
  
        } catch (Exception ex) {
            System.err.println("Exception: "+ex);
        }

    }
    
    public static void removeFiles(ArrayList<String> files){
        for(String f : files){
            File fileT = new File(f);
            fileT.delete(); 
        }

    }    
    
    public static void removeUnnecessaryFiles(String experimentName, int foldInit){
        //Remove learning and validation files.
        File fileT = new File("./Learning-" + foldInit + ".arff");
        fileT.delete();
        fileT = new File("./Validation-" + foldInit + ".arff");
        fileT.delete(); 
        try {
            FileUtils.deleteDirectory("./results-"+ experimentName);
        } catch (IOException ex) {
           System.out.println(ex);
        }
    }
    
    public static void removeUnnecessaryFilesSyn(long[] intemBudgets, String experimentName){
        //Remove learning and validation files.
        System.out.println("Remove learning and validation files.");
        for(long ib : intemBudgets){
            File fileT = new File("./Training-" + ib+".arff");
            fileT.delete(); 
            fileT = new File("./TrainingSyn-" + ib+".arff");
            fileT.delete(); 
            
            fileT = new File("./LearningIterativeSyn-"+ib+".arff");
            fileT.delete();
            fileT = new File("./LearningLabelPowersetSyn-"+ib+".arff");
            fileT.delete(); 
            fileT = new File("./LearningRandomSyn-"+ib+".arff");
            fileT.delete();             
            
            fileT = new File("./ValidationIterativeSyn-"+ ib + ".arff");
            fileT.delete();
            fileT = new File("./ValidationLabelPowersetSyn-"+ ib + ".arff");
            fileT.delete();
            fileT = new File("./ValidationRandomSyn-"+ ib + ".arff");
            fileT.delete();                 
            try {
                FileUtils.deleteDirectory("./results-" + experimentName);
            } catch (IOException ex) {
                System.out.println(ex);
            }        
            
        }

    }    
    
    public static void removeUnnecessaryFiles(long[] intemBudgets, String experimentName){
        //Remove learning and validation files.
        System.out.println("Remove learning and validation files.");
        for(long ib : intemBudgets){
            File fileT = new File("./Training-" + ib+".arff");
            fileT.delete(); 

            fileT = new File("./LearningIterative-"+ib+".arff");
            fileT.delete();
            fileT = new File("./LearningLabelPowerset-"+ib+".arff");
            fileT.delete(); 
            fileT = new File("./LearningRandom-"+ib+".arff");
            fileT.delete();             
            
            fileT = new File("./ValidationIterative-"+ ib + ".arff");
            fileT.delete();
            fileT = new File("./ValidationLabelPowerset-"+ ib + ".arff");
            fileT.delete();
            fileT = new File("./ValidationRandom-"+ ib + ".arff");
            fileT.delete();
            try {
                FileUtils.deleteDirectory("./results-" + experimentName);
            } catch (IOException ex) {
                System.out.println(ex);
            }        
            
        }

    }
    
    public static String[] splitDataInDifferentWaysSyn(long seed,                                               
                                              int foldsToLearn,
                                              int foldsToValid,
                                              int nLabels, 
                                              long timeBudget,
                                              int method) throws Exception{
        
        String[] learningAndValidationSetDirs = new String[2];
        switch (method) {
            case 0:
                learningAndValidationSetDirs = splitDataInAnIterativelyStratifiedWayWithSyntheticInstanceGeneration(seed, foldsToLearn, foldsToValid, nLabels, timeBudget);
                break;
            case 1:
                learningAndValidationSetDirs = splitDataLabelPowerSetStratifiedWayWithSyntheticInstanceGeneration(seed, foldsToLearn, foldsToValid, nLabels, timeBudget);
                break;
            case 2:
                learningAndValidationSetDirs = splitDataAtRandomWithSyntheticInstanceGeneration(seed, foldsToLearn, foldsToValid, nLabels, timeBudget);
                break;                
            default:
                learningAndValidationSetDirs = splitDataInAnIterativelyStratifiedWayWithSyntheticInstanceGeneration(seed, foldsToLearn, foldsToValid, nLabels, timeBudget);
                break;
        }
       
        return learningAndValidationSetDirs;
    }    
    
    public static String[] splitDataInDifferentWays(long seed,                                               
                                              int foldsToLearn,
                                              int foldsToValid,
                                              int nLabels, 
                                              long timeBudget,
                                              int method) throws Exception{
        
        String[] learningAndValidationSetDirs = new String[2];
        switch (method) {
            case 0:
                learningAndValidationSetDirs = splitDataInAnIterativelyStratifiedWay(seed, foldsToLearn, foldsToValid, nLabels, timeBudget);
                break;
            case 1:
                learningAndValidationSetDirs = splitDataLabelPowerSetStratifiedWay(seed, foldsToLearn, foldsToValid, nLabels, timeBudget);
                break;
            case 2:
                learningAndValidationSetDirs = splitDataAtRandom(seed, foldsToLearn, foldsToValid, nLabels, timeBudget);
                break;   
            default:
                learningAndValidationSetDirs = splitDataInAnIterativelyStratifiedWay(seed, foldsToLearn, foldsToValid, nLabels, timeBudget);
                break;
        }
       
        return learningAndValidationSetDirs;
    }
    
    /**
     * It splits the training data (based on a label powerset stratified way) into two subsets: learning and validation.     
     * While the former is used to learn the model, the latter is used to valid the produce model 
     * by that MLC algorithm. It uses Mulan Java library to perform the stratified sampling.
     * @param seed - The seed to sample the data.
     * @param foldsToLearn - the number of folds to learn the model.
     * @param foldsToValid - the number of folds to valid the model.
     * @param nLabels - The number of labels of the input dataset.     
     * @param timeBudget The current time budget.
     * 
     * @return a string vector with the directories of the learning and validation sets. 
     */
    public static String[] splitDataLabelPowerSetStratifiedWayWithSyntheticInstanceGeneration(long seed,                                               
                                              int foldsToLearn,
                                              int foldsToValid,
                                              int nLabels, 
                                              long timeBudget) throws Exception{
//        System.out.println("seed: "+seed);
//        System.out.println("foldsToLearn: "+foldsToLearn);
//        System.out.println("foldsToValid: "+foldsToValid);
        try {
            String arffDir = "Training-"+timeBudget+".arff";        
            MultiLabelInstances dataset = new MultiLabelInstances(arffDir, nLabels);
            MLSOL mlsol = new MLSOL();
            MultiLabelInstances new_dataset = mlsol.build(dataset);
            
            LabelPowersetStratification lps = new LabelPowersetStratification((int) seed);
            MultiLabelInstances[] folds = lps.stratify(new_dataset, (foldsToLearn + foldsToValid));
            
            Instances learningInst = null;
            for(int f=0; f < foldsToLearn; f++){
                if(f==0){
                    learningInst = new Instances(folds[f].getDataSet());
                }else{
                    learningInst.addAll(folds[f].getDataSet());
                }
            }
            
            Instances validInst = null;
            for(int f=foldsToLearn; f < (foldsToLearn+foldsToValid); f++){
                if(f==foldsToLearn){
                    validInst = new Instances(folds[f].getDataSet());
                }else{
                    validInst.addAll(folds[f].getDataSet());
                }
            }  
            
            MultiLabelInstances validationData = new MultiLabelInstances(validInst, dataset.getLabelsMetaData());
            validationData.getDataSet().setRelationName(dataset.getDataSet().relationName());      
            
            MultiLabelInstances learningData = new MultiLabelInstances(learningInst, dataset.getLabelsMetaData());
            learningData.getDataSet().setRelationName(dataset.getDataSet().relationName());
            
            try (FileWriter fLearning = new FileWriter("LearningLabelPowersetSyn-"+timeBudget+".arff", false)) {
                fLearning.write(learningData.getDataSet().toString());
                fLearning.close();
            }
            
            try (FileWriter fValidation = new FileWriter("ValidationLabelPowersetSyn-"+timeBudget+".arff", false)) {
                fValidation.write(validationData.getDataSet().toString());
                fValidation.close();
            }       
        
        } catch (InvalidDataFormatException ex) {
            System.err.println("Invalid data format exception: "+ex);
        } catch (IOException ex) {
            System.err.println("General exception: "+ex);
        }        
        
        String[] learningANDvalidationData = new String[2];
        learningANDvalidationData[0] = "LearningLabelPowersetSyn-"+timeBudget+".arff";
        learningANDvalidationData[1] = "ValidationLabelPowersetSyn-"+timeBudget+".arff";

        return learningANDvalidationData;
    }    
    
    /**
     * It splits the training data (based on a label powerset stratified way) into two subsets: learning and validation.     
     * While the former is used to learn the model, the latter is used to valid the produce model 
     * by that MLC algorithm. It uses Mulan Java library to perform the stratified sampling.
     * @param seed - The seed to sample the data.
     * @param foldsToLearn - the number of folds to learn the model.
     * @param foldsToValid - the number of folds to valid the model.
     * @param nLabels - The number of labels of the input dataset.     
     * @param timeBudget The current time budget.
     * 
     * @return a string vector with the directories of the learning and validation sets. 
     */
    public static String[] splitDataLabelPowerSetStratifiedWay(long seed,                                               
                                              int foldsToLearn,
                                              int foldsToValid,
                                              int nLabels, 
                                              long timeBudget){
//        System.out.println("seed: "+seed);
//        System.out.println("foldsToLearn: "+foldsToLearn);
//        System.out.println("foldsToValid: "+foldsToValid);
        try {
            String arffDir = "Training-"+timeBudget+".arff";        
            MultiLabelInstances dataset = new MultiLabelInstances(arffDir, nLabels);
            
            LabelPowersetStratification lps = new LabelPowersetStratification((int) seed);
            MultiLabelInstances[] folds = lps.stratify(dataset, (foldsToLearn + foldsToValid));
            
            Instances learningInst = null;
            for(int f=0; f < foldsToLearn; f++){
                if(f==0){
                    learningInst = new Instances(folds[f].getDataSet());
                }else{
                    learningInst.addAll(folds[f].getDataSet());
                }
            }
            
            Instances validInst = null;
            for(int f=foldsToLearn; f < (foldsToLearn+foldsToValid); f++){
                if(f==foldsToLearn){
                    validInst = new Instances(folds[f].getDataSet());
                }else{
                    validInst.addAll(folds[f].getDataSet());
                }
            }  
            
            MultiLabelInstances validationData = new MultiLabelInstances(validInst, dataset.getLabelsMetaData());
            validationData.getDataSet().setRelationName(dataset.getDataSet().relationName());      
            
            MultiLabelInstances learningData = new MultiLabelInstances(learningInst, dataset.getLabelsMetaData());
            learningData.getDataSet().setRelationName(dataset.getDataSet().relationName());
            
            try (FileWriter fLearning = new FileWriter("LearningLabelPowerset-"+timeBudget+".arff", false)) {
                fLearning.write(learningData.getDataSet().toString());
                fLearning.close();
            }
            
            try (FileWriter fValidation = new FileWriter("ValidationLabelPowerset-"+timeBudget+".arff", false)) {
                fValidation.write(validationData.getDataSet().toString());
                fValidation.close();
            }       
        
        } catch (InvalidDataFormatException ex) {
            System.err.println("Invalid data format exception: "+ex);
        } catch (IOException ex) {
            System.err.println("General exception: "+ex);
        }        
        
        String[] learningANDvalidationData = new String[2];
        learningANDvalidationData[0] = "LearningLabelPowerset-"+timeBudget+".arff";
        learningANDvalidationData[1] = "ValidationLabelPowerset-"+timeBudget+".arff";

        return learningANDvalidationData;
    }
    
    /**
     * It splits the training data (in an iteratively stratified way) into two subsets: learning and validation.     
     * While the former is used to learn the model, the latter is used to valid the produce model 
     * by that MLC algorithm. It uses Mulan Java library to perform the stratified sampling.
     * @param seed - The seed to sample the data.
     * @param foldsToLearn - the number of folds to learn the model.
     * @param foldsToValid - the number of folds to valid the model.
     * @param nLabels - The number of labels of the input dataset.     
     * @param timeBudget The current time budget.
     * 
     * @return a string vector with the directories of the learning and validation sets. 
     */
    public static String[] splitDataInAnIterativelyStratifiedWayWithSyntheticInstanceGeneration(long seed,                                               
                                              int foldsToLearn,
                                              int foldsToValid,
                                              int nLabels, 
                                              long timeBudget) throws Exception{
//        System.out.println("seed: "+seed);
//        System.out.println("foldsToLearn: "+foldsToLearn);
//        System.out.println("foldsToValid: "+foldsToValid);
        try {
            String arffDir = "Training-"+timeBudget+".arff";        
            MultiLabelInstances dataset = new MultiLabelInstances(arffDir, nLabels);
            MLSOL mlsol = new MLSOL();
            MultiLabelInstances new_dataset = mlsol.build(dataset);
          
            
            IterativeStratification is = new IterativeStratification(seed);
            MultiLabelInstances[] folds = is.stratify(new_dataset, (foldsToLearn + foldsToValid));
            
            Instances learningInst = null;
            for(int f=0; f < foldsToLearn; f++){
                if(f==0){
                    learningInst = new Instances(folds[f].getDataSet());
                }else{
                    learningInst.addAll(folds[f].getDataSet());
                }
            }
            
            Instances validInst = null;
            for(int f=foldsToLearn; f < (foldsToLearn+foldsToValid); f++){
                if(f==foldsToLearn){
                    validInst = new Instances(folds[f].getDataSet());
                }else{
                    validInst.addAll(folds[f].getDataSet());
                }
            }  
            
            MultiLabelInstances validationData = new MultiLabelInstances(validInst, dataset.getLabelsMetaData());
            validationData.getDataSet().setRelationName(dataset.getDataSet().relationName());      
            
            MultiLabelInstances learningData = new MultiLabelInstances(learningInst, dataset.getLabelsMetaData());
            learningData.getDataSet().setRelationName(dataset.getDataSet().relationName());
            
            try (FileWriter fLearning = new FileWriter("LearningIterativeSyn-"+timeBudget+".arff", false)) {
                fLearning.write(learningData.getDataSet().toString());
                fLearning.close();
            }
            
            try (FileWriter fValidation = new FileWriter("ValidationIterativeSyn-"+timeBudget+".arff", false)) {
                fValidation.write(validationData.getDataSet().toString());
                fValidation.close();
            }       
        
        } catch (InvalidDataFormatException ex) {
            System.err.println("Invalid data format exception: "+ex);
        } catch (IOException ex) {
            System.err.println("General exception: "+ex);
        }        
        
        String[] learningANDvalidationData = new String[2];
        learningANDvalidationData[0] = "LearningIterativeSyn-"+timeBudget+".arff";
        learningANDvalidationData[1] = "ValidationIterativeSyn-"+timeBudget+".arff";

        return learningANDvalidationData;
    }       
    
    /**
     * It splits the training data (in an iteratively stratified way) into two subsets: learning and validation.     
     * While the former is used to learn the model, the latter is used to valid the produce model 
     * by that MLC algorithm. It uses Mulan Java library to perform the stratified sampling.
     * @param seed - The seed to sample the data.
     * @param foldsToLearn - the number of folds to learn the model.
     * @param foldsToValid - the number of folds to valid the model.
     * @param nLabels - The number of labels of the input dataset.     
     * @param timeBudget The current time budget.
     * 
     * @return a string vector with the directories of the learning and validation sets. 
     */
    public static String[] splitDataInAnIterativelyStratifiedWay(long seed,                                               
                                              int foldsToLearn,
                                              int foldsToValid,
                                              int nLabels, 
                                              long timeBudget){
//        System.out.println("seed: "+seed);
//        System.out.println("foldsToLearn: "+foldsToLearn);
//        System.out.println("foldsToValid: "+foldsToValid);
        try {
            String arffDir = "Training-"+timeBudget+".arff";        
            MultiLabelInstances dataset = new MultiLabelInstances(arffDir, nLabels);
            
            IterativeStratification is = new IterativeStratification(seed);
            MultiLabelInstances[] folds = is.stratify(dataset, (foldsToLearn + foldsToValid));
            
            Instances learningInst = null;
            for(int f=0; f < foldsToLearn; f++){
                if(f==0){
                    learningInst = new Instances(folds[f].getDataSet());
                }else{
                    learningInst.addAll(folds[f].getDataSet());
                }
            }
            
            Instances validInst = null;
            for(int f=foldsToLearn; f < (foldsToLearn+foldsToValid); f++){
                if(f==foldsToLearn){
                    validInst = new Instances(folds[f].getDataSet());
                }else{
                    validInst.addAll(folds[f].getDataSet());
                }
            }  
            
            MultiLabelInstances validationData = new MultiLabelInstances(validInst, dataset.getLabelsMetaData());
            validationData.getDataSet().setRelationName(dataset.getDataSet().relationName());      
            
            MultiLabelInstances learningData = new MultiLabelInstances(learningInst, dataset.getLabelsMetaData());
            learningData.getDataSet().setRelationName(dataset.getDataSet().relationName());
            
            try (FileWriter fLearning = new FileWriter("LearningIterative-"+timeBudget+".arff", false)) {
                fLearning.write(learningData.getDataSet().toString());
                fLearning.close();
            }
            
            try (FileWriter fValidation = new FileWriter("ValidationIterative-"+timeBudget+".arff", false)) {
                fValidation.write(validationData.getDataSet().toString());
                fValidation.close();
            }       
        
        } catch (InvalidDataFormatException ex) {
            System.err.println("Invalid data format exception: "+ex);
        } catch (IOException ex) {
            System.err.println("General exception: "+ex);
        }        
        
        String[] learningANDvalidationData = new String[2];
        learningANDvalidationData[0] = "LearningIterative-"+timeBudget+".arff";
        learningANDvalidationData[1] = "ValidationIterative-"+timeBudget+".arff";

        return learningANDvalidationData;
    }        

    /**
     * It splits the training data (in a stratified way) into two subsets: learning and validation.     
     * While the former is used to learn the model, the latter is used to valid the produce model 
     * by that MLC algorithm. It uses Mulan Java library to perform the stratified sampling.
     * @param seed - The seed to sample the data.
     * @param foldsToLearn - the number of folds to learn the model.
     * @param foldsToValid - the number of folds to valid the model.
     * @param nLabels - The number of labels of the input dataset.     
     * @param timeBudget The current time budget.
     * 
     * @return a string vector with the directories of the learning and validation sets. 
     */
    public static String[] splitDataInAStratifiedWay(long seed,                                               
                                              int foldsToLearn,
                                              int foldsToValid,
                                              int nLabels, 
                                              long timeBudget){
//        System.out.println("seed: "+seed);
//        System.out.println("foldsToLearn: "+foldsToLearn);
//        System.out.println("foldsToValid: "+foldsToValid);
        try {
            String arffDir = "Training-"+timeBudget+".arff";        
            MultiLabelInstances dataset = new MultiLabelInstances(arffDir, nLabels);
            
            IterativeStratification is = new IterativeStratification(seed);
            MultiLabelInstances[] folds = is.stratify(dataset, (foldsToLearn + foldsToValid));
            
            Instances learningInst = null;
            for(int f=0; f < foldsToLearn; f++){
                if(f==0){
                    learningInst = new Instances(folds[f].getDataSet());
                }else{
                    learningInst.addAll(folds[f].getDataSet());
                }
            }
            
            Instances validInst = null;
            for(int f=foldsToLearn; f < (foldsToLearn+foldsToValid); f++){
                if(f==foldsToLearn){
                    validInst = new Instances(folds[f].getDataSet());
                }else{
                    validInst.addAll(folds[f].getDataSet());
                }
            }  
            
            MultiLabelInstances validationData = new MultiLabelInstances(validInst, dataset.getLabelsMetaData());
            validationData.getDataSet().setRelationName(dataset.getDataSet().relationName());      
            
            MultiLabelInstances learningData = new MultiLabelInstances(learningInst, dataset.getLabelsMetaData());
            learningData.getDataSet().setRelationName(dataset.getDataSet().relationName());
            
            try (FileWriter fLearning = new FileWriter("Learning-"+timeBudget+".arff", false)) {
                fLearning.write(learningData.getDataSet().toString());
                fLearning.close();
            }
            
            try (FileWriter fValidation = new FileWriter("Validation-"+timeBudget+".arff", false)) {
                fValidation.write(validationData.getDataSet().toString());
                fValidation.close();
            }       
        
        } catch (InvalidDataFormatException ex) {
            System.err.println("Invalid data format exception: "+ex);
        } catch (IOException ex) {
            System.err.println("General exception: "+ex);
        }        
        
        String[] learningANDvalidationData = new String[2];
        learningANDvalidationData[0] = "Learning-"+timeBudget+".arff";
        learningANDvalidationData[1] = "Validation-"+timeBudget+".arff";

        return learningANDvalidationData;
    }
    
    /**
     * It randomly splits the training data into two subsets: learning and validation.
     * While the former is used to learn the model, the latter is used to valid the
     * produce model by that MLC algorithm.
     * @param seed
     * @param foldsToLearn
     * @param foldsToValid
     * @param nLabels
     * @param timeBudget
     * @return a string vector with the directories of the learning and validation sets. 
     * @throws java.lang.Exception 
     */
    public static String[] splitDataAtRandomWithSyntheticInstanceGeneration(long seed,                                               
                                              int foldsToLearn,
                                              int foldsToValid,
                                              int nLabels, 
                                              long timeBudget) throws Exception{
        String arffDir = "Training-"+timeBudget+".arff";        
//        FileReader reader = new FileReader(arffDir);
        MultiLabelInstances dataset = new MultiLabelInstances(arffDir, nLabels);
        MLSOL mlsol = new MLSOL();
        MultiLabelInstances new_dataset = mlsol.build(dataset);
        BufferedWriter bw = new BufferedWriter(new FileWriter("TrainingSyn-"+timeBudget+".arff", false));
        bw.write(new_dataset.getDataSet().toString());  
        bw.close();

        String new_arffDir = "TrainingSyn-"+timeBudget+".arff";        
        FileReader new_reader = new FileReader(new_arffDir);        
        
        Instances fullTrainData = new Instances(new_reader);
        
    
        
        int foldsToLearnB = foldsToLearn;
        int foldsToValidB = foldsToValid;
        int folds = (foldsToLearnB + foldsToValidB);

        
        Random rand = new Random(seed);   // create seeded number generator
        Instances randData = new Instances(fullTrainData);   // create copy of original data
        randData.randomize(rand);
        
        Instances training = new Instances(randData, 0);
        Instances validation = new Instances(randData, 0);
        double numInstancesToLearn = foldsToLearnB/((double) folds) * fullTrainData.numInstances();
     
        for(int i=0; i < randData.numInstances(); i++){
            if(i <= numInstancesToLearn ){
                training.add(randData.get(i));
            }else{
                validation.add(randData.get(i));
            }            
        }

        training.setRelationName(fullTrainData.relationName());
        validation.setRelationName(fullTrainData.relationName());

        try (FileWriter fLearning = new FileWriter("LearningRandomSyn-"+timeBudget +".arff", false)) {
            fLearning.write(training.toString());
            fLearning.close();
        }
        
        try (FileWriter fValidation = new FileWriter("ValidationRandomSyn-"+timeBudget+".arff", false)) {
            fValidation.write(validation.toString());
            fValidation.close();
        }
        
        String[] learningANDvalidationData = new String[2];
        learningANDvalidationData[0] = "LearningRandomSyn-"+timeBudget +".arff";
        learningANDvalidationData[1] = "ValidationRandomSyn-"+timeBudget+".arff";

        return learningANDvalidationData;
    }    

    /**
     * It randomly splits the training data into two subsets: learning and validation.
     * While the former is used to learn the model, the latter is used to valid the
     * produce model by that MLC algorithm.
     * @param seed
     * @param foldsToLearn
     * @param foldsToValid
     * @param nLabels
     * @param timeBudget
     * @return a string vector with the directories of the learning and validation sets. 
     * @throws java.lang.Exception 
     */
    public static String[] splitDataAtRandom(long seed,                                               
                                              int foldsToLearn,
                                              int foldsToValid,
                                              int nLabels, 
                                              long timeBudget) throws Exception{
        String arffDir = "Training-"+timeBudget+".arff";        
        FileReader reader = new FileReader(arffDir);
        Instances fullTrainData = new Instances(reader);
        int foldsToLearnB = 4;
        int foldsToValidB = 1;
        int folds = (foldsToLearnB + foldsToValidB);

        
        Random rand = new Random(seed);   // create seeded number generator
        Instances randData = new Instances(fullTrainData);   // create copy of original data
        randData.randomize(rand);
        
        Instances training = new Instances(randData, 0);
        Instances validation = new Instances(randData, 0);
        double numInstancesToLearn = foldsToLearnB/((double) folds) * fullTrainData.numInstances();
     
        for(int i=0; i < randData.numInstances(); i++){
            if(i <= numInstancesToLearn ){
                training.add(randData.get(i));
            }else{
                validation.add(randData.get(i));
            }            
        }

        training.setRelationName(fullTrainData.relationName());
        validation.setRelationName(fullTrainData.relationName());

        try (FileWriter fLearning = new FileWriter("LearningRandom-"+timeBudget +".arff", false)) {
            fLearning.write(training.toString());
            fLearning.close();
        }
        
        try (FileWriter fValidation = new FileWriter("ValidationRandom-"+timeBudget+".arff", false)) {
            fValidation.write(validation.toString());
            fValidation.close();
        }
        
        String[] learningANDvalidationData = new String[2];
        learningANDvalidationData[0] = "LearningRandom-"+timeBudget +".arff";
        learningANDvalidationData[1] = "ValidationRandom-"+timeBudget+".arff";

        return learningANDvalidationData;
    }
        


    /**
     * It randomly splits the training data into two subsets: learning and validation.
     * While the former is used to learn the model, the latter is used to valid the
     * produce model by that MLC algorithm. 
     * @param seed - The seed to sample the data.
     * @param fold - The fold to be sampled.
     * @param fullTrainData - The instances with the training set.
     * @return a string vector with the directories of the learning and validation sets.
     * @throws Exception 
     */
    public static String[] splitData(long seed, int fold, Instances fullTrainData) throws Exception {

        Instances full = fullTrainData; // current training set.
        full.randomize(new Random(seed));

        RemoveFolds train = new RemoveFolds();
        RemoveFolds valid = new RemoveFolds();
        train.setInputFormat(full);
        train.setSeed(seed);
        train.setNumFolds(4);  // it uses 3/4 for training.
        train.setFold(1);
        train.setInvertSelection(true); // inverte = pega 3/4
        Instances training = Filter.useFilter(full, train);
        training.setRelationName(fullTrainData.relationName());

        valid.setInputFormat(full);
        valid.setSeed(seed);
        valid.setNumFolds(4); // it uses 1/4 for validating.
        valid.setFold(1);
        valid.setInvertSelection(false); // it gets 1/4 for validating.
        Instances validation = Filter.useFilter(full, valid);
        validation.setRelationName(fullTrainData.relationName());

        
        try (FileWriter fLearning = new FileWriter("LearningRandom-"+fold +".arff", false)) {
            fLearning.write(training.toString());
            fLearning.close();
        }
        
        try (FileWriter fValidation = new FileWriter("ValidationRandom-"+fold+".arff", false)) {
            fValidation.write(validation.toString());
            fValidation.close();
        }
        
        String[] learningANDvalidationData = new String[2];
        learningANDvalidationData[0] = "LearningRandom-"+fold +".arff";
        learningANDvalidationData[1] = "ValidationRandom-"+fold+".arff";

        return learningANDvalidationData;
    }
    

    /**
     * It splits the training data (in a stratified way) into two subsets: learning and validation.     
     * While the former is used to learn the model, the latter is used to valid the produce model 
     * by that MLC algorithm. It uses Mulan Java library to perform the stratified sampling.
     * @param seed - The seed to sample the data.
     * @param fold - The fold to be sampled.
     * @param nLabels - The number of labels of the input dataset.
     * @param timeBudget The current time budget.
     * 
     * @return a string vector with the directories of the learning and validation sets. 
     */
    public static String[] splitDataInAStratifiedWay_old(long seed, 
                                              int fold, 
                                              int nLabels, 
                                              long timeBudget){
        try {
            String arffDir = "Training"+timeBudget+".arff";        
            MultiLabelInstances dataset = new MultiLabelInstances(arffDir, nLabels);
            
            IterativeStratification is = new IterativeStratification(seed);
            MultiLabelInstances[] folds = is.stratify(dataset, 5);
            
            //It creates the validation set.
            MultiLabelInstances validationData = new MultiLabelInstances(folds[fold].getDataSet(), dataset.getLabelsMetaData());
            validationData.getDataSet().setRelationName(dataset.getDataSet().relationName());
            
            Instances learningInst = null;
            boolean validFirstFold = true;
            
            for(int f=0; f< folds.length; f++){                
                if(f!=fold && validFirstFold){
                    learningInst = new Instances(folds[f].getDataSet());
                    validFirstFold = false;
                }else if (f!=fold){
                    learningInst.addAll(folds[f].getDataSet());
                }                
            }            
            
            MultiLabelInstances learningData = new MultiLabelInstances(learningInst, dataset.getLabelsMetaData());
            learningData.getDataSet().setRelationName(dataset.getDataSet().relationName());
            
            try (FileWriter fLearning = new FileWriter("LearningIterative-"+timeBudget+".arff", false)) {
                fLearning.write(learningData.getDataSet().toString());
                fLearning.close();
            }
            
            try (FileWriter fValidation = new FileWriter("ValidationIterative-"+timeBudget+".arff", false)) {
                fValidation.write(validationData.getDataSet().toString());
                fValidation.close();
            }       
        
        } catch (InvalidDataFormatException ex) {
            System.err.println("Invalid data format exception: "+ex);
        } catch (IOException ex) {
            System.err.println("General exception: "+ex);
        }        
        
        String[] learningANDvalidationData = new String[2];
        learningANDvalidationData[0] = "Learning-"+timeBudget+".arff";
        learningANDvalidationData[1] = "Validation-"+timeBudget+".arff";

        return learningANDvalidationData;
    }
    
    
    /**
     * It generates a file with a specific log.
     *
     *
     * @param strB the buffer to save.
     * @param name the name of the file.
     * @param timeBudget the given time budget.
     * @throws java.io.IOException when there is any IO exception.
     */
    public static void savingLog(StringBuilder strB,
            String name,
            long timeBudget,
            String savingDirectory,
            String experimentName,
            long seed,
            int foldInit) throws IOException {
        if (strB != null) {
            String fileDir = savingDirectory + File.separator + "results-" + experimentName + File.separator + name + "-" + experimentName + "-tb" + (timeBudget / 60000) + "s" + seed + "f" + foldInit + ".csv";
            try {
                BufferedWriter bf = new BufferedWriter(new FileWriter(fileDir, true));
                bf.write(strB.toString());
                bf.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }

    }

   
    
}
