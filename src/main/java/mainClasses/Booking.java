package mainClasses;

/**
 * @author TEAM 4
 */
public class Booking
{
    private int booking_id;

    private int customer_id;

    private int event_id;

    private String ticket_count;

    private String booking_date;

    private String total_payment;

    /* SETTERS - GETTERS */

    /* getter - customerName */

    public int getCustomer_id() {
        return customer_id;
    }

    /* setter - customerName */

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    /* getter - EventId */

    public int getEvent_id() {
        return event_id;
    }

    /* setter - EventId */

    public void setEvent_id(int EventId) {
        this.event_id = EventId;
    }

    /* getter - ticketCount */

    public String getTicket_count() {
        return ticket_count;
    }

    /* setter - ticketCount */

    public void setTicket_count(String ticket_count) {
        this.ticket_count = ticket_count;
    }

    /* getter - bookingDate */

    public String getBooking_date() {
        return booking_date;
    }

    /* setter - bookingDate */

    public void setBooking_date(String booking_date) {
        this.booking_date = booking_date;
    }

    /* getter - totalPayment */

    public String getTotal_payment() {
        return total_payment;
    }

    /* setter - totalPayment */

    public void setTotal_payment(String total_payment) {
        this.total_payment = total_payment;
    }

    public int getBooking_id() {return booking_id;}

    public void setBooking_id(int booking_id) {
        this.booking_id = booking_id;}
}
