package mainClasses;

/** @author TEAM 4 */
public class Event{

    private int event_id;

    private String type_event;

    private String event_name;

    private String event_date;

    private String start_time;

    private int capacity;

    /* SETTERS - GETTERS */

    /* getter - eventID */

    public int getEvent_id(){return event_id;}

    /* setter - eventID */

    public void setEvent_id(int event_id){
        this.event_id = event_id;
    }

    /* getter - typeEvent */

    public String getType_event(){
        return type_event;
    }

    /* setter - typeEvent*/

    public void setType_event(String type_event){
        this.type_event = type_event;
    }

    /* getter - nameEvent */

    public String getEvent_name(){
        return event_name;
    }

    /* setter - nameEvent */

    public void setEvent_name(String event_name){
        this.event_name = event_name;
    }

    /* getter - dateEvent */

    public String getEvent_date(){
        return event_date;
    }

    /* setter - dateEvent */

    public void setEvent_date(String event_date){
        this.event_date = event_date;
    }

    /* getter - startTime */

    public String getStart_time(){
        return start_time;
    }

    /* setter - startTime */

    public void setStart_time(String start_time){
        this.start_time = start_time;
    }

    /* getter - capacity */

    public int getCapacity(){
        return capacity;
    }

    /* setter - capacity */

    public void setCapacity(int capacity){
        this.capacity=capacity;
    }
}
