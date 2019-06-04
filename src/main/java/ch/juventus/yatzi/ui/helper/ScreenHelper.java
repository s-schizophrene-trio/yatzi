package ch.juventus.yatzi.ui.helper;

import ch.juventus.yatzi.ui.controller.StatusController;
import ch.juventus.yatzi.ui.enums.ScreenType;
import ch.juventus.yatzi.ui.interfaces.ViewContext;
import ch.juventus.yatzi.ui.interfaces.ViewController;
import ch.juventus.yatzi.ui.models.View;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * Additional functionality to simplify the UI building
 */
public class ScreenHelper {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public final static String BASE_PATH_FXML = "view/";
    public final static String BASE_PATH_CSS = "css/";
    public final static String BASE_PATH_IMAGES = "images/";

    public URL getStyleUrl(ViewContext context, ScreenType screenType) {
        return  context.getClassloader().getResource(
                this.getFilePath(
                        ScreenHelper.BASE_PATH_CSS, screenType, "css"
                )
        );
    }

    /**
     * Shows a screen based on the View Type
     *
     * @param context An instance of the view context to clear and add the new screen
     * @param screenType The ScreenType of the requested screen
     */
    public void showScreen(ViewContext context, ScreenType screenType) {

        // Clear the current Screen
        this.clearScreen(context);

        // add the status bar
        context.getViewHandler().setStatusController(this.addStatusBar(context));

        View view = this.loadScreenWithController(context, screenType);

        try {
            this.addNode(context, view.getNode());

            // resize the window to scene size
            Stage rootStage = context.getStage();
            rootStage.sizeToScene();
            //this.centerStageOnScreen(rootStage);

            LOGGER.debug("show view: {}", screenType);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Failed to show view with type {}", screenType.toString());
        }
    }

    /**
     * Loads a screen including the according controller based on a screen type. If the
     * screen type is unknown by this function,
     * an error will logged.
     *
     * @param context An instance of the ViewContext to load the screen and its controller
     * @param screenType the screen type to load
     * @return an initialized screen object based on the screen type.
     */
    public View loadScreenWithController(ViewContext context, ScreenType screenType) {

        // load a view based on the view type
        View view = this.buildView(context.getClassloader(),
                this.getFilePath(BASE_PATH_FXML, screenType, "fxml"), screenType);

        // get the controller of the according view and initialize it
        ViewController controller = view.getFxmlLoader().getController();
        AnchorPane anchorPane = (AnchorPane)context.getRootNode();
        anchorPane.setBackground(null);
        controller.afterInit(context);

        LOGGER.debug("view {} loaded", screenType);
        return view;
    }

    /**
     * Builds a screen object based on the fxml path and the according screen type
     *
     * @param classloader An instance of a classloader to access files
     * @param fxmlPath   The relative path to the fxml layout file with base src/main/resources/
     * @param screenType The screen type to load
     * @return An initialized View object with a loaded fxml file.
     */
    public View<Node> buildView(ClassLoader classloader, String fxmlPath, ScreenType screenType) {

        URL fxmlUrl = classloader.getResource(fxmlPath);
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);

        LOGGER.debug("build {} screen", screenType);

        Node node = null;
        try {
            node = fxmlLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("failed to load screen with type: {}", screenType);
        }

