package servlets;

import database.tables.EditCustomersTable;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import database.tables.EditTicketsTable;
import mainClasses.Event;
import database.tables.EditEventsTable;
import mainClasses.Ticket;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Iterator;


public class RegisterServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(RegisterServlet.class.getName());
    /* getting the available events */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            ArrayList<Event> events = EditEventsTable.databaseToAvailableEvents();

            // Check if the event got created correctly
            if (events == null)
            {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                try (PrintWriter out = response.getWriter()) {
                    out.write("{\"error\": \"No events found\", \"errorCode\": \"NO_EVENTS_FOUND\"}");
                }
                return;
            }
            /* ------------------------------ */

            /* ------------------------------ */

            // Start building the JSON response
            StringBuilder jsonResponse = new StringBuilder("{\"events\": [");

            for (int i = 0; i < events.size(); i++)
            {
                Event event = events.get(i);

                ArrayList<Ticket> availableEventTickets = EditTicketsTable.databaseToAvailableTickets("1");

                // Check if the event got created correctly
                if (availableEventTickets == null)
                {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    try (PrintWriter out = response.getWriter()) {
                        out.write("{\"error\": \"No tickets found\", \"errorCode\": \"NO_TICKETS_FOUND\"}");
                    }
                    return;
                }

                availableEventTickets.removeIf(ticket -> ticket.getEvent_id() != event.getEvent_id());
                /*
                int size = availableEventTickets.size();
                System.out.println("Ο αριθμός των στοιχείων είναι: " + size);*/

                int availableVip = 0;
                int availableGeneral = 0;
                int availableStudent = 0;
                int availableChild = 0;
                System.out.println("Eftasa");
                float totalPayment = 0;

                for (Ticket ticket : availableEventTickets)
                {
                    switch (ticket.getSeat_type())
                    {
                        case "VIP":
                            availableVip++;
                            totalPayment += Float.parseFloat(ticket.getTicket_price());
                            break;
                        case "GENERAL_ADMISSION":
                            availableGeneral++;
                            totalPayment += Float.parseFloat(ticket.getTicket_price());
                            break;
                        case "STUDENT":
                            availableStudent++;
                            totalPayment += Float.parseFloat(ticket.getTicket_price());
                            break;
                        case "CHILD":
                            availableChild++;
                            totalPayment += Float.parseFloat(ticket.getTicket_price());
                            break;
                    }
                }


                // Start a new JSON object for each event
                jsonResponse.append("{")
                        .append("\"event_id\": ").append(event.getEvent_id()).append(", ")
                        .append("\"event_name\": \"").append(event.getEvent_name()).append("\", ")
                        .append("\"event_date\": \"").append(event.getEvent_date()).append("\", ")
                        .append("\"total_payment\": ").append(totalPayment).append(", ")
                        .append("\"vip_available_seats\": ").append(availableVip).append(", ")
                        .append("\"general_available_seats\": ").append(availableGeneral).append(", ")
                        .append("\"student_available_seats\": ").append(availableStudent).append(", ")
                        .append("\"child_available_seats\": ").append(availableChild)
                        .append("}");

                // Προσθήκη κόμματος αν δεν είναι το τελευταίο αντικείμενο
                if (i < events.size() - 1) {
                    jsonResponse.append(", ");
                }
            }

            jsonResponse.append("]}");

            // Write the JSON response to the output
            try (PrintWriter out = response.getWriter()) {
                out.write(jsonResponse.toString());
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\": \"Error fetching events\"}");
        }
    }

    /* add to database the new customer (register) */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Heyyy");
        /* Set response content type */
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        /* Read JSON from request body */
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }
        String jsonInput = jsonBuilder.toString();

        System.out.println(jsonInput);

        /* Process the JSON data */
        EditCustomersTable eut = new EditCustomersTable();
        try {
            eut.addCustomerFromJSON(jsonInput);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\": \"Failed to add customer!\"}");
            return;
        }

        /* Send success response back to frontend */
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"message\": \"Customer added successfully!\"}");
    }
}