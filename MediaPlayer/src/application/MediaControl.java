package application;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class MediaControl extends BorderPane {

	//timeline
	private MediaPlayer mediaPlayer;
	private MediaView mediaView;
	private final boolean repeat = false;
	private Duration duration;
	private Slider timeSlider;
	private Label playTime;
	private Slider volumeSlider;
	private HBox mediaBar;

	//private HBox toolbar;
	private VBox Box;

	public MediaControl(final MediaPlayer mp) {
		this.mediaPlayer = mp;
		setStyle("-fx-background-color: white;");
		mediaView = new MediaView(mediaPlayer);
		BorderPane mvPane = new BorderPane() {};
		mvPane.setCenter(mediaView);
		mvPane.setStyle("-fx-background-color: black;");
		setCenter(mvPane);
		setFill();
		Box = new VBox(sliderBar());
		setTop(Box);
	}

	private void setFill() {
		final DoubleProperty width = mediaView.fitWidthProperty();
		final DoubleProperty height = mediaView.fitHeightProperty();
		width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
		height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
	}
	
	private HBox sliderBar() {
		mediaBar = new HBox();
		mediaBar.setAlignment(Pos.CENTER);
		mediaBar.setPadding(new Insets(5, 10, 5, 10));
		BorderPane.setAlignment(mediaBar, Pos.CENTER);
		mediaBar.setStyle("-fx-background-color: white;");
		
		
		
		mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				updateValues();
			}
		});


		mediaPlayer.setOnReady(new Runnable() {
			public void run() {
				duration = mediaPlayer.getMedia().getDuration();
				updateValues();
			}
		});

		mediaPlayer.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
		mediaPlayer.setOnEndOfMedia(new Runnable() {
			public void run() {
				if (!repeat) {
				}
			}
		});

		// Add Time label
		Label timeLabel = new Label("Time: ");
		mediaBar.getChildren().add(timeLabel);

		// Add time slider
		timeSlider = new Slider();
		HBox.setHgrow(timeSlider, Priority.ALWAYS);
		timeSlider.setMinWidth(50);
		timeSlider.setMaxWidth(Double.MAX_VALUE);
		timeSlider.valueProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				if (timeSlider.isValueChanging()) {
					mediaPlayer.seek(duration.multiply(timeSlider.getValue() / 100.0));
				}
			}
		});
		mediaBar.getChildren().add(timeSlider);

		// Add Play label
		playTime = new Label();
		playTime.setMinWidth(50);
		mediaBar.getChildren().add(playTime);
		
		//Add Separator
		Separator sep1 = new Separator(Orientation.VERTICAL);
		mediaBar.getChildren().add(sep1);
		
		// Add the volume label
		Label volumeLabel = new Label("Vol: ");
		mediaBar.getChildren().add(volumeLabel);

		// Add Volume slider
		volumeSlider = new Slider();
		volumeSlider.setPrefWidth(70);
		volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
		volumeSlider.setMinWidth(30);
		volumeSlider.valueProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				if (volumeSlider.isValueChanging())
					mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);  
			}
		});
		mediaBar.getChildren().add(volumeSlider);
		return mediaBar;

	}

	protected void updateValues() {
		if (playTime != null && timeSlider != null && volumeSlider != null) {
			Platform.runLater(new Runnable() {
				@SuppressWarnings("deprecation")
				public void run() {
					Duration currentTime = mediaPlayer.getCurrentTime();
					playTime.setText(formatTime(currentTime, duration));
					timeSlider.setDisable(duration.isUnknown());
					if (!timeSlider.isDisabled() && duration.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging())
						timeSlider.setValue(currentTime.divide(duration).toMillis() * 100.0);

					if (!volumeSlider.isValueChanging())
						volumeSlider.setValue((int) Math.round(mediaPlayer.getVolume() * 100));
				}
			});
		}
	}

	private static String formatTime(Duration elapsed, Duration duration) {
		int intElapsed = (int) Math.floor(elapsed.toSeconds());
		int elapsedHours = intElapsed / (60 * 60);
		if (elapsedHours > 0)
			intElapsed -= elapsedHours * 60 * 60;
		int elapsedMinutes = intElapsed / 60;
		int elapsedSeconds = intElapsed - elapsedHours * 60 * 60  - elapsedMinutes * 60;

		if (duration.greaterThan(Duration.ZERO)) {
			int intDuration = (int) Math.floor(duration.toSeconds());
			int durationHours = intDuration / (60 * 60);
			if (durationHours > 0) {
				intDuration -= durationHours * 60 * 60;
			}
			int durationMinutes = intDuration / 60;
			int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
			if (durationHours > 0)
				return String.format("%d:%02d:%02d/%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds, durationHours, durationMinutes, durationSeconds);
			else 
				return String.format("%02d:%02d/%02d:%02d", elapsedMinutes, elapsedSeconds, durationMinutes, durationSeconds);
		} 
		else {
			if (elapsedHours > 0)
				return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
			else 
				return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);
		}
	}

	//private HBox toolBar() {
	//	return toolbar;
	//}
	
}