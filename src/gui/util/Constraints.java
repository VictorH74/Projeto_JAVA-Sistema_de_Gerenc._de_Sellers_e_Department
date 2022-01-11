package gui.util;

import javafx.scene.control.TextField;

public class Constraints {

	public static void apenasNumInt(TextField txt) {
		txt.textProperty().addListener(
				(obs, valorAntigo, novoValor) -> {
					if(novoValor != null && !novoValor.matches("\\d*"))
						txt.setText(valorAntigo);
				}
				);
	}
	public static void apenasNumDouble(TextField txt) {
		txt.textProperty().addListener(
				(obs, valorAntigo, novoValor) -> {
					if(novoValor != null && !novoValor.matches("\\d*([\\.]\\d*)?"))
						txt.setText(valorAntigo);
				}
				);
	}
	public static void tamanhoMaximo(TextField txt, int max) {
		txt.textProperty().addListener(
				(obs, valorAntigo, novoValor) -> {
					if(novoValor != null && novoValor.length() > max)
						txt.setText(valorAntigo);
				}
				);
	}
}
