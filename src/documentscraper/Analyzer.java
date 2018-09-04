package documentscraper;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipFile;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author dellingw
 */
public class Analyzer{
    private File file;
    private HashMap<String,Integer> wordCount;
    private LinkedHashMap<String,Integer> sorted;
    private final List<String> commonWords;
    public Analyzer(File file){
        this.file=file;
        wordCount=new HashMap();
        commonWords=Arrays.asList(new String[]{"the","be","to","of","and","a","in","that","have","I","it","for","not","on","with","he","as","you","do","at","this","but","his","by","from","they","we","say","her","she","or","an","will","my","one","all","would","there","their","what","so"});
        
    }
    public LinkedHashMap<String,Integer> analyze(Boolean multi){
        String fileName=file.getName();
        if(fileName.endsWith(".doc")||fileName.endsWith(".ppt")||fileName.endsWith(".docx")){
            try {
                String contents=ExtractorFactory.createExtractor(file).getText().toLowerCase();
                contents=contents.replaceAll("-", " ");
                String[] contArr=contents.toLowerCase().split("[^a-zA-Z0-9]");
                for(int i=0;i<contArr.length;i++){
                    if(!commonWords.contains(contArr[i])&&contArr[i].length()>2&& contArr[i].split("\\d").length<3&&contArr[i].length()<27||multi){
                        String temp=contArr[i];
                        if(wordCount.containsKey(temp)){
                            wordCount.put(temp, wordCount.get(temp)+1);
                        }
                        else{
                            wordCount.put(temp, 1);
                        }
                        if(multi){
                            for(int j=1;j<=2;j++){
                                if(contArr.length-i>j){
                                    temp=contArr[i].concat(" "+contArr[i+j]);
                                    if(wordCount.containsKey(temp)){
                                        wordCount.put(temp, wordCount.get(temp)+1);
                                    }
                                    else{
                                        wordCount.put(temp, 1);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch(Exception e){
                e.printStackTrace();
            } 
        }
        else if(fileName.endsWith(".xlsx")||fileName.endsWith(".xlsm")){
            try{
                XSSFWorkbook doc = new XSSFWorkbook(file);
                Iterator<Sheet> sheets=doc.sheetIterator();
                while (sheets.hasNext()){
                    Sheet sheet= sheets.next();
                    Iterator<Row> rows=sheet.rowIterator();
                    while (rows.hasNext()){ 
                        Row row=rows.next(); 
                        Iterator<Cell> cells = row.cellIterator(); 
                        while (cells.hasNext()){
                            Cell cell=cells.next();
                            String cellValue = cell.toString();
                            String[] cellValues=cellValue.toLowerCase().split("[^a-zA-Z0-9]");
                            for(int i=0;i<cellValues.length;i++){
                                if(!commonWords.contains(cellValues[i])&&cellValues[i].length()>2&& cellValues[i].split("\\d").length<3&&cellValues[i].length()<27||multi){
                                    String temp=cellValues[i];
                                    if(wordCount.containsKey(temp)){
                                        wordCount.put(temp, wordCount.get(temp)+1);
                                    }
                                    else{
                                        wordCount.put(temp, 1);
                                    }
                                    if(multi){
                                        for(int j=1;j<=2;j++){
                                            if(cellValues.length-i>j){
                                                temp=cellValues[i].concat(" "+cellValues[i+j]);
                                                if(wordCount.containsKey(temp)){
                                                    wordCount.put(temp, wordCount.get(temp)+1);
                                                }
                                                else{
                                                    wordCount.put(temp, 1);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } 
                }
                doc.close();
            } catch(Exception e){
                e.printStackTrace();
            } 
        }
        else if(fileName.endsWith(".xls")){
            try {
                Workbook doc = WorkbookFactory.create(new FileInputStream(file));
                Iterator<Sheet> sheets=doc.sheetIterator();
                    while (sheets.hasNext()){
                        Sheet sheet= sheets.next();
                        Iterator<Row> rows=sheet.rowIterator();
                        while (rows.hasNext()){ 
                            Row row=rows.next(); 
                            Iterator<Cell> cells = row.cellIterator(); 
                            while (cells.hasNext()){
                                Cell cell=cells.next();
                                String cellValue = cell.toString();
                                String[] cellValues=cellValue.toLowerCase().split("[^a-zA-Z0-9]");
                                for(int i=0;i<cellValues.length;i++){
                                    if(!commonWords.contains(cellValues[i])&&cellValues[i].length()>2&& cellValues[i].split("\\d").length<3&&cellValues[i].length()<27||multi){
                                        String temp=cellValues[i];
                                        if(wordCount.containsKey(temp)){
                                            wordCount.put(temp, wordCount.get(temp)+1);
                                        }
                                        else{
                                            wordCount.put(temp, 1);
                                        }
                                        if(multi){
                                            for(int j=1;j<=2;j++){
                                                if(cellValues.length-i>j){
                                                    temp=cellValues[i].concat(" "+cellValues[i+j]);
                                                    if(wordCount.containsKey(temp)){
                                                        wordCount.put(temp, wordCount.get(temp)+1);
                                                    }
                                                    else{
                                                        wordCount.put(temp, 1);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } 
                    }
                doc.close();
            } catch(Exception e){
                e.printStackTrace();
            } 
        }
        else if(fileName.endsWith(".pdf")){
            try {
                PDDocument doc = PDDocument.load(file);
                PDFTextStripper pdfStrip=new PDFTextStripper();
                String[] stripper=pdfStrip.getText(doc).toLowerCase().split("[^a-zA-Z0-9]");
                for(int i=0;i<stripper.length;i++){
                    if(!commonWords.contains(stripper[i])&&stripper[i].length()>2 && stripper[i].split("\\d").length<3&&stripper[i].length()<24||multi){
                        String temp=stripper[i];
                        if(wordCount.containsKey(temp)){
                            wordCount.put(temp, wordCount.get(temp)+1);
                        }
                        else{
                            wordCount.put(temp, 1);
                        }
                        if(multi){
                            for(int j=1;j<=2;j++){
                                if(stripper.length-i>j){
                                    temp=stripper[i].concat(" "+stripper[i+j]);
                                    if(wordCount.containsKey(temp)){
                                        wordCount.put(temp, wordCount.get(temp)+1);
                                    }
                                    else{
                                        wordCount.put(temp, 1);
                                    }
                                }
                            }
                        }
                    }
                }
                doc.close();
            } catch(Exception e){

            }
        }
        else{
        }
        Set<String> wordSet=wordCount.keySet();
        Collection<Integer> values=wordCount.values();
        ArrayList<String> keyArr=new ArrayList(wordSet);
        ArrayList<Integer> valueArr=new ArrayList(values);
        boolean change=true;
        while(change){
            change=false;
            for(int i=1;i<wordSet.size();i++){
                if(valueArr.get(i)>valueArr.get(i-1)){
                    int temp=valueArr.get(i);
                    valueArr.set(i, valueArr.get(i-1));
                    valueArr.set(i-1,temp);

                    String temp2=keyArr.get(i);
                    keyArr.set(i, keyArr.get(i-1));
                    keyArr.set(i-1,temp2);
                    change=true;
                }
                else if(valueArr.get(i).equals(valueArr.get(i-1))&&keyArr.get(i).length()>keyArr.get(i-1).length()){
                    int temp=valueArr.get(i);
                    valueArr.set(i, valueArr.get(i-1));
                    valueArr.set(i-1,temp);

                    String temp2=keyArr.get(i);
                    keyArr.set(i, keyArr.get(i-1));
                    keyArr.set(i-1,temp2);
                    change=true;
                }
            }
        }
        LinkedHashMap<String,Integer> sortedMap=new LinkedHashMap();
        for(int i=0;i<keyArr.size();i++){
            sortedMap.put(keyArr.get(i), valueArr.get(i));
        }
        return sortedMap;
    }
    public boolean search(ArrayList<TextField> texts,String search,ScrollPane pane){
        boolean found=false;
        for(TextField field:texts){
            String text=field.getText();
            if(text.equals(search)){
                double nodeX = field.getParent().getBoundsInParent().getMaxX();
                double nodeY = field.getBoundsInParent().getMinY();
                double width = pane.getContent().getBoundsInLocal().getWidth();
                double height = pane.getContent().getBoundsInLocal().getHeight();
                pane.setVvalue(nodeY/height);
                pane.setHvalue(nodeX/width);
                field.setBackground(new Background(new BackgroundFill(Paint.valueOf("FCFF00"), CornerRadii.EMPTY, Insets.EMPTY)));
                found=true;
            }
            else{
                field.setBackground(new Background(new BackgroundFill(Paint.valueOf("DADADA"), CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }
                 
        return found;
    }
}
