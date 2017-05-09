/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redactiontry;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Map;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.tools.data.FileHandler;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Instance;
import java.util.List;
import java.util.ArrayList;  
import java.lang.StringBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.DefaultDataset;

/**
 *
 * @author Eric Loeper
 */
public class RedactionTry {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception
    {
        // TODO code application logic here
        Dataset data;
        
        System.out.println("Data file:");
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter dataFilter = new FileNameExtensionFilter("DATA","data");
        chooser.setFileFilter(dataFilter);
        chooser.setMultiSelectionEnabled(true);
        int returnVal = chooser.showOpenDialog(null);
        data = FileHandler.loadDataset(chooser.getSelectedFiles()[0],0," ");
        
        List<String> classes = new ArrayList<String>();
        List<String> classChanges = new ArrayList<String>();
        List<List<double[]>> doubleChanges = new ArrayList<List<double[]>>();
        List<Double> sizes = new ArrayList<Double>();
        List<Double> midpoints = new ArrayList<Double>();
        for(Instance i: data)
        {
            if(!classes.contains(i.classValue().toString()))
            {
                classes.add(i.classValue().toString());
            }
        }
        for(String s: classes)
        {
            List<Instance> temp = new ArrayList<Instance>();
            for(Instance i: data)
            {
                if(s.equals(i.classValue()))
                    temp.add(i);
            }
            Instance[] temp2 = new Instance[temp.size()];//(Instance[])temp.toArray();
            for (int j = 0; j < temp.size(); j ++)
            {
                temp2[j] = temp.get(j);
            }
            List<Double> Unredact = new ArrayList<Double>();
            List<Double> Redact = new ArrayList<Double>();
            List<Double> diffUnredact = new ArrayList<Double>();
            List<Double> diffRedact = new ArrayList<Double>();
            int j = 0;
            for(Double d:temp2[0])
                Unredact.add(d);
            for(Double d:temp2[1])
                Redact.add(d);
            Double[] unr = new Double[Unredact.size()];
            for(int k=0; k<Unredact.size(); k++)
                unr[k] = Unredact.get(k);
            Double[] red = new Double[Redact.size()];
            for(int k=0; k<Redact.size(); k++)
                red[k] = Redact.get(k);
            Double blockMid = 0.0;
            
            boolean unbrok = true;
            boolean reached = false;
            for(int i=0; i<unr.length; i++)
            {
                if(unr[i].intValue()!=red[i].intValue())
                {
                    diffUnredact.add(unr[i]);
                    diffRedact.add(red[i]);
                    if(unbrok)
                        j++;
                    reached = true;
                }
                else
                {
                    if(reached)
                        unbrok = false;
                    if(j > 2)
                        blockMid = unr[j/2]; 
                }
            }
            
            double[] diffUn = new double[diffUnredact.size()];
            double[] diffRd = new double[diffRedact.size()];
            for(int l=0; l<diffUnredact.size();l++)
            {
                diffUn[l] = diffUnredact.get(l);
            }
            for(int l=0; l<diffRedact.size();l++)
            {
                diffRd[l] = diffRedact.get(l);
            }
            List<double[]> temp3 = new ArrayList<double[]>();
            temp3.add(diffUn);
            temp3.add(diffRd);
            classChanges.add(s);
            doubleChanges.add(temp3);
            sizes.add((double)j);
            midpoints.add(blockMid);
        }
        
        
        System.out.println("Word file:");
        FileNameExtensionFilter wordFilter = new FileNameExtensionFilter("WORDS","words");
        chooser.setFileFilter(wordFilter);
        chooser.setMultiSelectionEnabled(true);
        returnVal = chooser.showOpenDialog(null);
        File namefile = chooser.getSelectedFile();
        String src = namefile.toString();
        List<String> wordList = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(src));
        String line;
        while ((line = br.readLine()) != null)
        {
            wordList.add(line);
        }
        br.close();
        
        System.out.println("Redact this:");
        FileNameExtensionFilter workFilter = new FileNameExtensionFilter("TXT","txt");
        chooser.setFileFilter(workFilter);
        chooser.setMultiSelectionEnabled(true);
        returnVal = chooser.showOpenDialog(null);
        File workfile = chooser.getSelectedFile();
        String srcwork = workfile.toString();
        List<String> workStrings = new ArrayList<String>();
        BufferedReader br2 = new BufferedReader(new FileReader(srcwork));
        while ((line = br2.readLine()) != null)
        {
            workStrings.add(line);
        }
        br2.close();
        Dataset workData = new DefaultDataset();
        
