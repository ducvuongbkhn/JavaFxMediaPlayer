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
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
	private boolean VolumeFlag;
	private Label lblStatus = new Label();


	//Color TextColor = Color.rgb(255, 255, 255, 0.5);
	Color TextColor = Color.WHITE;
	String ButtonStyle = "-fx-background-color: rgba(153, 255, 255, 0);";
	String ToolStyle = "-fx-background-color: rgba(0, 0, 0, .7);";
	Cursor buttonCursor = Cursor.HAND;
	DropShadow shadow = new DropShadow(20, Color.WHITE);

	//image
	private ImageView imgOpen = new ImageView(new Image(getClass().getResourceAsStream("/Icon/Open.png")));
	private ImageView imgBack = new ImageView(new Image(getClass().getResourceAsStream("/Icon/Back.png")));
	private ImageView imgForward = new ImageView(new Image(getClass().getResourceAsStream("/Icon/Forward.png")));
	private ImageView imgPlay = new ImageView(new Image(getClass().getResourceAsStream("/Icon/Play.png")));
	private ImageView imgPause = new ImageView(new Image(getClass().getResourceAsStream("/Icon/Pause.png")));
	private ImageView imgReload = new ImageView(new Image(getClass().getResourceAsStream("/Icon/Reload.png")));
	private ImageView imgStop = new ImageView(new Image(getClass().getResourceAsStream("/Icon/Stop.png")));
	private ImageView imgFullscreen = new ImageView(new Image(getClass().getResourceAsStream("/Icon/Fullscreen.png")));
	private ImageView imgVolume = new ImageView(new Image(getClass().getResourceAsStream("/Icon/Volume.png")));
	private ImageView imgMute = new ImageView(new Image(getClass().getResourceAsStream("/Icon/Mute.png")));

	Button btnVolume = new Button("", imgVolume);
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle(mediafile.getName());
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Icon/Logo.png")));
		primaryStage.setScene(ConfigScene(primaryStage));
		primaryStage.setResizable(false);
		primaryStage.initStyle(StageStyle.UTILITY);  //HIDDEn
		//primaryStage.setFullScreen(true);
		effectDoubleClicked(primaryStage);
		effectViewClicked();
		primaryStage.show();
		setFill();
		player.play();
		view.setCursor(Cursor.HAND);
		FlagPlay = true;
		VolumeFlag = true;
		setStatus("Play");

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
		mediaBar.setStyle(ToolStyle);
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
					if (timeSlider.getValue() == 0.0)
						btnVolume.setGraphic(imgMute);
					if (timeSlider.getValue() == 100.0)
						btnVolume.setGraphic(imgVolume);

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


		/*
		// Add the volume label
		Label volumeLabel = new Label("Vol: ");
		volumeLabel.setTextFill(TextColor);
		mediaBar.getChildren().add(volumeLabel);
		*/

		mediaBar.getChildren().add(btnVolume());

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
		toolbar.getChildren().addAll(lblStatus);
		toolbar.alignmentProperty().isBound();
		toolbar.setPadding(new Insets(5, 10, 5, 10));
		toolbar.setStyle(ToolStyle);
		BorderPane.setAlignment(toolbar, Pos.CENTER);
		return toolbar;
	}

	//button Open file
	private Button btnOpen(Stage primaryStage) {
		Button btnOpen = new Button();
		btnOpen.setStyle(ButtonStyle);
		btnOpen.setCursor(buttonCursor);
		btnOpen.setTooltip(new Tooltip("Open"));
		btnOpen.setGraphic(imgOpen);
		setEffectButton(btnOpen);

		btnOpen.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				//left
				if (event.getButton() == MouseButton.PRIMARY) 
				{
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
							if (timeSlider.isValueChanging()) 
							{
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
		Button btnPlay = new Button();
		btnPlay.setStyle(ButtonStyle);
		btnPlay.setCursor(buttonCursor);
		btnPlay.setTooltip(new Tooltip("Play"));
		btnPlay.setGraphic(imgPlay);
		setEffectButton(btnPlay);

		btnPlay.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				//left
				if (event.getButton() == MouseButton.PRIMARY) {
					player.play();
					FlagPlay = true;
					setStatus("Play");
				}

			}
		});
		return btnPlay;
	}

	//button pause
	private Button btnPause() {
		Button btnPause = new Button();
		btnPause.setStyle(ButtonStyle);
		btnPause.setCursor(buttonCursor);
		btnPause.setTooltip(new Tooltip("Pause"));
		btnPause.setGraphic(imgPause);
		setEffectButton(btnPause);

		btnPause.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				//left
				if (event.getButton() == MouseButton.PRIMARY) {
					player.pause();
					FlagPlay = false;
					setStatus("Pause");
				}

			}
		});
		return btnPause;
	}

	//button back
	private Button btnBack() {
		Button btnBack = new Button();
		btnBack.setStyle(ButtonStyle);
		btnBack.setCursor(buttonCursor);
		btnBack.setTooltip(new Tooltip("Back"));
		btnBack.setGraphic(imgBack);
		setEffectButton(btnBack);

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
		Button btnForward = new Button();
		btnForward.setStyle(ButtonStyle);
		btnForward.setCursor(buttonCursor);
		btnForward.setTooltip(new Tooltip("Forward"));
		btnForward.setGraphic(imgForward);
		setEffectButton(btnForward);

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
		Button btnStop = new Button();
		btnStop.setStyle(ButtonStyle);
		btnStop.setCursor(buttonCursor);
		btnStop.setTooltip(new Tooltip("Stop"));
		btnStop.setGraphic(imgStop);
		setEffectButton(btnStop);

		btnStop.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event)
			{
				if (event.getButton() == MouseButton.PRIMARY) 
				{
					player.stop();
					setStatus("Stopped");
				}

			}
		});
		return btnStop;
	}

	//button reload
	private Button btnReload() {
		Button btnReload = new Button();
		btnReload.setStyle(ButtonStyle);
		btnReload.setCursor(buttonCursor);
		btnReload.setTooltip(new Tooltip("Reload"));
		btnReload.setGraphic(imgReload);
		setEffectButton(btnReload);

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
		Button btnFullscreen = new Button();
		btnFullscreen.setStyle(ButtonStyle);
		btnFullscreen.setCursor(buttonCursor);
		btnFullscreen.setTooltip(new Tooltip("Full Screen"));
		btnFullscreen.setGraphic(imgFullscreen);
		setEffectButton(btnFullscreen);

		btnFullscreen.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				if (event.getButton() == MouseButton.PRIMARY) 
				{
					primaryStage.setFullScreen(!primaryStage.isFullScreen());
					if (primaryStage.isFullScreen()) 
					{
						slideOut.play();
					}
					else 
					{
						slideIn.play();
					}
				}

			}
		});
		return btnFullscreen;
	}

	//button volume
	private Button btnVolume() {
		btnVolume.setStyle(ButtonStyle);
		btnVolume.setCursor(buttonCursor);
		btnVolume.setTooltip(new Tooltip("Volume"));
		setEffectButton(btnVolume);

		btnVolume.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				if (event.getButton() == MouseButton.PRIMARY) 
				{
					if (VolumeFlag) {
						btnVolume.setGraphic(imgMute);
						player.setVolume(0);
						VolumeFlag = false;
					}
					else
					{
						btnVolume.setGraphic(imgVolume);
						player.setVolume(100);
						VolumeFlag = true;
					}
					
				}

			}
		});
		return btnVolume;
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
			if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) 
			{
				if (mouseEvent.getClickCount() == 2) 
				{
					primaryStage.setFullScreen(!primaryStage.isFullScreen());                     
					if (primaryStage.isFullScreen()) 
					{
						view.setCursor(Cursor.NONE);
						slideOut.play();
					}
					else 
					{
						view.setCursor(Cursor.HAND);
						slideIn.play();
					}
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
					setStatus("Pause");
				}
				else 
				{
					player.play();
					FlagPlay = true;
					setStatus("Play");
				}

			}
		});
	}


	//update status
	private void setStatus(String status) {
		lblStatus.setText("  Status : " + status);
		lblStatus.setTextFill(TextColor);
		lblStatus.setPrefSize(90, 30);
		lblStatus.setStyle("-fx-border-color: white; -fx-background-color: rgba(153, 255, 255, 0);");
	}
	
	//effect shadow button
	private void setEffectButton(Button btn) {
		btn.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				btn.setEffect(shadow);
				
			}
		});
		btn.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				btn.setEffect(null);
				
			}
		});
	}

}