/**
 * Gretar Ævarsson
 * gretar80@gmail.com
 * © 2016
 */

package com.example.s198586.minvin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

/**
 * Created by Gretar on 23.11.2015.
 */
public class SorteringsDialog extends DialogFragment {

    AlertPositiveListener alertListener;

    interface AlertPositiveListener {
        void onPositiveClick(int position);
    }

    public void onAttach(Activity a) {
        super.onAttach(a);
        try{
            alertListener = (AlertPositiveListener) a;
        }catch(ClassCastException e){
            // Feil hvis aktivitetet ikke implementerer interfacet
            throw new ClassCastException("Feil, aktivitetet må implementere AlertPositiveListener: " + e.toString());
        }
    }

    OnClickListener positiveListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int arg) {
            AlertDialog alertDialog = (AlertDialog)dialogInterface;
            int posisjon = alertDialog.getListView().getCheckedItemPosition();
            alertListener.onPositiveClick(posisjon);
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // generere valg
        String[] sorteringsValg = new String[]{
                "Navn (A-Å)",
                "Navn (Å-A)",
                "Poeng (minste først)",
                "Poeng (meste først)",
                "Pris (billigste først)",
                "Pris (dyreste først)",
                "Årgang (gammel først)",
                "Årgang (ny først)",
                "Alkohol (laveste først)",
                "Alkohol (høyeste først)",
                "Land (A-Å)",
                "Land (Å-A)"
        };

        /** Getting the arguments passed to this fragment */
        // Bundle brukes her til å sende argumenter til fragmentet
        Bundle bundle = getArguments();
        int position = bundle.getInt("position");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sortere etter:");
        builder.setSingleChoiceItems(sorteringsValg, position, null);
        builder.setPositiveButton("OK", positiveListener);
        builder.setNegativeButton("Avbryt", null);

        AlertDialog dialog = builder.create();

        return dialog;
    }
}
