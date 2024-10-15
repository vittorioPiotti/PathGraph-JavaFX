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

package com.vittoriopiotti.pathgraph;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import com.vittoriopiotti.pathgraph.constants.AppConstants;
import com.vittoriopiotti.pathgraph.containers.PathGraphUI;
import com.vittoriopiotti.pathgraph.constants.SvgConstants;
import com.vittoriopiotti.pathgraph.utilities.UtilitiesUI;


public class App extends Application {


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
            PathGraphUI pg = new PathGraphUI(
                    primaryStage,
                    scene
            );

        /* 4. Add PathGraph in a container */
            ScrollPane page = UtilitiesUI.createPage(
                    primaryStage,
                    /* window name */
                    "PathGraph",
                    /* min width main pane */
                    400,
                    /* max width main pane */
                    1280,
                    /* min height main pane */
                    400,
                    /* max height main pane */
                    640,
                    /* graph to add */
                    new VBox(pg),
                    /* social icon footer */
                    SvgConstants.SVG_GITHUB,
                    /* social name footer */
                    "Github",
                    /* copyrights */
                    "© 2024 · Vittorio Piotti ",
                    /* link to site */
                    "https://github.com/vittorioPiotti"
            );

            root.setCenter(page);
            pg.setup().thenRun(() -> {
                    pg.newNode('A');
                    pg.newNode('B');
                    pg.newNode('C');
                    pg.newEdge('A', 'B', 1);
                    pg.newEdge('C', 'A', 1, false);
                    pg.newEdge('C', 'A', 1, AppConstants.DIRECTION_FIRST);
            });

    }

    public static void main(String[] args) {
        launch(args);
    }
}



