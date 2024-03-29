package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * DAO for ToDo App
 */
public class DAO {
	private String url;

	public DAO(String url) {
		this.url = url;
		// DriverManger に org.sqlite.JDBC クラス（JDBCドライバ)を登録する処理
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public ToDo get(int id) {
		ToDo todo = null;
		try (
				Connection conn = DriverManager.getConnection(url);
				PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM todo where id=?");
			) {
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				int myId = rs.getInt("id");
				String title = rs.getString("title");
				String date = rs.getString("date");
				int completedStr = rs.getInt("completed");
				todo = new ToDo(myId, title, date, completedStr == 1 ? true : false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return todo;
	}

	public ArrayList<ToDo> getAll() {
		var todos = new ArrayList<ToDo>();
		try (
				Connection conn = DriverManager.getConnection(url);
				PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM todo");
			) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int myId = rs.getInt("id");
				String title = rs.getString("title");
				String date = rs.getString("date");
				int completedStr = rs.getInt("completed");
				todos.add(new ToDo(myId, title, date, completedStr == 1 ? true : false));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return todos;
	}

	public ToDo create(String title, String date, boolean completed) {
		ToDo todo = null;
		try (
				Connection conn = DriverManager.getConnection(url);
				PreparedStatement pstmt = conn
						.prepareStatement("INSERT INTO todo(title, date, completed) VALUES(?, ?, ?)");
				Statement stmt = conn.createStatement();
			) {
			pstmt.setString(1, title);
			pstmt.setString(2, date);
			pstmt.setInt(3, completed ? 1: 0);
			pstmt.execute();

			// 追加された行のidを取得（autoincrement の場合、最後の行のidに等しい）
			ResultSet rs = stmt.executeQuery("SELECT seq FROM sqlite_sequence WHERE name=\"todo\"");
			rs.next();
			int id = rs.getInt("seq");

			todo = new ToDo(id, title, date, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return todo;
	}

	public void updateTitle(int id, String title) {
		try (
				Connection conn = DriverManager.getConnection(url);
				PreparedStatement pstmt = conn.prepareStatement("UPDATE todo SET title=(?) WHERE id=?");
			) {
			pstmt.setString(1, title);
			pstmt.setInt(2, id);
			int num = pstmt.executeUpdate();
			if (num <= 0) {
				System.out.println("id " + id + " の行はありません。");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateDate(int id, String date) {
		try (
				Connection conn = DriverManager.getConnection(url);
				PreparedStatement pstmt = conn.prepareStatement("UPDATE todo SET date=(?) WHERE id=?");
			) {
			pstmt.setString(1, date);
			pstmt.setInt(2, id);
			int num = pstmt.executeUpdate();
			if (num <= 0) {
				System.out.println("id " + id + " の行はありません。");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateCompleted(int id, boolean completed) {
		try (
				Connection conn = DriverManager.getConnection(url);
				PreparedStatement pstmt = conn.prepareStatement("UPDATE todo SET completed=(?) WHERE id=?");
			) {
			pstmt.setInt(1, completed ? 1 : 0);
			pstmt.setInt(2, id);
			int num = pstmt.executeUpdate();
			if (num <= 0) {
				System.out.println("id " + id + " の行はありません。");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete(int id) {
		try (
				Connection conn = DriverManager.getConnection(url);
				PreparedStatement pstmt = conn.prepareStatement("DELETE from todo WHERE id=?");
			) {
			pstmt.setInt(1, id);
			int num = pstmt.executeUpdate();
			if (num <= 0) {
				System.out.println("id " + id + " の行はありません。");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
