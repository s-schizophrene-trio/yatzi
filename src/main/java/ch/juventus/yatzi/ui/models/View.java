package ch.juventus.yatzi.ui.models;

import ch.juventus.yatzi.ui.enums.ScreenType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import lombok.*;

import java.net.URL;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class View<T extends Node> {

    @NonNull
    private FXMLLoader fxmlLoader;

    @NonNull
    private T node;

    @NonNull
    private ScreenType screenType;

}