        for(String s: workStrings)
        {
            List<Double> temp2 = new ArrayList<Double>();
            for(String wordst: s.split(" "))
            {
                if(!wordList.contains(s))
                    wordList.add(s);
                double temp = (double)wordList.indexOf(s);
                temp2.add(temp);
            }
            double[] temp3= new double[temp2.size()];
            for(int k = 0; k < temp2.size(); k++)
                temp3[k] = temp2.get(k);
            
            Instance temp4 = new DenseInstance(temp3);
            workData.add(temp4);
        }
        
        
        Classifier knn = new KNearestNeighbors(1);
        knn.buildClassifier(data);
        
        Dataset workClassified = new DefaultDataset();
        for(Instance i: workData)
        {
            double[] temp = new double[i.values().size()];
            List<Double> temp2 = new ArrayList<Double>();
            for (double d: i.values())
                temp2.add(d);
            
            for(int k = 0; k < temp2.size(); k++)
                temp[k] = temp2.get(k);
                
            Instance temp3 = new DenseInstance(temp,knn.classify(i));
            workClassified.add(temp3);
        }
        
        Dataset finished = new DefaultDataset();
        for(Instance i: workClassified)
        {
            String ikey = i.classValue().toString();//.toString();
            Double[] ivals = new Double[i.values().size()];//(Double[])i.values().toArray();
            for(int k=0; k<i.values().size(); k++)
            {
                ivals[k] = i.value(k);
            }
            List<Double> newvals = new ArrayList<Double>();
            int index = 0;
            
            for(int k = 0; k<classChanges.size(); k++)
            {
                if (ikey.equals(classChanges.get(k)))
                {
                    index = k;
                    break;
                }
            }
            System.out.println(index);
            
            double[] unr = doubleChanges.get(index).get(0);
            double[] red = doubleChanges.get(index).get(1);
            int blocksize = sizes.get(index).intValue();
            if(blocksize>2)
            {
                Double blockmid = midpoints.get(index);
                List<Double> indeces1 = new ArrayList<Double>();
                for(int j =0; j<ivals.length; j++)
                {
                    if(ivals[j]==blockmid)
                        indeces1.add((double)j);
                }
                int[] indeces2 = new int[indeces1.size()];
                for(int j=0; j<indeces1.size(); j++)
                    indeces2[j] = indeces1.get(j).intValue();
                
                for (int j: indeces2)
                {
                    int beg, end;
                    if (j - blocksize/2 <=0)
                        beg = 0;
                    else
                        beg = j-blocksize/2;
                    if (j + blocksize/2 >= ivals.length-1)
                        end = ivals.length-1;
                    else
                        end = j+blocksize/2;
                    
                    for (int k = 0; k<ivals.length; k++)
                    {
                        if(k>=beg && k<=end)
                        {
                            String temp = wordList.get(ivals[k].intValue());
                            char[] temp2 = temp.toCharArray();
                            StringBuilder temp3 = new StringBuilder();
                            for(char c: temp2)
                            {
                                temp3.append("~");
                            }
                            if(wordList.contains(temp3.toString()))
                                wordList.add(temp3.toString());
                            
                            Double temp4 = (double)wordList.indexOf(temp3.toString());
                            newvals.add(temp4);
                        }
                        else
                            newvals.add(ivals[k]);
                    }
                }
            }
            else
            {
                boolean stopper = true;
                for(Double d: ivals)
                {
                    for(int j=0; j<unr.length; j++)
                    {
                        if (d == unr[j] && stopper)
                        {
                            newvals.add(red[j]);
                            stopper = false;
                        }
                        else if(stopper)
                        {
                            newvals.add(d);
                            stopper = false;
                        }
                    }
                }
            }
            System.out.println("");
            double[] redactedvals = new double[newvals.size()];//(Double[])newvals.toArray();
            for (int k=0; k<newvals.size(); k++)
                redactedvals[k] = newvals.get(k);
            
            Instance tempin = new DenseInstance(redactedvals);
            finished.add(tempin);
        }
        String[] end = backToText(finished, wordList);
        
        FileWriter fw=new FileWriter("redacted.txt");
        BufferedWriter bw=new BufferedWriter(fw);
        
        for(String s: end)
        {
            System.out.println(s);
            bw.write(s);
            bw.newLine();
        }
        bw.flush();
        bw.close();
    } 

    private static String[] backToText(Dataset data, List<String> wordList) 
    {
        List<String> stringList = new ArrayList<String>();
        for (Instance i: data)
        {
            StringBuilder temp = new StringBuilder();
            for (double d: i.values())
            {
                int temp2 = (int)d;
                String temp3 = wordList.get(temp2);
                temp.append(temp3 + " ");
            }
            stringList.add(temp.toString());
        }
        String[] returnValue = new String[stringList.size()];
        for(int j=0; j<stringList.size(); j++)
            returnValue[j] = stringList.get(j);
        return returnValue;
    }
}