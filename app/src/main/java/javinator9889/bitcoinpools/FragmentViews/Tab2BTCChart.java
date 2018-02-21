package javinator9889.bitcoinpools.FragmentViews;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javinator9889.bitcoinpools.Constants;
import javinator9889.bitcoinpools.JSONTools.JSONTools;
import javinator9889.bitcoinpools.MainActivity;
import javinator9889.bitcoinpools.NetTools.net;
import javinator9889.bitcoinpools.R;

/**
 * Created by Javinator9889 on 28/01/2018.
 * Based on https://www.coindesk.com/api/ API
 */

public class Tab2BTCChart extends Fragment implements DatePickerDialog.OnDateSetListener {
    private static Map<Date, Float> BTCPRICE = new LinkedHashMap<>();
    private static final String API_URL = "https://api.coindesk.com/v1/bpi/historical/close.json";
    private static String REQUEST_URL;
    private static LineChart DESTINATIONLINECHART;
    private static final String STATS_URL = "https://api.blockchain.info/stats";
    @SuppressLint("StaticFieldLeak")
    private static Context FRAGMENT_CONTEXT;
    private static boolean lineChartCreated = false;
    private int year;
    private int month;
    private int day;
    private int writable_month;
    private boolean date_set = false;
    private List<CardsContent> cardsContents;

