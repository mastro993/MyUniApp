package progettomobdev.it.myuni;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class DialogModificaCreditiLaurea extends DialogFragment {

    LayoutInflater inflater;
    View view;
    Button btnAnnulla,btnSalva;
    NumberPicker npCrediti;
    Intent intent1;
    String crediti;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        inflater=getActivity().getLayoutInflater();
        view=inflater.inflate(R.layout.activity_dialog_modifica_crediti_laurea, null);

        btnAnnulla=(Button) view.findViewById(R.id.idannulla);
        btnSalva=(Button) view.findViewById(R.id.idsalva);
        npCrediti=(NumberPicker) view.findViewById(R.id.numberPicker);
        intent1=new Intent(getActivity(),PreferenzeActivity.class);

        npCrediti.setMaxValue(200);
        npCrediti.setMinValue(130);

        btnAnnulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(intent1);
            }
        });

        btnSalva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                crediti=Integer.toString(npCrediti.getValue());



                //SALVARE IL NUMERO NELLE SHARED

                intent1.putExtra("Crediti", crediti);
                dismiss();
            }
        });



        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Modifica crediti corso di laurea");
        builder.setView(view);
        return builder.create();
    }
}
