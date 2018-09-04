/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package documentscraper;

import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import static org.apache.commons.io.FileUtils.copyFileToDirectory;


/**
 *
 * @author dellingw
 */
public class GUI extends Application {
    private HashMap<File, ArrayList<String>> fileMap;
    private HashMap<File, CheckBox> fileMapChecks;
    private Scraper scpr;
    private Boolean notRead;
    private ArrayList<String> lastFolder;
    private Boolean recursive;
    private boolean openFileMenu=false;
    private boolean openCopyMenu=false;
    final private FileChooser fc = new FileChooser();
    final private DirectoryChooser dc=new DirectoryChooser();
    private ArrayList<String> pastSearches;
    private boolean clearChecks=false;
    private boolean getFromList=false;
    @Override
    public void start(Stage primaryStage) {
        pastSearches=new ArrayList();
        fileMapChecks=new HashMap();
        fileMap=new HashMap();
        scpr=new Scraper();
        notRead=true;
        lastFolder=new ArrayList();
        recursive=true;
        VBox holder = new VBox();
        
        HBox menuBar=new HBox();
        menuBar.setBackground(new Background(new BackgroundFill(Paint.valueOf("616161"), CornerRadii.EMPTY, Insets.EMPTY)));
        menuBar.setBorder(new Border(new BorderStroke(Paint.valueOf("000000"),Paint.valueOf("000000"),Paint.valueOf("000000"),Paint.valueOf("000000"),BorderStrokeStyle.NONE,BorderStrokeStyle.NONE,BorderStrokeStyle.SOLID,BorderStrokeStyle.NONE,CornerRadii.EMPTY,BorderWidths.DEFAULT,Insets.EMPTY)));
        
        Button fileOpen=new Button("Open");
        fileOpen.setCursor(Cursor.HAND);
        fileOpen.setMinSize(50, 30);
        fileOpen.setBackground(new Background(new BackgroundFill(Paint.valueOf("616161"), CornerRadii.EMPTY, Insets.EMPTY)));
        fileOpen.setBorder(new Border(new BorderStroke(Paint.valueOf("000000"),Paint.valueOf("000000"),Paint.valueOf("000000"),Paint.valueOf("000000"),BorderStrokeStyle.NONE,BorderStrokeStyle.SOLID,BorderStrokeStyle.NONE,BorderStrokeStyle.NONE,CornerRadii.EMPTY,BorderWidths.DEFAULT,Insets.EMPTY)));
        fileOpen.setTextFill(Paint.valueOf("FFFFFF"));
        Button fileSave=new Button("Save");
        fileSave.setCursor(Cursor.HAND);
        fileSave.setMinSize(50, 30);
        fileSave.setBackground(new Background(new BackgroundFill(Paint.valueOf("616161"), CornerRadii.EMPTY, Insets.EMPTY)));
        fileSave.setBorder(new Border(new BorderStroke(Paint.valueOf("000000"),Paint.valueOf("000000"),Paint.valueOf("000000"),Paint.valueOf("000000"),BorderStrokeStyle.NONE,BorderStrokeStyle.SOLID,BorderStrokeStyle.NONE,BorderStrokeStyle.NONE,CornerRadii.EMPTY,BorderWidths.DEFAULT,Insets.EMPTY)));
        fileSave.setTextFill(Paint.valueOf("FFFFFF"));
        menuBar.getChildren().addAll(fileOpen,fileSave);

        fileSave.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                fc.setTitle("Save");
                File saveFile=fc.showSaveDialog(primaryStage);
                ArrayList<Object> info=new ArrayList();
                info.add(fileMap);
                info.add(lastFolder);
                info.add(pastSearches);
                String temp=saveFile.getPath();
                if(!temp.endsWith(".ser"))
                    temp=temp.concat(".ser");
                saver(info,temp);
            }
        });
        
        Button copyMenu=new Button("Copy Files");
        copyMenu.setCursor(Cursor.HAND);
        copyMenu.setMinSize(50, 30);
        copyMenu.setBackground(new Background(new BackgroundFill(Paint.valueOf("616161"), CornerRadii.EMPTY, Insets.EMPTY)));
        copyMenu.setBorder(new Border(new BorderStroke(Paint.valueOf("000000"),Paint.valueOf("000000"),Paint.valueOf("000000"),Paint.valueOf("000000"),BorderStrokeStyle.NONE,BorderStrokeStyle.SOLID,BorderStrokeStyle.NONE,BorderStrokeStyle.NONE,CornerRadii.EMPTY,BorderWidths.DEFAULT,Insets.EMPTY)));
        copyMenu.setTextFill(Paint.valueOf("FFFFFF"));
        menuBar.getChildren().add(copyMenu);
        
        MenuButton AnalyzerMenu=new MenuButton("Analyzer");
        AnalyzerMenu.setCursor(Cursor.HAND);
        AnalyzerMenu.setMinSize(50, 30);
        AnalyzerMenu.setBackground(new Background(new BackgroundFill(Paint.valueOf("616161"), CornerRadii.EMPTY, Insets.EMPTY)));
        AnalyzerMenu.setBorder(new Border(new BorderStroke(Paint.valueOf("000000"),Paint.valueOf("000000"),Paint.valueOf("000000"),Paint.valueOf("000000"),BorderStrokeStyle.NONE,BorderStrokeStyle.SOLID,BorderStrokeStyle.NONE,BorderStrokeStyle.NONE,CornerRadii.EMPTY,BorderWidths.DEFAULT,Insets.EMPTY)));
        AnalyzerMenu.setTextFill(Paint.valueOf("FFFFFF"));
        MenuItem fromList =new MenuItem("From List");
        MenuItem fromBrowse =new MenuItem("Browse");
        MenuItem fromGroup= new MenuItem("Multi-File Analysis");
        AnalyzerMenu.getItems().addAll(fromList,fromBrowse,fromGroup);
        menuBar.getChildren().add(AnalyzerMenu);
        
        
        HBox top = new HBox();
        HBox top2= new HBox();
        Label count=new Label("Number of Files: ");
        count.setAlignment(Pos.BOTTOM_RIGHT);
        count.setMinWidth(100);
        TextField countField=new TextField("0");
        countField.setBackground(Background.EMPTY);
        countField.setEditable(false);
        VBox topholder=new VBox();
        topholder.getChildren().addAll(menuBar,top,top2);
        Label keySort = new Label("Title Sorter:");
        keySort.setAlignment(Pos.BOTTOM_RIGHT);
        keySort.setMinWidth(70);
        TextField keySortText= new TextField();
        keySortText.setMinWidth(300);
        Label valueSort = new Label("KeyWord Sorter:");
        valueSort.setAlignment(Pos.BOTTOM_RIGHT);
        valueSort.setMinWidth(100);
        TextField valueSortText= new TextField();
        valueSortText.setPromptText("For multiple seperate by comma");
        valueSortText.setMinWidth(300);
        Button sort=new Button("Sort");
        sort.setMinWidth(50);
        Button notReadTog=new Button("NR ON");
        Button hider=new Button("Hide Checked");
        Button uncheckAllButton=new Button("Uncheck All");
        top.getChildren().addAll(keySort,keySortText,valueSort,valueSortText,sort,notReadTog);
        top2.getChildren().addAll(notReadTog,uncheckAllButton,hider,count,countField);
        
        HBox holder2 =new HBox();
        VBox list = new VBox();
        HBox titles= new HBox();
        titles.setBackground(new Background(new BackgroundFill(Paint.valueOf("D6EAF8"), CornerRadii.EMPTY, Insets.EMPTY)));
        Label spacer=new Label();
        spacer.setPrefWidth(20);
        Label nameTitle= new Label("File Name");
        nameTitle.setMinWidth(300);
        Label valueTitle= new Label("KeyWord Hits");
        valueTitle.setMinWidth(300);
        Label pathTitle=new Label("Path");
        pathTitle.setAlignment(Pos.BOTTOM_LEFT);
        pathTitle.setMinWidth(360);
        titles.getChildren().addAll(spacer,nameTitle,valueTitle,pathTitle);
        topholder.getChildren().add(titles);
        BorderPane root = new BorderPane();
        StackPane branch1=new StackPane();
        ScrollPane branch2=new ScrollPane();
        branch1.setMaxHeight(100);
        HBox inputs1 = new HBox();
        inputs1.setMinHeight(20);
        HBox inputs2 = new HBox();
        inputs2.setMinHeight(20);
        TextField folder = new TextField();
        folder.setPromptText("Divide multiple folders with ::");
        folder.setMinWidth(200);
        TextField search = new TextField();
        search.setPromptText("For multiple search keywords Seperate by a comma(Search only one word on initial search)");
        search.setMinWidth(500);
        Label searchLabel=new Label("Keyword:");
        searchLabel.setMinWidth(60);
        Button btn1 = new Button();
        btn1.setText("search");
        Button recur = new Button();
        recur.setText("Recursive: Yes");
        Button delete = new Button();
        delete.setText("Remove");
        delete.setAlignment(Pos.CENTER_RIGHT);
        Label deleteLabel=new Label("Remove Files with Paths that Contain:");
        TextField delFol=new TextField();
        delFol.setMinWidth(200);
        delFol.setAlignment(Pos.CENTER_LEFT);
        Button loader = new Button();
        loader.setText("Load Files");
        Button loadFolFind = new Button();
        loadFolFind.setText("Find Folder");
        HBox delFolHolder=new HBox();
        delFolHolder.setMinHeight(25);
        Label output=new Label("Ready");
        output.setAlignment(Pos.CENTER_RIGHT);
                
        delFolHolder.getChildren().addAll(deleteLabel,delFol,delete,output);
        inputs1.setMinHeight(25);
        inputs2.setMinHeight(25);
        Button cancel=new Button("Cancel");
        cancel.setCursor(Cursor.HAND);
        cancel.setMinSize(50, 30);
        cancel.setBackground(new Background(new BackgroundFill(Paint.valueOf("616161"), CornerRadii.EMPTY, Insets.EMPTY)));
        cancel.setBorder(new Border(new BorderStroke(Paint.valueOf("000000"),Paint.valueOf("000000"),Paint.valueOf("000000"),Paint.valueOf("000000"),BorderStrokeStyle.NONE,BorderStrokeStyle.SOLID,BorderStrokeStyle.NONE,BorderStrokeStyle.NONE,CornerRadii.EMPTY,BorderWidths.DEFAULT,Insets.EMPTY)));
        cancel.setTextFill(Paint.valueOf("FFFFFF"));
        menuBar.getChildren().add(cancel);
        cancel.setVisible(false);
        cancel.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                getFromList=false;
                root.getBottom().setDisable(false);
                top2.setDisable(false);
                top.setDisable(false);
                for(CheckBox check:fileMapChecks.values()){
                    check.setDisable(false);
                }
                cancel.setVisible(false);
                AnalyzerMenu.setDisable(false);
                output.setText("Done");
            }
        });
        fromList.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                output.setText("Click a File to Analyze");
                getFromList=true;
                root.getBottom().setDisable(true);
                top2.setDisable(true);
                top.setDisable(true);
                for(CheckBox check:fileMapChecks.values()){
                    check.setDisable(true);
                }
                AnalyzerMenu.setDisable(true);
                cancel.setVisible(true);
            } 
        });
        
        fromBrowse.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                File file=fc.showOpenDialog(primaryStage);
                AnalyzerGUI analyzer=new AnalyzerGUI(file);
                Stage secondStage=new Stage();
                analyzer.start(secondStage);
            }
        });
        fromGroup.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                File file=dc.showDialog(primaryStage);
                MultiAnalyzerGUI analyzer=new MultiAnalyzerGUI(file);
                Stage secondStage=new Stage();
                analyzer.start(secondStage);
            }
        });
        
        fileOpen.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                output.setText("Opening...");
                fc.setTitle("Open");
                File openFile=fc.showOpenDialog(primaryStage);
                if(openFile!=null){
                    ArrayList temp=opener(openFile.getPath());
                    if(temp!=null){
                        fileMap=(HashMap<File, ArrayList<String>>)temp.get(0);
                        lastFolder=(ArrayList<String>)temp.get(1);
                        pastSearches=(ArrayList<String>)temp.get(2);
                        for(File file:fileMap.keySet()){
                            fileMapChecks.put(file,new CheckBox());
                        }
                        makeList(list,keySortText.getText(),valueSortText.getText(),countField,false,null,clearChecks,cancel);
                    }
                }
                output.setText("Opened");
            }
        });
        delete.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                output.setText("Deleting...");
                for(Iterator deleter = fileMap.keySet().iterator();deleter.hasNext();){
                    File file=(File)deleter.next();
                    if(file.getPath().contains(delFol.getText())&&!delFol.getText().isEmpty()){
                        deleter.remove();
                        fileMap.remove(file);
                    }
                }
                lastFolder.remove(delFol.getText());
                makeList(list,keySortText.getText(),valueSortText.getText(),countField,false,null,clearChecks,cancel);
                output.setText("Deleted");
            }
        });
        recur.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                recursive=!recursive;
                Button btn=(Button)event.getSource();
                if(recursive)
                    btn.setText("Recursive: Yes");
                else
                    btn.setText("Recursive: No");
            }
        });
        loader.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                output.setText("Loading...");
                String[] folderArr=folder.getText().split("::");
                boolean temp=scpr.scrape(folderArr,lastFolder,search.getText(),fileMap,fileMapChecks,recursive,true,pastSearches);
                makeList(list,keySortText.getText(),valueSortText.getText(),countField,false,null,clearChecks,cancel);
                if(!temp)
                    folder.setText("Folder Does Not Exist");
                output.setText("Loaded");
            }
        });
        loadFolFind.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                File folderToLoad=dc.showDialog(primaryStage);
                folder.setText(folderToLoad.getPath());
            }
        });
        btn1.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                output.setText("Searching...");
                String[] folderArr=folder.getText().split("::");
                scpr.scrape(folderArr,lastFolder,search.getText(),fileMap,fileMapChecks,recursive,false,pastSearches);
                makeList(list,keySortText.getText(),valueSortText.getText(),countField,false,null,clearChecks,cancel);
                output.setText("Searched");
            }
        });
        sort.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                output.setText("Sorting...");
                makeList(list,keySortText.getText(),valueSortText.getText(),countField,false, null,clearChecks,cancel);
                output.setText("Sorted");
               }
        });
        notReadTog.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                Button btn=(Button)event.getSource();
                if(notRead)
                    btn.setText("NR OFF");
                else
                    btn.setText("NR ON");
                notRead=!notRead;
                output.setText("Working...");
                makeList(list,keySortText.getText(),valueSortText.getText(),countField,false,null,clearChecks,cancel);
                output.setText("Done");
            }
        });
        copyMenu.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                dc.setTitle("Select Folder To Copy To");
                File copyFile=dc.showDialog(primaryStage);
                output.setText("Copying...");
                if(copyFile!=null)
                    makeList(list,keySortText.getText(),valueSortText.getText(),countField,true,new File(copyFile.getPath()),clearChecks,cancel);
                output.setText("Done");
            }
        });
        hider.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                Button temp=(Button)event.getSource();
                if(temp.getText().equals("Hide Checked")){
                    temp.setText("Show Checked");
                    clearChecks=true;
                }else{
                    temp.setText("Hide Checked");
                    clearChecks=false;
                }
                output.setText("Working...");
                makeList(list,keySortText.getText(),valueSortText.getText(),countField,true,null,clearChecks,cancel);
                output.setText("Done");
            }
        });
        uncheckAllButton.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                clearChecks=false;
                for(File file:fileMapChecks.keySet()){
                    CheckBox temp=fileMapChecks.get(file);
                    if(temp.isSelected())
                        fileMapChecks.get(file).fire();
                }
            }
        });

        holder2.getChildren().add(list);
        branch2.setContent(holder2);
        inputs1.getChildren().addAll(loadFolFind,folder,loader,recur);
        inputs2.getChildren().addAll(searchLabel,search,btn1);
        holder.getChildren().addAll(inputs1,inputs2,delFolHolder);
        branch1.getChildren().add(holder);
        root.setBottom(branch1);
        root.setCenter(branch2);
        root.setTop(topholder);
        menuBar.prefWidthProperty().bind(root.widthProperty());
        titles.prefWidthProperty().bind(root.widthProperty());
        
        Scene scene = new Scene(root,1000,600);
        output.setMinWidth(200);
        output.setTextAlignment(TextAlignment.RIGHT);
        output.setTranslateX(scene.getWidth()-660);
        primaryStage.setTitle("Document Scraper");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void mouseClicked(MouseEvent event){
        System.out.print("1");
        TextField temp=(TextField)event.getSource();
        if(event.getButton()==MouseEvent.BUTTON1&&event.getClickCount()==2&&temp.getId().equals("Path")){
            System.out.print("2");
            try{
                Runtime.getRuntime().exec(temp.getText());
            }catch(Exception e){
            }
        }    
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    public void makeList(VBox list,String keySort,String valueSort,TextField countField,boolean copyFiles, File copyFolder,Boolean checks,Button cancel){
        list.getChildren().remove(0, list.getChildren().size());
        Integer counter=0;
        ArrayList<String> valueSorts=new ArrayList((Arrays.asList(valueSort.toLowerCase().split(","))));
        
        for(File file:fileMap.keySet()){
            String name=file.getName();
            if((!fileMapChecks.get(file).isSelected()||!checks)&&(name.toLowerCase().contains(keySort.toLowerCase())||keySort.equals(""))&&(fileMap.get(file).containsAll(valueSorts)||valueSort.equals(""))){
                Boolean notRead2=false;
                HBox hTemp=new HBox();
                hTemp.setOnMouseClicked(e->{
            
                    if(e.getButton()==MouseButton.PRIMARY&&getFromList){
                        AnalyzerGUI analyzer=new AnalyzerGUI(file);
                        Stage secondStage=new Stage();
                        analyzer.start(secondStage);
                        cancel.fire();
                    }
                    
                });
                hTemp.prefWidth(1000);
                hTemp.minWidth(1000);
                CheckBox check=fileMapChecks.get(file);
                check.setPrefSize(20,20);
                Label lTemp=new Label(name);
                TextField lTemp2=new TextField();
                lTemp2.setBackground(Background.EMPTY);
                lTemp2.setEditable(false);  
                TextField lTemp3=new TextField(file.getPath());
                lTemp3.setEditable(false);
                lTemp3.setBackground(Background.EMPTY);
                lTemp3.setOnMouseClicked(e -> {
                    TextField temp=(TextField)e.getSource();
                    if(e.getButton().equals(MouseButton.PRIMARY)&&e.getClickCount()==2)
                        try{
                            File tempFile=new File(temp.getText());
                            Desktop.getDesktop().open(tempFile);
                        }catch(IOException exc){
                            exc.printStackTrace();
                        }
                });

                String concat;


                for(String str:fileMap.get(file)){
                    if(fileMap.get(file).get(0)==str)
                        concat = lTemp2.getText().concat(str);
                    else
                        concat = lTemp2.getText().concat(", "+str);   
                    lTemp2.setText(concat);
                }
                if(fileMap.get(file).contains("!Not Readable!")){
                    lTemp2.setStyle("-fx-text-fill: red;");
                    lTemp.setTextFill(Paint.valueOf("FF0000"));
                    lTemp2.setText("!Not Readable!");
                    notRead2=true;
                }
                if(!name.contains(".pdf") && !name.contains(".doc")&& !name.contains(".xls")&& !name.contains(".ppt")){
                    lTemp2.setStyle("-fx-text-fill: red;");
                    lTemp2.setText("File Not Read");
                    lTemp.setTextFill(Paint.valueOf("FF0000"));
                    notRead2=true;
                }
                if(!notRead2 || notRead){
                    counter=counter+1;
                    hTemp.getChildren().add(check);
                    hTemp.getChildren().add(lTemp);
                    lTemp.setMinWidth(300);
                    lTemp.setMaxWidth(300);
                    hTemp.getChildren().add(lTemp2);
                    lTemp2.setMinWidth(300);
                    lTemp2.setMaxWidth(300);
                    hTemp.getChildren().add(lTemp3);
                    lTemp3.setMinWidth(360);
                    hTemp.setBackground(new Background(new BackgroundFill(Paint.valueOf("DADADA"), CornerRadii.EMPTY, Insets.EMPTY)));
                    hTemp.setBorder(new Border(new BorderStroke(Paint.valueOf("000000"),BorderStrokeStyle.SOLID,CornerRadii.EMPTY,BorderWidths.DEFAULT)));
                    list.getChildren().add(hTemp);
                    if(copyFiles){
                        try{
                            fileCopier(file,copyFolder);
                        }catch(Exception e){
                        }
                    }
                }
            }
        }
        countField.setText(counter.toString());
    }
    public ArrayList<Object> opener(String file){
        ArrayList<Object> openedFile = null;
        try {
           FileInputStream fileIn = new FileInputStream(file);
           ObjectInputStream in = new ObjectInputStream(fileIn);
           openedFile = (ArrayList<Object>) in.readObject();
           in.close();
           fileIn.close();
        } catch (IOException i) {
           i.printStackTrace();
           return null;
        } catch (ClassNotFoundException c) {
           c.printStackTrace();
           return null;
        }
        return openedFile;
    }
    public void saver(ArrayList<Object> file,String fileLoc){
        try {
            FileOutputStream fileOut =
            new FileOutputStream(fileLoc);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(file);
            out.close();
            fileOut.close();
        } catch (IOException i) {
        i.printStackTrace();
      }
    }
    private static void fileCopier(File sourceFile, File copyFolder)throws IOException {
        copyFileToDirectory(sourceFile,copyFolder);
    }
}
   
