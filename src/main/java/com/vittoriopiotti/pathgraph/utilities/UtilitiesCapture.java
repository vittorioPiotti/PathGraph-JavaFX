
/*
 * PathGraph v1.0.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/releases/tag/1.0.0)
 * PathGraph | Copyright 2024  Vittorio Piotti
 * Licensed under GPL v3.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/blob/main/LICENSE.txt)
 */


package com.vittoriopiotti.pathgraph.utilities;

import com.vittoriopiotti.pathgraph.app.Constants;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import javafx.scene.transform.Transform;

import java.util.concurrent.CompletableFuture;

/**
 * Utility class for capturing images from a JavaFX Pane and saving them as PNG files.
 * This class provides methods for taking snapshots of JavaFX components with optional
 * animation effects and handling the file saving process asynchronously.
 *
 * @author vittoriopiotti
 */
public class UtilitiesCapture {

    /**
     * Captures a PNG image of the specified Pane and saves it to a file asynchronously.
     * An optional flash animation effect can be displayed during the capture process.
     *
     * @param parent    the Pane to capture as an image.
     * @param isAnimated indicates whether to show a flash effect during the capture.
     * @return integer status code:
     *         - SUCCESS if the capture and saving process is successful,
     *         - ERROR if an error occurs during the capture or saving,
     *         - INTERRUPTED if the user cancels the file saving dialog.
     */
    public static CompletableFuture<Integer> takeScreenshot(Pane parent, boolean isAnimated) {
        double scaleFactor = 3.0;
        Rectangle flashEffect = new Rectangle();
        flashEffect.setFill(Color.WHITE);
        flashEffect.setOpacity(0);
        parent.getChildren().add(flashEffect);
        flashEffect.widthProperty().bind(parent.widthProperty());
        flashEffect.heightProperty().bind(parent.heightProperty());

        CompletableFuture<Integer> futureResult = new CompletableFuture<>();

        if (isAnimated) {
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.1), flashEffect);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            PauseTransition pause = new PauseTransition(Duration.seconds(0.1));
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), flashEffect);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);

            fadeIn.setOnFinished(event -> pause.play());
            pause.setOnFinished(event -> fadeOut.play());
            fadeOut.setOnFinished(event -> takeSnapshot(parent, scaleFactor).thenAccept(result -> {
                parent.getChildren().remove(flashEffect);
                futureResult.complete(result);
            }));
            fadeIn.play();
        } else {
            takeSnapshot(parent, scaleFactor).thenAccept(result -> {
                parent.getChildren().remove(flashEffect);
                futureResult.complete(result);
            });
        }

        return futureResult;
    }

    /**
     * Captures a PNG image of the specified Pane and saves it to a file asynchronously.
     * An optional flash animation effect can be displayed during the capture process.
     * Animation is active
     *
     * @param parent    the Pane to capture as an image.
     * @return integer status code:
     *         - SUCCESS if the capture and saving process is successful,
     *         - ERROR if an error occurs during the capture or saving,
     *         - INTERRUPTED if the user cancels the file saving dialog.
     */
    public static CompletableFuture<Integer> takeScreenshot(Pane parent) {
        return takeScreenshot(parent,true);
    }

    /**
     * Takes a snapshot of the specified Pane at a given scale factor and returns
     * the result as a CompletableFuture with a status code.
     *
     * @param parent      the Pane to capture as an image.
     * @param scaleFactor the scale factor for the snapshot (e.g., for higher resolution).
     * @return a CompletableFuture that completes with an integer status code:
     *         - SUCCESS if the snapshot and saving process is successful,
     *         - ERROR if an error occurs during the capture or saving.
     */
    private static CompletableFuture<Integer> takeSnapshot(Pane parent, double scaleFactor) {
        CompletableFuture<Integer> snapshotFuture = new CompletableFuture<>();
        double width = parent.getBoundsInParent().getWidth();
        double height = parent.getBoundsInParent().getHeight();
        WritableImage highResImage = new WritableImage((int) (width * scaleFactor), (int) (height * scaleFactor));
        SnapshotParameters params = new SnapshotParameters();
        params.setTransform(Transform.scale(scaleFactor, scaleFactor));

        parent.snapshot(snapshotResult -> {
            WritableImage snapshot = snapshotResult.getImage();
            if (snapshot.getWidth() == 0 || snapshot.getHeight() == 0) {
                snapshotFuture.complete(Constants.ERROR);
            } else {
                saveImageToFile(parent, snapshot).thenAccept(snapshotFuture::complete);
            }
            return null;
        }, params, highResImage);

        return snapshotFuture;
    }

    /**
     * Saves the provided image to a file using a file chooser dialog. The method is executed
     * on the JavaFX Application Thread and returns the result asynchronously.
     *
     * @param parent the Pane that is used as the owner for the file chooser dialog.
     * @param image  the image to save as a PNG file.
     * @return a CompletableFuture that completes with an integer status code:
     *         - SUCCESS if the image is saved successfully,
     *         - ERROR if an error occurs during the saving process,
     *         - INTERRUPTED if the user cancels the file dialog.
     */
    private static CompletableFuture<Integer> saveImageToFile(Pane parent, WritableImage image) {
        CompletableFuture<Integer> saveFuture = new CompletableFuture<>();

        Platform.runLater(() -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Image As...");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PNG Image", "*.png")
            );
            File file = fileChooser.showSaveDialog(parent.getScene().getWindow());
            if (file != null) {
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                    saveFuture.complete(Constants.SUCCESS);
                } catch (IOException ignored) {
                    saveFuture.complete(Constants.ERROR);
                }
            } else {
                saveFuture.complete(Constants.INTERRUPTED);
            }
        });

        return saveFuture;
    }
}
