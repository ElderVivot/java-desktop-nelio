package model.dao;

import db.DB;
import model.dao.impl.DepartmentDaoJDBC;
import model.dao.impl.SellerDaoJDBC;

// Factory é um padrão de programação onde retorna sempre um objeto
public class DaoFactory {
	
	public static SellerDao createSellerDao() {
		// na hora de retornar o resultado já faz a conexão com o banco
		return new SellerDaoJDBC(DB.getConnection());
	}

	public static DepartmentDao createDepartmentDao() {
		// na hora de retornar o resultado já faz a conexão com o banco
		return new DepartmentDaoJDBC(DB.getConnection());
	}
}
