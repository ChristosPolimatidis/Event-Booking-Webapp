/**
 * Project hy360_2024
 * Ticket class
 * Contains all the fields for the ticket java class (with setters, getters)
 *
 * @Author Team 4
 */
package mainClasses;

public class Ticket
{
    /** ID of the ticket */
    private int ticket_id;

    /** ID of the booking this ticket belongs too */
    private int booking_id;

    /** ID of the event, the ticket belongs to */
    private int event_id;

    /** SeatType enum of the ticket */
    private String seat_type;

    /** Price of the ticket */
    private String ticket_price;

    /** Availability of the ticket */
    private String ticket_availability;

    /** Status of the ticket [used (false) or new (true)]*/
    private String ticket_checked;

    public Ticket(int booking_id, int event_id, String seat_type, String ticket_price,
                  String ticket_availability, String ticket_checked)
    {
        this.booking_id = booking_id;
        this.event_id = event_id;
        setSeat_type(seat_type);
        this.ticket_price = ticket_price;
        this.ticket_availability = ticket_availability;
        this.ticket_checked = ticket_checked;
    }

    /**
     * SETTERS AND GETTERS of all the fields
     */
    public int getTicket_id() {return ticket_id;}

    public void setTicket_id(int ticket_id) {
        this.ticket_id = ticket_id;}

    public String getTicket_price() {return ticket_price;}

    public void setTicket_price(String ticket_price) {this.ticket_price = ticket_price;}

    public String getTicket_availability() {return ticket_availability;}

    public void setTicket_availability(String ticket_availability) {this.ticket_availability = ticket_availability;}

    public String getTicket_checked() {return ticket_checked;}

    public void setTicket_checked(String ticket_checked) {this.ticket_checked = ticket_checked;}

    public int getEvent_id() {return event_id;}

    public void setEvent_id(int event_id) {this.event_id = event_id;}

    public int getBooking_id() {return booking_id;}

    public void setBooking_id(int booking_id) {this.booking_id = booking_id;}

    public String getSeat_type() {return seat_type;}

    public void setSeat_type(String seat_type) {this.seat_type = seat_type;}
}