package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import com.google.gson.Gson;
import database.tables.EditTicketsTable;
import mainClasses.Ticket;

import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicketServlet extends HttpServlet
{
    private static final Logger LOGGER = Logger.getLogger(TicketServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        LOGGER.log(Level.SEVERE, "Get method is not supported on Servlet TicketsServlet");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        /* Set response content type */
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Read the JSON input from the request body
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader())
        {
            String line;
            while ((line = reader.readLine()) != null)
                jsonBuilder.append(line);
        }
        String jsonInput = jsonBuilder.toString();  // JSON data from the client

        // Log the incoming JSON for debugging
        System.out.println("Received JSON: " + jsonInput);

        // Parse the JSON into a Map using Gson
        Gson gson = new Gson();
        Map<String, Object> eventDetails = gson.fromJson(jsonInput, Map.class);

        // Retrieve the event details from the Map
        int eventId = Integer.parseInt(eventDetails.get("event_id").toString());
        int bookingId = Integer.parseInt(eventDetails.get("booking_id").toString());
        int vipSeats = Integer.parseInt(eventDetails.get("vip_seats").toString());
        int generalSeats = Integer.parseInt(eventDetails.get("general_seats").toString());
        int studentSeats = Integer.parseInt(eventDetails.get("student_seats").toString());
        int childSeats = Integer.parseInt(eventDetails.get("child_seats").toString());
        String vipPrice = eventDetails.get("vip_price").toString();
        String generalPrice = eventDetails.get("general_price").toString();
        String studentPrice = eventDetails.get("student_price").toString();
        String childPrice = eventDetails.get("child_price").toString();

        System.out.println(eventId);
        System.out.println(bookingId);
        System.out.println(vipSeats);
        System.out.println(generalSeats);
        System.out.println(studentSeats);
        System.out.println(childSeats);
        System.out.println(vipPrice);
        System.out.println(generalPrice);
        System.out.println(studentPrice);
        System.out.println(childPrice);

        if (vipPrice == null || generalPrice == null || studentPrice == null || childPrice == null)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            try (PrintWriter out = response.getWriter())
            {
                out.write("{\"error\": \"Invalid price parameters\", \"errorCode\": \"INVALID_PRICES\"}");
            }
            return;
        }

        try
        {
            for(int i=0; i<vipSeats; i++)
            {
                Ticket ticket = new Ticket(bookingId, eventId, "VIP", vipPrice, "1", "0");
                EditTicketsTable.addNewTicket(ticket);
            }
            for(int i=0; i<generalSeats; i++)
            {
                Ticket ticket = new Ticket(bookingId, eventId, "GENERAL_ADMISSION", generalPrice, "1", "0");
                EditTicketsTable.addNewTicket(ticket);
            }
            for(int i=0; i<studentSeats; i++)
            {
                Ticket ticket = new Ticket(bookingId, eventId, "STUDENT", studentPrice, "1", "0");
                EditTicketsTable.addNewTicket(ticket);
            }
            for(int i=0; i<childSeats; i++)
            {
                Ticket ticket = new Ticket(bookingId, eventId, "CHILD", childPrice, "1", "0");
                EditTicketsTable.addNewTicket(ticket);
            }
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\": \"Failed to init the tickets!\"}");
            return;
        }

        /* Send success response back to frontend */
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"message\": \"Tickets created successfully!\"}");
    }
}