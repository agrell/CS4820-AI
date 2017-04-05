/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package textchangetest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import java.util.Scanner;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

/**
 *
 * @author Eric Loeper
 */
public class TextChangeTest {

    private static Scanner scanner = new Scanner( System.in );
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        // TODO code application logic here
        selectFiles();
    }
    
    public static void selectFiles()
    {
        String input;
        
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT","txt");
        chooser.setFileFilter(filter);
        chooser.setMultiSelectionEnabled(true);
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            File[] Files=chooser.getSelectedFiles();
            System.out.println("Please wait...");
            System.out.println("Enter information to be redacted:");
            input = scanner.nextLine();
            for( int i=0;i<Files.length;i++)
            {
                redactThis(Files[i].toString(),"redactedtext"+i+".txt", input);
            }
        }
    }
    
    public static void redactThis(String src, String desc, String red)
    {
        List<String> records = new ArrayList<String>();
        try
        {
            FileWriter fw=new FileWriter(desc);
            BufferedWriter bw=new BufferedWriter(fw);
            
            BufferedReader br = new BufferedReader(new FileReader(src));
            String line;
            
            while ((line = br.readLine()) != null)
            {
                records.add(line);
            }
            br.close();
            
            for (String l : records)
            {
                l = l.replaceAll(red,"[REDACTED]");
                bw.write(l);
                bw.newLine();
            }
            
            bw.flush();
            bw.close();
        }
        catch(Exception e){e.printStackTrace();}
    }
}
