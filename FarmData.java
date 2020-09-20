package application;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * public class that is used to represent the data stored in all 4 types of data reports
 */
public class FarmData implements Comparable <FarmData>{
    private SimpleStringProperty date = new SimpleStringProperty("");
    private SimpleStringProperty month = new SimpleStringProperty("");
    private SimpleStringProperty farmID = new SimpleStringProperty("");
    private SimpleIntegerProperty weight = new SimpleIntegerProperty(0);;
    private SimpleStringProperty percent;


    /**
     * constructor used for reading from file
     * @param date
     * @param farmID
     * @param weight
     */
    public FarmData(String date, String month, String farmID, Integer weight){
        this.date = new SimpleStringProperty(date);
        this.month = new SimpleStringProperty(month);
        this.farmID = new SimpleStringProperty(farmID);
        this.weight = new SimpleIntegerProperty(weight);
    }

    /**
     * constructor used for adding to data set
     * @param date
     * @param farmID
     * @param weight
     */
    public FarmData(String date,  String farmID, Integer weight){
        this.date = new SimpleStringProperty(date);
        this.farmID = new SimpleStringProperty(farmID);
        this.weight = new SimpleIntegerProperty(weight);
    }

    /**
     * used for farm report
     * @param farmID
     * @param weight
     */
    public FarmData(String farmID, Integer weight) {
        this.farmID = new SimpleStringProperty(farmID);
        this.weight = new SimpleIntegerProperty(weight);
    }

    /**
     * used for farm report
     * @param month
     */
    public FarmData(String month) {
        this.month= new SimpleStringProperty(month);
    }


    @Override
    /**
     * used farm ID
     */
    public boolean equals(Object o){
        if(o == null) return false;
        if(this == o) return false;
        if(!(o instanceof FarmData)) return false;
        FarmData other = (FarmData) o;
        if(this.getDate().equals(other.getDate()) && this.getWeight().equals(other.getWeight()) && this.getFarmID().equals(other.getFarmID())) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return getWeight().hashCode();
    }


    // methods for date
    public void setDate(String date) {
        if (this.month == null) this.month = new SimpleStringProperty(date);
        this.month.set(date);
    }

    public String getDate() {
        if (this.date == null) this.date = new SimpleStringProperty("");
        return this.date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }

    // method for month
    public void setMonth(String month) {
        if (this.month == null) this.month = new SimpleStringProperty(month);
        this.month.set(month);
    }

    public String getMonth() {
        if (this.month == null) this.month = new SimpleStringProperty();
        return this.month.get();
    }

    public StringProperty monthProperty() {
        return month;
    }


    // methods for farmID field
    public void setFarmID(String farmID) {
        if(this.farmID == null) this.farmID = new SimpleStringProperty(farmID);
        this.farmID.set(farmID);
    }

    public String getFarmID() {
        if(this.farmID == null) this.farmID = new SimpleStringProperty();
        return farmID.get();
    }

    public SimpleStringProperty farmIDProperty() {
        return farmID;
    }


    // methods for weight field

    /**
     * sets weight will never be null since no every constructor required a weight
     * @param weight
     */
    public void setWeight(Integer weight) {
        this.weight.set(weight);
    }

    public Integer getWeight() {
        return weight.get();
    }

    public SimpleIntegerProperty weightProperty() {
        return weight;
    }

    public void addWeight(Integer additional){
        if(weight == null) weight = new SimpleIntegerProperty();

        // for some reason add doesn't function as expected so did this instead
        weight.set(weight.get() + additional);
    }


    // methods for percent field


    public void setPercent(String percent) {
        if(this.percent == null) this.percent = new SimpleStringProperty();
        this.percent.set(percent);
    }

    public String getPercent() {
        return percent.get();
    }

    public SimpleStringProperty percentProperty() {
        return percent;
    }

    @Override
    public String toString() {
        String s = "";
        if(date != null || !date.get().equals("")) s+= "Date: " + date.get() + "   ";
        if(farmID != null) s += "FarmID: " + farmID.get() + "   ";
        //if(month != null) s += "Month: " + month.get() + "   ";
        if(weight != null) s += "Weight: " + weight.get() + "   ";
        if(percent != null) s += "Percent: " + percent.get() + "   ";

        return s;
    }

    public String printToCsvFile(){
        return getDate() + "," + getFarmID() + "," + getWeight();
    }


    @Override
    public int compareTo(FarmData o) {
        // TODO Auto-generated method stub
        Integer comp1 = Integer.parseInt(this.getFarmID().substring(5).trim());
        Integer comp2 = Integer.parseInt(o.getFarmID().substring(5).trim());
        return comp1.compareTo(comp2);
    }
    public static void main(String[] args) {


    }

}
