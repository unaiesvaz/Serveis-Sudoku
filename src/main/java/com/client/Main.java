package com.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application { //.\run.ps1 com.client.Main

    public static CtrlConfig ctrlConfig;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        final int windowWidth = 1000;
        final int windowHeight = 700;

        UtilsViews.parentContainer.setStyle("-fx-font: 14 arial;");

        UtilsViews.addView(
                getClass(),
                "ViewConfig",
                "/assets/viewConfig.fxml"
        );

        ctrlConfig = (CtrlConfig) UtilsViews.getController("ViewConfig");

        Scene scene = new Scene(UtilsViews.parentContainer);

        stage.setScene(scene);
        stage.setTitle("Sudoku Online");
        stage.setMinWidth(windowWidth);
        stage.setMinHeight(windowHeight);
        stage.show();
    }
}