package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {
	
	private Connection conn;

	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) {
		
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement(
					"INSERT INTO seller " 
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES (?, ?, ?, ?, ?)" , Statement.RETURN_GENERATED_KEYS
					);
			
			st.setString(1,obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			
			st.execute();
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
		}
	}


	@Override
	public void update(Seller obj) {
		
		PreparedStatement st = null;
		
		try {				
			st = conn.prepareStatement(
					"UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
					+ "WHERE Id = ? "
					);
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			
			st.setInt(6, obj.getId());
			
			st.executeUpdate();
			
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
		}
		

	}

	@Override
	public void deleteById(Integer id) {
		
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM seller " + "WHERE Id = ?");
			st.setInt(1, id);

			st.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);

		}

	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " + "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " + "WHERE seller.Id = ?");
			st.setInt(1, id);

			rs = st.executeQuery();

			if (rs.next()) {

				Department dep = instanteateDepartment(rs);
				Seller obj = instanteateSeller(rs, dep);

				return obj;
			}

			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);

		}
	}

	private Department instanteateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));

		return dep;
	}

	private Seller instanteateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller obj = new Seller();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		//obj.setBirthDate(rs.getDate("BirthDate")); -> ir� instanciar o "java.sql.Date"
		obj.setBirthDate(new java.util.Date(rs.getTimestamp("BirthDate").getTime()));
		obj.setDepartment(dep);

		return obj;
	}

	@Override
	public List<Seller> findAll() {

		List<Seller> list = new ArrayList<>();

		Statement st = null;
		ResultSet rs = null;

		try {
			st = conn.createStatement();
			rs = st.executeQuery("SELECT seller.*, department.Name as DepName " // <-as 2 tabelas a serem exibidas |
																				// department.Name as DepName � para n�o
																				// ficar 2 variaveis iguais "Name"
					+ "FROM seller INNER JOIN department " // comando para junta as 2 tabelas com a cond. abaixo
					+ "ON seller.DepartmentId = department.id" // <-condi��o
			);

			while (rs.next()) {
				Department dep = instanteateDepartment(rs);
				Seller obj = instanteateSeller(rs, dep);
				list.add(obj);
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		return list;
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " 
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id " 
					+ "WHERE DepartmentId = ? "
					+ "ORDER BY Name"
							);
			st.setInt(1, department.getId());

			rs = st.executeQuery();
			
			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();

			while (rs.next()) {
				
				Department dep = map.get(rs.getInt("DepartmentId"));
				
				if(dep == null) {
					dep = instanteateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}

				Seller obj = instanteateSeller(rs, dep);
				list.add(obj);
			}

			return list;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);

		}
	}

}
