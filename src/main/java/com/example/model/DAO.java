package com.example.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
		try (
				Connection conn = DriverManager.getConnection(url);
				PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM todo where id=?");
			) {
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return new ToDo(
						rs.getInt("id"),
						rs.getString("title"),
						LocalDate.parse(rs.getString("date")),
						rs.getInt("priority"),
						rs.getInt("completed") == 1
				);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<ToDo> getAll() {
		var todos = new ArrayList<ToDo>();
		try (
				Connection conn = DriverManager.getConnection(url);
				PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM todo");
			) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				todos.add(new ToDo(
						rs.getInt("id"),
						rs.getString("title"),
						LocalDate.parse(rs.getString("date")),
						rs.getInt("priority"),
						rs.getInt("completed") == 1
				));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return todos;
	}

	public ToDo create(String title, LocalDate date, int priority, boolean completed) {
		try (
				Connection conn = DriverManager.getConnection(url);
				PreparedStatement pstmt = conn.prepareStatement(
						"INSERT INTO todo(title, date, priority, completed) VALUES(?, ?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS);
			) {
			pstmt.setString(1, title);
			pstmt.setString(2, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			pstmt.setInt(3, priority);
			pstmt.setInt(4, completed ? 1 : 0);
			pstmt.executeUpdate();

			// AUTOINCREMENTで生成された id を取得します。
			ResultSet rs = pstmt.getGeneratedKeys();
			rs.next();
			return new ToDo(
					rs.getInt(1),
					title,
					date,
					priority,
					completed
			);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

    private void updateField(String query, int id, Object value) {
        try (
        		Connection conn = DriverManager.getConnection(url);
        		PreparedStatement pstmt = conn.prepareStatement(query);
        	) {
            pstmt.setObject(1, value);
            pstmt.setInt(2, id);
            int num = pstmt.executeUpdate();
            if (num <= 0) {
                System.out.println("id " + id + " の行はありません。");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	public void updateTitle(int id, String title) {
        updateField("UPDATE todo SET title=? WHERE id=?", id, title);
    }

    public void updateDate(int id, LocalDate date) {
        updateField("UPDATE todo SET date=? WHERE id=?", id, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    public void updateCompleted(int id, boolean completed) {
        updateField("UPDATE todo SET completed=? WHERE id=?", id, completed ? 1 : 0);
    }
    
    // ここは発展課題で追加
	public void updatePriority(int id, int priority) {
		updateField("UPDATE todo SET priority=? WHERE id=?", id, priority);
	}

    public void delete(int id) {
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM todo WHERE id=?")) {
            pstmt.setInt(1, id);
            int num = pstmt.executeUpdate();
            if (num <= 0) {
                System.out.println("id " + id + " の行はありません。");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void deleteAll() {
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM todo")) {
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
