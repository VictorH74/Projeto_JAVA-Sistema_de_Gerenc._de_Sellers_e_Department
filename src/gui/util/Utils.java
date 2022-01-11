package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {
	
	// 1: Metodo para pegar o obj "Stage" a partir do obj de evento "event"
	// 2: Acessa o Stage aonde o controle que recebeu o evento, se encontra
	// 2.a Ex.: -Se clica no botão, eu vou pegar o Stage daquele botão
	
	// ActionEvent event -> evento que o (por exemplo) botão recebeu
	public static Stage currentStage(ActionEvent event) {
		return (Stage)((Node) event.getSource()).getScene().getWindow();
	}
	
	// Metodo para ajudar a converter o valor da "caixinha" para Integer
	public static Integer tryParseToInt(String str) {
		try {
			return Integer.parseInt(str);
		}catch(NumberFormatException e) {
			return null;
		}
	}

}
