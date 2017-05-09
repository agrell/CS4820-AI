/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datasetmaker;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import java.util.Scanner;
import java.util.ArrayList;  
import java.lang.StringBuilder;

/**
 *
 * @author Eric Loeper
 */
public class Datasetmaker {

    /**
     * @param args the command line arguments
     */
    private static Scanner scanner = new Scanner( System.in );
    
    public static void main(String[] args) 
    {
        JFrame frame = new JFrame();
        ArrayList<File> filesToAdd = new ArrayList<File>();
        ArrayList<File> filesRedacted = new ArrayList<File>();
        ArrayList<String> classifications = new ArrayList<String>();
        
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT","txt");
        chooser.setFileFilter(filter);
        chooser.setMultiSelectionEnabled(false);
        boolean choosing = true;
        while(choosing)
        {
            System.out.println("Nonredacted file:");
            int returnVal = chooser.showOpenDialog(null);
            if(returnVal == JFileChooser.APPROVE_OPTION)
            {
                File Files=chooser.getSelectedFile();
                filesToAdd.add(Files);
                
            }
            System.out.println("Redacted file:");
            returnVal = chooser.showOpenDialog(null);
            if(returnVal == JFileChooser.APPROVE_OPTION)
            {
                File Filesb=chooser.getSelectedFile();
                filesRedacted.add(Filesb);
            }
            
            boolean classif = true;
            while(classif)
            {
                System.out.println("Enter classification, unique to this redacted/unredacted duo:");
                String input = scanner.nextLine();
                if (classifications.contains(input))
                    System.out.println(input + " is already in use, classification needs to be unique.");
                else
                {
                    classifications.add(input);
                    classif = false;
                }
            }
            
            int contVal = JOptionPane.showConfirmDialog(frame,"Add another file to the set?","Set Addition",
                    JOptionPane.YES_NO_OPTION);
            if(contVal != JOptionPane.YES_OPTION)
                choosing = false;
        }
        
        setMaker(filesToAdd, filesRedacted, classifications);
    }
    
    public static void setMaker(ArrayList<File> fl, ArrayList<File> fl2, ArrayList<String> cl)
    {
        int i = 0;
        File[] files = fl.toArray(new File[fl.size()]);
        File[] redacFiles = fl2.toArray(new File[fl2.size()]);
        String[] classes = cl.toArray(new String[cl.size()]);
        
        List<String> words = new ArrayList<String>();
        words.add("linebreak");
        
        try
        {
            FileWriter fw=new FileWriter("dataset.data");
            BufferedWriter bw=new BufferedWriter(fw);
            FileWriter fw2=new FileWriter("dataset.words");
            BufferedWriter bw2 = new BufferedWriter(fw2);
            
            for(File f: files)
            {
                List<String> records = new ArrayList<String>();
                List<String> records2 = new ArrayList<String>();
                StringBuilder builder = new StringBuilder();
                String src = f.toString();
                
                BufferedReader br = new BufferedReader(new FileReader(src));
                String line;
                
                while ((line = br.readLine()) != null)
                {
                    records.add(line);
                }
                br.close();
                
                for (String s : records)
                {
                    builder.append(s);
                    builder.append(" ");
                    builder.append("linebreak");
                    builder.append(" ");
                }
                
                StringBuilder dataBuilder1 = new StringBuilder();
                dataBuilder1.append(classes[i]+" ");
                for(String Word: builder.toString().split(" "))
                {
                    if (!words.contains(Word))
                        words.add(Word);
                    
                    int index = -1;
                    for (int j = 0; j<words.toArray().length; j++)
                    {
                        if(words.toArray()[j].equals(Word))
                        {
                            index = j;
                            break;
                        }
                    }
                    dataBuilder1.append(index + " ");
                }
                dataBuilder1.deleteCharAt(dataBuilder1.length()-1);
                
                
                String src2 = redacFiles[i].toString();
                BufferedReader br2 = new BufferedReader(new FileReader(src2));
                String line2;
                StringBuilder builder2 = new StringBuilder();
                
                while((line2 = br2.readLine()) != null)
                {
                    records2.add(line2);
                }
                br2.close();
                
                for (String s : records2)
                {
                    //s = s.replaceAll(" ", "spacereplace");
                    builder2.append(s);
                    builder2.append(" ");
                    builder2.append("linebreak");
                    builder2.append(" ");
                }
                
                StringBuilder dataBuilder2 = new StringBuilder();
                dataBuilder2.append(classes[i]+" ");
                for(String Word: builder2.toString().split(" "))
                {
                    if (!words.contains(Word))
                        words.add(Word);
                    
                    int index = -1;
                    for (int j = 0; j<words.toArray().length; j++)
                    {
                        if(words.toArray()[j].equals(Word))
                        {
                            index = j;
                            break;
                        }
                    }
                    dataBuilder2.append(index + " ");
                }
                dataBuilder2.deleteCharAt(dataBuilder2.length()-1);
                
                
                bw.write(dataBuilder1.toString());
                bw.newLine();
                bw.write(dataBuilder2.toString());
                bw.newLine();
                
                i++;
            }
            for(String s : words)
            {
                bw2.write(s);
                bw2.newLine();
            }
            bw.flush();
            bw2.flush();
            bw.close();
            bw2.close();
        }
        catch(Exception e){e.printStackTrace();}
    }
}
