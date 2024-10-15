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




package com.vittoriopiotti.pathgraph.containers;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.vittoriopiotti.pathgraph.constants.AppConstants;
import com.vittoriopiotti.pathgraph.constants.SvgConstants;
import com.vittoriopiotti.pathgraph.dto.GraphDTO;
import com.vittoriopiotti.pathgraph.dto.NodeDTO;
import com.vittoriopiotti.pathgraph.graph.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import com.vittoriopiotti.pathgraph.utilities.UtilitiesUI;


/**
 * The PathGraphUI class extends {@link PathGraph} to provide a user interface
 * layer on top of the graph management functionalities. It allows for the
 * interaction with the graph through a visual interface, enabling features
 * such as zooming and dragging while integrating user-driven events.
 * This class is specifically designed to be the user-facing aspect of
 * the graph, making it easier for applications to manage user interactions
 * with the underlying graph logic.
 *
 * @param <V> The type of the graph's nodes.
 * @param <E> The type of the graph's edges.
 *
 * @author vittoripiotti
 */
public class PathGraphUI<V,E> extends PathGraph<E,V> {
    /**
     * Context menu that is currently open. Initially set to null.
     */
    private ContextMenu openContextMenu = null;

    /**
     * Flag indicating if dragging is currently active.
     */
    private boolean dragged = false;

    /**
     * The X-coordinate where the drag started.
     */
    private double startX;

    /**
     * The Y-coordinate where the drag started.
     */
    private double startY;

    /**
     * Constant representing the "open menu" action state.
     */
    private final int OPEN_MENU = 0;

    /**
     * Constant representing the state waiting for a click action.
     */
    private final int WAIT_CLICK = 1;

    /**
     * Constant representing the state waiting for user input.
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final int WAIT_INPUT = 2;

    /**
     * The current action state when a node is clicked. Default is OPEN_MENU.
     */
    private int clickNodeAction = OPEN_MENU;

    /**
     * The last X-coordinate where the arrow menu was displayed.
     */
    private double lastArrowMenuX = 0;

    /**
     * The last Y-coordinate where the arrow menu was displayed.
     */
    private double lastArrowMenuY = 0;

    /**
     * The inbound vertex associated with an edge.
     */
    private Vertex<E> vertexInbound;

    /**
     * The outbound vertex associated with an edge.
     */
    private Vertex<E> vertexOutbound;

    /**
     * Text field for user input.
     */
    private TextField textField;

    /**
     * Second text field for additional input. It is final and initialized elsewhere.
     */
    private final TextField textFieldSecond;

    /**
     * Third text field for user input.
     */
    private TextField textFieldThird;

    /**
     * HBox container for holding buttons, identified as buttonContainer7.
     */
    private final HBox buttonContainer7;

    /**
     * HBox container for holding buttons, identified as buttonContainer72.
     */
    private final HBox buttonContainer72;

    /**
     * HBox container for holding buttons, identified as buttonContainer9.
     */
    private final HBox buttonContainer9;

    /**
     * VBox container for holding buttons, identified as buttonContainer2.
     */
    private final VBox buttonContainer2;

    /**
     * HBox container for holding buttons, identified as buttonContainer3.
     */
    private final HBox buttonContainer3;

    /**
     * VBox container for holding buttons, identified as buttonContainer5.
     */
    private final VBox buttonContainer5;

    /**
     * VBox container for holding buttons, identified as buttonContainer6.
     */
    private final VBox buttonContainer6;

    /**
     * VBox container for holding buttons, identified as buttonContainer8.
     */
    private final VBox buttonContainer8;

    /**
     * Button component, identified as btn22.
     */
    private Button btn22;

    /**
     * Button component, identified as btn52.
     */
    private Button btn52;

    /**
     * Button component, identified as btn6.
     */
    private Button btn6;

    /**
     * Constant representing the state for creating a cost.
     */
    private final int CREATE_COST = 0;

    /**
     * Constant representing the state for renaming a cost.
     */
    private final int RENAME_COST = 1;

    /**
     * Constant representing the state for renaming a node.
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final int RENAME_NODE = 2;

    /**
     * Runnable to hide the context menu if it is showing and the current action is not WAIT_CLICK.
     */
    private final Runnable hideContextMenu = () -> {
        if (clickNodeAction != WAIT_CLICK) {
            try {
                if (openContextMenu.isShowing()) {
                    openContextMenu.hide();
                }
            } catch (Exception ignored) {
            }
        }
    };

    /**
     * BiConsumer for handling node creation, triggered by a mouse event.
     * Calls the {@link #newNode(MouseEvent)} method with the event parameter.
     */
    private final BiConsumer<MouseEvent, Object> newNode = (event, param) -> newNode(event);

    /**
     * Initiates the low-level edge creation process upon receiving a Vertex parameter.
     * It disables connections with the inbound vertex.
     *
     * @param param The Vertex instance involved in the low-level edge creation.
     */
    @SuppressWarnings("unchecked")
    private void newEdge(Object param) {
        if (param instanceof Vertex) {
            hideContextMenu.run();
            vertexInbound = (Vertex<E>) param;
            boolean flag = this.disabledConnectionsWithNode(vertexInbound);
            clickNodeAction = WAIT_CLICK;
        }
    }

    /**
     * BiConsumer for handling low-level edge creation.
     * Calls the {@link #newEdge(Object)} method with the parameter.
     */
    private final BiConsumer<MouseEvent, Object> lowLevelNewEdge = (event, param) -> newEdge(param);

    /**
     * Runnable for creating a high-level edge.
     * Calls the {@link #newEdge()} method.
     */
    private final Runnable highLevelNewEdge = this::newEdge;


    /**
     * BiConsumer for handling low-level node deletion.
     * Calls the {@link #deleteNode(Object)} method with the parameter.
     */
    private final BiConsumer<MouseEvent, Object> lowLevelDeleteNode = (event, param) -> deleteNode(param);

    /**
     * BiConsumer for handling low-level edge deletion.
     * Calls the {@link #deleteEdge(Object)} method with the parameter.
     */
    private final BiConsumer<MouseEvent, Object> lowLevelDeleteEdge = (event, param) -> deleteEdge(param);

