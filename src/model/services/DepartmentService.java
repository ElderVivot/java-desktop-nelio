package model.services;

import java.util.ArrayList;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {
	
	DepartmentDao departmentDao = DaoFactory.createDepartmentDao();
	
	public List<Department> findAll(){
		return departmentDao.findAll();
	}
	
	public void saveOrUpdate(Department obj) {
		// quando o id é igual a null quer dizer que estou salvando um novo departamento no banco de dados
		if(obj.getId() == null) {
			departmentDao.insert(obj);
		} else {
			departmentDao.update(obj);
		}
	}

}
