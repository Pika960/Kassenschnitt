package de.gabrieldaennermedien.kassenschnitt;

//imports
import androidx.annotation.NonNull;

class DbEntry {
    //private instances
    private final String day;
    private final String year;
    private final String weekday;

    //private values
    private final double money;
    private final long   id;

    /**
     * Constructor.
     * @param day the day of the entry.
     * @param year the year of the entry.
     * @param weekday the weekday of the entry.
     * @param money the money of the day/entry.
     * @param id the id of the entry (primary key).
     */
    DbEntry(String day, String year, String weekday, double money, long id) {
        this.day     = day;
        this.year    = year;
        this.weekday = weekday;
        this.money   = money;
        this.id      = id;
    }

    /**
     * getDay returns the day of the entry.
     * @return the day of the entry.
     */
    String getDay() {
        return day;
    }

    /**
     * getYear returns the year of the entry.
     * @return the year of the entry.
     */
    String getYear() {
        return year;
    }

    /**
     * getWeekday returns the weekday of the entry.
     * @return the weekday of the entry.
     */
    String getWeekday() {
        return weekday;
    }

    /**
     * getMoney returns the money of the entry.
     * @return the money of the entry.
     */
    double getMoney() {
        return money;
    }

    /**
     * getId returns the value of the primary key.
     * @return the id of the entry.
     */
    long getId() {
        return id;
    }

    /**
     * makeMoney2Decimal converts the money value to a string with 2 decimal places.
     * @return the converted money value.
     */
    private String makeMoney2Decimal() {
        try {
            String[] content = Double.toString(money).split("\\.");

            if (content[1].length() < 2) {
                content[1] += "0";
            }

            return content[0] + "." + content[1];
        } catch (Exception exc) {
            return Double.toString(money);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String toString() {
        return day + "." + year + " (" + StringConversionHelper.convertWeekdayToShort(weekday)
                + ")" + ": " + makeMoney2Decimal()
                + App.getContext().getString(R.string.result_currency);
    }
}