        return new View<>(fxmlLoader, node, screenType);
    }

    /**
     * Builds a nwe scene based on the AnchorPane. The Global Style File will be added to the stylesheet-chain.
     *
     * @param context  The View Context with a valid root node inside
     * @return The built scene, ready to show.
     */
    public Scene buildScene(ViewContext context) {
        if (context.getRootNode() != null) {

            Scene scene = new Scene((AnchorPane)context.getRootNode());

            // set styles for all available styles
            for (ScreenType screenType : ScreenType.values()) {

                URL styleUrl = this.getStyleUrl(context, screenType);

                if (styleUrl != null) {
                    scene.getStylesheets().add(styleUrl.toExternalForm());
                } else {
                    LOGGER.warn("cloud not find a style file for {} screen", screenType);
                }
            }

            return scene;
        } else {
            LOGGER.error("failed to display the scene because of an error during parent initialization");
            return null;
        }
    }

    /**
     * Adds a new Statusbar to the bottom screen
     * @param context An instance of a ViewContext to add the statusbar
     * @return The status controller to manage the notifications
     */
    public StatusController addStatusBar(ViewContext context) {

        // Load a view based on the view type
        View view = this.buildView(context.getClassloader(),
                this.getFilePath(BASE_PATH_FXML, ScreenType.STATUS, "fxml"),
                ScreenType.STATUS);

        // Get the controller of the according view and initialize it
        StatusController statusController = view.getFxmlLoader().getController();
        statusController.afterInit(context);

        AnchorPane statusBar = (AnchorPane) view.getNode();
        AnchorPane.setBottomAnchor(statusBar, 0D);

        try {
            AnchorPane layout = (AnchorPane) context.getRootNode();
            layout.getChildren().add(statusBar);
            LOGGER.debug("added statusbar to root layout");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Failed to add the statusbar");
        }

        return statusController;
    }

    /**
     * Generates a full relative path to the fxml file based on the screen type.
     *
     * @param baseBath The base path, the file is located at
     * @param fileType The file type (ending) of the file
     * @param screenType The screen type of the fxml file
     * @return A full relative path string to the fxml file of this screen type.
     */
    public String getFilePath(String baseBath, ScreenType screenType, String fileType) {
        return baseBath + screenType.toString().toLowerCase() + "." + fileType;
    }

    /**
     * Adds a Node to the Main Anchor Pane
     *
     * @param context A instance ot the ViewContext to add the node to
     * @param node A Node to add to the root layout
     */
    public void addNode(ViewContext context, Node node) {
        LOGGER.debug("add layout to new screen");

        AnchorPane anchorPane = (AnchorPane) context.getRootNode();
        anchorPane.getChildren().add(node);
    }

    /**
     * Centers a Window on the View
     *
     * @param primaryStage The primary stage is used to center the window on the screen
     */
    public void centerStageOnScreen(Stage primaryStage) {
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);
    }

    /**
     * Replaces the current layout with a new JavaFX Node. If the Root View has existing child,
     * they will be removed first.
     *
     * @param context An instance of the ViewContext to have access to the root node
     */
    public void clearScreen(ViewContext context) {
        LOGGER.debug("replace layout to new screen");

        AnchorPane layout =  (AnchorPane) context.getRootNode();
        layout.getChildren().clear();
    }

    /* ----------- Image Handling ----------------- */
    /**
     * Loads an Image from resources
     * @param classLoader An instance of the main class loader, to load the files from resource.
     * @param subPath The sub-path in the base image folder eg. "icons/"
     * @param key filename (lowercase)
     * @param fileExt file ending eg. "png"
     * @return An initialized Image Object
     */
    public Image getImage(ClassLoader classLoader, String subPath, String key, String fileExt) {
        LOGGER.trace("load {} image for key {}", fileExt, key);
        String imagePath = BASE_PATH_IMAGES + subPath + key.toLowerCase() + "." + fileExt;
        LOGGER.trace("load image from {}", imagePath);

        // Load the image from resources
        Image image = new Image(classLoader.getResourceAsStream(imagePath));

        return image;
    }

    /**
     * Renders the Image View based on the Image key and file extension
     *
     * @param classLoader The class loader instance to load the files
     * @param subPath The sub directory, the image is stored in
     * @param imageKey The unique image name without file extension
     * @param fileExt  The file extension of the image
     * @param height   An optional fit height of the image view
     * @param width    An optional fit width of the image view
     * @return An Image View based on the Serve Type with an image loaded and resized it.
     */
    public ImageView renderImageView(ClassLoader classLoader, String subPath, String imageKey, String fileExt, Double height, Double width) {
        ImageView imageView = new ImageView(this.getImage(classLoader, subPath, imageKey, fileExt));

        // Resize the Image View if the values are present
        if (height != null) imageView.setFitHeight(height);
        if (width != null) imageView.setFitWidth(width);

        return imageView;
    }

}
