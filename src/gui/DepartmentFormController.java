package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {
	
	// eu preciso do obj Department pois quando eu carregar minha tela de DepartmentForm vou popular minhas caixas de texto
	// do formul�rio. E estes dados precisam ser buscados da entidade.
	// a 'populariza��o' � extremamente necess�rio principalmente quando j� existe o department e vamos apenas alter�-lo
	private Department entity;
	
	// o service � que vai gravar no BD os dados
	private DepartmentService service;
	
	// o DataChangeListener recebe um objeto dos eventos que est�o interessados em serem alterados
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
	
	// insere um novo objeto que est� interessado no padr�o Observer pra ser atualizado automaticamente
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListener.add(listener);
	}
	
	@FXML // pra poder fechar a janela preciso do parametro do ActionEvent
	public void onBtSaveAction(ActionEvent event) {
		// programa��o defensiva, vejo se est�o nulo o entity e service
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
		} catch (DBException e) {
			Alerts.showAlert("Erro saving objeto", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	// quando houver uma altera��o este ser� o met�do repons�vel por notificar
	private void notifyDataChangeListener() {
		for(DataChangeListener listener: dataChangeListener ) {
			listener.onDataChange();
		}
	}

	private Department getFormData() {
		Department obj = new Department();
		
		// o txtId n�o � permitido o usu�rio digitar, e estamos buscando por ele. Analistar melhor o pq disto, pq acho
		// que ele � autoincrement no banco, por isso que est� dando certo esta opera��o
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		obj.setName(txtName.getText());
		
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
		// caso o programador esque�a de setar o entity
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		// quando carregar o update j� seta como padr�o a entidade que estou alterando
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}
}
