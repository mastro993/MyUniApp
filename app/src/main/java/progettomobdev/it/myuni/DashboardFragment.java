package progettomobdev.it.myuni;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.syncfusion.charts.CategoryAxis;
import com.syncfusion.charts.ChartDataPoint;
import com.syncfusion.charts.ChartSelectionChangingEvent;
import com.syncfusion.charts.ChartSelectionEvent;
import com.syncfusion.charts.ChartZoomPanBehavior;
import com.syncfusion.charts.DoughnutSeries;
import com.syncfusion.charts.LineSeries;
import com.syncfusion.charts.NumericalAxis;
import com.syncfusion.charts.ObservableArrayList;
import com.syncfusion.charts.SfChart;
import com.syncfusion.charts.enums.AxisLabelsIntersectAction;
import com.syncfusion.charts.enums.EdgeLabelsDrawingMode;
import com.syncfusion.charts.enums.LabelContent;
import com.syncfusion.charts.enums.Visibility;
import com.syncfusion.charts.enums.ZoomMode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DashboardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    private Integer i = 0;
    private ObservableArrayList array2;
    private View view;

    private OnFragmentInteractionListener mListener;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        return view;
    }

    private void creaDashboard(){

        EsameRepo erepo = new EsameRepo(getContext());
        MateriaRepo mrepo = new MateriaRepo(getContext());


        // Impostazione media ponderata

        Double mediap;
        TextView media_pond = (TextView) view.findViewById(R.id.media_ponderata_dashboard);
        if (Libretto.getMediaPonderata(erepo.getListaEsami()) == null) {
            media_pond.setText("nd");
            mediap = 0.0;
        } else {
            mediap = round(Libretto.getMediaPonderata(erepo.getListaEsami()), 2);
            media_pond.setText(mediap.toString());
        }


        if (mediap < 20.0) {
            media_pond.setTextColor(0xffc0392b);
        } else if (mediap < 25.0) {
            media_pond.setTextColor(0xfff1c40f);

        } else if (mediap < 30.0) {
            media_pond.setTextColor(0xff27ae60);
        } else {
            media_pond.setTextColor(0xff2980b9);
        }
        // Impostazione esami sostenuti

        TextView esami_sost = (TextView) view.findViewById(R.id.esami_sostenuti_dashboard);
        Integer numero_esami = erepo.getListaEsami().size();
        esami_sost.setText(numero_esami.toString());

        // impostazione crediti acquisiti

        TextView crediti_acq = (TextView) view.findViewById(R.id.crediti_acquisiti);
        final ProgressBar progresso_cr = (ProgressBar) view.findViewById(R.id.progresso_crediti);

        final Integer tot_crediti = EsameRepo.getTotCrediti();
        String crediti_string = tot_crediti + "/" + Libretto.getCreditiLaurea();
        crediti_acq.setText(crediti_string);
        progresso_cr.setMax(Libretto.getCreditiLaurea());
        progresso_cr.setProgress(tot_crediti);

        progresso_cr.post(new Runnable() {
            @Override
            public void run() {
                progresso_cr.setProgress(tot_crediti);
            }
        });

        // Impostazione proiezione

        TextView voto_proiezione = (TextView) view.findViewById(R.id.voto_proiezione);
        final ProgressBar progresso_proiezione = (ProgressBar) view.findViewById(R.id.progresso_proiezione);

        final Double vproiez = round(mediap / 3 * 11, 2);
        voto_proiezione.setText(vproiez + "/110");
        final Integer vproiezI = vproiez.intValue();

        progresso_proiezione.setMax(110);
        progresso_proiezione.setProgress(vproiezI);


        // Impostazione grafici

        final SfChart chart, chart2;

        chart = (SfChart) view.findViewById(R.id.chart1);
        chart2 = (SfChart) view.findViewById(R.id.chart2);

        setFirstChart(chart, erepo);
        setSecondChart(chart2, erepo);
    }

    // Funzione per arrotondare un dobule
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    //PRIMO GRAFICO
    private void setFirstChart(final SfChart chart, final EsameRepo erepo) {
        //grafico voti
        ChartZoomPanBehavior zoomPanBehavior = new ChartZoomPanBehavior();
        zoomPanBehavior.setZoomMode(ZoomMode.X);
        chart.getBehaviors().add(zoomPanBehavior);

        //asse x
        CategoryAxis categoryAxis = new CategoryAxis();
        chart.setPrimaryAxis(categoryAxis);
        chart.getPrimaryAxis().setEdgeLabelsDrawingMode(EdgeLabelsDrawingMode.Shift);
        chart.getPrimaryAxis().setLabelRotationAngle(315);
        chart.getPrimaryAxis().setLabelsIntersectAction(AxisLabelsIntersectAction.Hide);
        chart.getPrimaryAxis().getTitle().setText("Data");
        chart.getTitle().setText("Andamento Esami");
        chart.getTitle().setTextSize(22f);
        chart.getTitle().setTypeface(Typeface.DEFAULT_BOLD);
        chart.getTitle().setTextColor(getResources().getColor(R.color.grey));
        chart.getTitle().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        chart.getPrimaryAxis().setVisibility(Visibility.Visible);

        //asse y
        NumericalAxis secondNumericalAxis = new NumericalAxis();
        secondNumericalAxis.setMinimum(17);
        secondNumericalAxis.setMaximum(34);
        chart.setSecondaryAxis(secondNumericalAxis);
        chart.getSecondaryAxis().getTitle().setText("Voto");


        final LineSeries lineSeries = new LineSeries();
        lineSeries.setColor(getResources().getColor(R.color.colorAccent));
        lineSeries.setStrokeWidth(0.7f);
        lineSeries.getDataMarker().getLabelStyle().setMarginLeft(5);
        lineSeries.getDataMarker().setShowLabel(true);

        ObservableArrayList<ChartDataPoint> array = new ObservableArrayList();

        for (Esame e : erepo.getListaEsami()) {
            if(e.getVoto() >= 18){
                String data = new SimpleDateFormat("dd/MM").format(e.getData());
                array.add(new ChartDataPoint(data, e.getVoto()));
            }
        }
        //modalita visualizzazione dati

        lineSeries.setDataSource(array);
        chart.getSeries().add(lineSeries);

        lineSeries.setDataPointSelectionEnabled(true);

        chart.setOnSelectionChangingListener(new SfChart.OnSelectionChangingListener() {
            @Override
            public void onSelectionChanging(SfChart sfChart, ChartSelectionChangingEvent chartSelectionChangingEvent) {
                if (i != 0) {
                    infodialog(chartSelectionChangingEvent.getSelectedDataPointIndex(), erepo);
                    lineSeries.setSelectedDataPointIndex(chartSelectionChangingEvent.getSelectedDataPointIndex());
                } else {
                    i++;
                }
            }
        });
    }

    //SECONDO GRAFICO
    private void setSecondChart(final SfChart chart, final EsameRepo erepo) {
        //grafico moda
        ChartZoomPanBehavior zoomPanBehavior = new ChartZoomPanBehavior();
        zoomPanBehavior.setZoomMode(ZoomMode.X);
        chart.getBehaviors().add(zoomPanBehavior);
        chart.getLegend().setVisibility(Visibility.Visible);
        chart.getLegend().getLabelStyle().setMarginTop(5);
        chart.getLegend().getLabelStyle().setTextColor(getResources().getColor(R.color.grey));

        //asse x
        CategoryAxis categoryAxis = new CategoryAxis();
        chart.setPrimaryAxis(categoryAxis);
        chart.getPrimaryAxis().setEdgeLabelsDrawingMode(EdgeLabelsDrawingMode.Shift);
        chart.getPrimaryAxis().setLabelRotationAngle(315);
        chart.getPrimaryAxis().setLabelsIntersectAction(AxisLabelsIntersectAction.Hide);
        chart.getTitle().setText("Moda voti");
        chart.getTitle().setTextSize(22f);
        chart.getTitle().setTypeface(Typeface.DEFAULT_BOLD);
        chart.getTitle().setTextColor(getResources().getColor(R.color.grey));
        chart.getTitle().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        chart.getPrimaryAxis().getTitle().setText("Voto");
        chart.getPrimaryAxis().setVisibility(Visibility.Visible);

        //asse y
        NumericalAxis secondNumericalAxis = new NumericalAxis();
        chart.setSecondaryAxis(secondNumericalAxis);

        array2 = new ObservableArrayList();

        HashMap<Integer, Integer> moda = new HashMap<Integer, Integer>();
        moda = erepo.getModaEsami();
        moda.remove(0); // Levo gli esami contati come idoneità

        for (Integer key : moda.keySet()) {
            array2.add(new ChartDataPoint(key.toString(), moda.get(key)));
        }

        //modalita visualizzazione dati

        final DoughnutSeries doughnutSeries = new DoughnutSeries();
        doughnutSeries.setDataSource(array2);
        chart.getSeries().add(doughnutSeries);
        doughnutSeries.setDataPointSelectionEnabled(true);
        doughnutSeries.getDataMarker().setShowLabel(true);
        doughnutSeries.getDataMarker().setLabelContent(LabelContent.Percentage);

        chart.setOnSelectionChangedListener(new SfChart.OnSelectionChangedListener() {
            @Override
            public void onSelectionChanged(SfChart sfChart, ChartSelectionEvent chartSelectionEvent) {
                if (i != 0) {
                    infodialogModa(chartSelectionEvent.getSelectedDataPointIndex(), erepo, array2);
                    doughnutSeries.setSelectedDataPointIndex(chartSelectionEvent.getSelectedDataPointIndex());
                } else {
                    i++;
                }
            }
        });
    }

    public void infodialog(Integer index, EsameRepo erepo) {
        Integer c = 0;
        MateriaRepo mrepo = new MateriaRepo(getContext());

        Integer a = 0;
        for (Esame e : erepo.getListaEsami()) {
            if(e.getVoto() >= 18){
                a++;
                if (c == index) {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                    builder1.setTitle(mrepo.getMateriaByID(e.getIdMateria()).getNome());

                    String data = new SimpleDateFormat("dd/MM/yyyy").format(e.getData());

                    TextView myMsg = new TextView(getContext());
                    myMsg.setText("Data: " + data + "\n\n"
                            + "Cfu: " + e.getCrediti() + "\n\n"
                            + "Voto: " + e.getVoto());
                    myMsg.setGravity(Gravity.CENTER_HORIZONTAL);

                    builder1.setView(myMsg);
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    builder1.setIcon(R.drawable.ic_examallert);

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                    break;
                } else
                    c++;
            }
        }
    }

    public void infodialogModa(Integer index, EsameRepo erepo, ObservableArrayList<ChartDataPoint> array2) {
        if (index >= 0) {
            MateriaRepo mrepo = new MateriaRepo(getContext());
            String votoselezionato = array2.get(index).getX().toString();
            Integer votoselezionatoI = Integer.valueOf(votoselezionato);

            String txtMsg = "\n";

            for (Esame e : erepo.getListaEsamiPerVoto(votoselezionatoI)) {

                txtMsg = txtMsg + mrepo.getMateriaByID(e.getIdMateria()).getNome() + "\n \n";

            }
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            builder1.setTitle("Esami con voto " + votoselezionato);

            TextView myMsg = new TextView(getContext());
            myMsg.setText(txtMsg);
            myMsg.setGravity(Gravity.CENTER_HORIZONTAL);

            builder1.setView(myMsg);
            builder1.setCancelable(true);

            builder1.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            builder1.setIcon(R.drawable.ic_examallert);

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        i = 0;
        creaDashboard();
        Log.d("DASHBOARD", "refresh");
    }
}
