/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package documentscraper;

import java.io.File;
import java.util.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.util.stream.Stream;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.POITextExtractor;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFSlideShowImpl;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.*;
import org.apache.poi.xwpf.usermodel.*;

/**
 *
 * @author dellingw
 */
public class Scraper {
    private ArrayList<String> search;
    private Scanner reader;
    private File folder;
    public Scraper(){
        reader = new Scanner(System.in);
    }
    public boolean scrape(String[] folderNames,ArrayList<String> lastFolder, String search, HashMap<File,ArrayList<String>> fileMap,HashMap<File,CheckBox> fileMapChecks,Boolean recursive,Boolean loadFiles,ArrayList<String> pastSearches){
        if(search!=null/*&&!search.equals("")*/&&!loadFiles){
            this.search=new ArrayList(Arrays.asList(search.toLowerCase().split(",")));

            for(Iterator deleter = this.search.iterator();deleter.hasNext();){
                String searchName=(String)deleter.next();
                if(pastSearches.contains(searchName)){
                    deleter.remove();
                    this.search.remove(searchName);                    
                }
            }
            pastSearches.addAll(this.search);
        }
        boolean exists=true;
        for(String folderName:folderNames){
            folder=new File(folderName);
            try{
                if(!loadFiles){
                    fileMap.keySet().stream().parallel().forEach(file->scraping(file,fileMap,recursive,pastSearches));
                }else {
                    loader(fileMap,fileMapChecks,recursive,folderNames,lastFolder,pastSearches);
                }
            }catch(Exception e){
                System.err.print("folder not compatible\n");
                e.printStackTrace();
                exists=false;
            }   
        }
        return exists;
    }
    private void scraping(File file,HashMap<File,ArrayList<String>> fileMap,Boolean recursive,ArrayList<String> pastSearches){

        String fileName=file.getName();
        Boolean found=false;
        ArrayList<String> searches=new ArrayList(this.search);
        for(int i=0;i<searches.size();i++){
            String replaceAll = searches.get(i).replaceAll("-", " ");
            searches.set(i, replaceAll);
        }
        if(!searches.isEmpty()){
            for(Iterator deleter = searches.iterator();deleter.hasNext();){
                String searchName=(String)deleter.next();
                if(fileName.toLowerCase().contains(searchName)){
                    fileMap.get(file).add(searchName);                    
                    deleter.remove();
                    searches.remove(searchName);
                }
            }
            if(fileName.endsWith(".doc")||fileName.endsWith(".ppt")){
                try {
                    String contents=ExtractorFactory.createExtractor(file).getText().toLowerCase();
                    contents=contents.replaceAll("-", " ");
                    for(String str:searches){
                        found= contents.contains(str);
                        if(found)   
                            fileMap.get(file).add(str);
                    }
                    
                } catch(Exception e){
                    e.printStackTrace();
                } 
            }
            else if(fileName.endsWith(".xlsx")||fileName.endsWith(".xlsm")){
                try {
                    OPCPackage opcPackage=OPCPackage.open(file);
                    ReadOnlySharedStringsTable table=new ReadOnlySharedStringsTable(opcPackage);
                    List<String> tableItems=table.getItems();
                    for(String str:searches){
                        for(int i=0;i<tableItems.size();i++){  
                            String tableStr=tableItems.get(i).replaceAll("-", " ");;
                            if(tableStr.contains(str)){
                                found=true;
                            }
                            if(found){   
                                fileMap.get(file).add(str);
                                break;
                            }
                        }
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
            else if(fileName.endsWith(".xls")){
                try {
                    int counter=0;
                    Workbook doc = WorkbookFactory.create(new FileInputStream(file));
                    Iterator<Sheet> sheets=doc.sheetIterator();
                    for(String str:searches){
                        outer:
                        while (sheets.hasNext()){
                            Sheet sheet= sheets.next();
                            Iterator<Row> rows=sheet.rowIterator();
                            while (rows.hasNext()){ 
                                Row row=rows.next(); 
                                Iterator<Cell> cells = row.cellIterator(); 
                                while (cells.hasNext()){
                                    Cell cell=cells.next();
                                    String cellValue = cell.toString();
                                    if(cellValue.toLowerCase().replaceAll("-", " ").contains(str)){
                                       fileMap.get(file).add(str);
                                       break outer;
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
                if(!fileMap.get(file).contains("!Not Readable!")){
                    try {
                        int counter=0;
                        PDDocument doc = PDDocument.load(file);
                        PDFTextStripper pdfStrip=new PDFTextStripper();
                        int k=0;
                        outer:
                        for(int i=0;i<doc.getPages().getCount();i++){
                            pdfStrip.setStartPage(i);
                            pdfStrip.setEndPage(i);
                            String stripper=pdfStrip.getText(doc).toLowerCase().replaceAll("-", " ");;
                            if(stripper.length()<5){
                               k++;
                            }else{
                                k=100;
                            }
                            if(k==4){
                                throw new Exception();
                            }
                            for(String str:searches){
                                if(stripper.contains(str)&&!fileMap.get(file).contains(str)){
                                    fileMap.get(file).add(str);
                                    counter++;
                                }
                                if(counter==searches.size()){
                                    break outer;
                                }
                            }
                        }
                        doc.close();
                    } catch(Exception e){
                        fileMap.get(file).add("!Not Readable!");
                    }
                }
            }else{
                ArrayList<String> temp=fileMap.get(file);
                if(!temp.contains("!Not Readable!")){
                    temp.add("!Not Readable!");
                }
            }
        }
    }
    public void loader(HashMap<File,ArrayList<String>> fileMap,HashMap<File,CheckBox> fileMapChecks,Boolean recursive,String[] folderNames,ArrayList<String> lastFolder,ArrayList<String> pastSearches){
        for(String folderName:folderNames){
            folder=new File(folderName);
            if(folder!=null&&folder.listFiles().length>0){
                lastFolder.add(folderName);
                for(File file:folder.listFiles()){
                    if(!file.isDirectory()){
                            fileMap.putIfAbsent(file, new ArrayList());
                            fileMapChecks.putIfAbsent(file,new CheckBox());
                    }
                    else if(recursive){
                        String[] temp={file.getPath()};
                        new Scraper().scrape(temp,new ArrayList<String>(),null,fileMap,fileMapChecks,true,true,null);
                    }
                }
            }
        }
    }
}
