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
import lk.ijse.studentmanagementmain.dao.StudentData;
import lk.ijse.studentmanagementmain.dao.impl.StudentDataProcess;
import lk.ijse.studentmanagementmain.dto.StudentDTO;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet(urlPatterns = "/student")
public class StudentController extends HttpServlet {
    Connection connection;
    StudentData studentData = new StudentDataProcess();
    @Override
    public void init() throws ServletException {
        /*try {

            var driverclass = getServletContext().getInitParameter("driver-class");
            var dbURL = getServletContext().getInitParameter("dbURL");
            var dbUserName = getServletContext().getInitParameter("dbUserName");
            var dbPassword = getServletContext().getInitParameter("dbPassword");

            Class.forName(driverclass);
            this.connection = DriverManager.getConnection(dbURL, dbUserName, dbPassword);


        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }*/
        try {
            var ctx = new InitialContext();
            DataSource pool = (DataSource) ctx.lookup("java:comp/env/jdbc/stuRegistration");
            this.connection =  pool.getConnection();
        }catch (NamingException | SQLException e){
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
        /*String id = UUID.randomUUID().toString();*/
        Jsonb jsonb = JsonbBuilder.create();
        StudentDTO studentDTO = jsonb.fromJson(req.getReader(), StudentDTO.class);
        //persist student data
            boolean isSaved = studentData.saveStudent(studentDTO,connection);
            if (isSaved) {
                resp.getWriter().write("Save student");
                resp.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                resp.getWriter().write("unable to save student");
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
    }catch(
    Exception e)

    {
        e.printStackTrace();
        resp.getWriter().write("unable to save student");
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
        StudentDTO studentDTO ;
        try {
            studentDTO = studentData.getStudent(stuId,connection);
            System.out.println(studentDTO);
            if (studentDTO.getId()==null){
                resp.getWriter().write("Wrong Id Please try again !!!");
            }else {
                resp.getWriter().write(studentDTO.toString());
                System.out.println("all clear");
            }

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
        String stuId = req.getParameter("id");

        try {
            boolean isDelete = studentData.deleteStudent(stuId,connection);
            if (isDelete){
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
        StudentDTO studentDTO = new StudentDTO(stuId,stuName,stuEmail,stuCity,stuLevel);

        try {
            boolean isUpdate = studentData.updateStudent(studentDTO,connection);

            if (isUpdate) {
                resp.getWriter().write("Update student Successfully !!!");
            } else {
                resp.getWriter().write("unable to Update !! Please try again !!!");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
