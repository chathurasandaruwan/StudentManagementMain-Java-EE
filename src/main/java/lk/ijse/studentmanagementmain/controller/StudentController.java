package lk.ijse.studentmanagementmain.controller;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.studentmanagementmain.dto.StudentDTO;

import java.io.IOException;
import java.io.Writer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet(urlPatterns = "/student")
public class StudentController extends HttpServlet {
    Connection connection;
    static String SAVE_STUDENT = "INSERT INTO students(id,name,city,email,level)VALUE(?,?,?,?,?)";
    static String GET_STUDENT = "SELECT * FROM students WHERE id = ?";
    static String DELETE = "DELETE FROM students WHERE id = ?";
    static String UPDATE_STUDENT = "UPDATE students SET name =? , city = ?, email =?,level=? WHERE id = ?";

    @Override
    public void init() throws ServletException {
        try {

            var driverclass = getServletContext().getInitParameter("driver-class");
            var dbURL = getServletContext().getInitParameter("dbURL");
            var dbUserName = getServletContext().getInitParameter("dbUserName");
            var dbPassword = getServletContext().getInitParameter("dbPassword");

            Class.forName(driverclass);
            this.connection = DriverManager.getConnection(dbURL, dbUserName, dbPassword);


        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

            if (!req.getContentType().toLowerCase().startsWith("application/json") || req.getContentType() == null) {
//            send error
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        }
        try {
        String id = UUID.randomUUID().toString();
        Jsonb jsonb = JsonbBuilder.create();
        List<StudentDTO> studentDTO = jsonb.fromJson(req.getReader(), new ArrayList<StudentDTO>() {
        }.getClass().getGenericSuperclass());

        studentDTO.forEach(System.out::println);
        //persist student data

        PreparedStatement preparedStatement = connection.prepareStatement(SAVE_STUDENT);
        for (StudentDTO stu : studentDTO) {
            preparedStatement.setString(1, stu.getId());
            preparedStatement.setString(2, stu.getName());
            preparedStatement.setString(3, stu.getEmail());
            preparedStatement.setString(4, stu.getCity());
            preparedStatement.setString(5, stu.getLevel());

            if (preparedStatement.executeUpdate() != 0) {
                resp.getWriter().write("Save student");
            } else {
                resp.getWriter().write("unable to save student");
            }
        }

    }catch(
    Exception e)

    {
        e.printStackTrace();
    }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!req.getContentType().toLowerCase().startsWith("application/json") || req.getContentType() == null) {
//            send error
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        }
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();
        String stuId = jsonObject.getString("id");
        System.out.println(stuId);
        StudentDTO studentDTO = new StudentDTO();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_STUDENT);
            preparedStatement.setString(1, stuId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                studentDTO.setId(resultSet.getString("id"));
                studentDTO.setName(resultSet.getString("name"));
                studentDTO.setEmail( resultSet.getString("email"));
                studentDTO.setCity( resultSet.getString("city"));
                studentDTO.setLevel( resultSet.getString("level"));
            }
            System.out.println(studentDTO);
            resp.getWriter().write(studentDTO.toString());
            System.out.println("all clear");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!req.getContentType().toLowerCase().startsWith("application/json") || req.getContentType() == null) {
//            send error
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        }
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();
        String stuId = jsonObject.getString("id");
        System.out.println(stuId);

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE);
            preparedStatement.setString(1,stuId);
            if (preparedStatement.executeUpdate()>0){
                resp.getWriter().write(stuId+" : Delete successfully!!!");
            }else {
                resp.getWriter().write("Some thing wrong!! Please Try again!!!");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!req.getContentType().toLowerCase().startsWith("application/json") || req.getContentType() == null) {
//            send error
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        }
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();
        String stuId = jsonObject.getString("id");
        String stuName = jsonObject.getString("name");
        String stuEmail = jsonObject.getString("email");
        String stuCity = jsonObject.getString("city");
        String stuLevel = jsonObject.getString("level");

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_STUDENT);
            preparedStatement.setString(1,stuName);
            preparedStatement.setString(2,stuEmail);
            preparedStatement.setString(3,stuCity);
            preparedStatement.setString(4,stuLevel);
            preparedStatement.setString(5,stuId);

            if (preparedStatement.executeUpdate() != 0) {
                resp.getWriter().write("Update student Successfully !!!");
            } else {
                resp.getWriter().write("unable to Update !! Please try again !!!");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
