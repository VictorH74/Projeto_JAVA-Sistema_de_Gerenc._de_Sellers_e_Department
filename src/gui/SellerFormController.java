package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

public class SellerFormController implements Initializable{
	private Seller entity;
	private SellerService service;
	
	// Lista de objetos que irão receber o metodo do serviço de evento
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private DatePicker dpBirthDate;
	
	@FXML
	private TextField txtBaseSalary;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Label labelErrorEmail;
	
	@FXML
	private Label labelErrorBirthDate;
	
	@FXML
	private Label labelErrorBaseSalary;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	public void setSeller(Seller entity) {
		this.entity = entity;
	}
	
	public void setSellerService(SellerService service) {
		this.service = service;
	}
	
	// Metodo para inscrever obj na lista de objts que irão receber o metodo do serviço de evento
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		// Verificação pq estamos fazendo uma injerção de dependencia manual e não com um framework
		if(entity == null)
			throw new IllegalStateException("Entity was null");
		if(service == null)
			throw new IllegalStateException("Service was null");
		
		try {
			entity = getDadosForm();
			service.saveOrUpdate(entity);
			
			notifyDataChangeListeners();
			
			// para fechar a janela
			// pegar referencia para a janela atual
			Utils.palcoAtual(event).close();
		}catch(ValidationException e) {
			setErrorMessages(e.getErrors());
		}catch(DbException e) {
			Alerts.mostrarAlerta("Erro ao salavar objeto", null, e.getMessage(), AlertType.ERROR);
		}
	}
	private void notifyDataChangeListeners() {
		for(DataChangeListener listener : dataChangeListeners) {
			listener.onDadosAlterados();
		}
		
	}

	private Seller getDadosForm() {
		Seller obj = new Seller();
		
		//Exceção que será lançada quando ocorrer erro citado na verificação
		ValidationException exception = new ValidationException("Validation error");
		
		obj.setId(Utils.converterParaInt(txtId.getText()));
		
		//obj a ser verificado "txtName"
		if(txtName.getText() == null || txtName.getText().trim().equals(""))
			exception.addError("name", "Field can't be empty");
		
		obj.setName(txtName.getText());
		
		// Testar se a coleção de erros da Classe "ValidationException" tem pelo menos 1 erro
		if(exception.getErrors().size() > 0)
			// caso a condição for verdadeira, lançará a exceção seguinte
			throw exception;
		
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.palcoAtual(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNode();
	}
	
	private void initializeNode() {
		Constraints.apenasNumInt(txtId);
		Constraints.tamanhoMaximo(txtName, 60);
		Constraints.apenasNumDouble(txtBaseSalary);
		Constraints.tamanhoMaximo(txtEmail, 60);
		Utils.formatarDatePicker(dpBirthDate, "dd/MM/yyyy");
		
	}

	// Incluir valores nas TextFields da tela "SellerForm.fxml"
	public void atualizarDadosDoForm() {
		if(entity == null)
			throw new IllegalStateException("Entity was null");
		
		// tbm pode ser -> txtId.setText(entity.getId().toString());
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		
		Locale.setDefault(Locale.US);
		
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		
		if(entity.getBirthDate() != null) {
						   // Converter valor da data para o formato da tada local do dispositivo do usuário
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
	}
	
	// Metodo que prenche a mesnagem de erro no Label vazio da janela de "SellerForm"
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if(fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}

}
