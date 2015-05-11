package application;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class MediaControl extends BorderPane{

	//timeline
	private MediaView mediaView;

	public MediaControl(MediaPlayer mediaPlayer) {
		setStyle("-fx-background-color: white;");
		mediaView = new MediaView(mediaPlayer);
		BorderPane mvPane = new BorderPane() {};
		mvPane.setCenter(mediaView);
		mvPane.setStyle("-fx-background-color: black;");
		setCenter(mvPane);
		setFill();
	}
	
	private void setFill() {
		final DoubleProperty width = mediaView.fitWidthProperty();
		final DoubleProperty height = mediaView.fitHeightProperty();
		width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
		height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
	}
}
