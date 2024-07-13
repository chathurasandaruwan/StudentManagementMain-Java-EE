package lk.ijse.studentmanagementmain.dao.impl;

import lk.ijse.studentmanagementmain.dao.StudentData;
import lk.ijse.studentmanagementmain.dto.StudentDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDataProcess implements StudentData {
    static String SAVE_STUDENT = "INSERT INTO students(id,name,city,email,level)VALUE(?,?,?,?,?)";
    static String GET_STUDENT = "SELECT * FROM students WHERE id = ?";
    static String DELETE = "DELETE FROM students WHERE id = ?";
    static String UPDATE_STUDENT = "UPDATE students SET name =? , city = ?, email =?,level=? WHERE id = ?";
    @Override
    public boolean saveStudent(StudentDTO studentDTO, Connection connection) {
        return false;
    }

    @Override
    public StudentDTO getStudent(String stuId, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(GET_STUDENT);
        StudentDTO studentDTO = new StudentDTO();
        preparedStatement.setString(1, stuId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            studentDTO.setId(resultSet.getString("id"));
            studentDTO.setName(resultSet.getString("name"));
            studentDTO.setEmail( resultSet.getString("email"));
            studentDTO.setCity( resultSet.getString("city"));
            studentDTO.setLevel( resultSet.getString("level"));
        }
        return studentDTO;
    }

    @Override
    public String updateStudent(StudentDTO studentDTO, Connection connection) {
        return null;
    }

    @Override
    public boolean deleteStudent(String id, Connection connection) {
        return false;
    }
}
