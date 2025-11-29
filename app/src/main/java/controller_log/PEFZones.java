package controller_log;

import com.example.smart_air_app.utils.DateValidator;

public class PEFZones {
    private int pb;
    private int highest_pef;
    private String date;

    //this is used in helperPB it's basically a constructor but this way we just adjust the given PEFZone
    public void initializePEF(int pb, int highest_pef, String date) {
        this.pb = pb;
        this.highest_pef = highest_pef;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Personal best: " + pb + "\nHighest PEF: " + highest_pef + "\ndate: " + date;
    }

    //calculates the zone (red, yellow, green or N/A if pb isn't given or highest_pef isn't given
    public String calculateZone() {
        if (date == null || pb == -1 || date.equals("") || highest_pef < 0) {
            return "N/A";
        }

        double ratio = (highest_pef * 1.0)/pb;

        String currDate = DateValidator.getTodaysDate();

        //the highest_pef wasn't set
        if (!currDate.equals(date)) {
            return "N/A";
        }

        if (ratio >= 0.8) {
            return "Green";
        }
        else if (ratio >= 0.5) {
            return "Yellow";
        }
        else {
            return "Red";
        }
    }

    public void setPB(int pb) {
        //if the pb has been set today then we only update the pb if the given pb > the current pb
        if (pb > this.pb) {
            this.pb = pb;
        }
    }

    public void setHighest_pef(int highest_pef) {
        String currDate = DateValidator.getTodaysDate();

        //if the highest pef hasn't been set today then we save it immediately and the date
        if (!date.equals(currDate)) {
            this.highest_pef = highest_pef;
            this.date = currDate;
        }

        //if the highest pef has been saved today, then save if the given pef is greater than the current pef
        if (highest_pef > this.highest_pef) {
            this.highest_pef = highest_pef;
        }
    }

    //this forces to set the highest_pef and date without validation USE AT OWN RISK!!!!
    public void forceSetHighest_pef(int highest_pef, String date) {
        this.highest_pef = highest_pef;
        this.date = date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPB() {
        return pb;
    }

    public int getHighest_pef() {
        return highest_pef;
    }

    public String getDate() {
        return date;
    }
}