    /**
     * BiConsumer for handling edge direction changes at a low level.
     * Calls the {@link #rotateEdge(Object)} method with the parameter.
     */
    private final BiConsumer<MouseEvent, Object> lowLevelRotateEdge = (event, param) -> rotateEdge(param);

    /**
     * BiConsumer for handling edge splitting at a low level.
     * Calls the {@link #splitEdge(Object)} method with the parameter.
     */
    private final BiConsumer<MouseEvent, Object> lowLevelSplitEdge = (event, param) -> splitEdge(param);

    /**
     * BiConsumer for handling edge renaming operations.
     * Calls the {@link #lowLevelEdgeRenameHandler(Object)} method with the parameter.
     */
    private final BiConsumer<MouseEvent, Object> lowLevelRenameEdgeHandler = (event, param) -> lowLevelEdgeRenameHandler(param);

    /**
     * BiConsumer for handling pre-rename operations for an edge.
     * Calls the {@link #lowLevelEdgeRenameBefore(Object)} method with the parameter.
     */
    private final BiConsumer<MouseEvent, Object> lowLevelEdgeRenameBefore = (event, param) -> lowLevelEdgeRenameBefore(param);

    /**
     * BiConsumer for handling post-rename operations for an edge.
     * Calls the {@link #lowLevelEdgeRenameAfter()} method.
     */
    private final BiConsumer<MouseEvent, Object> lowLevelEdgeRanameAfter = (event, param) -> lowLevelEdgeRenameAfter();

    /**
     * Runnable for stopping the current element action.
     * Calls the {@link #stopElementAction()} method.
     */
    private final Runnable stopElementAction = this::stopElementAction;


    /**
     * Runnable for toggling the visibility of the UI.
     * Calls the {@link #toggleUI()} method.
     */
    private final Runnable toggleUI = this::toggleUI;

    /**
     * Runnable for renaming an element.
     * Calls the {@link #renameElement()} method.
     */
    private final Runnable renameElement = this::renameElement;

    /**
     * The primary stage of the application, used for setting and managing the main window.
     */
    private final Stage primaryStage;

    /**
     * Label for displaying or inputting information about elements.
     */
    private final Label labelInputElement;

    /**
     * Container for zoom control buttons, using a {@link VBox}.
     */
    private final VBox zoomButtonsContainer;

    /**
     * General container for holding buttons, using a {@link VBox}.
     */
    private final VBox buttonContainer;

    /**
     * Slider component for adjusting zoom or other parameters.
     */
    private final Slider slider;

    /**
     * Variable indicating whether the UI is currently hidden. Default is true.
     */
    private boolean isHide = true;

    /**
     * Array representing the hide status of various UI menus.
     * Each index corresponds to a specific menu's visibility state:
     * - [0]: top-left menu
     * - [1]: bottom-left menu
     * - [2]: bottom-mid menu
     * - [3]: right-mid menu
     * - [4]: top-right menu
     */
    private final boolean[] isHideMenus = {
            false, // top-left menu visibility
            false, // bottom-left menu visibility
            false, // bottom-mid menu visibility
            false, // right-mid menu visibility
            false  // top-right menu visibility
    };

    /**
     * Array representing the enabled state of various UI menus.
     * Each index corresponds to a specific menu's enabled state:
     * - [0]: top-left menu
     * - [1]: bottom-left menu
     * - [2]: bottom-mid menu
     * - [3]: right-mid menu
     * - [4]: top-right menu
     */
    private boolean[] isEnabledMenus = {
            true,  // top-left menu enabled
            true,  // bottom-left menu enabled
            true,  // bottom-mid menu enabled
            true,  // right-mid menu enabled
            true   // top-right menu enabled
    };


