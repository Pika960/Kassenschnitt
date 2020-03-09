package de.gabrieldaennermedien.kassenschnitt;

/**
 * StringConversionHelper is a helper class which has the purpose to convert strings to a
 * different format.
 */
class StringConversionHelper {
    //private static instances
    private static final String month_january   = App.getContext().getString(R.string.month_january);
    private static final String month_february  = App.getContext().getString(R.string.month_february);
    private static final String month_march     = App.getContext().getString(R.string.month_march);
    private static final String month_april     = App.getContext().getString(R.string.month_april);
    private static final String month_may       = App.getContext().getString(R.string.month_may);
    private static final String month_june      = App.getContext().getString(R.string.month_june);
    private static final String month_july      = App.getContext().getString(R.string.month_july);
    private static final String month_august    = App.getContext().getString(R.string.month_august);
    private static final String month_september = App.getContext().getString(R.string.month_september);
    private static final String month_october   = App.getContext().getString(R.string.month_october);
    private static final String month_november  = App.getContext().getString(R.string.month_november);
    private static final String month_december  = App.getContext().getString(R.string.month_december);

    private static final String weekday_monday    = App.getContext().getString(R.string.weekday_monday);
    private static final String weekday_tuesday   = App.getContext().getString(R.string.weekday_tuesday);
    private static final String weekday_wednesday = App.getContext().getString(R.string.weekday_wednesday);
    private static final String weekday_thursday  = App.getContext().getString(R.string.weekday_thursday);
    private static final String weekday_friday    = App.getContext().getString(R.string.weekday_friday);
    private static final String weekday_saturday  = App.getContext().getString(R.string.weekday_saturday);
    private static final String weekday_sunday    = App.getContext().getString(R.string.weekday_sunday);

    private static final String weekday_monday_short    = App.getContext().getString(R.string.weekday_monday_short);
    private static final String weekday_tuesday_short   = App.getContext().getString(R.string.weekday_tuesday_short);
    private static final String weekday_wednesday_short = App.getContext().getString(R.string.weekday_wednesday_short);
    private static final String weekday_thursday_short  = App.getContext().getString(R.string.weekday_thursday_short);
    private static final String weekday_friday_short    = App.getContext().getString(R.string.weekday_friday_short);
    private static final String weekday_saturday_short  = App.getContext().getString(R.string.weekday_saturday_short);
    private static final String weekday_sunday_short    = App.getContext().getString(R.string.weekday_sunday_short);

    /**
     * monthToNumber converts the name of a month to the corresponding number.
     * @param month the month which should be converted.
     * @return the number of the month.
     */
    static String monthToNumber(String month) {
        if (month.equalsIgnoreCase(month_january.toLowerCase())) {
            return ".01";
        }

        else if (month.toLowerCase().contains(month_february.toLowerCase())) {
            return ".02";
        }

        else if (month.toLowerCase().contains(month_march.toLowerCase())) {
            return ".03";
        }

        else if (month.toLowerCase().contains(month_april.toLowerCase())) {
            return ".04";
        }

        else if (month.toLowerCase().contains(month_may.toLowerCase())) {
            return ".05";
        }

        else if (month.toLowerCase().contains(month_june.toLowerCase())) {
            return ".06";
        }

        else if (month.toLowerCase().contains(month_july.toLowerCase())) {
            return ".07";
        }

        else if (month.toLowerCase().contains(month_august.toLowerCase())) {
            return ".08";
        }

        else if (month.toLowerCase().contains(month_september.toLowerCase())) {
            return ".09";
        }

        else if (month.toLowerCase().contains(month_october.toLowerCase())) {
            return ".10";
        }

        else if (month.toLowerCase().contains(month_november.toLowerCase())) {
            return ".11";
        }

        else if (month.toLowerCase().contains(month_december.toLowerCase())) {
            return ".12";
        }

        else {
            return ".01";
        }
    }

    /**
     * numberToMonth converts the number of a month to the corresponding name.
     * @param number the month which should be converted.
     * @return the name of the month.
     */
    static String numberToMonth(String number) {
        if (number.contains(".01")) {
            return month_january;
        }

        else if (number.contains(".02")) {
            return month_february;
        }

        else if (number.contains(".03")) {
            return month_march;
        }

        else if (number.contains(".04")) {
            return month_april;
        }

        else if (number.contains(".05")) {
            return month_may;
        }

        else if (number.contains(".06")) {
            return month_june;
        }

        else if (number.contains(".07")) {
            return month_july;
        }

        else if (number.contains(".08")) {
            return month_august;
        }

        else if (number.contains(".09")) {
            return month_september;
        }

        else if (number.contains(".10")) {
            return month_october;
        }

        else if (number.contains(".11")) {
            return month_november;
        }

        else if (number.contains(".12")) {
            return month_december;
        }

        else {
            return "";
        }
    }

    /**
     * convertWeekdayToShort converts the full name of a weekday to the corresponding short one.
     * @param weekday the full name of the day.
     * @return the short name of the day.
     */
    static String convertWeekdayToShort(String weekday) {
        if (weekday.equalsIgnoreCase(weekday_monday)) {
            return weekday_monday_short;
        }

        else if (weekday.equalsIgnoreCase(weekday_tuesday)) {
            return weekday_tuesday_short;
        }

        else if (weekday.equalsIgnoreCase(weekday_wednesday)) {
            return weekday_wednesday_short;
        }

        else if (weekday.equalsIgnoreCase(weekday_thursday)) {
            return weekday_thursday_short;
        }

        else if (weekday.equalsIgnoreCase(weekday_friday)) {
            return weekday_friday_short;
        }

        else if (weekday.equalsIgnoreCase(weekday_saturday)) {
            return weekday_saturday_short;
        }

        else if (weekday.equalsIgnoreCase(weekday_sunday)) {
            return weekday_sunday_short;
        }

        else {
            return "Err";
        }
    }
}
