package application;
	
import java.io.File;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.util.Duration;


public class MediaPlayerMain extends Application {
	private Duration duration;
	private Slider timeSlider;
	private Label playTime;
	private Slider volumeSlider;
	private HBox mediaBar;
	private HBox toolbar;
	private boolean FlagFull = false;

	private double w;
	private double h;
	Media media = new Media(new File("E:\\Entertainment\\Video\\Walt Disney\\Peter Pan 2.MP4").toURI().toString());
	//public static Media media = new Media("http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv");
	MediaPlayer player = new MediaPlayer(media);
	MediaView view = new MediaView(player);
	
	Color TextColor = Color.rgb(255, 255, 255, 0.5);
	String ButtonStyle = "-fx-background-color: red;";
	
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Coppy Movie Player");
		primaryStage.setScene(InitScene(primaryStage));
		//primaryStage.setResizable(false);
		//primaryStage.initStyle(StageStyle.UTILITY);  //HIDDEn
		primaryStage.show();
		setFill();
		player.play();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private Scene InitScene(Stage primaryStage) {
		Group root = new Group();
		BorderPane mvPane = new BorderPane(root);
		mvPane.setStyle("-fx-background-color: black;");
		VBox vbox = new VBox();	
		vbox.setAlignment(Pos.BOTTOM_CENTER);
		vbox.setPadding(new Insets(5, 10, 5, 10));
		vbox.getChildren().add(mediaBar());
		//vbox.getChildren().add(toolBar(primaryStage));
		Scene scene = new Scene(mvPane, 1067, 600, Color.BLACK);
		root.getChildren().add(view);
		root.getChildren().add(vbox);
		
		//hieu ung cho timeline
		final Timeline slideIn = new Timeline();
		final Timeline slideOut = new Timeline();
		root.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				slideIn.play();
			}
		});
		root.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				slideOut.play();
			}
		});
		
		player.setOnReady(new Runnable() {
			@Override
			public void run() {
				duration = player.getMedia().getDuration();
				updateSize(primaryStage);
				primaryStage.setMinWidth(w);
				primaryStage.setMinHeight(h);
				vbox.setMinSize(w, 100);
				vbox.setTranslateY(h - 100);

				slideOut.getKeyFrames().addAll(
						new KeyFrame(new Duration(0),
								new KeyValue(vbox.translateYProperty(), h-100),
								new KeyValue(vbox.opacityProperty(), 0.9)
								),
								new KeyFrame(new Duration(300),
										new KeyValue(vbox.translateYProperty(), h),
										new KeyValue(vbox.opacityProperty(), 0.0)
										)
						);
				slideIn.getKeyFrames().addAll(
						new KeyFrame(new Duration(0),
								new KeyValue(vbox.translateYProperty(), h),
								new KeyValue(vbox.opacityProperty(), 0.0)
								),
								new KeyFrame(new Duration(300),
										new KeyValue(vbox.translateYProperty(), h-100),
										new KeyValue(vbox.opacityProperty(), 0.9)
										)
						);
			}
		});
		return scene;
	}
	
	private HBox mediaBar() {
		//timeline
		mediaBar = new HBox();
		mediaBar.setAlignment(Pos.CENTER);
		mediaBar.setPadding(new Insets(5, 10, 5, 10));
		mediaBar.setStyle("-fx-background-color: rgba(153, 255, 255, .1);"); //transparent
		player.currentTimeProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				updateValues();
			}
		});

		// Add Time label
		Label timeLabel = new Label("Time: ");
		timeLabel.setTextFill(TextColor);
		mediaBar.getChildren().add(timeLabel);

		// Add time slider
		timeSlider = new Slider();
		HBox.setHgrow(timeSlider, Priority.ALWAYS);
		timeSlider.setMinWidth(50);
		timeSlider.setMaxWidth(Double.MAX_VALUE);
		timeSlider.setCursor(Cursor.HAND);
		timeSlider.valueProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				if (timeSlider.isValueChanging()) {
					player.seek(duration.multiply(timeSlider.getValue() / 100.0));
				}
			}
		});
		mediaBar.getChildren().add(timeSlider);

		// Add Play label
		playTime = new Label();
		playTime.setPrefWidth(130);
		playTime.setMinWidth(50);
		playTime.setTextFill(TextColor);
		mediaBar.getChildren().add(playTime);


		// Add the volume label
		Label volumeLabel = new Label("Vol: ");
		volumeLabel.setTextFill(TextColor);
		mediaBar.getChildren().add(volumeLabel);

		// Add Volume slider
		volumeSlider = new Slider();
		volumeSlider.setPrefWidth(70);
		volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
		volumeSlider.setMinWidth(30);
		volumeSlider.valueProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				if (volumeSlider.isValueChanging())
					player.setVolume(volumeSlider.getValue() / 100.0);  
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
					Duration currentTime = player.getCurrentTime();
					playTime.setText(formatTime(currentTime, duration));
					timeSlider.setDisable(duration.isUnknown());
					if (!timeSlider.isDisabled() && duration.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging())
						timeSlider.setValue(currentTime.divide(duration).toMillis() * 100.0);

					if (!volumeSlider.isValueChanging())
						volumeSlider.setValue((int) Math.round(player.getVolume() * 100));
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
	
	public void setFill() {
		final DoubleProperty width = view.fitWidthProperty();
		final DoubleProperty height = view.fitHeightProperty();
		width.bind(Bindings.selectDouble(view.sceneProperty(), "width"));
		height.bind(Bindings.selectDouble(view.sceneProperty(), "height"));
	}
	
	
	private void updateSize(Stage primaryStage) {
		w = primaryStage.getScene().getWidth();
		h = primaryStage.getScene().getHeight();
	}
}
