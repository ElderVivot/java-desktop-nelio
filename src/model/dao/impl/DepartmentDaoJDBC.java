package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DBException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao{
	
	private Connection conexao;
	
	public DepartmentDaoJDBC(Connection conexao) {
		this.conexao = conexao;
	}

	@Override
	public void insert(Department obj) {
		PreparedStatement st = null;
		try {
			st = conexao.prepareStatement(
					"INSERT INTO department "
					+ "(Name) "
					+ "VALUES (?)",Statement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getName());
			
			int rowsAffected = st.executeUpdate();
			if( rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DBException("Erro inesperado, nenhuma linha inserida.");
			}
		} catch(SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Department obj) {
		PreparedStatement st = null;
		try {
			st = conexao.prepareStatement(
					"UPDATE department SET Name = ? WHERE Id = ?");
			st.setString(1, obj.getName());
			st.setInt(2, obj.getId());
			
			int rowsAffected = st.executeUpdate();
			if(rowsAffected > 0 ) {
				System.out.println("Atualização do ID " + obj.getId() + " feita com sucesso");
			} else {
				throw new DBException("Erro Inesperado. Nenhum registro atualizado");
			}
			
		} catch(SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conexao.prepareStatement(
					"DELETE FROM department WHERE Id = ?");
			st.setInt(1, id);
			
			int rowsAffected = st.executeUpdate();
			if(rowsAffected > 0 ) {
				System.out.println("Remoção do ID " + id + " feita com sucesso");
			} else {
				throw new DBException("Erro Inesperado. Nenhum registro atualizado");
			}
			
		} catch(SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Department findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conexao.prepareStatement(
					"SELECT department.* " + 
					"  FROM department" + 
					" WHERE department.Id = ?");
			
			st.setInt(1, id);
			
			rs = st.executeQuery();
			
			// o primeiro resultado que o ResultSet retorna são o nome das colunas, por isto que tem que ser
			// feito um rs.next()
			if(rs.next()) {
				
				// instancia os models pelo resultado que trouxe --> criamos métodos pra deixar mais organizado
				Department department = instantiateDepartment(rs);
				
				return department;
			}
			
			// caso o rs.next() não retorne nada quer dizer que não encontrou nenhum Department com este Id
			// Portanto, tem que retornar null o resultado
			return null;
		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conexao.prepareStatement(
					"SELECT department.* " + 
					"  FROM department");
			rs = st.executeQuery();
			
			List<Department> departments = new ArrayList<>();
			
			while(rs.next()) {
				Department department = instantiateDepartment(rs);
				departments.add(department);
			}
			return departments;
		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}
	
	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department department = new Department();
		department.setId(rs.getInt("Id"));
		department.setName(rs.getString("Name"));
		return department;
	}

}
