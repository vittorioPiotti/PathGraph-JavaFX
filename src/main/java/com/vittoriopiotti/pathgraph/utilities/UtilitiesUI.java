

/*
 * PathGraph v1.0.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/releases/tag/1.0.0)
 * PathGraph | Copyright 2024  Vittorio Piotti
 * Licensed under GPL v3.0 (https://github.com/vittorioPiotti/PathGraph-JavaFX/blob/main/LICENSE.txt)
 */

package com.vittoriopiotti.pathgraph.utilities;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.vittoriopiotti.pathgraph.constants.SvgConstants;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
/**
 * Utility class for creating various JavaFX UI components such as buttons, labels, text fields, and forms.
 * This class contains static methods to simplify the instantiation and styling of common UI elements.
 *
 * @author vittoriopiotti
 */
public class UtilitiesUI {
    /**
     * Creates a new {@link Slider} for controlling the zoom level.
     *
     * @param setZoom a consumer that accepts a {@code Double} value
     *                representing the zoom level set by the slider.
     * @param initialZoom the initial value of the zoom slider. Must be
     *                    between 0.2 and 5.
     * @return an instance of {@link Slider} configured with the specified
     *         settings.
     *
     * @throws IllegalArgumentException if {@code initialZoom} is not
     *                                  between 0.2 and 5.
     */
    public static Slider createSlider(Consumer<Double> setZoom,double initialZoom){
        Slider slider = new Slider(0.2, 5, 0.2);
        slider.setShowTickMarks(false);
        slider.setShowTickLabels(false);
        slider.setMajorTickUnit(0.1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);
        slider.setOrientation(Orientation.VERTICAL);
        slider.setValue(initialZoom);
        slider.setBlockIncrement(0.01);
        slider.setStyle("-fx-background-color: #f8f9fa; "
                + "-fx-border-color: #d3d4d5; "
                + "-fx-border-width: 0 1px 0 1px; "
                + "-fx-padding: 7px 0px 7px 0px; "
                + "-fx-background-radius: 0; "
                + "-fx-border-radius: 0;"
                + "-fx-pref-width: 35px; "
        );
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            setZoom.accept((double)newValue);
        });
        return slider;
    }
    /**
     * Creates a joystick control as a {@link Pane}, allowing for
     * two-dimensional input through dragging.
     *
     * @param callback a {@link BiConsumer} that accepts two {@code Double}
     *                 values representing the joystick's horizontal and
     *                 vertical movements, respectively. The values are
     *                 adjusted based on the direction of the joystick.
     *
     * @return a {@link Pane} containing the joystick and its handle.
     */
    public static Pane createJoystick(BiConsumer<Double, Double> callback) {
        final double JOYSTICK_RADIUS = 25;
        final double HANDLE_RADIUS = 17;
        final double ALLOWED_OVERFLOW = 6;
        Circle joystick = new Circle(JOYSTICK_RADIUS, Color.web("#f8f9fa"));
        joystick.setStroke(Color.web("#d3d4d5"));
        joystick.setStrokeWidth(1);
        joystick.setCenterX(JOYSTICK_RADIUS);
        joystick.setCenterY(JOYSTICK_RADIUS);
        Circle handle = new Circle(HANDLE_RADIUS, Color.WHITE);
        handle.setStroke(Color.web("#d3d4d5"));
        handle.setCenterX(JOYSTICK_RADIUS);
        handle.setCenterY(JOYSTICK_RADIUS);
        SVGPath svgDirection = new SVGPath();
        svgDirection.setContent(SvgConstants.SVG_DPAD);
        svgDirection.setFill(Color.BLACK);
        svgDirection.setLayoutX(handle.getCenterX() - 7.7);
        svgDirection.setLayoutY(handle.getCenterY() - 7.7);
        Pane pane = new Pane();
        pane.getChildren().addAll(joystick, handle, svgDirection);
        Timeline updateStateTimeline = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            double centerX = joystick.getCenterX();
            double centerY = joystick.getCenterY();
            double deltaX = handle.getCenterX() - centerX;
            double deltaY = handle.getCenterY() - centerY;
            double threshold = 5;
            boolean isTop = deltaY < -threshold;
            boolean isBot = deltaY > threshold;
            boolean isLeft = deltaX < -threshold;
            boolean isRight = deltaX > threshold;
            if (callback != null) {
                callback.accept(isRight ? -3.5 : isLeft ? 3.5 : 0, isTop ? 3.5 : isBot ? -2.5 : 0);
            }
        }));
        updateStateTimeline.setCycleCount(Timeline.INDEFINITE);
        handle.setOnMouseDragged(event -> {
            double centerX = joystick.getCenterX();
            double centerY = joystick.getCenterY();
            double deltaX = event.getX() - centerX;
            double deltaY = event.getY() - centerY;
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            double maxDistance = JOYSTICK_RADIUS - HANDLE_RADIUS + ALLOWED_OVERFLOW;
            if (distance > maxDistance) {
                double angle = Math.atan2(deltaY, deltaX);
                handle.setCenterX(centerX + maxDistance * Math.cos(angle));
                handle.setCenterY(centerY + maxDistance * Math.sin(angle));
            } else {
                handle.setCenterX(event.getX());
                handle.setCenterY(event.getY());
            }
            svgDirection.setLayoutX(handle.getCenterX() - 7.7);
            svgDirection.setLayoutY(handle.getCenterY() - 7.7);
            if (updateStateTimeline.getStatus() != Animation.Status.RUNNING) {
                updateStateTimeline.play();
            }
        });
        handle.setOnMouseReleased(event -> {
            updateStateTimeline.stop();
            Timeline timeline = new Timeline();
            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(16), e -> {
                double dx = joystick.getCenterX() - handle.getCenterX();
                double dy = joystick.getCenterY() - handle.getCenterY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                if (distance > 1) {
                    handle.setCenterX(handle.getCenterX() + dx * 0.15);
                    handle.setCenterY(handle.getCenterY() + dy * 0.15);
                } else {
                    handle.setCenterX(joystick.getCenterX());
                    handle.setCenterY(joystick.getCenterY());
                    timeline.stop();
                }
                svgDirection.setLayoutX(handle.getCenterX() - 7.7);
                svgDirection.setLayoutY(handle.getCenterY() - 7.7);
            });
            timeline.getKeyFrames().add(keyFrame);
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        });
        return pane;
    }
    /**
     * Creates a footer component consisting of a social media icon, a label, and copyright notes.
     * The footer supports hover and click animations and can open a specified URL when clicked.
     *
     * @param svgSocial      the SVG content for the social media icon to be displayed.
     * @param socialName     the name of the social media platform to be displayed alongside the icon.
     * @param copyrightNotes  the copyright notes to be displayed in the footer.
     * @param url            the URL to be opened when the social media icon is clicked.
     * @param maxWidth       the maximum width of the footer component.
     *
     * @return an {@link HBox} containing the footer components.
     */
    public static HBox createFooter(String svgSocial, String socialName, String copyrightNotes, String url, double maxWidth) {
        final boolean[] isClickActive = {false};
        Label ciaoLabel = new Label(socialName);
        ciaoLabel.setStyle("-fx-font: 12pt 'sans-serif';");
        SVGPath githubIcon = new SVGPath();
        githubIcon.setContent(svgSocial);
        githubIcon.setScaleX(1.5);
        githubIcon.setScaleY(1.5);
        StackPane iconContainer = new StackPane();
        iconContainer.setPrefSize(35, 35);
        iconContainer.setAlignment(Pos.CENTER);
        iconContainer.getChildren().add(githubIcon);
        HBox innerHbox = new HBox(0);
        innerHbox.getChildren().addAll(iconContainer, ciaoLabel);
        innerHbox.setAlignment(Pos.CENTER_LEFT);
        innerHbox.setCursor(Cursor.HAND);
        Rectangle verticalSeparator = new Rectangle(1.5, 20);
        verticalSeparator.setArcWidth(5);
        verticalSeparator.setArcHeight(5);
        verticalSeparator.setFill(Color.web("#404040"));
        HBox hbox = new HBox(15);
        hbox.setPadding(new Insets(20, 0, 0, 0));
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setMaxWidth(maxWidth);
        Label copyrightLabel = new Label(copyrightNotes);
        copyrightLabel.setTextFill(Color.web("#404040"));
        copyrightLabel.setStyle("-fx-font: 11pt 'sans-serif';");
        hbox.getChildren().addAll(innerHbox, verticalSeparator, copyrightLabel);
        ScaleTransition scaleUp = new ScaleTransition(Duration.seconds(0.3), innerHbox);
        scaleUp.setToX(1.1);
        scaleUp.setToY(1.1);
        ScaleTransition scaleDown = new ScaleTransition(Duration.seconds(0.3), innerHbox);
        scaleDown.setToX(1);
        scaleDown.setToY(1);
        innerHbox.setOnMouseEntered(event -> {
            if (!isClickActive[0]) {
                scaleUp.play();
                ciaoLabel.setTextFill(Color.web("#007bff"));
                githubIcon.setFill(Color.web("#007bff"));
            }
        });
        innerHbox.setOnMouseExited(event -> {
            if (!isClickActive[0]) {
                scaleDown.play();
                ciaoLabel.setTextFill(Color.BLACK);
                githubIcon.setFill(Color.BLACK);
            }
        });
        ScaleTransition scaleOut = new ScaleTransition(Duration.seconds(0.3), innerHbox);
        scaleOut.setToX(0);
        scaleOut.setToY(0);
        ScaleTransition scaleIn = new ScaleTransition(Duration.seconds(0.3), innerHbox);
        scaleIn.setToX(1);
        scaleIn.setToY(1);
        innerHbox.setOnMouseClicked(event -> {
            if (!isClickActive[0]) {
                isClickActive[0] = true;
                scaleOut.setOnFinished(e -> {
                    scaleIn.play();
                    scaleIn.setOnFinished(f -> {
                        try {
                            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
                        } catch (Exception ignored) {

                        }
                        isClickActive[0] = false;
                        if (innerHbox.isHover()) {
                            scaleUp.play();
                            ciaoLabel.setTextFill(Color.web("#007bff"));
                            githubIcon.setFill(Color.web("#007bff"));
                        } else {
                            ciaoLabel.setTextFill(Color.BLACK);
                            githubIcon.setFill(Color.BLACK);
                        }
                    });
                });
                scaleOut.play();
            }
        });
        return hbox;
    }
    /**
     * Creates a styled form component with a title and specified content.
     * The form has customizable dimensions and applies specific styles
     * for appearance and layout.
     *
     * @param minWidth    the minimum width of the form.
     * @param maxWidth    the maximum width of the form.
     * @param minHeight   the minimum height of the form.
     * @param maxHeight   the maximum height of the form.
     * @param title       the title of the form to be displayed at the top.
     * @param content     a {@link VBox} containing the content to be displayed
     *                    within the form.
     *
     * @return a {@link VBox} representing the styled form with the title and content.
     *
     * @throws IllegalArgumentException if the content is null or if the
     *                                  specified dimensions are invalid (e.g.,
     *                                  minWidth > maxWidth).
     */
    public static VBox createForm( double minWidth,double maxWidth, double minHeight,double maxHeight, String title, VBox content) {
        VBox form = new VBox();
        Label formTitle = new Label(title);
        formTitle.setPadding(new Insets(0, 0, 10, 0));
        formTitle.setStyle("-fx-font: bolder 18pt 'sans-serif';");
        form.setSpacing(15);
        form.setPadding(new Insets(0));
        form.setMinWidth(minWidth);
        form.setMaxWidth(maxWidth);
        form.setMinHeight(minHeight);
        form.setMaxHeight(maxHeight);
        form.getStyleClass().add("form-container");
        form.getChildren().addAll(formTitle,content);
        form.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-border-color: #dee2e6;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 16px;" +
                        "-fx-background-radius: 16px;" +
                        "-fx-padding: 30px 25px 25px 25px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 16, 0, 0, 8);"
        );
        return form;
    }
    /**
     * Creates a scrollable page with customizable dimensions and a structured layout.
     * The page consists of a content form, styled with padding and alignment, and
     * includes a footer with social media information and copyright notes.
     * <br>
     * The page is placed within a ScrollPane and automatically adjusts to fit its width
     * and height. The form content and footer are wrapped inside containers to ensure
     * consistent styling and positioning.
     *
     * @param primaryStage   the primary stage of the application, where the title is set.
     * @param title          the title to be displayed on the primary stage.
     * @param minWidth       the minimum width of the content form.
     * @param maxWidth       the maximum width of the content form.
     * @param minHeight      the minimum height of the content form.
     * @param maxHeight      the maximum height of the content form.
     * @param contentInputForm a {@link VBox} containing the content of the form to be displayed.
     * @param svgSocial      the SVG icon path for the social media footer.
     * @param socialName     the name of the social media platform to be displayed in the footer.
     * @param copyrightNotes the copyright notes to be included in the footer.
     * @param url            the URL to be linked in the footer's social media section.
     *
     * @return a {@link ScrollPane} representing the complete page with content and footer.
     *
     * @throws IllegalArgumentException if the contentInputForm is null or if the
     *                                  specified dimensions are invalid (e.g., minWidth > maxWidth).
     */
    public static ScrollPane createPage(
            Stage primaryStage,
            String title,
            double minWidth, double maxWidth,double minHeight, double maxHeight, VBox contentInputForm,
            String svgSocial, String socialName, String copyrightNotes, String url
    ) {
        ScrollPane page = new ScrollPane();
        primaryStage.setTitle(title);
        VBox inputForm = createForm( minWidth, maxWidth, minHeight, maxHeight, title, contentInputForm);
        StackPane containerForm = new StackPane();
        VBox outerContainerForm = new VBox(containerForm);
        VBox paddingContainer = new VBox(outerContainerForm);
        containerForm.setAlignment(Pos.CENTER);
        outerContainerForm.setAlignment(Pos.CENTER);
        paddingContainer.setPadding(new Insets(50));
        paddingContainer.setAlignment(Pos.CENTER);
        paddingContainer.setStyle("-fx-background-color: #f8f9fa");
        containerForm.getChildren().clear();
        inputForm.setMaxWidth(maxWidth);
        HBox footer = createFooter(svgSocial,socialName,copyrightNotes,url,maxWidth);
        HBox innerContainerForm = new HBox(25, inputForm);
        VBox vBox = new VBox(0,innerContainerForm,footer);
        vBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(inputForm, Priority.ALWAYS);
        innerContainerForm.setAlignment(Pos.TOP_CENTER);
        innerContainerForm.setFillHeight(false);
        containerForm.getChildren().add(vBox);
        page.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-border-color: transparent;"
        );
        page.setFitToWidth(true);
        page.setFitToHeight(true);
        page.setContent(paddingContainer);
        return page;
    }
    /**
     * Creates a customized {@link TextField} with specific properties and event handling.
     *
     * @param promptText   the text to display as a prompt when the field is empty.
     * @param numbers      if true, restricts input to numbers only.
     * @param strings      if true, restricts input to strings only.
     * @param focus        if true, the field will maintain focus when clicked.
     * @param maxLenght   the maximum length of text that can be entered in the field.
     * @param width        the width of the text field.
     * @param eventHandler a {@link BiConsumer} to handle mouse events, invoked on ENTER key press.
     * @param param        additional parameter passed to the event handler.
     * @param v1          the corner radius for the top-left corner of the text field's background.
     * @param v2          the corner radius for the top-right corner of the text field's background.
     * @param v3          the corner radius for the bottom-right corner of the text field's background.
     * @param v4          the corner radius for the bottom-left corner of the text field's background.
     * @param b1          the width of the border on the top side of the text field.
     * @param b2          the width of the border on the right side of the text field.
     * @param b3          the width of the border on the bottom side of the text field.
     * @param b4          the width of the border on the left side of the text field.
     *
     * @return a {@link TextField} with the specified properties and event handling.
     *
     * @throws IllegalArgumentException if the maximum length is less than zero.
     */
    public static TextField createTextField(String promptText, boolean numbers, boolean strings, boolean focus, int maxLenght, double width, BiConsumer<MouseEvent, Object> eventHandler, Object param, double v1, double v2, double v3, double v4, double b1, double b2, double b3, double b4) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setMinSize(width, 25);
        textField.setPrefSize(width, 25);
        textField.setMaxSize(width, 25);
        textField.setBackground(new Background(new BackgroundFill(
                Color.web("white"),
                new CornerRadii(v1, v2, v3, v4, false),
                Insets.EMPTY
        )));
        textField.setBorder(new Border(new BorderStroke(
                Color.web("#d3d4d5"),
                BorderStrokeStyle.SOLID,
                new CornerRadii(v1, v2, v3, v4, false),
                new BorderWidths(b1, b2, b3, b4)
        )));
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                eventHandler.accept(null, param);
            }
        });
        if (focus) {
            textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    Platform.runLater(textField::requestFocus);
                }
            });
        }
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (numbers && !strings) {
                if (!newValue.matches("\\d*")) {
                    textField.setText(newValue.replaceAll("\\D", ""));
                }
            }
            if (newValue.length() > maxLenght) {
                textField.setText(oldValue);
            } else {
                if (!numbers && strings) {
                    if (!newValue.matches("[a-zA-Z]*") || newValue.toUpperCase().matches(".*([a-zA-Z])\\1.*")) {
                        textField.setText(oldValue);
                    } else {
                        textField.setText(newValue.toUpperCase());
                    }
                }
            }
        });
        textField.setOnMouseClicked(event -> {
            textField.requestFocus();
            textField.deselect();
        });
        textField.selectionProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection.getLength() > 0) {
                textField.deselect();
            }
        });
        return textField;
    }
    /**
     * Creates a customized {@link Label} with specific properties and optional SVG content.
     *
     * @param textOrSVG the text to display on the label or the SVG content if {@code isSVG} is true.
     * @param width the preferred width of the label.
     * @param v the corner radius for the top-left corner of the label's background.
     * @param v1 the corner radius for the top-right corner of the label's background.
     * @param v2 the corner radius for the bottom-right corner of the label's background.
     * @param v3 the corner radius for the bottom-left corner of the label's background.
     * @param b the width of the border on the top side of the label.
     * @param b1 the width of the border on the right side of the label.
     * @param b2 the width of the border on the bottom side of the label.
     * @param b3 the width of the border on the left side of the label.
     * @param isSVG if true, {@code textOrSVG} is treated as SVG content; otherwise, it is treated as text.
     *
     * @return a {@link Label} with the specified properties and content.
     */
    public static Label createLabel(String textOrSVG, double width, double v, double v1, double v2, double v3, double b, double b1, double b2, double b3, boolean isSVG) {
        Label label = new Label();
        label.setPrefWidth(width);
        label.setPrefHeight(25);
        label.setMinWidth(width);
        label.setMinHeight(25);
        label.setMaxWidth(width);
        label.setMaxHeight(25);
        label.setBackground(new Background(new BackgroundFill(Color.web("#f8f9fa"),
                new CornerRadii(v, v1, v2, v3, false), Insets.EMPTY)));
        label.setBorder(new Border(new BorderStroke(Color.web("#d3d4d5"), BorderStrokeStyle.SOLID,
                new CornerRadii(v, v1, v2, v3, false), new BorderWidths(b, b1, b2, b3))));
        label.setStyle("-fx-alignment: center;-fx-text-fill: #595959;");
        if (isSVG) {
            SVGPath svg = new SVGPath();
            svg.setContent(textOrSVG);
            svg.setFill(Color.BLACK);
            svg.setStrokeWidth(1);
            label.setGraphic(svg);
        } else {
            label.setText(textOrSVG);
        }
        return label;
    }
    /**
     * Creates a customized {@link Button} with optional SVG graphics and event handling.
     *
     * @param svgContent1 the SVG content for the primary graphic of the button.
     * @param svgContent2 the SVG content for a secondary graphic; can be {@code null} or empty.
     * @param color the fill color for the SVG graphics.
     * @param width the preferred width of the button.
     * @param v the corner radius for the top-left corner of the button's background.
     * @param v1 the corner radius for the top-right corner of the button's background.
     * @param v2 the corner radius for the bottom-right corner of the button's background.
     * @param v3 the corner radius for the bottom-left corner of the button's background.
     * @param b the width of the border on the top side of the button.
     * @param b1 the width of the border on the right side of the button.
     * @param b2 the width of the border on the bottom side of the button.
     * @param b3 the width of the border on the left side of the button.
     * @param eventHandler an event handler that can be either a {@link BiConsumer} for mouse events or a {@link Runnable} for general actions.
     * @param param additional parameter to be passed to the event handler.
     *
     * @return a {@link Button} with the specified SVG graphics, properties, and event handling.
     *
     * @throws IllegalArgumentException if any of the SVG content is invalid or if the width is less than 0.
     */
    public static Button createButton(String svgContent1, String svgContent2, Color color, double width, double v, double v1, double v2, double v3, double b, double b1, double b2, double b3, Object eventHandler, Object param) {
        SVGPath svg1 = new SVGPath();
        svg1.setContent(svgContent1);
        svg1.setFill(color);
        Node graphic;
        if (svgContent2 == null || svgContent2.isEmpty()) {
            graphic = svg1;
        } else {
            SVGPath svg2 = new SVGPath();
            svg2.setContent(svgContent2);
            svg2.setFill(color);
            VBox svgContainer = new VBox(5);
            svgContainer.getChildren().addAll(svg1, svg2);
            graphic = svgContainer;
        }
        Button button = new Button();
        button.setGraphic(graphic);
        button.setBackground(new Background(new BackgroundFill(Color.web("#f8f9fa"),
                new CornerRadii(v, v1, v2, v3, false), Insets.EMPTY)));
        button.setPrefWidth(width);
        button.setPrefHeight(svgContent2 == null || svgContent2.isEmpty() ? 25 : 50); // Altezza in base al numero di SVG
        button.setMinWidth(width);
        button.setMinHeight(svgContent2 == null || svgContent2.isEmpty() ? 25 : 50);
        button.setMaxWidth(width);
        button.setMaxHeight(svgContent2 == null || svgContent2.isEmpty() ? 25 : 50);
        button.setBorder(new Border(new BorderStroke(Color.web("#d3d4d5"), BorderStrokeStyle.SOLID,
                new CornerRadii(v, v1, v2, v3, false), new BorderWidths(b, b1, b2, b3))));
        button.setOnMouseEntered((e) -> {
            button.setBackground(new Background(new BackgroundFill(Color.web("#d3d4d5"),
                    new CornerRadii(v, v1, v2, v3, false), Insets.EMPTY)));
        });
        button.setOnMouseExited((e) -> {
            button.setBackground(new Background(new BackgroundFill(Color.web("#f8f9fa"),
                    new CornerRadii(v, v1, v2, v3, false), Insets.EMPTY)));
        });
        if (eventHandler instanceof BiConsumer) {
            @SuppressWarnings("unchecked")
            BiConsumer<MouseEvent, Object> biConsumer = (BiConsumer<MouseEvent, Object>) eventHandler;
            button.setOnMouseClicked((mouseEvent) -> biConsumer.accept(mouseEvent, param));
        } else if (eventHandler instanceof Runnable runnable) {
            button.setOnMouseClicked(event -> runnable.run());
        }
        return button;
    }

}
