package com.example.login;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class AnalysisActivity extends AppCompatActivity {

    TextView txtStandard;
    TextView txtExpenseSpecific;
    TextView txtTotalExpenses;
    TextView txtTotalIncome;
    TextView txtCurrentBalance;
    TextView txtSummary;
    TextView txtSummarySpecific;
    TextView txtTotalSpending;
    View lilo_standard_analysis;
    View lilo_specific_analysis;
    BarChart bcMonthlyAnalysis;
    PieChart pcSpecificAnalysis;
    Spinner spinnerMonth;
    Spinner spinnerYear;
    ArrayList<String> months;
    ArrayList<String> years;
    LinkedList<Transaction> transactions;
    ArrayList<BarEntry> barIncome;
    ArrayList<BarEntry> barExpense;
    ArrayList<PieEntry> pieExpense;
    int[] weeklyIncome;
    int[] weeklyExpense;
    HashMap<String, Integer> expenses;
    int monthSelected;
    int yearSelected;
    String maxExpense = "noExpense";
    int maxExpenseAmount = Integer.MIN_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_analysis);

        txtStandard = findViewById(R.id.txtStandard);
        txtExpenseSpecific = findViewById(R.id.txtExpenseSpecific);
        txtTotalExpenses = findViewById(R.id.txtTotalExpenses);
        txtTotalIncome = findViewById(R.id.txtTotalIncome);
        txtCurrentBalance = findViewById(R.id.txtCurrentBalance);
        txtSummary = findViewById(R.id.txtSummary);
        txtSummarySpecific = findViewById(R.id.txtSummarySpecific);
        txtTotalSpending = findViewById(R.id.txtTotalSpending);
        lilo_standard_analysis = findViewById(R.id.lilo_standard_analysis);
        lilo_specific_analysis = findViewById(R.id.lilo_specific_analysis);
        bcMonthlyAnalysis = findViewById(R.id.bcMonthlyAnalysis);
        pcSpecificAnalysis = findViewById(R.id.pcSpecificAnalysis);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear = findViewById(R.id.spinnerYear);

        loadMonths();
        loadYears();
        loadTransactions();

        // setting the adapter for the spinnerMonth
        ArrayAdapter<String> monthsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthsAdapter);


        // default selected month for the spinner (january)
        spinnerMonth.setSelection(0);
        monthSelected = 1;


        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                monthSelected = position + 1;
                standardAnalysisFormatter();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // for the spinnerYear
        ArrayAdapter<String> yearsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearsAdapter);

        // setting the default spinner selections to 2024
        spinnerYear.setSelection(0);
        yearSelected = 2024;


        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    yearSelected = 2024;
                }

                if (position == 1) {
                    yearSelected = 2023;
                }

                if (position == 2) {
                    yearSelected = 2022;
                }

                standardAnalysisFormatter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        updateIncomeExpense(monthSelected, yearSelected);



        txtStandard.setSelected(true);

        txtExpenseSpecific.setOnClickListener(v -> {
            txtExpenseSpecific.setSelected(true);
            txtStandard.setSelected(false);

            lilo_specific_analysis.setVisibility(View.VISIBLE);
            lilo_standard_analysis.setVisibility(View.GONE);

        });

        txtStandard.setOnClickListener(v -> {
            txtStandard.setSelected(true);
            txtExpenseSpecific.setSelected(false);

            lilo_standard_analysis.setVisibility(View.VISIBLE);
            lilo_specific_analysis.setVisibility(View.GONE);
        });

    }


    private void standardAnalysisFormatter() {
        updateIncomeExpense(monthSelected, yearSelected);

        pieExpense = new ArrayList<>();

        barIncome = new ArrayList<>();
        barExpense = new ArrayList<>();

        int sumIncome = 0;
        int sumExpense = 0;

        for (Map.Entry<String, Integer> expense: expenses.entrySet()) {
            pieExpense.add(new PieEntry(expense.getValue(), expense.getKey()));

            if (expense.getValue() > maxExpenseAmount) {
                maxExpenseAmount = expense.getValue();
                maxExpense = expense.getKey();
            }
        }

        PieDataSet pieDataSet = new PieDataSet(pieExpense, "Expenses");
        PieData pieData = new PieData(pieDataSet);
        pcSpecificAnalysis.setData(pieData);


        // visual for pie chart
        PieChartVisualLoader(pieDataSet);

        pcSpecificAnalysis.invalidate();



        for (int i = 1; i <= weeklyIncome.length; i++) {
            barIncome.add(new BarEntry(i, weeklyIncome[i - 1]));
            barExpense.add(new BarEntry(i + 0.3f, weeklyExpense[i - 1]));


            sumIncome += weeklyIncome[i - 1];
            sumExpense += weeklyExpense[i - 1];
        }

        BarDataSet barExpenseDataSet = new BarDataSet(barExpense, "expense");
        BarDataSet barIncomeDataSet = new BarDataSet(barIncome, "income");
        BarData barData = new BarData(barIncomeDataSet, barExpenseDataSet);
        bcMonthlyAnalysis.setData(barData);

        barChartVisualLoader(barExpenseDataSet, barIncomeDataSet, barData);


        // Setting the texts
        txtTotalExpenses.setText(sumExpense + " VND");
        txtTotalIncome.setText(sumIncome + " VND");

        String tempText;
        if (sumIncome > sumExpense) {
            tempText = "You got more money then you spend! Well done! Do it like this next months too.";
        } else if (sumIncome < sumExpense) {
            tempText = "You are spending too much. Your income is lower then your spending. Please reconsider before you spend!";
        } else {
            tempText = "What a coincidence! Your income and your expenses are the same!";
        }

        txtSummary.setText(String.format("In this month, your total expenses is %d VND and your total income is %d VND.%n%s", sumExpense, sumIncome, tempText));

        txtTotalSpending.setText(sumExpense + " VND");

        if (sumExpense == 0) {
            txtSummarySpecific.setText("");
        } else {
            txtSummarySpecific.setText(String.format("For this month, you spent the most on %s. The spending on %s is %d VND. Reconsider agiain before spending on %s next time!", maxExpense, maxExpense, maxExpenseAmount, maxExpense));
        }

    }


    private List<Integer> generateColors(int count) {
        List<Integer> colors = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            colors.add(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        }

        return colors;
    }

    private void PieChartVisualLoader(PieDataSet pieDataSet) {
        pcSpecificAnalysis.setDrawHoleEnabled(false);
        pcSpecificAnalysis.setDrawSliceText(false);
        pcSpecificAnalysis.setExtraOffsets(20f, 10f, 20f, 10f);
        pcSpecificAnalysis.setDescription(null);
        pcSpecificAnalysis.setDrawRoundedSlices(true);

        pcSpecificAnalysis.animateY(1000);

        List<Integer> colors = generateColors(pieExpense.size());
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(Color.BLACK);

        Legend legend = pcSpecificAnalysis.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER); // Aligns vertically in the center
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT); // Aligns horizontally to the right
        legend.setOrientation(Legend.LegendOrientation.VERTICAL); // Makes the legend items vertical
        legend.setTextColor(Color.WHITE);
        legend.setYEntrySpace(10f);
        legend.setDrawInside(false);
    }

    private void barChartVisualLoader(BarDataSet barExpenseDataSet, BarDataSet barIncomeDataSet, BarData barData) {
        barExpenseDataSet.setColors(Color.parseColor("#DB69A8"));
        barExpenseDataSet.setValueTextColor(Color.WHITE);
        barExpenseDataSet.setValueTextSize(4f);

        barIncomeDataSet.setColors(Color.parseColor("#A2DFA3"));
        barIncomeDataSet.setValueTextSize(4f);
        barIncomeDataSet.setValueTextColor(Color.WHITE);

        bcMonthlyAnalysis.animateY(2000);

        bcMonthlyAnalysis.getLegend().setTextColor(Color.WHITE);
        bcMonthlyAnalysis.getLegend().setYOffset(10f);

        XAxis xAxis = bcMonthlyAnalysis.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // X-axis at the bottom

        Description xDescription = new Description();
        xDescription.setText("(Weeks)");
        xDescription.setPosition(bcMonthlyAnalysis.getWidth() / 1.8f, bcMonthlyAnalysis.getHeight() - 74);
        bcMonthlyAnalysis.setDescription(xDescription);
        xDescription.setTextColor(Color.WHITE);
        xDescription.setTextSize(10f);

        YAxis yAxis = bcMonthlyAnalysis.getAxisLeft();
        yAxis.setTextColor(Color.WHITE);
        yAxis.setGranularity(1f); // Left Y-axis
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false);

        bcMonthlyAnalysis.getAxisRight().setEnabled(false); // Hide right Y-axis

        bcMonthlyAnalysis.setExtraOffsets(0f, 20f, 20f, 10f);

        barData.setBarWidth(0.3f);
        bcMonthlyAnalysis.groupBars(1f, 0.2f, 0.05f);
        bcMonthlyAnalysis.invalidate();
    }

    private void loadTransactions() {
        transactions = new LinkedList<>();

        for (int i = 1; i <= 50; i++) {
            if (i > 9 && i <= 31) {
                if (i % 2 == 0) {
                    transactions.add(new Transaction("admin", "Transport", "" + (i * 100000), "Blah Blah", i + "/07/2024", true));
                } else {
                    transactions.add(new Transaction("admin", "Shopping", "" + (i * 30000), "This is the note for the testing. Ha Ha.", i + "/07/2024", false));
                }
            } else {
                transactions.add(new Transaction("admin", "Food", "" + (i * 10000 ), "Nothing to say", "07/09/2024", false));
            }

        }

        for (int i = 1; i <= 50; i++) {
            if (i > 9 && i <= 31) {
                if (i % 2 == 0) {
                    transactions.add(new Transaction("admin", "Transport", "" + (i * 30000), "Blah Blah", i + "/09/2024", false));
                } else {
                    transactions.add(new Transaction("admin", "Shopping", "" + (i * 7000), "This is the note for the testing. Ha Ha.", i + "/08/2024", false));
                }
            } else {
                transactions.add(new Transaction("admin", "Others", "" + (i * 10000 ), "Nothing to say", "17/09/2024", false));
            }

        }

        for (int i = 1; i <= 50; i++) {
            if (i > 9 && i <= 30) {
                if (i % 3 == 0) {
                    transactions.add(new Transaction("admin", "Transport", "" + (i * 30000), "Blah Blah", i + "/07/2024", false));
                } else {
                    transactions.add(new Transaction("admin", "Food", "" + (i * 30000), "This is the note for the testing. Ha Ha.", i + "/07/2024", false));
                }
            } else {
                transactions.add(new Transaction("admin", "Shopping", "" + (i * 20000 ), "Nothing to say", "03/09/2024", true));
            }

        }
    }

    private void updateIncomeExpense(int month, int year) {
        expenses = new HashMap<>();

        if (month != 2) {
            weeklyIncome = new int[5];
            weeklyExpense = new int[5];
        } else {
            weeklyIncome = new int[4];
            weeklyExpense = new int[4];
        }

        for (Transaction transaction: transactions) {
            String category = transaction.getCategory();
            int amount = Integer.parseInt(transaction.getAmount().trim());
            boolean sign = transaction.isIncome();

            String date = transaction.getDate();
            int transactionMonth = Integer.parseInt(date.substring(3, 5));
            int transactionYear = Integer.parseInt(date.substring(6, 10));
            int week = (Integer.parseInt(date.substring(0, 2)) - 1) / 7;
            boolean transactionSign = transaction.isIncome();

            if (transactionMonth == month && transactionYear == year) {
                if (!sign) {
                    if (expenses.containsKey(category)) {
                        expenses.put(category, expenses.get(category) + amount);
                    } else {
                        expenses.put(category, amount);
                    }
                }

                if (transactionSign) {
                    weeklyIncome[week] += Integer.parseInt(transaction.getAmount());
                } else {
                    weeklyExpense[week] += Integer.parseInt(transaction.getAmount());
                }
            }
        }

    }

    private void loadMonths() {
        months = new ArrayList<>();
        months.add("January");
        months.add("February");
        months.add("March");
        months.add("April");
        months.add("May");
        months.add("June");
        months.add("July");
        months.add("August");
        months.add("September");
        months.add("October");
        months.add("November");
        months.add("December");
    }

    private void loadYears() {
        years = new ArrayList<>();

        years.add("2024");
        years.add("2023");
        years.add("2022");
    }

    public void switchAddTransactionActivity(View view) {
        Intent intentAddTransactionActivity = new Intent(this, AddTransactionActivity.class);
        startActivity(intentAddTransactionActivity);
    }
}