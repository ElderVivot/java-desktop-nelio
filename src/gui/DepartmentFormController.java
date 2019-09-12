package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DBException;
import gui.listener.DataChangeListener;
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

public class DepartmentFormController implements Initializable {
	
	// eu preciso do obj Department pois quando eu carregar minha tela de DepartmentForm vou popular minhas caixas de texto
	// do formulário. E estes dados precisam ser buscados da entidade.
	// a 'popularização' é extremamente necessário principalmente quando já existe o department e vamos apenas alterá-lo
	private Department entity;
	
	// o service é que vai gravar no BD os dados
	private DepartmentService service;
	
	// o DataChangeListener recebe um objeto dos eventos que estão interessados em serem alterados
	private List<DataChangeListener> dataChangeListener = new ArrayList<>();
	
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
	
	// insere um novo objeto que está interessado no padrão Observer pra ser atualizado automaticamente
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListener.add(listener);
	}
	
	@FXML // pra poder fechar a janela preciso do parametro do ActionEvent
	public void onBtSaveAction(ActionEvent event) {
		// programação defensiva, vejo se estão nulo o entity e service
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if(service == null) {
			throw new IllegalStateException("Service was null");
		}
		
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListener();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DBException e) {
			Alerts.showAlert("Erro saving objeto", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	// quando houver uma alteração este será o metódo reponsável por notificar
	private void notifyDataChangeListener() {
		for(DataChangeListener listener: dataChangeListener ) {
			listener.onDataChange();
		}
	}

	private Department getFormData() {
		Department obj = new Department();
		
		// instancia a validação
		ValidationException exception = new ValidationException("Validate error");
		
		// o txtId não é permitido o usuário digitar, e estamos buscando por ele. Analistar melhor o pq disto, pq acho
		// que ele é autoincrement no banco, por isso que está dando certo esta operação
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "O campo nome não pode ser vazio.");
		}
		obj.setName(txtName.getText());
		
		// lanço a exceção caso encontre erros
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return obj;
	}

	@FXML
	public void onBtCancelAction() {
		System.out.println("onBtCancelAction");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}
	
	public void updateFormData() {
		// caso o programador esqueça de setar o entity
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		// quando carregar o update já seta como padrão a entidade que estou alterando
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}
	
	// seta o erro na Label
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if(fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}
}
