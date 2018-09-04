package documentscraper;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Box;
import javafx.stage.Stage;

/**
 *
 * @author dellingw
 */
public class MultiAnalyzerGUI extends Application {
    private File folder;
    private ArrayList<String> pastSearches;
    private ArrayList<LinkedHashMap<String,Integer>> maps=new ArrayList();
    private ArrayList<VBox> searchVBoxs;
    private VBox files;
    public MultiAnalyzerGUI(File file){
        this.folder=file;
        pastSearches=new ArrayList();
        files=new VBox();
        searchVBoxs=new ArrayList();
    }
    @Override
    public void start(Stage secondStage) {
        
        BorderPane root=new BorderPane();
        
        HBox menuBar=new HBox();
        menuBar.setAlignment(Pos.CENTER_RIGHT);
        menuBar.setBackground(new Background(new BackgroundFill(Paint.valueOf("616161"), CornerRadii.EMPTY, Insets.EMPTY)));
        menuBar.setBorder(new Border(new BorderStroke(Paint.valueOf("000000"),Paint.valueOf("000000"),Paint.valueOf("000000"),Paint.valueOf("000000"),BorderStrokeStyle.NONE,BorderStrokeStyle.NONE,BorderStrokeStyle.SOLID,BorderStrokeStyle.NONE,CornerRadii.EMPTY,BorderWidths.DEFAULT,Insets.EMPTY)));
        TextField searchField=new TextField();
        searchField.setPromptText("Enter Word to Search");
        searchField.setMinWidth(100);
        Button search=new Button("Find");
        menuBar.getChildren().addAll(searchField,search);
        root.setBottom(menuBar);
        
        HBox titleHolder=new HBox();
        
        TextField title=new TextField("File Name");
        title.setMinSize(300,25);
        title.setMaxSize(300,25);
        title.setEditable(false);
        title.setBackground(new Background(new BackgroundFill(Paint.valueOf("d6eaf8"), CornerRadii.EMPTY, Insets.EMPTY)));
        title.setBorder(new Border(new BorderStroke(Paint.valueOf("000000"),BorderStrokeStyle.SOLID,CornerRadii.EMPTY,BorderWidths.DEFAULT)));
        title.setAlignment(Pos.CENTER);
        files.setMinWidth(300);
        
        for(File file:folder.listFiles()){
            Analyzer analyzer=new Analyzer(file);
            LinkedHashMap<String,Integer> map=analyzer.analyze(true);
            maps.add(map);
            TextField temp=new TextField(file.getName());
            if(map.keySet().size()<1){
                temp.setStyle("-fx-text-fill:red;");
            }
            temp.setMinSize(300,25);
            temp.setMaxSize(300,25);
            temp.setEditable(false);
            temp.setBackground(new Background(new BackgroundFill(Paint.valueOf("DADADA"), CornerRadii.EMPTY, Insets.EMPTY)));
            temp.setBorder(new Border(new BorderStroke(Paint.valueOf("000000"),BorderStrokeStyle.SOLID,CornerRadii.EMPTY,BorderWidths.DEFAULT)));
            files.getChildren().add(temp);
        }
        Region spacer=new Region();
        spacer.setMinHeight(13);
        files.getChildren().add(spacer);
        Region spacer2=new Region();
        spacer2.setMinWidth(13);
        titleHolder.getChildren().add(spacer2);
        ScrollPane scrollSearches=new ScrollPane();
        ScrollPane scrollfileNames=new ScrollPane();
        ScrollPane scrollTitles=new ScrollPane();
        scrollTitles.setPadding(Insets.EMPTY);
        scrollTitles.setMaxHeight(25);
        scrollTitles.setPrefHeight(25);
        scrollTitles.setMinHeight(25);
        scrollfileNames.setPadding(Insets.EMPTY);
        scrollSearches.setPadding(Insets.EMPTY);
        scrollSearches.setBackground(new Background(new BackgroundFill(Paint.valueOf("DADADA"), CornerRadii.EMPTY, Insets.EMPTY)));
        HBox holder=new HBox();
        HBox topTitleHolder=new HBox();
        topTitleHolder.setMaxHeight(25);    
        topTitleHolder.getChildren().addAll(title,scrollTitles);
        scrollTitles.setContent(titleHolder);
        scrollSearches.setContent(holder);
        scrollfileNames.setContent(files);
        scrollfileNames.vvalueProperty().bindBidirectional(scrollSearches.vvalueProperty());
        scrollSearches.hvalueProperty().bindBidirectional(scrollTitles.hvalueProperty());
        scrollSearches.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        scrollSearches.setHbarPolicy(ScrollBarPolicy.ALWAYS);
        scrollfileNames.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrollfileNames.setVbarPolicy(ScrollBarPolicy.NEVER);
        scrollTitles.setVbarPolicy(ScrollBarPolicy.NEVER);
        scrollTitles.setHbarPolicy(ScrollBarPolicy.NEVER);
        root.setTop(topTitleHolder);
        root.setLeft(scrollfileNames);
        root.setCenter(scrollSearches);
        
        search.setOnAction(new EventHandler(){
            @Override
            public void handle(Event event) {
                if(!searchField.getText().equals("")){
                    String searchTemp=searchField.getText().toLowerCase().replace(", ",",").replace(" ,",",");
                   
                    String[] searches=searchTemp.split(",");
                    for(String search:searches){
                        if(!pastSearches.contains(search)){
                            VBox temp=new VBox();
                            HBox searchBox=new HBox();
                            Button sortHL=new Button(" ^ ");
                            sortHL.maxHeight(15);
                            sortHL.maxWidth(15);
                            sortHL.setPadding(Insets.EMPTY);
                            sortHL.setOnAction(new EventHandler(){
                                @Override
                                public void handle(Event event) {
                                    Button temp=(Button)event.getSource();
                                    sort(titleHolder.getChildren().indexOf(temp.getParent()));
                                }
                            });
                            searchVBoxs.add(0,temp);
                            temp.setAlignment(Pos.CENTER);
                            TextField title=new TextField(search);
                            title.setMinHeight(25);
                            title.setMaxHeight(25);
                            title.setEditable(false);
                            searchBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("B4CCCC"), CornerRadii.EMPTY, Insets.EMPTY)));
                            title.setBackground(Background.EMPTY);
                            searchBox.setBorder(new Border(new BorderStroke(Paint.valueOf("000000"),BorderStrokeStyle.SOLID,CornerRadii.EMPTY,BorderWidths.DEFAULT)));
                            title.setAlignment(Pos.CENTER);
                            searchBox.getChildren().addAll(title,sortHL);
                            titleHolder.getChildren().add(0,searchBox);
                            for(LinkedHashMap<String,Integer> map:maps){
                                TextField text=new TextField("0");
                                text.setMinHeight(25);
                                text.setMaxHeight(25);
                                text.setMaxWidth(166);
                                text.setMinWidth(166);
                                text.setEditable(false);
                                text.setAlignment(Pos.CENTER);
                                text.setBackground(new Background(new BackgroundFill(Paint.valueOf("DADADA"), CornerRadii.EMPTY, Insets.EMPTY)));
                                text.setBorder(new Border(new BorderStroke(Paint.valueOf("000000"),BorderStrokeStyle.SOLID,CornerRadii.EMPTY,BorderWidths.DEFAULT)));
                                for(String str:map.keySet()){
                                    if(str.equals(search)){
                                        text.setText(map.get(str)+"");
                                    }
                                }
                                temp.getChildren().add(text);
                            }
                            holder.getChildren().add(0,temp);
                            pastSearches.add(search);
                        }
                        
                    }
                }
            }
        });
        secondStage.setTitle("Word Analysis for "+folder.getName());
        secondStage.setScene(new Scene(root,1000,600));
        secondStage.show();
    }
    public void sort(int index){
        List<Node> vboxChildren=searchVBoxs.get(index).getChildren();
        boolean change=true;
        while(change){
            change=false;
            for(int i=1;i<vboxChildren.size();i++){
                TextField text1=(TextField)vboxChildren.get(i);
                TextField text2=(TextField)vboxChildren.get(i-1);
                if(Integer.parseInt(text1.getText())>Integer.parseInt(text2.getText())){
                    change=true;
                    for(VBox vbox:searchVBoxs){
                        TextField textTemp=(TextField)vbox.getChildren().get(i);
                        vbox.getChildren().remove(i);
                        vbox.getChildren().add(i-1,textTemp);
                    }
                        TextField text3=(TextField)files.getChildren().get(i);
                        files.getChildren().remove(i);
                        files.getChildren().add(i-1,text3);
                        LinkedHashMap<String, Integer> mapTemp = maps.get(i);
                        maps.remove(i);
                        maps.add(i-1,mapTemp);
                                           
                }
            }
        }
    }
        
}
