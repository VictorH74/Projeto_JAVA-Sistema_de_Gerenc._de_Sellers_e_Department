package gui.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class Utils {

	// 1: Metodo para pegar o obj "Stage" a partir do obj de evento "event"
	// 2: Acessa o Stage aonde o controle que recebeu o evento, se encontra
	// 2.a Ex.: -Se clica no botão, eu vou pegar o Stage daquele botão

	// ActionEvent event -> evento que o (por exemplo) botão recebeu
	public static Stage palcoAtual(ActionEvent event) {
		return (Stage) ((Node) event.getSource()).getScene().getWindow();
	}

	// Metodo para ajudar a converter o valor da "caixinha" para Integer
	public static Integer converterParaInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static <T> void formatarDataDaColuna(TableColumn<T, Date> tableColumn, String format) {
		tableColumn.setCellFactory(column -> {
			TableCell<T, Date> cell = new TableCell<T, Date>() {
				private SimpleDateFormat sdf = new SimpleDateFormat(format);

				@Override
				protected void updateItem(Date item, boolean empty) {
					super.updateItem(item, empty);
					if (empty)
						setText(null);
					else
						setText(sdf.format(item));
				}
			};
			return cell;
		});
	}

	public static <T> void formatarValorDoubleDaColuna(TableColumn<T, Double> tableColumn, int decimalPlaces) {
		tableColumn.setCellFactory(column -> {
			TableCell<T, Double> cell = new TableCell<T, Double>() {

				@Override
				protected void updateItem(Double item, boolean empty) {
					super.updateItem(item, empty);
					if (empty)
						setText(null);
					else {
						Locale.setDefault(Locale.US);
						setText(String.format("%." + decimalPlaces + "f", item));
					}
				}
			};
			return cell;
		});
	}

	public static void formatarDatePicker(DatePicker datePicker, String format) {
		datePicker.setConverter(new StringConverter<LocalDate>() {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(format);

			{
				datePicker.setPromptText(format.toLowerCase());
			}

			@Override
			public String toString(LocalDate date) {
				if (date != null)
					return dateFormatter.format(date);
				else
					return "";
			}

			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty())
					return LocalDate.parse(string, dateFormatter);
				else
					return null;
			}
		});

	}
}
