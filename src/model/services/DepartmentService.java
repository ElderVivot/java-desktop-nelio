package model.services;

import java.util.ArrayList;
import java.util.List;
import model.entities.Department;

public class DepartmentService {
	
	public List<Department> findAll(){
		List<Department> departments = new ArrayList<Department>();
		
		// estou declarando alguns pra poder 'mockar' os dados.
		departments.add(new Department(1, "Books"));
		departments.add(new Department(2, "Computers"));
		
		return departments;
	}

}
