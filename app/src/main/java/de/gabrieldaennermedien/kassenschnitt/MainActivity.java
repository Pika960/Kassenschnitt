package de.gabrieldaennermedien.kassenschnitt;

//imports
import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * MainActivity is the core activity of the application and is started when the the app starts.
 */
public class MainActivity extends AppCompatActivity {
    //private instances
    private DataSource dataSource;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        initializeContextualActionBar();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            dataSource = new DataSource(this, false);
        }

        else {
            dataSource = new DataSource(this, true);
        }

        dataSource.open();
        showAllListEntries();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        super.onPause();

        dataSource.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_item) {
            AlertDialog newEntryDialog = createNewEntryDialog();
            newEntryDialog.show();

            return true;
        }

        if (id == R.id.action_calc_specific_month) {
            AlertDialog calcEntryDialog = createSearchEntryMonthDialog();
            calcEntryDialog.show();

            return true;
        }

        if (id == R.id.action_calc_specific_year) {
            AlertDialog calcEntryDialog = createSearchEntryYearDialog();
            calcEntryDialog.show();

            return true;
        }

        if (id == R.id.action_calc_all_months) {
            double[] array = dataSource.getWholeMoneyAsSum();
            double money = array[1] / array[0];

            if (Double.isNaN(money)) {
                money = 0.0D;
            }

            NumberFormat n = NumberFormat.getInstance();
            n.setMaximumFractionDigits(2);

            String msg = getString(R.string.result_complete_1) + (int)array[0]
                    + getString(R.string.result_complete_2) + " " + n.format(array[1])
                    + getString(R.string.result_complete_3)
                    + "\n\n" + getString(R.string.result_average) + ": " + n.format(money)
                    + getString(R.string.result_currency);
            showInfoAlert(getString(R.string.msg_result), msg);

            return true;
        }

        if (id == R.id.action_best_in_year) {
            AlertDialog bestInYearDialog = createBestEntriesInYearDialog();
            bestInYearDialog.show();

            return true;
        }

        if(id == R.id.action_change_orentation) {
            final int orientation = getResources().getConfiguration().orientation;

            switch(orientation) {
                case Configuration.ORIENTATION_PORTRAIT:
                    setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case Configuration.ORIENTATION_LANDSCAPE:
                    setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
            }
        }

        if (id == R.id.action_about) {
            Toast.makeText(MainActivity.this, getString(R.string.app_name) + "\n" +
                    getString(R.string.app_version) + "\n" + getString(R.string.copyright_info)
                    + " " + getString(R.string.company_name), Toast.LENGTH_LONG).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * initializeContextualActionBar initializes the context action bar.
     */
    private void initializeContextualActionBar() {
        final ListView entriesListView = findViewById(R.id.listview_entries);
        entriesListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        entriesListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            int itemCount = 0;

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked) {
                    itemCount++;
                }

                else {
                    itemCount--;
                }

                String cabTitle = itemCount + " " + getString(R.string.cab_checked_string);
                mode.setTitle(cabTitle);
                mode.invalidate();
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.menu_contextual_action_bar, menu);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                MenuItem item = menu.findItem(R.id.cab_change);

                if (itemCount == 1) {
                    item.setVisible(true);
                }

                else {
                    item.setVisible(false);
                }

                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                boolean returnValue = true;
                SparseBooleanArray touchedEntriesPositions = entriesListView.getCheckedItemPositions();

                switch (item.getItemId()) {
                    case R.id.cab_delete:
                        ArrayList<DbEntry> dbEntryToDeleteList = new ArrayList<>();

                        for (int i = 0; i < touchedEntriesPositions.size(); i++) {
                            boolean isChecked = touchedEntriesPositions.valueAt(i);

                            if (isChecked) {
                                int positionInListView = touchedEntriesPositions.keyAt(i);
                                DbEntry dbEntry = (DbEntry) entriesListView.getItemAtPosition(positionInListView);
                                dbEntryToDeleteList.add(dbEntry);
                            }
                        }

                        showDeleteEntryDialog(getString(R.string.msg_confirmation),
                                (getString(R.string.confirmation_1) + itemCount
                                        + getString(R.string.confirmation_2)), dbEntryToDeleteList);
                        showAllListEntries();
                        mode.finish();
                        break;

                    case R.id.cab_change:
                        for (int i = 0; i < touchedEntriesPositions.size(); i++) {
                            boolean isChecked = touchedEntriesPositions.valueAt(i);

                            if (isChecked) {
                                int positionInListView = touchedEntriesPositions.keyAt(i);
                                DbEntry dbEntry = (DbEntry) entriesListView.getItemAtPosition(positionInListView);

                                AlertDialog editEntryDialog = createEditEntryDialog(dbEntry);
                                editEntryDialog.show();
                            }
                        }

                        mode.finish();
                        break;

                    default:
                        returnValue = false;
                        break;
                }
                return returnValue;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                itemCount = 0;
            }
        });
    }

    /**
     * dateStringToWeekday converts a date sequence into the corresponding weekday.
     * @param date the date which should be converted.
     * @return the requested weekday.
     */
    private String dateStringToWeekday(String date) {
        String dateString;

        try {
            String[] content   = date.split("\\.");
            String parsingDate = content[0] + "/" + content[1] + "/" + content[2];

            SimpleDateFormat newDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.GERMAN);
            Date             customDate    = newDateFormat.parse(parsingDate);

            newDateFormat.applyPattern("EEEE");

            if (customDate != null) {
                dateString = newDateFormat.format(customDate);
            }

            else {
                dateString = "ERROR";
            }
        }
        catch(Exception exc) {
            dateString = "ERROR";
        }

        return dateString;
    }

    /**
     * showAllListEntries refreshes the ListView to show all database entries.
     */
    private void showAllListEntries () {
        List<DbEntry> entries = dataSource.getAllEntries();

        ArrayAdapter<DbEntry> entryArrayAdapter = new ArrayAdapter<DbEntry> (this,
                android.R.layout.simple_list_item_multiple_choice, entries) {
            @Override
            @NonNull
            public View getView(int position, View view, @NonNull ViewGroup viewGroup) {
                View v = super.getView(position, view, viewGroup);
                ((TextView)v).setTypeface(Typeface.MONOSPACE);
                return v;
            }
        };

        ListView entriesListView = findViewById(R.id.listview_entries);
        entriesListView.setAdapter(entryArrayAdapter);
    }

    /**
     * createBestEntriesInYearDialog creates a dialog which can be used to search for the best days
     * from a year in the db.
     * @return the created dialog.
     */
    private AlertDialog createBestEntriesInYearDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,
                R.style.AppCompatAlertDialogStyle);
        LayoutInflater inflater = getLayoutInflater();

        final ViewGroup nullParent = null;
        View dialogsView = inflater.inflate(R.layout.dialog_search, nullParent);

        final EditText editTextSearch = dialogsView.findViewById(R.id.editText_search);

        Calendar calender = Calendar.getInstance();
        SimpleDateFormat year = new SimpleDateFormat("yyyy", Locale.GERMAN);

        String predefinedYear = year.format(calender.getTime());

        editTextSearch.setText(predefinedYear);

        builder.setView(dialogsView)
                .setTitle(getString(R.string.dialog_search))
                .setPositiveButton(getString(R.string.button_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String yearString = editTextSearch.getText().toString();

                        if ((TextUtils.isEmpty(yearString))) {
                            showInfoAlert(getString(R.string.msg_error),
                                    getString(R.string.error_empty_search));
                            return;
                        }

                        if (yearString.length() == 4) {
                            List<DbEntry> entries = dataSource.getBestDaysInYear(yearString);

                            StringBuilder sb = new StringBuilder(getString(R.string.result_statistics)
                                    + yearString + ":\n\n");

                            for(DbEntry entry : entries) {
                                sb.append(entry.toString());
                                sb.append("\n");
                            }

                            showInfoAlertMonospace(getString(R.string.msg_statistics), sb.toString());
                        }

                        else {
                            showInfoAlert(getString(R.string.msg_error),
                                    getString(R.string.error_dennis_nedry));
                        }

                        showAllListEntries();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.button_negative), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    /**
     * createEditEntryDialog creates a dialog which can be used to edit an entry of the db.
     * @param entry the entry which should be edited
     * @return the created dialog.
     */
    private AlertDialog createEditEntryDialog(final DbEntry entry) {
        AlertDialog.Builder builder  = new AlertDialog.Builder(MainActivity.this,
                R.style.AppCompatAlertDialogStyle);
        LayoutInflater inflater = getLayoutInflater();

        final ViewGroup nullParent = null;
        View dialogsView = inflater.inflate(R.layout.dialog_edit_item, nullParent);

        final EditText editTextNewDay = dialogsView.findViewById(R.id.editText_new_day);
        editTextNewDay.setText(String.valueOf(entry.getDay()));

        final EditText editTextNewYear = dialogsView.findViewById(R.id.editText_new_year);
        editTextNewYear.setText(entry.getYear());

        final EditText editTextNewWeekday = dialogsView.findViewById(R.id.editText_new_weekday);
        editTextNewWeekday.setText(entry.getWeekday());


        final EditText editTextNewMoney = dialogsView.findViewById(R.id.editText_new_money);
        editTextNewMoney.setText(String.format(Locale.US, "%.2f", entry.getMoney()));

        builder.setView(dialogsView)
                .setTitle(getString(R.string.dialog_edit_item))
                .setPositiveButton(getString(R.string.button_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String dayString     = editTextNewDay.getText().toString();
                        String yearString    = editTextNewYear.getText().toString();
                        String weekdayString = editTextNewWeekday.getText().toString();
                        String moneyString   = editTextNewMoney.getText().toString();

                        if(TextUtils.isEmpty(dayString) || TextUtils.isEmpty(yearString)
                                || TextUtils.isEmpty(weekdayString)
                                || TextUtils.isEmpty(moneyString)) {
                            showInfoAlert(getString(R.string.msg_error),
                                    getString(R.string.error_elements_missing));
                            return;
                        }

                        double money = Double.parseDouble(moneyString);
                        dataSource.updateEntry(entry.getId(), dayString, yearString,
                                weekdayString, money);

                        showAllListEntries();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.button_negative), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    /**
     * createNewEntryDialog creates a dialog which can be used to add a new item to the db.
     * @return the created dialog.
     */
    private AlertDialog createNewEntryDialog() {
        AlertDialog.Builder builder  = new AlertDialog.Builder(MainActivity.this,
                R.style.AppCompatAlertDialogStyle);
        LayoutInflater inflater = getLayoutInflater();

        final ViewGroup nullParent = null;
        View dialogsView = inflater.inflate(R.layout.dialog_new_item, nullParent);

        final EditText editTextDay   = dialogsView.findViewById(R.id.editText_day);
        final EditText editTextYear  = dialogsView.findViewById(R.id.editText_year);
        final EditText editTextMoney = dialogsView.findViewById(R.id.editText_money);

        Calendar         calender = Calendar.getInstance();
        SimpleDateFormat dayMonth = new SimpleDateFormat("dd.MM", Locale.GERMAN);
        SimpleDateFormat year     = new SimpleDateFormat("yyyy", Locale.GERMAN);

        final String predefinedDay  = dayMonth.format(calender.getTime());
        final String predefinedYear = year.format(calender.getTime());
        final String predefinedHour = "400";

        editTextDay.setText(predefinedDay);
        editTextYear.setText(predefinedYear);
        editTextMoney.setText(predefinedHour);

        builder.setView(dialogsView)
                .setTitle(getString(R.string.dialog_add_item))
                .setPositiveButton(getString(R.string.button_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String dayString = editTextDay.getText().toString();
                        String yearString = editTextYear.getText().toString();
                        String moneyString = editTextMoney.getText().toString();

                        if(TextUtils.isEmpty(dayString) || TextUtils.isEmpty(yearString)
                                || TextUtils.isEmpty(moneyString)) {
                            showInfoAlert(getString(R.string.msg_error),
                                    getString(R.string.error_elements_missing));
                            return;
                        }

                        double money = Double.parseDouble(moneyString);
                        final String weekdayString = dateStringToWeekday(dayString + "." + yearString);

                        dataSource.createEntry(dayString, yearString, weekdayString, money);

                        showAllListEntries();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.button_negative), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    /**
     * createSearchEntryMonthDialog creates a dialog which can be used to search for a specific
     * year in the db.
     * @return the created dialog.
     */
    private AlertDialog createSearchEntryMonthDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,
                R.style.AppCompatAlertDialogStyle);
        LayoutInflater inflater = getLayoutInflater();

        final ViewGroup nullParent = null;
        View dialogsView = inflater.inflate(R.layout.dialog_search, nullParent);

        final EditText editTextSearch = dialogsView.findViewById(R.id.editText_search);

        Calendar calender = Calendar.getInstance();
        SimpleDateFormat dayMonth = new SimpleDateFormat("dd.MM", Locale.GERMAN);
        SimpleDateFormat year = new SimpleDateFormat("yyyy", Locale.GERMAN);

        String part1 = dayMonth.format(calender.getTime());
        String part2 = year.format(calender.getTime());

        final String predefinedDay = part1 + " " + part2;
        String displayedMsg = StringConversionHelper.numberToMonth(predefinedDay) + " " + part2;

        editTextSearch.setText(displayedMsg);

        builder.setView(dialogsView)
                .setTitle(getString(R.string.dialog_search))
                .setPositiveButton(getString(R.string.button_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String dayString = editTextSearch.getText().toString();
                        String[] content;

                        if ((TextUtils.isEmpty(dayString))) {
                            showInfoAlert(getString(R.string.msg_error),
                                    getString(R.string.error_empty_search));
                            return;
                        }

                        if (dayString.contains(" ") && !dayString.endsWith(" ")
                                && dayString.length() >= 8) {
                            content = dayString.split(" ");

                            double[] array = dataSource.getMonthlyMoneyAsSum(content[0], content[1]);
                            double   money = array[1] / array[0];

                            if (Double.isNaN(money)) {
                                money = 0.0D;
                            }

                            NumberFormat n = NumberFormat.getInstance();
                            n.setMaximumFractionDigits(2);

                            String msg = getString(R.string.result_month_1) + (int)array[0]
                                    + getString(R.string.result_month_2) + " " + n.format(array[1])
                                    + getString(R.string.result_month_3)
                                    + "\n\n" + getString(R.string.result_average) + ": "
                                    + n.format(money) + getString(R.string.result_currency);
                            showInfoAlert(getString(R.string.msg_result), msg);
                        }

                        else {
                            showInfoAlert(getString(R.string.msg_error),
                                    getString(R.string.error_dennis_nedry));
                        }

                        showAllListEntries();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.button_negative), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    /**
     * createSearchEntryYearDialog creates a dialog which can be used to search for a specific
     * year in the db.
     * @return the created dialog.
     */
    private AlertDialog createSearchEntryYearDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,
                R.style.AppCompatAlertDialogStyle);
        LayoutInflater inflater = getLayoutInflater();

        final ViewGroup nullParent = null;
        View dialogsView = inflater.inflate(R.layout.dialog_search, nullParent);

        final EditText editTextSearch = dialogsView.findViewById(R.id.editText_search);

        Calendar calender = Calendar.getInstance();
        SimpleDateFormat year = new SimpleDateFormat("yyyy", Locale.GERMAN);

        String predefinedYear = year.format(calender.getTime());

        editTextSearch.setText(predefinedYear);

        builder.setView(dialogsView)
                .setTitle(getString(R.string.dialog_search))
                .setPositiveButton(getString(R.string.button_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String yearString = editTextSearch.getText().toString();

                        if ((TextUtils.isEmpty(yearString))) {
                            showInfoAlert(getString(R.string.msg_error),
                                    getString(R.string.error_empty_search));
                            return;
                        }

                        if (yearString.length() == 4) {
                            double[] array = dataSource.getYearlyMoneyAsSum(yearString);
                            double   money = array[1] / array[0];

                            if (Double.isNaN(money)) {
                                money = 0.0D;
                            }

                            NumberFormat n = NumberFormat.getInstance();
                            n.setMaximumFractionDigits(2);

                            String msg = getString(R.string.result_year_1) + yearString + " ("
                                    + (int)array[0]
                                    + getString(R.string.result_year_2) + " " + n.format(array[1])
                                    + getString(R.string.result_year_3)
                                    + "\n\n" + getString(R.string.result_average) + ": "
                                    + n.format(money) + getString(R.string.result_currency);
                            showInfoAlert(getString(R.string.msg_result), msg);
                        }

                        else {
                            showInfoAlert(getString(R.string.msg_error),
                                    getString(R.string.error_dennis_nedry));
                        }

                        showAllListEntries();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.button_negative), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    /**
     * showDeleteEntryDialog creates a dialog which can be used to ensure that the user really wants
     * to perform this operation.
     * @param entries list with entries which will be delete if requested
     */
    private void showDeleteEntryDialog(String title, String msg, ArrayList<DbEntry> entries) {
        final ArrayList<DbEntry> entriesToBeDeleted = entries;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,
                R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(getString(R.string.button_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for(DbEntry dbEntry : entriesToBeDeleted) {
                    dataSource.deleteEntry(dbEntry);
                }

                showAllListEntries();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.button_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    /**
     * showInfoAlert creates a simple dialog with a title, text and an okay button
     * @param title title of the dialog
     * @param msg main text/message of the dialog
     */
    private void showInfoAlert(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,
                R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(getString(R.string.button_positive), null);
        builder.show();
    }

    /**
     * showInfoAlertMonospace creates a simple dialog with a title, text and an okay button
     * @param title title of the dialog
     * @param msg main text/message of the dialog
     */
    private void showInfoAlertMonospace(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,
                R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(getString(R.string.button_positive), null);

        AlertDialog dialog = builder.show();

        TextView textView = dialog.findViewById(android.R.id.message);

        if(textView != null) {
            textView.setTypeface(Typeface.MONOSPACE);
        }
    }
}
