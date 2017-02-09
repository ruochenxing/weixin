package com.coreService.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.coreService.entity.User;
/*
CREATE TABLE IF NOT EXISTS `tb_user` (
  `user_id` varchar(30) NOT NULL,
  `name` varchar(20),
  `age` varchar(16) DEFAULT NULL,
  `sex` varchar(20) DEFAULT NULL,
  `address` varchar(100) DEFAULT NULL,
  `like` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
*/
public class UserDao {
	private BaseDao dao;
	
	public UserDao(){
		dao=new BaseDao();
	}
	/**
	 * 根据ID查找用户
	 */
	public User queryUserById(String id){
		String sql = "select * from tb_user where user_id=?";
		Object[] params = new Object[] { id };
		dao.executeSQL(sql, params);
		ResultSet result = dao.getResultSet();
		try {
			if (result != null && result.next()) {
				User user=new User();
				user.setUserId(result.getString("user_id"));
				user.setName(result.getString("name"));
				user.setAge(result.getString("age"));
				user.setSex(result.getString("sex"));
				user.setAddress(result.getString("address"));
				user.setLike(result.getString("like"));
				return user;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close();
		}
		return null;		
	}
	/**
	 * 根据姓名查找用户
	 * */
	public User queryUserByName(String name) {
		String sql = "select * from tb_user where name=?";
		Object[] params = new Object[] { name };
		dao.executeSQL(sql, params);
		ResultSet result = dao.getResultSet();
		try {
			if (result != null && result.next()) {
				User user=new User();
				user.setUserId(result.getString("user_id"));
				user.setName(result.getString("name"));
				user.setAge(result.getString("age"));
				user.setSex(result.getString("sex"));
				user.setAddress(result.getString("address"));
				user.setLike(result.getString("like"));
				return user;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close();
		}
		return null;
	}
	/**
	 * 修改用户
	 * */
	public boolean updateUser(User user){
		String sql="update tb_user set name=?,age=?,sex=?,address=?,`like`=? where user_id=?";
		Object[] params=new Object[]{user.getName(),user.getAge(),user.getSex(),user.getAddress(),user.getLike(),user.getUserId()};
		dao.executeSQL(sql, params);
		int i=dao.getUpdateCount();
		close();
		return i==1;
	}
	
	/**
	 * 添加用户
	 * */
	public boolean addUser(User user){
		String sql="insert into tb_user values(?,?,?,?,?,?)";
		Object[] params=new Object[]{user.getUserId(),user.getName(),user.getAge(),user.getSex(),user.getAddress(),user.getLike()};
		dao.executeSQL(sql, params);
		int i=dao.getUpdateCount();
		close();
		return 1==i;
	}
	/**
	 * 删除用户
	 * */
	public boolean deleteUser(Integer id,String name){
		String sql="delete from tb_user ";
		Object []params;
		if(id==null&&name==null){
			return false;
		}
		if(id==null){
			sql+="where name=?";
			params=new Object[]{name};
		}
		else{
			sql+="where user_id=?";
			params=new Object[]{id};
		}
		dao.executeSQL(sql, params);
		int i=dao.getUpdateCount();
		close();
		return i==1;
	}
	private void close(){
		dao.close();
	}
}
