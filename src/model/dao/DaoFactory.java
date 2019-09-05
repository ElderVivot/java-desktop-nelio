package model.dao;

import db.DB;
import model.dao.impl.DepartmentDaoJDBC;
import model.dao.impl.SellerDaoJDBC;

// Factory � um padr�o de programa��o onde retorna sempre um objeto
public class DaoFactory {
	
	public static SellerDao createSellerDao() {
		// na hora de retornar o resultado j� faz a conex�o com o banco
		return new SellerDaoJDBC(DB.getConnection());
	}

	public static DepartmentDao createDepartmentDao() {
		// na hora de retornar o resultado j� faz a conex�o com o banco
		return new DepartmentDaoJDBC(DB.getConnection());
	}
}
