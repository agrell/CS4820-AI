/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdftotextconverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import java.awt.Desktop;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;

/**
 *
 * @author Eric Loeper
 */
public class PDFToTextConverter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        selectPDFFiles();
    }
    
     public static void selectPDFFiles()
     {
         JFileChooser chooser = new JFileChooser();
         FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF","pdf");
         chooser.setFileFilter(filter);
         chooser.setMultiSelectionEnabled(true);
         int returnVal = chooser.showOpenDialog(null);
         if(returnVal == JFileChooser.APPROVE_OPTION)
         {
             File[] Files=chooser.getSelectedFiles();
             System.out.println("Please wait...");
             for( int i=0;i<Files.length;i++)
             {
                 convertPDFToText(Files[i].toString(),"textfrompdf"+i+".txt");
             }
             System.out.println("Conversion complete");
         }
     }
     
     public static void convertPDFToText(String src,String desc)
     {
         try
         {
             FileWriter fw=new FileWriter(desc);
             BufferedWriter bw=new BufferedWriter(fw);
             PdfReader pr=new PdfReader(src);
             int pNum=pr.getNumberOfPages();
             for(int page=1;page<=pNum;page++)
             {
                 String text=PdfTextExtractor.getTextFromPage(pr, page);
                 bw.write(text);
                 bw.newLine();
             }
             bw.flush();
             bw.close();
         }catch(Exception e){e.printStackTrace();}
         
     }
}
