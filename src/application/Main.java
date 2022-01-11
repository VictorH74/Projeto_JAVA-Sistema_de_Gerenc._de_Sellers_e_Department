package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Main extends Application {
	
	private static Scene mainScene;
	
	@Override
	public void start(Stage primaryStage) {
		
		try {
			/*ScrollPane -> Painel que mostra barra de rolagem quando o conteudo fica maior
			 * que a propria janela*/
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
			ScrollPane scrollPane = loader.load();
			mainScene = new Scene(scrollPane); // objeto de cena
			
			scrollPane.setFitToHeight(true);
			scrollPane.setFitToWidth(true);
			
			primaryStage.setScene(mainScene); // dentro do palco terá a cena "scene"
			primaryStage.setTitle("Main View / by Victor Hugo");
			primaryStage.show(); // mostrar o conteúdo do palco
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Scene getMainScene() {
		return mainScene;
	}
	
	public static void main(String[] args) {
		launch(args);// metodo da classe "Application". Serve para iniciar a aplicação javafx
		System.out.println("Test");
	}
}