    /**
     * Constructor for the PathGraphUI class that extends a Pane component for displaying and managing an interactive graph
     * with zoom, drag, and various operations on nodes and edges.
     *
     * @param primaryStage The main {@link Stage} of the application.
     * @param scene The current {@link Scene} where the graph is displayed.
     * @param g The graph to be displayed, represented as an instance of {@link Graph} with nodes and edges as {@link String}.
     * @param topLeft A boolean indicating whether the top-left control panel is visible.
     * @param botLeft A boolean indicating whether the bottom-left control panel is visible.
     * @param botMid A boolean indicating whether the bottom-center control panel is visible.
     * @param rightMid A boolean indicating whether the right-center control panel is visible.
     * @param topRight A boolean indicating whether the top-right control panel is visible.
     * @param isHide A boolean indicating whether the control panels should be initially hidden.
     */
    public PathGraphUI(
            Stage primaryStage,
            Scene scene,
            Graph<String,
            String> g,
            boolean topLeft,
            boolean botLeft,
            boolean botMid,
            boolean rightMid,
            boolean topRight,
            boolean isHide
    ) {
        super(g);
        this.primaryStage = primaryStage;
        this.setAllCallbacks(
                hideContextMenu,
                this::setOnClickArrow,
                this::setOnClickNode,
                this::setOnClickBackground,
                this::changeZoom,
                ()->adjustPosition(0.5)
        );
        final BiConsumer<Double, Double> doDrag = this::doDrag;
        final Consumer<Double> setZoom = this::doZoom;
        final Runnable uploadJSON = this::uploadJSON_;
        final Runnable downloadJSON = () -> downloadJSON(this.getScene().getWindow());
        final Runnable newElement = this::newElement;
        final Runnable splitEdge = this::splitEdge;
        final Runnable rotateEdge = this::rotateEdge;
        final Runnable deleteElement = this::deleteElement;
        final Runnable renameElementHandler = this::renameElementHandler;
        final Runnable takeScreenshot = this::takeScreenshot;
        final Runnable adjustPosition = () -> adjustPosition(0.5);
        final Runnable clearGraph = this::clearGraphAndResetSlider;
        final Runnable showPath = this::showPath;


        slider = UtilitiesUI.createSlider(setZoom,this.getZoomFactor());
        Label label = UtilitiesUI.createLabel(SvgConstants.SVG_ZOOM_IN, 35, 5, 5, 0, 0, 1, 1, 0, 1, true);
        Label label2 = UtilitiesUI.createLabel(SvgConstants.SVG_ZOOM_OUT, 35, 0, 0, 5, 5, 0, 1, 1, 1, true);
        buttonContainer = new VBox(0,label,slider,label2);
        Button btnEraser = UtilitiesUI.createButton(SvgConstants.SVG_ERASER, "",Color.BLACK, 35, 5, 5, 5, 5, 1, 1, 1, 1, clearGraph, null);
        zoomButtonsContainer = new VBox(0,btnEraser);
        Pane joystick = UtilitiesUI.createJoystick(doDrag);
        buttonContainer2 = new VBox(0, joystick);
        Button btnDownload = UtilitiesUI.createButton(SvgConstants.SVG_FILETYPE_JSON, SvgConstants.SVG_DOWNLOAD, Color.BLACK, 35, 0, 5, 5, 0, 1, 1, 1, 0, downloadJSON, null);
        Button btnUpload = UtilitiesUI.createButton(SvgConstants.SVG_FILETYPE_JSON, SvgConstants.SVG_UPLOAD, Color.BLACK, 35, 5, 0, 0, 5, 1, 0, 1, 1, uploadJSON, null);
        buttonContainer3 = new HBox(0, btnUpload, btnDownload);
        Button btnCenter = UtilitiesUI.createButton(SvgConstants.SVG_GEO_ALT,"", Color.BLACK, 35, 5, 5, 5, 5, 1, 1, 1, 1, adjustPosition, null);
        buttonContainer5 = new VBox(0, btnCenter);
        Button btnPNG = UtilitiesUI.createButton(SvgConstants.SVG_CAMERA, "",Color.BLACK, 35, 5, 5, 5, 5, 1, 1, 1, 1, takeScreenshot, null);
        buttonContainer6 = new VBox(0, btnPNG);
        textFieldSecond = UtilitiesUI.createTextField("AB",false, true, false, 2, 35, null, null, 5, 0, 0, 5, 1, 0, 1, 1);
        Button btn1 = UtilitiesUI.createButton(SvgConstants.SVG_PLUS, "",Color.GREEN, 35, 0, 0, 0, 0, 1, 0, 0, 1, newElement, null);
        Button btn2 = UtilitiesUI.createButton(SvgConstants.SVG_MINUS,"", Color.RED, 35, 0, 0, 0, 0, 1, 0, 0, 0, deleteElement, null);
        Button btn5 = UtilitiesUI.createButton(SvgConstants.SVG_ROCKET_TAKEOFF, "",Color.web("#0466c8"), 35, 0, 5, 0, 0, 1, 1, 0, 0, showPath, null);
        buttonContainer7 = new HBox(0, textFieldSecond, btn1, btn2, btn5);
        labelInputElement = UtilitiesUI.createLabel("", 35, 5, 0, 0, 5, 1, 1, 1, 1,false);
        textFieldThird = UtilitiesUI.createTextField("123",true, false, true, 4, 70, null, null, 0, 0, 0, 0, 1, 1, 1, 0);
        btn22 = UtilitiesUI.createButton(SvgConstants.SVG_CHECK,"", Color.GREEN, 35, 0, 0, 0, 0, 1, 0, 1, 0, highLevelNewEdge, null);
        btn52 = UtilitiesUI.createButton(SvgConstants.SVG_X_XL,"", Color.RED, 35, 0, 5, 5, 0, 1, 1, 1, 0, stopElementAction, null);
        buttonContainer72 = new HBox(0, labelInputElement, textFieldThird, btn22, btn52);
        Button btn3 = UtilitiesUI.createButton(SvgConstants.SVG_ARROW_EXPAND,"", Color.BLACK, 35, 0, 0, 0, 5, 0, 0, 1, 1, splitEdge, null);
        Button btn4 = UtilitiesUI.createButton(SvgConstants.SVG_ARROW_CLOCKWISE,"", Color.BLACK, 35, 0, 0, 0, 0, 0, 0, 1, 0, rotateEdge, null);
        Button btn8 = UtilitiesUI.createButton(SvgConstants.SVG_PENCIL, "",Color.BLACK, 35, 0, 0, 5, 0, 0, 1, 1, 0, renameElementHandler, null);
        buttonContainer9 = new HBox(0, btn3, btn4, btn8);
        btn6 = UtilitiesUI.createButton(SvgConstants.SVG_EYE, "",Color.BLACK, 35, 5, 5, 5, 5, 1, 1, 1, 1, toggleUI, null);
        buttonContainer8 = new VBox(0, btn6);


        this.widthProperty().addListener((obs, oldVal, newVal) -> {
            adjustAllOnePosition(newVal.doubleValue(),true);
        });
        this.heightProperty().addListener((obs, oldVal, newVal) -> {
            adjustAllOnePosition(newVal.doubleValue(),false);
        });


        setUI(topLeft,botLeft,botMid,rightMid,topRight,isHide);


        primaryStage.fullScreenProperty().addListener((observable, oldValue, newValue) -> {
            hideContextMenu.run();
        });
        String css = Objects.requireNonNull(getClass().getResource("/dropdown.css")).toExternalForm();
        if (css != null) {
            scene.getStylesheets().add(css);
        }
    }

    /**
     * Constructor for the PathGraphUI class, providing an interactive graph UI with zoom, drag, and various controls.
     * This constructor allows specifying visibility for different control panels and whether to start in hidden mode.
     *
     * @param primaryStage The main {@link Stage} of the application.
     * @param scene The current {@link Scene} where the graph is displayed.
     * @param topLeft A boolean indicating whether the top-left control panel is visible.
     * @param botLeft A boolean indicating whether the bottom-left control panel is visible.
     * @param botMid A boolean indicating whether the bottom-center control panel is visible.
     * @param rightMid A boolean indicating whether the right-center control panel is visible.
     * @param topRight A boolean indicating whether the top-right control panel is visible.
     * @param isHide A boolean indicating whether the control panels should be initially hidden.
     */
    public PathGraphUI(
            Stage primaryStage,
            Scene scene,
            boolean topLeft,
            boolean botLeft,
            boolean botMid,
            boolean rightMid,
            boolean topRight,
            boolean isHide

    ) {
        this(
                primaryStage,
                scene,
                new DigraphEdgeList<>(),
                topLeft,
                botLeft,
                botMid,
                rightMid,
                topRight,
                isHide
        );
    }


