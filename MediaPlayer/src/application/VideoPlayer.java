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
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
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
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class VideoPlayer extends Application {

	private Duration duration;
	private Slider timeSlider;
	private Label playTime;
	private Slider volumeSlider;

	private double w;
	private double h;
	File mediafile = new File("E:\\Entertainment\\Video\\Walt Disney\\Peter Pan 2.MP4");
	Media media = new Media(mediafile.toURI().toString());
	MediaPlayer player = new MediaPlayer(media);
	MediaView view = new MediaView(player);

	final Timeline slideIn = new Timeline();
	final Timeline slideOut = new Timeline();
	VBox vbox = new VBox();	
	private boolean FlagPlay;
	private String status;


	Color TextColor = Color.rgb(255, 255, 255, 0.5);
	String ButtonStyle = "-fx-border-color: black; -fx-background-color: rgba(153, 255, 255, .1);";
	Cursor buttonCursor = Cursor.HAND;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle(mediafile.getName());
		primaryStage.setScene(ConfigScene(primaryStage));
		primaryStage.setResizable(false);
		primaryStage.initStyle(StageStyle.UTILITY);  //HIDDEn
		//primaryStage.setFullScreen(true);
		effectDoubleClicked(primaryStage);
		effectViewClicked();
		primaryStage.show();
		setFill();
		player.play();
		FlagPlay = true;
		updateStatus();

	}

	//Configuration scene for primary stage  
	private Scene ConfigScene(Stage primaryStage) {
		Group root = new Group();
		vbox.setAlignment(Pos.BOTTOM_CENTER);
		vbox.setPadding(new Insets(5, 10, 5, 10));
		vbox.getChildren().add(mediaBar(player));
		vbox.getChildren().add(toolBar(primaryStage));
		root.getChildren().add(view);
		root.getChildren().add(vbox);
		
		BorderPane mvPane = new BorderPane(root);
		mvPane.setStyle("-fx-background-color: black;");
		Scene scene = new Scene(mvPane, 1067, 600, Color.BLACK);
		//Scene scene = new Scene(mvPane, 800, 600, Color.BLACK);
		
		//effect for tool bar and media bar
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
				updateValues();
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

	//create media bar for slider
	private HBox mediaBar(MediaPlayer player) {
		//time line
		HBox mediaBar;
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
		volumeSlider.setCursor(Cursor.HAND);
		volumeSlider.valueProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				if (volumeSlider.isValueChanging())
					player.setVolume(volumeSlider.getValue() / 100.0);  
			}
		});
		mediaBar.getChildren().add(volumeSlider);
		return mediaBar;
	}

	//update value for time play and slider
	private void updateValues() {
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

	//format time for time play
	public static String formatTime(Duration elapsed, Duration duration) {
		int intElapsed = (int) Math.floor(elapsed.toSeconds());
		int elapsedHours = intElapsed / (60 * 60);
		int elapsedMinutes = intElapsed / 60 - elapsedHours * 60;
		int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
				- elapsedMinutes * 60;

		if (duration.greaterThan(Duration.ZERO)) {
			int intDuration = (int) Math.floor(duration.toSeconds());
			int durationHours = intDuration / (60 * 60);
			int durationMinutes = intDuration / 60 - durationHours * 60;
			int durationSeconds = intDuration - durationHours * 60 * 60
					- durationMinutes * 60;
			if (durationHours > 0) {
				return String.format("%d:%02d:%02d/%d:%02d:%02d", elapsedHours,
						elapsedMinutes, elapsedSeconds, durationHours,
						durationMinutes, durationSeconds);
			} else {
				return String.format("%02d:%02d/%02d:%02d", elapsedMinutes,
						elapsedSeconds, durationMinutes, durationSeconds);
			}
		} else {
			if (elapsedHours > 0) {
				return String.format("%d:%02d:%02d", elapsedHours,
						elapsedMinutes, elapsedSeconds);
			} else {
				return String.format("%02d:%02d", elapsedMinutes,
						elapsedSeconds);
			}
		}
	}

	//create tool bar button
	private HBox toolBar(Stage primaryStage) {
		HBox toolbar;
		toolbar = new HBox(btnOpen(primaryStage),btnPlay(), btnPause(), btnBack(), btnForward(), btnReload(), btnStop(), btnFullscreen(primaryStage));
		toolbar.setAlignment(Pos.CENTER);
		toolbar.alignmentProperty().isBound();
		toolbar.setPadding(new Insets(5, 10, 5, 10));
		toolbar.setStyle("-fx-background-color: rgba(153, 255, 255, .1);");
		BorderPane.setAlignment(toolbar, Pos.CENTER);
		return toolbar;
	}

	//button Open file
	private Button btnOpen(Stage primaryStage) {
		Button btnOpen = new Button("Open");
		btnOpen.setStyle(ButtonStyle);
		btnOpen.setCursor(buttonCursor);
		btnOpen.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				//left
				if (event.getButton() == MouseButton.PRIMARY) {
					FileChooser fc = new FileChooser();
					fc.getExtensionFilters().add(new ExtensionFilter("Media File", "*.flv", "*.mp4", "*.mpeg", "*.*"));
					File file = fc.showOpenDialog(null);
					String path = file.getAbsolutePath();
					path = path.replace("\\", "/");
					media = new Media(new File(path).toURI().toString());
					player.stop();
					player = new MediaPlayer(media);
					player.setAutoPlay(true);
					view.setMediaPlayer(player);
					primaryStage.setTitle(file.getName());


					player.currentTimeProperty().addListener(new InvalidationListener() {
						public void invalidated(Observable ov) {
							updateValues();
						}
					});

					timeSlider.valueProperty().addListener(new InvalidationListener() {
						public void invalidated(Observable ov) {
							if (timeSlider.isValueChanging()) {
								player.seek(duration.multiply(timeSlider.getValue() / 100.0));
							}
						}
					});

					volumeSlider.valueProperty().addListener(new InvalidationListener() {
						public void invalidated(Observable ov) {
							if (volumeSlider.isValueChanging())
								player.setVolume(volumeSlider.getValue() / 100.0);  
						}
					});

					player.setOnReady(new Runnable() {
						@Override
						public void run() {
							duration = player.getMedia().getDuration();
							updateValues();
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

				}

			}
		});
		return btnOpen;
	}

	//button play
	private Button btnPlay() {
		Button btnPlay = new Button("Play");
		btnPlay.setStyle(ButtonStyle);
		btnPlay.setCursor(buttonCursor);
		btnPlay.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				//left
				if (event.getButton() == MouseButton.PRIMARY) {
					player.play();
					FlagPlay = true;
					updateStatus();
				}

			}
		});
		return btnPlay;
	}

	//button pause
	private Button btnPause() {
		Button btnPause = new Button("Pause");
		btnPause.setStyle(ButtonStyle);
		btnPause.setCursor(buttonCursor);
		//btnOpen.setGraphic(imgOpen);
		btnPause.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				//left
				if (event.getButton() == MouseButton.PRIMARY) {
					player.pause();
					FlagPlay = false;
					updateStatus();
				}

			}
		});
		return btnPause;
	}

	//button back
	private Button btnBack() {
		Button btnBack = new Button("Back");
		btnBack.setStyle(ButtonStyle);
		btnBack.setCursor(buttonCursor);
		btnBack.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				
				if (event.getButton() == MouseButton.PRIMARY) 
				{
					player.seek(player.getCurrentTime().divide(1.5));
				}

			}
		});

		return btnBack;
	}

	//button forward
	private Button btnForward() {
		Button btnForward = new Button("Forward");
		btnForward.setStyle(ButtonStyle);
		btnForward.setCursor(buttonCursor);
		btnForward.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				
				if (event.getButton() == MouseButton.PRIMARY) 
				{
					player.seek(player.getCurrentTime().multiply(1.5));
				}

			}
		});
		return btnForward;
	}

	//button stop
	private Button btnStop() {
		Button btnStop = new Button("Stop");
		btnStop.setStyle(ButtonStyle);
		btnStop.setCursor(buttonCursor);
		btnStop.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event)
			{
				if (event.getButton() == MouseButton.PRIMARY) 
				{
					player.stop();
					status = "Stop";
				}

			}
		});
		return btnStop;
	}

	//button reload
	private Button btnReload() {
		Button btnReload = new Button("Reload");
		btnReload.setStyle(ButtonStyle);
		btnReload.setCursor(buttonCursor);
		btnReload.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				
				if (event.getButton() == MouseButton.PRIMARY) 
				{
					player.seek(player.getStartTime());
				}

			}
		});
		return btnReload;
	}

	//button full screen
	private Button btnFullscreen(Stage primaryStage) {
		Button btnFullscreen = new Button("Full");
		btnFullscreen.setStyle(ButtonStyle);
		btnFullscreen.setCursor(buttonCursor);
		btnFullscreen.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getButton() == MouseButton.PRIMARY) 
				{
					primaryStage.setFullScreen(!primaryStage.isFullScreen());
					if (primaryStage.isFullScreen()) 
					{
						btnFullscreen.setText("Not Full");
						slideOut.play();
					}
					else 
					{
						btnFullscreen.setText("Full");
						slideIn.play();
					}
				}

			}
		});
		return btnFullscreen;
	}

	//media view fill primary stage
	public void setFill() {
		final DoubleProperty width = view.fitWidthProperty();
		final DoubleProperty height = view.fitHeightProperty();
		width.bind(Bindings.selectDouble(view.sceneProperty(), "width"));
		height.bind(Bindings.selectDouble(view.sceneProperty(), "height"));
	}

	//update size for slider
	private void updateSize(Stage primaryStage) {
		w = primaryStage.getScene().getWidth();
		h = primaryStage.getScene().getHeight();
	}

	//effect double click
	private void effectDoubleClicked(Stage primaryStage) {
		view.addEventFilter(MouseEvent.MOUSE_PRESSED, (mouseEvent) -> {
			if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
				if (mouseEvent.getClickCount() == 2) 
				{
					primaryStage.setFullScreen(!primaryStage.isFullScreen());                     
					if (primaryStage.isFullScreen()) 
						slideOut.play();
				}
			}
		});
	}
	
	//effect click media view
		private void effectViewClicked() {
			view.addEventFilter(MouseEvent.MOUSE_PRESSED, (mouseEvent) -> {
				
				if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
					if (FlagPlay) 
					{
						player.pause();
						FlagPlay = false;
						updateStatus();
					}
					else 
					{
						player.play();
						FlagPlay = true;
						updateStatus();
					}
						
				}
			});
		}
		
	
	//update status
		private String updateStatus() {
			if (FlagPlay)
				status = "Play";
			else
				status = "Pause";
			System.out.println(status);
			return status;
		}


}