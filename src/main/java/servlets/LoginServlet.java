package servlets;

import com.google.gson.JsonObject;
import database.DB_Connection;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "LoginServlet", value = "/LoginServlet")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("Heyyyy");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        /* getting username and password from the request parameter */

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        JsonObject responseJson = new JsonObject();

        /* database connection via DB_Connection class and create the SQL statement */

        try (Connection con = DB_Connection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM customers WHERE customer_username = ? AND customer_password = ?")) {

            /* replace the placeholders (?) with the real values */

            ps.setString(1, username);
            ps.setString(2, password);

            /* Execute the query */

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                /* found */
                responseJson.addProperty("success", true); /* success login */

                responseJson.addProperty("username", rs.getString("customer_username")); /* success login */
                responseJson.addProperty("password", rs.getString("customer_password")); /* success login */
                responseJson.addProperty("id",rs.getString("customer_id"));

            }else{
                /* username with this password not found */
                responseJson.addProperty("success", false);
            }
            /* send the JSON object back to ajax */

            response.getWriter().write(responseJson.toString());
            System.out.println(responseJson);
        }catch (Exception e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Server error\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}