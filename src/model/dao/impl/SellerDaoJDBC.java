package model.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DBException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {
	
	private Connection conexao;
	
	// toda vez que eu instancia a SellerDaoJDBC já vai criar a conexão
	public SellerDaoJDBC(Connection conexao) {
		this.conexao = conexao;
	}

	@Override
	public void insert(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conexao.prepareStatement(
					"INSERT INTO seller "
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES "
					+ "(?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			
			int rowsAffected = st.executeUpdate();
			if(rowsAffected > 0 ) {
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					// como Id no banco é autoIncrement, então quando eu instancio o obj Seller do entities
					// não passo Id. E deixo pra pegar ele agora, com o Id automático que gerou
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DBException("Erro Inesperado. Nenhuma linha inserida");
			}
			
		} catch(SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conexao.prepareStatement(
					"UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
					+ "WHERE Id = ?");
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			st.setInt(6, obj.getId());
			
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
					"DELETE FROM seller "
					+ "WHERE id = ?");
			st.setInt(1, id);
			
			int rowsAffected = st.executeUpdate();
			if(rowsAffected > 0 ) {
				System.out.println("Exclusão do ID " + id + " feita com sucesso");
			} else {
				throw new DBException("Erro Inesperado. Nenhuma linha inserida");
			}
			
		} catch(SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conexao.prepareStatement(
					"SELECT seller.*,department.Name as DepartmentName\r\n" + 
					"  FROM seller INNER JOIN department\r\n" + 
					"    ON seller.DepartmentId = department.Id\r\n" + 
					" WHERE seller.Id = ?");
			
			st.setInt(1, id);
			
			rs = st.executeQuery();
			
			// o primeiro resultado que o ResultSet retorna são o nome das colunas, por isto que tem que ser
			// feito um rs.next()
			if(rs.next()) {
				
				// instancia os models pelo resultado que trouxe --> criamos métodos pra deixar mais organizado
				Department department = instantiateDepartment(rs);
				
				Seller seller = instantiateSeller(rs, department);
				
				return seller;
			}
			
			// caso o rs.next() não retorne nada quer dizer que não encontrou nenhum Seller com este Id
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
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conexao.prepareStatement(
					"SELECT seller.*,department.Name as DepartmentName\r\n" + 
					"  FROM seller INNER JOIN department\r\n" + 
					"    ON seller.DepartmentId = department.Id\r\n");
			
			rs = st.executeQuery();
			
			List<Seller> sellers = new ArrayList<>();
			
			Map<Integer, Department> map = new HashMap<>();
			
			while(rs.next()) {
				
				int idDepto = rs.getInt("DepartmentId");
				
				Department department_ = map.get(idDepto);
				if(department_ == null) {
					department_ = instantiateDepartment(rs);
					map.put(idDepto, department_);
				}
						
				Seller seller = instantiateSeller(rs, department_);
				
				sellers.add(seller);
			}
			return sellers;
		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conexao.prepareStatement(
					"SELECT seller.*,department.Name as DepartmentName\r\n" + 
					"  FROM seller INNER JOIN department\r\n" + 
					"    ON seller.DepartmentId = department.Id\r\n" + 
					" WHERE department.Id = ?");
			
			st.setInt(1, department.getId());
			
			rs = st.executeQuery();
			
			List<Seller> sellers = new ArrayList<>();
			
			// vai servir pra controlar a verificação se o Department já existe, afim de ficar instanciando
			// ele toda hora
			Map<Integer, Department> map = new HashMap<>();
			
			// pode ter mais de um valor, visto que estou buscando pelo ID do depto, e pode ter vários
			// vendedores vinculados à este
			while(rs.next()) {
				
				int idDepto = rs.getInt("DepartmentId");
				
				// verifica se já existe um depto instanciado, se não retornar nada pelo id quer dizer que não existe
				Department department_ = map.get(idDepto);
				if(department_ == null) {
					department_ = instantiateDepartment(rs);
					map.put(idDepto, department_);
				}
						
				Seller seller = instantiateSeller(rs, department_);
				
				sellers.add(seller);
			}
			return sellers;
		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}
	
	// métodos de instanciação para auxílio
	private Seller instantiateSeller(ResultSet rs, Department department) throws SQLException {
		
		Seller seller = new Seller();
		seller.setId(rs.getInt("Id"));
		seller.setName(rs.getString("Name"));
		seller.setEmail(rs.getString("Email"));
		seller.setBaseSalary(rs.getDouble("BaseSalary"));
		seller.setBirthDate(rs.getDate("BirthDate"));
		seller.setDepartment(department);
		
		return seller;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department department = new Department();
		department.setId(rs.getInt("DepartmentId"));
		department.setName(rs.getString("DepartmentName"));
		return department;
	}
}
