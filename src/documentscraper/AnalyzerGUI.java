package documentscraper;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

/**
 *
 * @author dellingw
 */
public class AnalyzerGUI extends Application {
    File file;
    ArrayList<TextField> texts=new ArrayList();
    public AnalyzerGUI(File file){
        this.file=file;
    }
    @Override
    public void start(Stage secondStage) {
        Analyzer analyzer=new Analyzer(file);
        LinkedHashMap<String,Integer> sortedMap=analyzer.analyze(false);
        BorderPane root=new BorderPane();
        
        HBox menuBar=new HBox();
        menuBar.setAlignment(Pos.CENTER_RIGHT);
        menuBar.setBackground(new Background(new BackgroundFill(Paint.valueOf("616161"), CornerRadii.EMPTY, Insets.EMPTY)));
        menuBar.setBorder(new Border(new BorderStroke(Paint.valueOf("000000"),Paint.valueOf("000000"),Paint.valueOf("000000"),Paint.valueOf("000000"),BorderStrokeStyle.NONE,BorderStrokeStyle.NONE,BorderStrokeStyle.SOLID,BorderStrokeStyle.NONE,CornerRadii.EMPTY,BorderWidths.DEFAULT,Insets.EMPTY)));
        TextField searchField=new TextField();
        searchField.setPromptText("Enter Word to Find");
        searchField.setMinWidth(100);
        Button search=new Button("Find");
        menuBar.getChildren().addAll(searchField,search);
        root.setBottom(menuBar);
        
        ScrollPane branch=new ScrollPane();
        branch.setBackground(new Background(new BackgroundFill(Paint.valueOf("DADADA"), CornerRadii.EMPTY, Insets.EMPTY)));
        HBox holder=new HBox();
        root.setCenter(branch);
        branch.setContent(holder);
        Integer lastNum=0;
        VBox col=new VBox();
        for(String str:sortedMap.keySet()){
            TextField text=new TextField(str);
            texts.add(text);
            text.setAlignment(Pos.CENTER);
            text.setEditable(false);
            text.setBackground(new Background(new BackgroundFill(Paint.valueOf("DADADA"), CornerRadii.EMPTY, Insets.EMPTY)));
            if(sortedMap.get(str).equals(lastNum)){
                col.getChildren().add(text);
                
            }
            else{
                col=new VBox();
                col.setBorder(new Border(new BorderStroke(Paint.valueOf("000000"),Paint.valueOf("000000"),Paint.valueOf("000000"),Paint.valueOf("000000"),BorderStrokeStyle.NONE,BorderStrokeStyle.SOLID,BorderStrokeStyle.NONE,BorderStrokeStyle.NONE,CornerRadii.EMPTY,BorderWidths.DEFAULT,Insets.EMPTY)));
                col.setPrefSize(130, 300);
                TextField title=new TextField("Occurs "+sortedMap.get(str)+" Times");
                title.setBackground(new Background(new BackgroundFill(Paint.valueOf("d6eaf8"), CornerRadii.EMPTY, Insets.EMPTY)));
                title.setAlignment(Pos.CENTER);
                col.getChildren().addAll(title,text);
                holder.getChildren().add(col);
            }
            lastNum=sortedMap.get(str);
        }
        search.setOnAction(new EventHandler(){
            @Override
            public void handle(Event event) {
                boolean found=analyzer.search(texts, searchField.getText().toLowerCase(), branch);
                if(!found){
                    searchField.setPromptText("Word not Found");
                    searchField.setText("");
                }
            }
        });
        searchField.setOnMouseClicked(new EventHandler(){
            @Override
            public void handle(Event event) {
                searchField.setPromptText("Enter Word to Find");
            }
        });
          
        secondStage.setTitle("Word Analysis for "+file.getName());
        secondStage.setScene(new Scene(root,600,450));
        secondStage.show();
    }
    
}
