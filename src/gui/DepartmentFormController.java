package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;

public class DepartmentFormController implements Initializable {
	
	// eu preciso do obj Department pois quando eu carregar minha tela de DepartmentForm vou popular minhas caixas de texto
	// do formul�rio. E estes dados precisam ser buscados da entidade.
	// a 'populariza��o' � extremamente necess�rio principalmente quando j� existe o department e vamos apenas alter�-lo
	private Department entity;
	
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
	
	@FXML
	public void onBtSaveAction() {
		System.out.println("onBtSaveAction");
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
