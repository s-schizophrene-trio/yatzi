package ch.juventus.yatzi.ui.controller;

import ch.juventus.yatzi.board.Board;
import ch.juventus.yatzi.ui.helper.ScreenType;
import ch.juventus.yatzi.ui.interfaces.ScreenController;
import ch.juventus.yatzi.ui.models.Screen;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The Main Controller manages all sub screens and handel as main context for the whole UI controllers.
 * @author Jan Minder
 */
@Getter
@Setter
public class MainController implements Initializable {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    // Resource Root Paths
    private final String BASE_PATH_FXML = "view/";
    private final String BASE_PATH_CSS = "css/";
    private final String BASE_PATH_IMAGES = "images/";

    // Holds the whole game board
    private Board board;

    // Class Loader to access resources
    private ClassLoader classloader;

    @FXML
    private AnchorPane yatziAnchorPane;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {

        LOGGER.debug("initialize main controller");

        this.classloader = Thread.currentThread().getContextClassLoader();

        // TODO: The right location to initialization?
        this.initBoard();

        this.showScreen(ScreenType.BOARD);

    }

    /* ----------------- Screen Handlers --------------------- */

    public void showScreen(ScreenType screenType) {

        // Load the requested screen from screen loader
        Screen screen = this.loadScreenWithController(screenType);

        try {
            replaceLayout(screen);
            LOGGER.debug("show screen: {}", screenType);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Failed to show screen with type {}", screenType.toString());
        }
    }

    /**
     * Replaces the current layout with a new JavaFX Node. If the Root View has existing child,
     * they will be removed first.
     * @param screen the screen object with the according layout node in it.
     */
    private void replaceLayout(Screen screen) {
        LOGGER.debug("replace layout to new screen: {}", screen.getScreenType());

        // Remove all Nodes from Root Layout
        if (yatziAnchorPane.getChildren().size() > 0) {
            yatziAnchorPane.getChildren().remove(0);
        }

        // Add the loaded Screen Node to the Root Layout
        yatziAnchorPane.getChildren().add(screen.getNode());
    }

    /**
     * Loads a screen including the according controller based on a screen type. If the
     * screen type is unknown by this function,
     * an error will logged.
     * @param screenType the screen type to load
     * @return an initialized screen object based on the screen type.
     */
    private Screen loadScreenWithController(ScreenType screenType) {

        // Load a screen based on the screen type
        Screen screen = this.buildScreen(this.getFxmlPath(screenType), screenType);

        // Get the controller of the according screen and initialize it
        ScreenController controller = screen.getFxmlLoader().getController();
        controller.afterInit(this);

        LOGGER.debug("screen {} loaded", screenType);
        return screen;
    }

    /**
     * Builds a screen object based on the fxml path and the according screen type
     * @param fxmlPath The relative path to the fxml layout file with base src/main/resources/
     * @param screenType The screen type to load
     * @return An initialized Screen object with a loaded fxml file.
     */
    private Screen buildScreen(String fxmlPath, ScreenType screenType) {

        Node node = null;

        URL fxmlUrl = this.classloader.getResource(fxmlPath);
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);

        LOGGER.debug("build {} screen", screenType);

        try {
            node = fxmlLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Failed to load screen with type: {}", screenType);
        }

        return new Screen(fxmlLoader,  node, screenType);
    }

    /* ----------------- Board Handlers --------------------- */

    /**
     * Initialize a new Game Board and set the defaults
     */
    private void initBoard() {
        this.board = new Board();
    }

    /* ----------------- Board Utils --------------------- */

    /**
     * Generates a full relative path to the fxml file based on the screen type.
     * @param screenType The screen type of the fxml file
     * @return A full relative path string to the fxml file of this screen type.
     */
    private String getFxmlPath(ScreenType screenType) {
      return BASE_PATH_FXML + screenType.toString().toLowerCase() + ".fxml";
    }

    /**
     * Builds the image path.
     * @param subPath The sub-path in the base image folder eg. "icons/"
     * @param key filename (lowercase)
     * @param fileExt file ending eg. "png"
     * @return A String with the relative image path
     */
    private String getImagePath(String subPath, String key, String fileExt) {
        String filePath = BASE_PATH_IMAGES + subPath + key.toLowerCase() + "." + fileExt;
        LOGGER.debug("path for image is {}", filePath);
        return filePath;
    }

    /**
     * Loads a Image from resources
     * @param subPath The sub-path in the base image folder eg. "icons/"
     * @param key filename (lowercase)
     * @param fileExt file ending eg. "png"
     * @return An initialized Image Object
     */
    public Image getImage(String subPath, String key, String fileExt) {
        LOGGER.debug("load {} image for key {}", fileExt, key);
        String imagePath = this.getImagePath(subPath, key, fileExt);
        LOGGER.debug("load image from {}", imagePath);

        // Load the image from resources
        Image image = new Image(this.classloader.getResourceAsStream(imagePath));

        return image;
    }

    /**
     * Renders the Image View based on the Image key and file extension
     *
     * @param imageKey The unique image name without file extension
     * @param fileExt  The file extension of the image
     * @param height   An optional fit height of the image view
     * @param width    An optional fit width of the image view
     * @return An Image View based on the Serve Type with an image loaded and resized it.
     */
    public ImageView renderImageView(String subPath, String imageKey, String fileExt, Double height, Double width) {
        ImageView imageView = new ImageView(this.getImage(subPath, imageKey, fileExt));

        // Resize the Image View if the values are present
        if (height != null) imageView.setFitHeight(height);
        if (width != null) imageView.setFitWidth(width);

        return imageView;
    }

}
