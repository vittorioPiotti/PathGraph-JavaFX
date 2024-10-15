/*
 * JavaFXSmartGraph v2.0.0 (https://github.com/brunomnsilva/JavaFXSmartGraph/releases/tag/v2.0.0)
 * JavaFXSmartGraph | Copyright 2024  brunomnsilva@gmail.com
 * Licensed under MIT (https://github.com/brunomnsilva/JavaFXSmartGraph/blob/master/LICENSE.txt)
 */


/*
 * PathGraph v1.0.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/releases/tag/1.0.0)
 * PathGraph | Copyright 2024  Vittorio Piotti
 * Licensed under GPL v3.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/blob/main/LICENSE.txt)
 */

package com.vittoriopiotti.pathgraph.example;


import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.vittoriopiotti.pathgraph.containers.PathGraphUI;


import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.scene.layout.BorderPane;


public class Example extends Application {


    @Override
    public void start(Stage primaryStage) {

        /* 1. Create javafx window */
        BorderPane root = new BorderPane();
        root.setBackground(Background.fill(Color.web("#dee2e6")));
        Scene scene = new Scene(root, 750, 550);
        primaryStage.setScene(scene);

        /* 2. Visualize primary stage */
        primaryStage.show();


        /* 3. Create PathGraph object*/
        PathGraphUI pg = new PathGraphUI(primaryStage, scene);

        /* 4. Add PathGraph in a container */
        root.setCenter(pg);

        /* 4. Setup PathGraph */
        pg.setup();

        /* 4. Configure PathGraph */
        pg.setUI(false,false,false,false,false,false );

        pg.enableListenersGraph(true);
        pg.enableListenersPane(true);
        pg.setAutomaticLayout(true);

        /* 5. Make Graphs with PathGraph*/
        pg.newNode('A');
        pg.newNode('B');
        pg.newNode('C');
        pg.newEdge('A', 'B', 1);
        pg.newEdge('C', 'A', 2);
        pg.newEdge('C', 'A', 3);


    }

    public static void main(String[] args) {
        launch(args);
    }
}
