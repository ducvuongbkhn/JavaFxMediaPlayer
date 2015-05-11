package application;
	
import java.io.File;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;


public class MediaPlayerMain extends Application {
	
    Media media = new Media(new File("E:\\Entertainment\\Video\\Doremon\\Doraemon (VTV1 - 2000) Tap 7.mp4").toURI().toString());
	//public static Media media = new Media("http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv");
	MediaPlayer mediaPlayer = new MediaPlayer(media);
	MediaView mediaView = new MediaView(mediaPlayer);
	
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Media Player");
        Group root = new Group();
        mediaPlayer.setAutoPlay(true);      
        Scene scene = new Scene(root, 800, 600);
        MediaControl mediaControl = new MediaControl(mediaPlayer);
        scene.setRoot(mediaControl);
        primaryStage.setScene(scene);
        primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