    public Tab2BTCChart() {
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DisplayMetrics dp = this.getResources().getDisplayMetrics();
        float dpHeight = dp.heightPixels;

        View createdView = inflater.inflate(R.layout.bitcoindata, container, false);
        ((Button) createdView.findViewById(R.id.datebutton)).setText(R.string.latest30days);
        createdView.findViewById(R.id.datebutton)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createDialog().show();
                    }
                });

        REQUEST_URL = API_URL;

        setupValues();

        cardsContents = new ArrayList<>();
        CardsAdapter adapter = new CardsAdapter(getContext(), cardsContents);
        DESTINATIONLINECHART = createdView.findViewById(R.id.lineChart);
        RecyclerView recyclerView = createdView.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(createdView.getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);

        prepareCards();

        int[] attrs = new int[]{R.attr.actionBarSize};
        TypedArray a = createdView.getContext().obtainStyledAttributes(attrs);
        int size = a.getDimensionPixelSize(0, 0);
        a.recycle();
        int FINALDP = (int) ((dpHeight - size) * 0.7);

        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) DESTINATIONLINECHART.getLayoutParams();
        lp.height = FINALDP;
        lp.matchConstraintMaxHeight = (int) dpHeight;
        DESTINATIONLINECHART.setLayoutParams(lp);
        DESTINATIONLINECHART.invalidate();

        FRAGMENT_CONTEXT = createdView.getContext();
        ((TextView) createdView.findViewById(R.id.longPressInfo)).setText(R.string.longclick);

        return createdView;
    }

    private void setupValues() {
        net httpsResponse = new net();
        httpsResponse.execute(REQUEST_URL);
        try {
            BTCPRICE = JSONTools.sortDateByValue(JSONTools.convert2DateHashMap(httpsResponse.get().getJSONObject("bpi")));
        } catch (InterruptedException | ExecutionException | JSONException e) {
            BTCPRICE = null;
            Log.e(Constants.LOG.MATAG, Constants.LOG.DATA_ERROR + e.getMessage());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        System.out.println(lineChartCreated);
        System.out.println("Is view visible for user? " + isVisibleToUser);
        if (isVisibleToUser && !lineChartCreated) {
            createLineChart(DESTINATIONLINECHART, FRAGMENT_CONTEXT);
        }
        if (isVisibleToUser)
            MainActivity.MAINACTIVITY_TOOLBAR.setTitle(getString(R.string.btcinfo));
    }

    private void createLineChart(@NonNull final LineChart destinationChart, @NonNull final Context fragmentContext) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                destinationChart.setDrawGridBackground(false);
                destinationChart.getDescription().setEnabled(false);
                destinationChart.setTouchEnabled(true);
                destinationChart.setDragEnabled(true);
                destinationChart.setScaleEnabled(true);
                destinationChart.setPinchZoom(true);
                CustomMarkerView mv = new CustomMarkerView(fragmentContext, R.layout.marker_view);
                mv.setChartView(destinationChart);
                destinationChart.setMarker(mv);
                ArrayList<Entry> values = new ArrayList<>();
                int i = 0;
                for (Date o : BTCPRICE.keySet()) {
                    values.add(new Entry(i, BTCPRICE.get(o)));
                    ++i;
                }
                LineDataSet lineDataSet;
                if ((destinationChart.getData() != null) && (destinationChart.getData().getDataSetCount() > 0)) {
                    lineDataSet = (LineDataSet) destinationChart.getData().getDataSetByIndex(0);
                    lineDataSet.setValues(values);
                    destinationChart.getData().notifyDataChanged();
                    destinationChart.notifyDataSetChanged();
                } else {
                    lineDataSet = new LineDataSet(values, fragmentContext.getString(R.string.btcprice));
                    lineDataSet.setDrawIcons(false);
                    lineDataSet.enableDashedLine(10f, 5f, 0f);
                    lineDataSet.enableDashedHighlightLine(10f, 5f, 0f);
                    lineDataSet.setColor(Color.BLACK);
                    lineDataSet.setCircleColor(Color.BLACK);
                    lineDataSet.setLineWidth(1f);
                    lineDataSet.setCircleRadius(3f);
                    lineDataSet.setDrawCircleHole(false);
                    lineDataSet.setValueTextSize(9f);
                    lineDataSet.setDrawFilled(true);
                    lineDataSet.setFormLineWidth(1f);
                    lineDataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                    lineDataSet.setFormSize(15.f);
                    lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    lineDataSet.setFillDrawable(ContextCompat.getDrawable(fragmentContext, R.drawable.fade_red));
                    lineDataSet.setDrawCircles(false);
                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(lineDataSet);
                    LineData data = new LineData(dataSets);
                    destinationChart.setData(data);
                }
                destinationChart.getAxisLeft().setValueFormatter(new LargeValueFormatter());
                destinationChart.animateX(2500);
                destinationChart.invalidate();
                lineChartCreated = true;
            }
        }, 100);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (year <= 2010) {
            this.year = 2010;
            if ((month <= 6) && (dayOfMonth < 17)) {
                this.month = 6;
                this.writable_month = 7;
                this.day = 17;
            } else if ((month < 6) && (dayOfMonth > 17)) {
                this.month = 6;
                this.writable_month = 7;
                this.day = 17;
            } else {
                this.day = dayOfMonth;
                this.month = month;
                this.writable_month = ++month;
                this.year = year;
            }
        } else {
            this.day = dayOfMonth;
            this.month = month;
            this.writable_month = ++month;
            this.year = year;
        }
        this.date_set = true;
        String buttonText = getString(R.string.since) + " " + parseDate();
        ((Button) getActivity().findViewById(R.id.datebutton)).setText(buttonText);
        forceReload();
    }

    private String parseDate() {
        String dateParsed = this.year + "-";
        if (this.writable_month <= 9)
            dateParsed += "0" + this.writable_month + "-";
        else
            dateParsed += this.writable_month + "-";
        if (this.day <= 9)
            dateParsed += "0" + this.day;
        else
            dateParsed += this.day;
        return dateParsed;
    }

    @NonNull
    public Dialog createDialog() {
        final Calendar calendar = Calendar.getInstance();
        final Calendar limitDate = Calendar.getInstance();
        limitDate.set(2010, 6, 17);

        if (!this.date_set) {
            this.year = calendar.get(Calendar.YEAR);
            this.month = calendar.get(Calendar.MONTH);
            this.day = calendar.get(Calendar.DAY_OF_MONTH);
            this.date_set = true;
        }
        System.out.println(this.year + " " + this.month + " " + this.day);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, this.year, this.month, this.day);

        calendar.add(Calendar.DATE, -1);

        dialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        dialog.getDatePicker().setMinDate(limitDate.getTimeInMillis());

        return dialog;
    }

    @SuppressLint("SimpleDateFormat")
    public void forceReload() {
        String dateParsed = parseDate();
        REQUEST_URL = API_URL + "?start=" + dateParsed + "&end=" + new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        System.out.println(REQUEST_URL);
        setupValues();
        createLineChart(DESTINATIONLINECHART, FRAGMENT_CONTEXT);
    }

    public static void setLineChartCreated() {
        lineChartCreated = false;
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @SuppressLint("DefaultLocale")
    private void prepareCards() {
        net httpsResponse = new net();
        Map<String, Float> cardsData = new LinkedHashMap<>();
        httpsResponse.execute(STATS_URL);
        try {
            cardsData = JSONTools.sortByValue(JSONTools.convert2HashMap(httpsResponse.get()));
        } catch (InterruptedException | ExecutionException e) {
            cardsData = null;
            Log.e(Constants.LOG.MATAG, Constants.LOG.DATA_ERROR + e.getMessage());
            Crashlytics.logException(e);
        } finally {
            assert cardsData != null;
            DecimalFormat df = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));
            cardsContents.add(new CardsContent(getString(R.string.market_price),
                    "$" + df.format(cardsData.get("market_price_usd"))));
            cardsContents.add(new CardsContent(getString(R.string.hash_rate),
                    df.format(cardsData.get("hash_rate")) + " GH/s"));
            cardsContents.add(new CardsContent(getString(R.string.difficulty),
                    df.format(cardsData.get("difficulty"))));
            cardsContents.add(new CardsContent(getString(R.string.min_blocks),
                    df.format(cardsData.get("n_blocks_mined") / 10) + " " + getString(R.string.blocks_name)));
            cardsContents.add(new CardsContent(getString(R.string.minutes_blocks),
                    df.format(cardsData.get("minutes_between_blocks")) + " " + getString(R.string.minutes_name)));
            cardsContents.add(new CardsContent(getString(R.string.total_fees),
                    df.format(cardsData.get("total_fees_btc") / 10000000) + " BTC"));
            cardsContents.add(new CardsContent(getString(R.string.total_trans),
                    df.format(cardsData.get("n_tx"))));
            cardsContents.add(new CardsContent(getString(R.string.min_benefit),
                    "$" + df.format(cardsData.get("miners_revenue_usd") / 100)));
        }
    }
}
