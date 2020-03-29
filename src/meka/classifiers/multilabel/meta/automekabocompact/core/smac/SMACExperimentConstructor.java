package meka.classifiers.multilabel.meta.automekabocompact.core.smac;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.Collections;
import meka.classifiers.multilabel.meta.automekabocompact.core.Conditional;
import meka.classifiers.multilabel.meta.automekabocompact.core.ExperimentConstructor;
import meka.classifiers.multilabel.meta.automekabocompact.core.Parameter;
import meka.classifiers.multilabel.meta.automekabocompact.core.ClassParams;
import meka.classifiers.multilabel.meta.automekabocompact.core.ParameterConditionalGroup;
import meka.classifiers.multilabel.meta.automekabocompact.core.Util;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import meka.classifiers.multilabel.meta.automekabocompact.core.Experiment;
import meka.classifiers.multilabel.meta.automekabocompact.core.HandleFiles;
import mulan.data.InvalidDataFormatException;
import mulan.data.IterativeStratification;
import mulan.data.MultiLabelInstances;
import org.codehaus.plexus.util.FileUtils;
import weka.core.Instances;

public class SMACExperimentConstructor extends ExperimentConstructor
{

    public void prepareExperiment(Experiment exp, String path, int fold, int n_labels)
    {
        path = URLDecoder.decode(path);
        try
        {
//            BufferedReader bf = new  BufferedWriter (new java.io.FileWriter(path + "automeka.params"));
            //Print out the param file
            printParamFile(new PrintStream(new java.io.File(path + "automeka.params")));
            //Write out the instance file
            printInstanceFile(new PrintStream(new java.io.File(path + "automeka.instances")));
            printTestInstanceFile(new PrintStream(new java.io.File(path + "automeka.test.instances")));
            printFeatureFile(new PrintStream(new java.io.File(path + "automeka.features")));
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(path + "trajetory.txt", true));
            bw.write(System.currentTimeMillis() +";"+ ";" + ";" + "\n" );
            bw.close();            

            //Write out the scenario file
            printScenarioFile(new PrintStream(new java.io.File(path + "automeka.scenario")));
            HandleFiles hf = new HandleFiles();
            hf.splitDataInAStratifiedWay(exp.name, path , exp.seed, fold, n_labels);


            meka.classifiers.multilabel.meta.automekabocompact.core.Util.makePath(path + "out");
            
            hf.copyFile(new File("weka.jar"), new File(path + "weka.jar"));
            hf.copyFile(new File("meka.jar"), new File(path + "meka.jar"));
            FileUtils.copyDirectory(new File("lib"), new File(path + "lib"));
        }
        catch(Exception e)
        {
            throw new RuntimeException("Failed to prepare the experiment", e);
        }
    }
    


    public String getTrajectoryParserClassName()
    {
        return "meka.classifiers.multilabel.meta.automekabocompact.core.smac.SMACTrajectoryParser";
    }

    @Override
    public List<String> getCallString(String experimentPath)
    {
        // assumes that autoweka.jar is at the root of the autoweka distribution
        // (as it will be for the WEKA package)
        String prefix = new File(URLDecoder.decode(SMACExperimentConstructor.class.getProtectionDomain().getCodeSource().getLocation().getPath())).getParentFile().toString();
        //Make sure that the properties we have tell us where the executable for smac lives
        if(mProperties.getProperty("smacexecutable") == null)
            throw new RuntimeException("The 'smacexecutable' property was not defined");

        Properties props = meka.classifiers.multilabel.meta.automekabocompact.core.Util.parsePropertyString(mExperiment.extraPropsString);

        String execExtension = "";
        if(System.getProperty("os.name").toLowerCase().indexOf("win") >= 0){
            execExtension = ".bat";
        }

        String smac = prefix + File.separator + mProperties.getProperty("smacexecutable") + execExtension;

        File f = new File(Util.expandPath(smac));
        if(!f.exists())
            throw new RuntimeException("Could not find SMAC executable '" + f.getAbsoluteFile() + "'");

        // now make it executable, it's not when extracted by the WEKA package
        // manager...
        f.setExecutable(true);

        List<String> args = new ArrayList<String>();
        args.add(smac);

        // seeds
        args.add("--seed");
        args.add("{SEED}");
        args.add("--validation-seed");
        args.add("{SEED}");
        args.add("--random-sample-seed");
        args.add("{SEED}");

        args.add("--scenarioFile");
        args.add("automeka.scenario");
        args.add("--logModel");
        args.add("false");
//        args.add("--validate-only-last-incumbent");
//        args.add("false");
        args.add("--logAllProcessOutput");
        args.add("TRUE");
        args.add("--adaptiveCapping");
        args.add("false");
        args.add("--runGroupName");
        args.add("automeka");
        args.add("--terminate-on-delete");
        args.add(experimentPath + File.separator + "out" + File.separator + "runstamps" + File.separator + "{SEED}.stamp");
        args.add("--kill-runs-on-file-delete");
        args.add(experimentPath + File.separator + "out" + File.separator + "runstamps" + File.separator + "{SEED}.stamp");

        args.add("--algo-cutoff-time");
        args.add("" + mExperiment.trainTimeout);

        args.add("--transform-crashed-quality-value");
        args.add("" + meka.classifiers.multilabel.meta.automekabocompact.core.ClassifierResult.getInfinity());

        args.add("--kill-run-exceeding-captime-factor");
        args.add("2.0");

        if(props.containsKey("deterministicInstanceOrdering"))
        {
            //throw new RuntimeException("This option only works on a hacked up version of SMAC");
            args.add("--deterministicInstanceOrdering");
            args.add(props.getProperty("deterministicInstanceOrdering"));
        }

        if(props.containsKey("initialIncumbent"))
        {
            args.add("--initialIncumbent");
            args.add(props.getProperty("initialIncumbent"));
        }

        if(props.containsKey("initialIncumbentRuns"))
        {
            args.add("--initialIncumbentRuns");
            args.add(props.getProperty("initialIncumbentRuns"));
        }

        if(props.containsKey("initialN"))
        {
            args.add("--initialN");
            args.add(props.getProperty("initialN"));
        }

        if(props.containsKey("initialChallenge"))
        {
            args.add("--initialChallenge");
            args.add(props.getProperty("initialChallenge"));
        }

        if(props.containsKey("stateSerializer"))
        {
            args.add("--stateSerializer");
            args.add(props.getProperty("stateSerializer"));
        }

        if(props.containsKey("acq-func"))
        {
            args.add("--acq-func");
            args.add(props.getProperty("acq-func"));
        }
        else
        {
            args.add("--acq-func");
            args.add("EI");
        }

        if(props.containsKey("executionMode"))
        {
            args.add("--executionMode");
            args.add(props.getProperty("executionMode"));
        }

        return args;
    }

    public String getType()
    {
        return "SMAC";
    }

    public void printInstanceFile(PrintStream out)
    {
        Properties props = meka.classifiers.multilabel.meta.automekabocompact.core.Util.parsePropertyString(mExperiment.extraPropsString);
        String instancesOverride = props.getProperty("instancesOverride", null);
        if(instancesOverride != null)
        {
            out.println(instancesOverride);
        }
        else
        {
            List<String> instanceStrings = mInstanceGenerator.getAllInstanceStrings(mExperiment.instanceGeneratorArgs);
            for(String s:instanceStrings)
            {
                out.println(s);
            }
        }
    }

    public void printFeatureFile(PrintStream out)
    {
        Set<String> featureNamesSet = new HashSet<String>();

        Map<String, Map<String, String>> features = mInstanceGenerator.getAllInstanceFeatures(mExperiment.instanceGeneratorArgs);
        for(String inst:features.keySet())
        {
            featureNamesSet.addAll(features.get(inst).keySet());
        }
        String[] featureNames = featureNamesSet.toArray(new String[]{});

        //Write out the main row
        out.print("instance");
        for(String feat: featureNames)
            out.print("," +feat);
        out.println();

        //Write out each feature
        for(String inst:features.keySet())
        {
            out.print(inst);
            for(String feat: featureNames)
                out.print("," + features.get(inst).get(feat));
            out.println();
        }
    }

    public void printTestInstanceFile(PrintStream out)
    {
        out.println("default");
    }

    public void printScenarioFile(PrintStream out)
    {
        String extraProps = "";
        if(mExperiment.extraPropsString != null && mExperiment.extraPropsString.length() > 0)
            extraProps = " -prop " + mExperiment.extraPropsString;

        Properties props = meka.classifiers.multilabel.meta.automekabocompact.core.Util.parsePropertyString(mExperiment.extraPropsString);
        String wrapper = props.getProperty("wrapper", "meka.classifiers.multilabel.meta.automekabocompact.core.smac.SMACWrapper");

        out.println("algo = \"" + meka.classifiers.multilabel.meta.automekabocompact.core.Util.getJavaExecutable() + "\" -Dautomeka.infinity=" + meka.classifiers.multilabel.meta.automekabocompact.core.ClassifierResult.getInfinity() + " -Xmx" + mExperiment.memory + " -cp \"" + meka.classifiers.multilabel.meta.automekabocompact.core.Util.getAbsoluteClasspath() + "\" " + wrapper + " -prop " + getWrapperPropString() + extraProps + " -wrapper");
        out.println("execdir = ./");
        out.println("deterministic = 1");
        out.println("run_obj = quality");
        out.println("overall_obj = mean");
        out.println("cutoff_time = " + (int)mExperiment.trainTimeout);
        out.println("target_run_cputime_limit = " + (int)mExperiment.trainTimeout);
        out.println("wallclock_limit = " + (int)mExperiment.tunerTimeout);
        out.println("outdir = out");
        out.println("paramfile = automeka.params");
        out.println("instance_file = automeka.instances");
        out.println("test_instance_file = automeka.test.instances");
        if(!mInstanceGenerator.getAllInstanceFeatures(mExperiment.instanceGeneratorArgs).isEmpty() && mProperties.get("instancesOverride") != null)
            out.println("feature_file = automeka.features");
    }
    


    public void printParamFile(PrintStream out) {

            ParameterConditionalGroup paramGroup = generateAlgorithmParameterConditionalGroupForDAG();
            List<String> parameters = new ArrayList<String>();
            List<String> conditionals = new ArrayList<String>();

            for (Parameter param : paramGroup.getParameters()) {
                parameters.add(param.toString());
                for (Conditional cond : paramGroup.getConditionalsForParameter(param)) {
                    conditionals.add(cond.toString());
                }
            }
            //Sort them for sanity
//            Collections.sort(parameters);
//            Collections.sort(conditionals);

            //Dump 'em
            for (String param : parameters) {
                out.println(param);
            }
      
            out.println("Conditionals:");
          for (String cond : conditionals) {
                out.println(cond);
            }
        
    }
}
