package application;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class MediaControl extends BorderPane{
	
	//slider bar
	private MediaPlayer mediaPlayer;
	private MediaView mediaView;
	private final boolean repeat = false;
	private boolean stopRequested = false;
	private boolean atEndOfMedia = false;
	private Duration duration;
	private Slider timeSlider;
	private Label playTime;
	private Slider volumeSlider;
	private HBox mediaBar;


	public MediaControl(MediaPlayer mediaPlayer) {
		
		setStyle("-fx-background-color: white;");
		mediaView = new MediaView(mediaPlayer);
		BorderPane mvPane = new BorderPane() {};
		mvPane.setCenter(mediaView);
		mvPane.setStyle("-fx-background-color: black;");
		setCenter(mvPane);
		setFill();
	}
	
	//cai dat autosize
	private void setFill() {
		
		final DoubleProperty width = mediaView.fitWidthProperty();
		final DoubleProperty height = mediaView.fitHeightProperty();
		width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
		height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
	}
	
	//Tao slider volume va timeline
	private HBox sliderBar() {
		return mediaBar;
	}
	
	//Tao thanh cong cu chua cac button
	private HBox toolBar() {
		return mediaBar;
	}
}
