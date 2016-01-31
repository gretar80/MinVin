/**
 * Gretar Ævarsson
 * gretar80@gmail.com
 * © 2016
 */

package com.example.s198586.minvin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NyVin extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    // komponenter
    private ImageView komp_bilde;
    private EditText komp_navn;
    private SeekBar komp_poengSlider;
    private TextView komp_poengLabel;
    private Switch komp_type;
    private EditText komp_pris;
    private EditText komp_argang;
    private EditText komp_alkohol;
    private EditText komp_land;
    private EditText komp_notater;

    private DB db;              // databasehandler
    private byte[] figur = {};  // data til figuren
    boolean erRodvin;           // false hvis bruker velger 'hvitvin'
    double poeng = 0;           // poeng
    String regexFeil = "";      // brukt for Toast hvis regex ikke stemmer
    private static final int CAMERA_REQUEST = 1888; // brukt for kamera

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ny_vin);

        // initialisere
        komp_bilde = (ImageView) findViewById(R.id.imageview_bilde);
        komp_navn = (EditText) findViewById(R.id.felt_navn);
        komp_poengSlider = (SeekBar) findViewById(R.id.felt_poeng);
        komp_poengLabel = (TextView) findViewById(R.id.label_poeng2);
        komp_type = (Switch) findViewById(R.id.felt_type);
        komp_pris = (EditText) findViewById(R.id.felt_pris);
        komp_argang = (EditText) findViewById(R.id.felt_argang);
        komp_alkohol = (EditText) findViewById(R.id.felt_alkohol);
        komp_land = (EditText) findViewById(R.id.felt_land);
        komp_notater = (EditText) findViewById(R.id.felt_notater);

        db = new DB(this);

        // sett alertListener til kamera-knapp
        komp_bilde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taBilde(v);
            }
        });

        // hvis brukeren ser på hvitvin-liste, så blir switch knapp automatisk sett til 'hvitvin'
        if(MainActivity.rodvinEllerHvitvin.equals("rodvin"))
            erRodvin = true;
        else
            erRodvin = false;
        komp_type.setChecked(!erRodvin);

        // sett alertListener til Switch knapp (rødvin/hvitvin)
        komp_type.setOnCheckedChangeListener(this);

        // oppdatere poeng når 'slider' til høyre/venstre
        komp_poengSlider.setOnSeekBarChangeListener(
                new OnSeekBarChangeListener() {
                    int venstreSide = 0;
                    int hoyreSide = 0;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progressVerdi, boolean fraBruker) {

                        if (progressVerdi % 10 == 0) {
                            venstreSide = progressVerdi / 10;
                            hoyreSide = 0;
                        }
                        else if (progressVerdi % 10 == 5) {
                            venstreSide = progressVerdi / 10;
                            hoyreSide = 5;
                        }

                        if (hoyreSide == 0)
                            komp_poengLabel.setText(venstreSide + "/10");
                        else
                            komp_poengLabel.setText(venstreSide + "," + hoyreSide + "/10");

                        poeng = (double)venstreSide + (double)hoyreSide/10;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ny_vin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.menu_lagre:
                if(sjekkFelter()) {
                    Vin innVin = new Vin();
                    // lagre figur i database, hvis bruker har brukt kamera
                    if(figur.length != 0)
                        innVin.setFigur(figur);

                    innVin.setNavn(komp_navn.getText().toString());
                    innVin.setPoeng(poeng);
                    innVin.setType(erRodvin ? "rodvin" : "hvitvin");
                    innVin.setPris(komp_pris.getText().toString().matches("") ?
                            0 : Double.parseDouble(komp_pris.getText().toString()));
                    innVin.setArgang(komp_argang.getText().toString().matches("") ?
                            0 : Integer.parseInt(komp_argang.getText().toString()));
                    innVin.setAlkohol(komp_alkohol.getText().toString().matches("") ?
                            0 : Double.parseDouble(komp_alkohol.getText().toString()));
                    innVin.setLand(komp_land.getText().toString().matches("") ?
                            "" : komp_land.getText().toString());
                    innVin.setNotater(komp_notater.getText().toString().matches("") ?
                            "" : komp_notater.getText().toString());

                    // legg i database
                    if(db.leggTilVin(innVin)) {
                        // legg til arrayliste i viewet, kun hvis vinen er av samme type og vinene i viewet
                        if(innVin.getType().equals(MainActivity.rodvinEllerHvitvin))
                            MainActivity.adapter.leggTilListe(innVin);

                        finish();
                        return true;
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Feil ved innsetning av " + innVin.getNavn() + " i database", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                else {
                    return false;
                }

            case R.id.menu_avbryt:
                finish();
                return true;

            default :
                return super.onOptionsItemSelected(item);
        }
    }

    // metode for å ta bilde på kameraen på mobilen
    private void taBilde(View v){
        // appen har riktig permission i manifest
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    // ta imot bilde etter at bruker har tatt den på kamera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            try {
                Bitmap bilde = (Bitmap) data.getExtras().get("data");
                // sette bilde i ImageViewet
                komp_bilde.setBackground(null);
                komp_bilde.setPadding(0, 0, 0, 0);
                komp_bilde.setImageBitmap(bilde);
                figur = KonvertBitmap.getBytes(bilde);
            }
            catch (Exception e){
                Log.d("NYVIN", "Feil i onActivityResult: " + e);
                Toast.makeText(NyVin.this, "Feil i bilde",Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(NyVin.this, "Kunne ikke lagre bilde",Toast.LENGTH_LONG).show();
        }
    }

    // sjekk for lovlige felter
    private boolean sjekkFelter(){
        // tomme felter er lovlig (uten 'navn' feltet)

        Pattern patternTekst = Pattern.compile("^[\\p{L}0-9 .'-]+$");

        // pris og alkohol har inputvalidering i selve viewet (kun tall)

        Matcher matcherNavn = patternTekst.matcher(komp_navn.getText().toString());

        if (!matcherNavn.find()) {
            regexFeil += "Feil navn\n";
        }

        if (!regexFeil.equals("")){
            Toast.makeText(this, regexFeil, Toast.LENGTH_LONG).show();
            regexFeil = "";
            return false;
        }
        else{
            return true;
        }
    }

    // brukt til å få verdi fra Switch knapp
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        erRodvin = !isChecked;
    }
}
