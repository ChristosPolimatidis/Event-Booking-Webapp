/**
 * Project hy360_2024
 * Customer class
 * Contains all the fields for the customer java class (with setters, getters)
 *
 * @Author Team 4
 */
package mainClasses;

public class Customer
{
    /** ID of the customer (IDENTIFIER FIELD) */
    private int customer_id;

    /** Username of the customer (UNIQUE)*/
    private String customer_username;

    /** Password of the Customer*/
    private String customer_password;

    /** First name of the customer */
    private String customer_firstname;

    /** Last name of the customer */
    private String customer_lastname;

    /** Email of the customer */
    private String customer_email;

    /** Number of the customer's card */
    private String customer_card_number;

    /** Full name Owner of the customer's card*/
    private String customer_card_owner;

    /** Expiration date of the customer's card */
    private String customer_card_expiration;

    /** CVV of the customer's Card */
    private int customer_card_cvv;

    /** Money refunded to the customer*/
    private String money_refunded = "0";

    /**
     * SETTERS AND GETTERS of all the fields
     */
    public int getCustomer_id() {return customer_id;}

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;}

    public String getCustomer_firstname() {return customer_firstname;}

    public void setCustomer_firstname(String customer_firstname) {
        this.customer_firstname = customer_firstname;}

    public String getCustomer_lastname() {return customer_lastname;}

    public void setCustomer_lastname(String customer_lastname) {
        this.customer_lastname = customer_lastname;}

    public String getCustomer_email() {return customer_email;}

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;}

    public String getCustomer_card_number() {return customer_card_number;}

    public void setCustomer_card_number(String customer_card_number) {
        this.customer_card_number = customer_card_number;}

    public String getCustomer_card_expiration() {return customer_card_expiration;}

    public void setCustomer_card_expiration(String customer_card_expiration) {
        this.customer_card_expiration = customer_card_expiration;}

    public int getCustomer_card_cvv() {return customer_card_cvv;}

    public void setCustomer_card_cvv(int customer_card_cvv) {
        this.customer_card_cvv = customer_card_cvv;}

    public String getCustomer_card_owner() {return customer_card_owner;}

    public void setCustomer_card_owner(String customer_card_owner) {
        this.customer_card_owner = customer_card_owner;}

    public String getCustomer_username() {return customer_username;}

    public void setCustomer_username(String customer_username) {
        this.customer_username = customer_username;}

    public String getCustomer_password() {return customer_password;}

    public void setCustomer_password(String customer_password) {
        this.customer_password = customer_password;}

    public String getMoney_refunded() {return money_refunded;}

    public void setMoney_refunded(String money_refunded) {
        this.money_refunded = money_refunded;}
}