    /**
     * Default constructor for the PathGraphUI class with all control panels visible and not hidden initially.
     *
     * @param primaryStage The main {@link Stage} of the application.
     * @param scene The current {@link Scene} where the graph is displayed.
     */
    public PathGraphUI(Stage primaryStage, Scene scene) {
        this(
                primaryStage,
                scene,new DigraphEdgeList<>(),
                true,
                true,
                true,
                true,
                true,
                false
        );
    }



    /**
     * Sets the visibility of various UI elements based on the provided parameters and controls the initial hide state.
     *
     * @param topLeft  Enables or disables the top-left menu.
     * @param botLeft  Enables or disables the bottom-left menu.
     * @param botMid   Enables or disables the bottom-middle menu.
     * @param rightMid Enables or disables the right-middle menu.
     * @param topRight Enables or disables the top-right menu.
     * @param isHide   If true, the UI is hidden initially; otherwise, it is shown.
     */
    public void setUI(boolean topLeft, boolean botLeft, boolean botMid, boolean rightMid, boolean topRight, boolean isHide){
        isEnabledMenus = new boolean[]{
                /* is enabled top-left menu */
                topLeft,
                /* is enabled bot-left menu */
                botLeft,
                /* is enabled bot-mid menu */
                botMid,
                /* is enabled right-mid menu */
                rightMid,
                /* is enabled top-right menu */
                topRight
        };
        if (isHide) {
            hideUI();
        } else {
            showUI();
        }
    };


    /**
     * Shows the UI if it is currently hidden.
     */
    public void showUI() {
        if(isHide){
            toggleUI();
        }
    };

    /**
     * Hides the UI if it is currently visible.
     */
    public void hideUI(){
        if(!isHide){
            toggleUI();
        }
    };


    /**
     * Toggles the visibility of the UI elements. Updates the button icon and adjusts the visibility
     * of different control panels and buttons based on their enabled state. Also, handles animations and layout adjustments.
     */
    public void toggleUI() {
        buttonContainer8.getChildren().remove(btn6);
        btn6 = UtilitiesUI.createButton(isHide ? SvgConstants.SVG_EYE : SvgConstants.SVG_EYE_SLASH, "",Color.BLACK, 35, 5, 5, 5, 5, 1, 1, 1, 1, toggleUI, null);
        buttonContainer8.getChildren().add(btn6);
        List<Node> nodesToAdd = new ArrayList<>();
        List<Node> nodesToRemove = new ArrayList<>();
        if (isEnabledMenus[0] && isHide != isHideMenus[0]) {
            nodesToAdd.addAll(Arrays.asList(buttonContainer7, buttonContainer9, buttonContainer72));
        } else {
            nodesToRemove.addAll(Arrays.asList(buttonContainer7, buttonContainer9, buttonContainer72));
        }
        if (isEnabledMenus[1] && isHide != isHideMenus[1]) {
            nodesToAdd.add(buttonContainer3);
        } else {
            nodesToRemove.add(buttonContainer3);
        }
        if (isEnabledMenus[2] && (isHide != isHideMenus[2])) {
            nodesToAdd.addAll(Arrays.asList(zoomButtonsContainer, buttonContainer2, buttonContainer5, buttonContainer6));
        } else {
            nodesToRemove.addAll(Arrays.asList(zoomButtonsContainer, buttonContainer2, buttonContainer5, buttonContainer6));
        }
        if (isEnabledMenus[3] && (isHide != isHideMenus[3])) {
            nodesToAdd.add(buttonContainer);
        } else {
            nodesToRemove.add(buttonContainer);
        }
        if (isEnabledMenus[4] && (!isHideMenus[4])) {
            nodesToAdd.add(buttonContainer8);
        } else {
            nodesToRemove.add(buttonContainer8);
        }
        updateWithFadeParallel(this, nodesToAdd, nodesToRemove);
        this.getChildren().remove(buttonContainer72);
        Platform.runLater(() -> {
            adjustAllOnePosition(this.getWidth(),true);
            adjustAllOnePosition(this.getHeight(),false);
        });
        isHide = !isHide;
    }



    /**
     * Changes the zoom level of the graph by updating the slider value.
     *
     * @param zoom The new zoom level to be set.
     */
    private void changeZoom(double zoom){
        slider.setValue(zoom);
    }

    /**
     * Clears the graph and resets the slider to its initial state.
     */
    private void clearGraphAndResetSlider() {
        clearGraph();
    }

    /**
     * Uploads a JSON file and processes the graph data from it.
     */
    private void uploadJSON_(){
        int status = uploadJSON(this.getScene().getWindow());
    }

    /**
     * Displays the path between two nodes if a valid input is provided. Sets up mouse event listeners
     * to reset styles if no dragging action occurs.
     */
    private void showPath() {
        String startEnd = textFieldSecond.getText();
        if (!startEnd.isEmpty()) {



            hideContextMenu.run();
            clickNodeAction = OPEN_MENU;
            if (startEnd.length() == 2) {
                GraphDTO g = this.getGraph();
                List<NodeDTO> lpn = g.findPath(startEnd.charAt(0), startEnd.charAt(1));
                boolean flag = this.showPath(lpn);
                if (flag) {
                    this.setOnMousePressed((MouseEvent event) -> {
                        dragged = false;
                        startX = event.getScreenX();
                        startY = event.getScreenY();
                    });
                    this.setOnMouseReleased((MouseEvent event) -> {
                        double endX = event.getScreenX();
                        double endY = event.getScreenY();
                        double distance = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
                        if (distance > 5) {
                            dragged = true;
                        }
                        if (!dragged) {
                            this.resetStyles();
                        }
                        event.consume();
                    });
                }
            }
        }
    }

