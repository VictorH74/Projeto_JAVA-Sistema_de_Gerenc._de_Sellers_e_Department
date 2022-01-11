package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable{
	private Department entity;
	private DepartmentService service;
	
	// Lista de objetos que irão receber o metodo do serviço de evento
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	public void setDepartmentService(DepartmentService service) {
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

	private Department getDadosForm() {
		Department obj = new Department();
		
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
		Constraints.apenasNumInt(txtId);
		Constraints.tamanhoMaximo(txtName, 30);
		
	}
	
	public void atualizarDadosDoForm() {
		if(entity == null)
			throw new IllegalStateException("Entity was null");
		
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}
	
	// Metodo que prenche a mesnagem de erro no Label vazio da janela de "DepartmentForm"
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if(fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}

}