    /**
     * Adjusts the position of a Pane component either horizontally or vertically based on the given parameters.
     *
     * @param p The {@link Pane} whose position needs to be adjusted.
     * @param pos The position value to set (X or Y based on the flag).
     * @param isX A boolean flag indicating whether to adjust the X position (true) or Y position (false).
     */
    private void adjustOnePosition(Pane p, double pos, boolean isX){
        if (p.getHeight() > 0 && p.getWidth() > 0) {
            if(isX){
                p.setLayoutX(pos);
            }else{
                p.setLayoutY(pos);
            }
        }
    }


    /**
     * Updates the parent pane by fading in nodes that need to be added and fading out nodes that need to be removed.
     * Transitions are applied in parallel.
     *
     * @param parentPane The parent pane where nodes are added or removed.
     * @param nodesToAdd List of nodes to be added to the parent pane with a fade-in effect.
     * @param nodesToRemove List of nodes to be removed from the parent pane with a fade-out effect.
     */
    private void updateWithFadeParallel(Pane parentPane, List<Node> nodesToAdd, List<Node> nodesToRemove) {
        boolean hasChanges = false;
        ParallelTransition parallelTransition = new ParallelTransition();
        for (Node node : nodesToAdd) {
            if (!parentPane.getChildren().contains(node)) {
                node.setOpacity(0.0);
                parentPane.getChildren().add(node); // Aggiungi il nodo al pannello
                hasChanges = true;
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), node);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                parallelTransition.getChildren().add(fadeIn);
            }
        }
        for (Node node : nodesToRemove) {
            if (parentPane.getChildren().contains(node)) {
                hasChanges = true;
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), node);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(event -> parentPane.getChildren().remove(node));
                parallelTransition.getChildren().add(fadeOut);
            }
        }
        if (hasChanges) {
            parallelTransition.play();
        }
    }

    /**
     * Adjusts the position of various containers within the UI based on the provided position and state (horizontal or vertical).
     *
     * @param pos The position value (width or height) used for adjusting components.
     * @param state If true, adjusts components horizontally; if false, adjusts vertically.
     */
    private void adjustAllOnePosition(double pos,boolean state){
        adjustOnePosition(zoomButtonsContainer, state ? (pos - zoomButtonsContainer.getWidth())/2 - 53  : pos - zoomButtonsContainer.getHeight() - 23, state);
        adjustOnePosition(buttonContainer,      state ? pos - buttonContainer.getWidth() - 10           : (pos - buttonContainer.getHeight()) / 2,      state);
        adjustOnePosition(buttonContainer3,     state ? 10                                              : pos - buttonContainer3.getHeight() - 10,      state);
        adjustOnePosition(buttonContainer5,     state ? (pos - buttonContainer5.getWidth()) / 2 + 53    : pos - buttonContainer5.getHeight() - 23,      state);
        adjustOnePosition(buttonContainer6,     state ? (pos - buttonContainer6.getWidth()) / 2 + 98    : pos - buttonContainer6.getHeight() - 23,      state);
        adjustOnePosition(buttonContainer8,     state ? pos - buttonContainer8.getWidth() - 10          : 10,   state);
        adjustOnePosition(buttonContainer2,     state ? (pos - buttonContainer2.getWidth()) / 2         : pos - buttonContainer2.getHeight() - 10,      state);
        adjustOnePosition(buttonContainer9,     state ? 45 : 35, state);
        adjustOnePosition(buttonContainer72,10, state);
        adjustOnePosition(buttonContainer7, 10, state);
    }

    /**
     * Handles background clicks to show a context menu. If the event is a right-click or a double-click,
     * it opens a menu for node creation.
     *
     * @param event The mouse event that triggers this action.
     */
    private void onClickBackground(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY || event.getClickCount() == 2) {
            hideContextMenu.run();
            Button button = UtilitiesUI.createButton(SvgConstants.SVG_PLUS,"", Color.GREEN, 35, 5, 5, 5, 5, 1, 1, 1, 1, newNode, null);
            CustomMenuItem item = new CustomMenuItem(button);
            item.setHideOnClick(false);
            ContextMenu backgroundMenu = new ContextMenu();
            backgroundMenu.getItems().add(item);
            backgroundMenu.show(this, event.getScreenX() + 10, event.getScreenY());
            forceOpenContextMenu(backgroundMenu, event.getScreenX() + 10, event.getScreenY());
            openContextMenu = backgroundMenu;
        } else {
            hideContextMenu.run();
        }
    }

    /**
     * Handles background clicks based on the current action. If the action is set to open a menu,
     * it calls {@link #onClickBackground(MouseEvent)}; otherwise, it activates all nodes and resets the action.
     *
     * @param event The mouse event that triggers this action.
     */
    private void setOnClickBackground(MouseEvent event) {
        if (clickNodeAction == OPEN_MENU) {
            onClickBackground(event);
        } else {
            this.activeAllNodes();
            clickNodeAction = OPEN_MENU;
            hideContextMenu.run();
        }
        event.consume();
    }

    /**
     * Handles clicks on nodes. If the action is set to open a menu, it opens the node menu;
     * otherwise, it creates a new edge starting from the clicked node.
     *
     * @param event The mouse event that triggers this action.
     * @param vertex The vertex associated with the node being clicked.
     */
    private void setOnClickNode(MouseEvent event, Vertex<E> vertex) {
        if (clickNodeAction == OPEN_MENU) {
            openNodeMenu(event, vertex);
        } else {
            createNewEdge(event, vertex);
        }
        event.consume();
    }

    /**
     * Renames the edge based on the value entered in the text field. If the value is valid and the edge is an instance of {@link Edge},
     * the method performs the renaming. The context menu is hidden afterward.
     *
     * @param edge The edge object to be renamed.
     */
    @SuppressWarnings("unchecked")
    private void lowLevelEdgeRenameBefore(Object edge) {
        if (edge instanceof Edge) {
            String inputValue = textField.getText();
            if (!inputValue.isEmpty() && Integer.parseInt(inputValue) > 0) {
                boolean flag = this.renameEdge((Edge<V, E>) edge,  Integer.parseInt(inputValue));
            }
            hideContextMenu.run();
        }
    }

    /**
     * Renames an edge after creation at a low level.
     * Sets the click action on the node to {@link #OPEN_MENU}, hides the context menu, and
     * attempts to create an edge between the outbound and inbound vertices with the provided length.
     *
     * @throws NumberFormatException if the content of {@code textField} cannot be converted to an integer.
     */
    private void lowLevelEdgeRenameAfter() {
        clickNodeAction = OPEN_MENU;
        hideContextMenu.run();
        boolean flag = this.newEdge(vertexOutbound, vertexInbound, Integer.parseInt(textField.getText()), AppConstants.BIDIRECTIONAL);
    }

    /**
     * Creates a new edge and shows a context menu associated with the specified vertex.
     * If the vertex is active, it sets the outbound vertex and creates a text field for the edge length.
     * A context menu with the text field and a confirm button is displayed.
     *
     * @param event the mouse event that triggered the edge creation.
     * @param vertex the vertex associated with which the new edge is created.
     */
    private void createNewEdge(MouseEvent event, Vertex<E> vertex) {
        clickNodeAction = OPEN_MENU;
        hideContextMenu.run();
        if (this.isActiveVertex(vertex)) {
            vertexOutbound = vertex;
            textField = UtilitiesUI.createTextField("123",true, false, true, 4, 52.5, lowLevelEdgeRanameAfter, null, 5, 0, 0, 5, 1, 0, 1, 1);
            textField.requestFocus();
            Button button2 = UtilitiesUI.createButton(SvgConstants.SVG_CHECK,"", Color.GREEN, 35, 0, 5, 5, 0, 1, 1, 1, 1, lowLevelEdgeRanameAfter,  null);
            HBox buttonBox = new HBox(0, textField, button2);
            buttonBox.setPadding(Insets.EMPTY);
            buttonBox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(5), Insets.EMPTY)));
            buttonBox.setBorder(Border.EMPTY);
            CustomMenuItem item = new CustomMenuItem(buttonBox);
            item.setHideOnClick(false);
            ContextMenu vertexMenu = new ContextMenu();
            vertexMenu.getItems().add(item);
            vertexMenu.show(this, event.getScreenX() + 10, event.getScreenY());
            forceOpenContextMenu(vertexMenu, event.getScreenX() + 10, event.getScreenY());
            openContextMenu = vertexMenu;
            openContextMenu.getStyleClass().add("context-menu");
        }
        clickNodeAction = WAIT_INPUT;
    }

    /**
     * Opens the node menu when a right-click or double-click occurs on the specified vertex.
     * Hides any existing context menu and creates a new context menu with buttons for deleting and creating edges.
     *
     * @param event the mouse event that triggered the opening of the node menu.
     * @param vertex the vertex for which the menu is opened.
     */
    private void openNodeMenu(MouseEvent event, Vertex<E> vertex) {
        if (event.getButton() == MouseButton.SECONDARY || event.getClickCount() == 2) {
            hideContextMenu.run();
            Button button1 = UtilitiesUI.createButton(SvgConstants.SVG_MINUS,"", Color.RED, 35, 5, 0, 0, 5, 1, 0, 1, 1, lowLevelDeleteNode,  vertex);
            Button button2 = UtilitiesUI.createButton(SvgConstants.SVG_PLUS,"", Color.GREEN, 35, 0, 5, 5, 0, 1, 1, 1, 0, lowLevelNewEdge,  vertex);
            HBox buttonBox = new HBox(0, button1, button2);
            buttonBox.setPadding(Insets.EMPTY);
            buttonBox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(5), Insets.EMPTY)));
            buttonBox.setBorder(Border.EMPTY);
            CustomMenuItem item = new CustomMenuItem(buttonBox);
            item.setHideOnClick(false);
            ContextMenu vertexMenu = new ContextMenu();
            vertexMenu.getItems().add(item);
            vertexMenu.show(this, event.getScreenX() + 10, event.getScreenY());
            forceOpenContextMenu(vertexMenu, event.getScreenX() + 10, event.getScreenY());
            openContextMenu = vertexMenu;
            openContextMenu.getStyleClass().add("context-menu");
        } else {
            hideContextMenu.run();
        }
    }

    /**
     * Forces the context menu to open at the specified coordinates.
     * This is useful when the application is in fullscreen mode, where the context menu might not show immediately.
     *
     * @param contextMenu the context menu to be opened.
     * @param x the x-coordinate where the menu should be displayed.
     * @param y the y-coordinate where the menu should be displayed.
     */
    private void forceOpenContextMenu(ContextMenu contextMenu, double x, double y) {
        if (primaryStage.isFullScreen()) {
            PauseTransition delay = new PauseTransition(Duration.seconds(0.01));
            delay.setOnFinished(e -> {
                contextMenu.show(this, x, y);
            });
            delay.play();
        }
    }

    /**
     * Sets the action for clicking on an arrow associated with the specified edge.
     * Opens a context menu with options to delete, rename, and modify the edge's direction.
     *
     * @param event the mouse event that triggered the arrow click.
     * @param edge the edge associated with which the arrow click action is performed.
     */
    private void setOnClickArrow(MouseEvent event, Edge<V, E> edge) {
        lastArrowMenuY = event.getScreenY();
        lastArrowMenuX = event.getScreenX();
        this.activeAllNodes();
        clickNodeAction = OPEN_MENU;
        if (event.getButton() == MouseButton.SECONDARY || event.getClickCount() == 2) {
            hideContextMenu.run();
            Button button1 = UtilitiesUI.createButton(SvgConstants.SVG_MINUS,"", Color.RED, 35, 5, 0, 0, 0, 1, 0, 0, 1, lowLevelDeleteEdge,  edge);
            Button button2 = UtilitiesUI.createButton(SvgConstants.SVG_PENCIL,"", Color.BLACK, 35, 0, 5, 0, 0, 1, 1, 0, 0, lowLevelRenameEdgeHandler,  edge);
            HBox buttonBoxTop = new HBox(0, button1, button2);
            buttonBoxTop.setPadding(Insets.EMPTY);
            buttonBoxTop.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,
                    new CornerRadii(5), Insets.EMPTY)));
            buttonBoxTop.setBorder(Border.EMPTY);
            HBox buttonBoxBottom = new HBox(0);
            if (this.isDoubleEdge(edge)) {
                Button button3 = UtilitiesUI.createButton(SvgConstants.SVG_ARROW_REPEAT, "",Color.BLACK, 70, 0, 0, 5, 5, 0, 1, 1, 1, lowLevelRotateEdge,  edge);
                buttonBoxBottom.getChildren().add(button3);
                buttonBoxBottom.setPadding(Insets.EMPTY);
                buttonBoxBottom.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,
                        new CornerRadii(5), Insets.EMPTY)));
                buttonBoxBottom.setBorder(Border.EMPTY);

            } else {
                Button button3 = UtilitiesUI.createButton(SvgConstants.SVG_ARROW_CLOCKWISE,"", Color.BLACK, 35, 0, 0, 0, 5, 0, 0, 1, 1, lowLevelRotateEdge,  edge);
                Button button4 = UtilitiesUI.createButton(SvgConstants.SVG_ARROW_EXPAND,"", Color.BLACK, 35, 0, 0, 5, 0, 0, 1, 1, 0, lowLevelSplitEdge,  edge);
                buttonBoxBottom.getChildren().addAll(button3, button4);
                buttonBoxBottom.setPadding(Insets.EMPTY);
                buttonBoxBottom.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,
                        new CornerRadii(5), Insets.EMPTY)));
                buttonBoxBottom.setBorder(Border.EMPTY);
            }
            VBox buttonBox = new VBox(buttonBoxTop, buttonBoxBottom);
            CustomMenuItem item = new CustomMenuItem(buttonBox);
            item.setHideOnClick(false);
            ContextMenu edgeMenu = new ContextMenu();
            edgeMenu.getItems().add(item);
            edgeMenu.show(this, event.getScreenX() + 10, event.getScreenY());
            forceOpenContextMenu(edgeMenu, event.getScreenX() + 10, event.getScreenY());
            openContextMenu = edgeMenu;
            openContextMenu.getStyleClass().add("context-menu");
        } else {
            hideContextMenu.run();
        }
        event.consume();
    }

    /**
     * Handles the creation of a new node when the user clicks on the canvas.
     *
     * @param event the mouse event containing the click coordinates
     */
    private void newNode(MouseEvent event) {
        char newNodeLabel = this.getNewNodeLabel().charAt(0);
        hideContextMenu.run();
        boolean flag = this.newNode(event.getX(), event.getY(), newNodeLabel);
    }

    /**
     * Creates a new edge between two nodes based on user input from text fields.
     */
    private void newEdge(){
        String newElement = textFieldSecond.getText();
        boolean flag = this.newEdge(newElement.charAt(0), newElement.charAt(1), Integer.parseInt(textFieldThird.getText()),true);
        stopElementAction();
    }

    /**
     * Stops the current element action by hiding the input fields and resetting text fields.
     */
    private void stopElementAction(){
        this.getChildren().remove(buttonContainer72);
        this.getChildren().add(buttonContainer9);
        this.getChildren().add(buttonContainer7);
        textFieldThird.setText("");
    }

    /**
     * Sets up a dynamic menu with a text field and buttons for user input.
     *
     * @param promptText the prompt text displayed in the text field
     * @param numbers    specifies whether the text field accepts numeric input
     * @param strings    specifies whether the text field accepts string input
     * @param focus      specifies whether the text field should gain focus
     * @param maxLength  the maximum length of the input in the text field
     * @param width      the width of the text field
     * @param eventHandler the event handler to be executed on button click
     */
    private void setDynamicMenu(String promptText, boolean numbers, boolean strings, boolean focus, int maxLength, double width,Object eventHandler){
        textFieldThird = UtilitiesUI.createTextField(promptText,numbers, strings, focus, maxLength, width, null, null, 0, 0, 0, 0, 1, 1, 1, 0);
        btn22 = UtilitiesUI.createButton(SvgConstants.SVG_CHECK,"", Color.GREEN, 35, 0, 0, 0, 0, 1, 0, 1, 0, eventHandler, null);
        btn52 = UtilitiesUI.createButton(SvgConstants.SVG_X_XL,"", Color.RED, 35, 0, 5, 5, 0, 1, 1, 1, 0, stopElementAction, null);
    }

    /**
     * Initiates an element action (create or rename) based on the provided state.
     *
     * @param newElement the element's identifier
     * @param state      the state indicating whether to create or rename an element
     */
    private void startElementAction(String newElement,int state){
        buttonContainer72.getChildren().remove(btn22);
        buttonContainer72.getChildren().remove(btn52);
        buttonContainer72.getChildren().remove(textFieldThird);
        if(state == CREATE_COST){
            setDynamicMenu("123",true,false,true,4,70,highLevelNewEdge);
        }else if(state == RENAME_COST){
            setDynamicMenu("123",true,false,true,4,70,renameElement);
        }else{
            setDynamicMenu("A",false,true,false,1,35,renameElement);
        }
        buttonContainer72.getChildren().add(textFieldThird);
        buttonContainer72.getChildren().add(btn22);
        buttonContainer72.getChildren().add(btn52);
        this.getChildren().remove(buttonContainer7);
        this.getChildren().remove(buttonContainer9);
        this.getChildren().add(buttonContainer72);
        Platform.runLater(() -> {
            buttonContainer72.setLayoutX(10);
            buttonContainer72.setLayoutY(10);
        });
        labelInputElement.setText(newElement);
    }

    /**
     * Handles the creation of a new element based on user input.
     */
    private void newElement() {
        String newElement = textFieldSecond.getText();
        if(!newElement.isEmpty()) {
            if (newElement.length() == 1) {
                boolean flag = this.newNode(0, 0, newElement.charAt(0));
            } else {
                if (
                        !String.valueOf(newElement.charAt(0)).equals(String.valueOf(newElement.charAt(1))) &&
                        !this.isExistEdge(newElement.charAt(0), newElement.charAt(1))
                ) {
                    startElementAction(newElement, CREATE_COST);
                }
            }
        }
    }

    /**
     * Deletes an element (node or edge) based on the input from the text field.
     * If the input length is 1, it deletes a node; otherwise, it deletes an edge.
     */
    private void deleteElement() {
        String deleteElement = textFieldSecond.getText();
        if(!deleteElement.isEmpty()) {
            if (deleteElement.length() == 1) {
                boolean flag = this.deleteNode(deleteElement.charAt(0));
            } else {
                boolean flag = this.deleteEdge(deleteElement.charAt(0), deleteElement.charAt(1));
            }
        }
    }


    /**
     * Handles the renaming of an element (node or edge).
     * If the input is a single character, it checks if the node exists
     * and starts the renaming process for a node. If the input is two
     * characters, it checks if the edge can be renamed and if both nodes exist.
     */
    private void renameElementHandler(){
        String newElement = textFieldSecond.getText();
        if(!newElement.isEmpty()) {
            if (newElement.length() == 1 && this.isExistNode(newElement.charAt(0))) {
                startElementAction(newElement, RENAME_NODE);
            } else {
                if (
                        newElement.charAt(0) != newElement.charAt(1) &&
                        this.isEdgeToRename(newElement.charAt(0), newElement.charAt(1)) &&
                        this.isExistNode(newElement.charAt(0)) &&
                        this.isExistNode(newElement.charAt(1))
                ) {

                    startElementAction(newElement, RENAME_COST);
                }
            }
        }
    }


    /**
     * Renames an element (node or edge) based on user input.
     * If the selection is a single character, it renames a node.
     * If the selection consists of two characters, it renames an edge and sets its cost.
     */
    private void renameElement() {
        String selection = textFieldSecond.getText();
        String inputValue = textFieldThird.getText();
        if(selection.length() == 1) {
            if(
                !inputValue.isEmpty() &&
                !isExistNode(inputValue.charAt(0))
            ) {
                boolean flag =  this.renameNode(selection.charAt(0),inputValue.charAt(0));
            }
        }else{
            if(
                    selection.charAt(0) != selection.charAt(1) &&
                            this.isEdgeToRename(selection.charAt(0),selection.charAt(1)) &&
                            this.isExistNode(selection.charAt(0)) &&
                            this.isExistNode(selection.charAt(1)) &&
                            !inputValue.isEmpty() &&
                            Integer.parseInt(inputValue) > 0
            ) {
                boolean flag = this.setCost(selection.charAt(0),selection.charAt(1), Integer.parseInt(inputValue));
            }
        }
        textFieldSecond.setText("");
        stopElementAction();
    }

    /**
     * Splits an edge based on the user input from the text field.
     */
    private void splitEdge(){
        String splitEdge = textFieldSecond.getText();
        if(!splitEdge.isEmpty()) {
            boolean flag = this.splitEdge(splitEdge.charAt(0), splitEdge.charAt(1));
        }
    }

    /**
     * Rotates an edge based on the user input from the text field.
     */
    private void rotateEdge(){
        String splitEdge = textFieldSecond.getText();
        if(!splitEdge.isEmpty()) {
            boolean flag = this.rotateEdge(splitEdge.charAt(0), splitEdge.charAt(1));
        }
    }



    /**
     * Deletes a low-level node based on the given Vertex parameter.
     *
     * @param param The Vertex instance representing the node to be deleted.
     */
    @SuppressWarnings("unchecked")
    private void deleteNode( Object param) {
        if (param instanceof Vertex) {
            boolean flag = this.deleteNode((Vertex<E>) param);
            hideContextMenu.run();
        }
    }

    /**
     * Deletes a low-level edge based on the given Edge parameter.
     *
     * @param param The Edge instance representing the edge to be deleted.
     */
    @SuppressWarnings("unchecked")
    private void deleteEdge( Object param) {
        if (param instanceof Edge) {
            boolean flag = this.deleteEdge((Edge<V, E>) param);
            hideContextMenu.run();
        }
    }

    /**
     * Changes the direction of a low-level edge based on the given Edge parameter.
     *
     * @param param The Edge instance representing the edge whose direction is to be changed.
     */
    @SuppressWarnings("unchecked")
    private void rotateEdge( Object param) {
        if (param instanceof Edge) {
            boolean flag = this.rotateEdge((Edge<V, E>) param);
            hideContextMenu.run();
        }
    }

    /**
     * Splits a low-level edge based on the given Edge parameter.
     *
     * @param param The Edge instance representing the edge to be split.
     */
    @SuppressWarnings("unchecked")
    private void splitEdge(Object param) {
        if (param instanceof Edge) {
            boolean flag = this.splitEdge((Edge<V, E>) param);
            hideContextMenu.run();
        }
    }

    /**
     * Initializes the low-level edge rename handler with a text field and button for user input.
     * The context menu is shown at the last known arrow menu coordinates.
     *
     * @param param The Edge instance to be renamed.
     */
    private void lowLevelEdgeRenameHandler( Object param) {
        if (param instanceof Edge) {
            hideContextMenu.run();
            textField = UtilitiesUI.createTextField("123",true, false, true, 4, 52.5, lowLevelEdgeRenameBefore, param, 5, 0, 0, 5, 1, 0, 1, 1);
            textField.requestFocus();
            Button button2 = UtilitiesUI.createButton(SvgConstants.SVG_CHECK,"", Color.GREEN, 35, 0, 5, 5, 0, 1, 1, 1, 1, lowLevelEdgeRenameBefore, param);
            HBox buttonBox = new HBox(0, textField, button2);
            buttonBox.setPadding(Insets.EMPTY);
            buttonBox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(5), Insets.EMPTY)));
            buttonBox.setBorder(Border.EMPTY);
            CustomMenuItem item = new CustomMenuItem(buttonBox);
            item.setHideOnClick(false);
            ContextMenu editEdgeMenu = new ContextMenu();
            editEdgeMenu.getItems().add(item);
            editEdgeMenu.show(this, lastArrowMenuX + 10, lastArrowMenuY);
            forceOpenContextMenu(editEdgeMenu, lastArrowMenuX + 10, lastArrowMenuY);
            openContextMenu = editEdgeMenu;
            openContextMenu.getStyleClass().add("context-menu");
        }
    }
}
