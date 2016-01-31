/**
 * Gretar Ævarsson
 * gretar80@gmail.com
 * © 2016
 */

package com.example.s198586.minvin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VinDetaljer extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
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

    private DB db;          // databasehandler
    private byte[] figur;   // data til figuren
    boolean erRodvin;       // false hvis bruker velger 'hvitvin'
    double poeng;           // poeng
    String regexFeil = "";  // brukt for Toast hvis regex ikke stemmer
    private static final int CAMERA_REQUEST = 1888; // brukt for kamera
    private Vin valgtVin;   // det valgte vin objektet som skal vises
    private boolean editable = false;   // blir 'true' hvis bruker trykker på 'endre' knapp
    private int vinID;      // ID nummeret til objektet i databasen
    private int pos;        // posisjonen til objektet i arraylisten i adapteren

    // brukt for å skjule/vise linjen undir hvert EditText felt
    Drawable bakgrunn_navn;
    Drawable bakgrunn_pris;
    Drawable bakgrunn_argang;
    Drawable bakgrunn_alkohol;
    Drawable bakgrunn_land;
    Drawable bakgrunn_notater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ny_vin);

        // database
        db = new DB(this);

        // hente vin som skal vises og initialisere variabler
        vinID = getIntent().getIntExtra("vinID", 0);
        pos = getIntent().getIntExtra("pos", 0);
        valgtVin = db.finnVin(vinID);
        poeng = valgtVin.getPoeng();
        figur = valgtVin.getFigur();

        // initialisere elementer
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

        // brukes når EditText-felter skal kunne endres
        komp_navn.setTag(komp_navn.getKeyListener());
        komp_pris.setTag(komp_pris.getKeyListener());
        komp_argang.setTag(komp_argang.getKeyListener());
        komp_alkohol.setTag(komp_alkohol.getKeyListener());
        komp_land.setTag(komp_land.getKeyListener());
        komp_notater.setTag(komp_notater.getKeyListener());

        // brukes til å fjerne/vise linje undir EditText feltene
        bakgrunn_navn = komp_navn.getBackground();
        bakgrunn_pris = komp_pris.getBackground();
        bakgrunn_argang = komp_argang.getBackground();
        bakgrunn_alkohol = komp_alkohol.getBackground();
        bakgrunn_land = komp_land.getBackground();
        bakgrunn_notater = komp_notater.getBackground();

        // sette felter til read-only
        felterKanEndres(editable);

        // fylle inn felter fra valgt vin
        if(valgtVin.getFigur() == null || valgtVin.getFigur().length == 0) {
            // hvis ingen bilde finnes for valgt vin, gjør ingenting
        }
        else {
            komp_bilde.setBackground(null);
            komp_bilde.setPadding(0, 0, 0, 0);
            komp_bilde.setImageBitmap(KonvertBitmap.getImage(valgtVin.getFigur()));
        }

        if(!valgtVin.getNavn().equals(""))
            komp_navn.setText(valgtVin.getNavn());

        Double progress = valgtVin.getPoeng() * 10;
        komp_poengSlider.setProgress(progress.intValue());

        double labelPoeng = valgtVin.getPoeng();
        komp_poengLabel.setText(String.format("%.1f", labelPoeng)  + "/10");

        erRodvin = valgtVin.getType().equals("rodvin");
        komp_type.setChecked(!erRodvin);

        if(valgtVin.getPris() == 0)
            komp_pris.setText("");
        else{
            Double pris = valgtVin.getPris();
            komp_pris.setText(pris.toString());
        }

        if(valgtVin.getArgang() == 0)
            komp_argang.setText("");
        else{
            Integer argang = valgtVin.getArgang();
            komp_argang.setText(argang.toString());
        }

        if(valgtVin.getAlkohol() == 0)
            komp_alkohol.setText("");
        else{
            Double alkohol = valgtVin.getAlkohol();
            komp_alkohol.setText(alkohol.toString());
        }

        if(!komp_land.getText().equals("")){
            komp_land.setText(valgtVin.getLand());
        }

        if(!komp_notater.getText().equals("")){
            komp_notater.setText(valgtVin.getNotater());
        }

        // hvis brukeren ser på hvitvin-liste, så blir switch knapp sett til 'hvitvin'
        if(MainActivity.rodvinEllerHvitvin.equals("rodvin"))
            erRodvin = true;
        else
            erRodvin = false;

        komp_type.setChecked(!erRodvin);

        // sett alertListener til Switch knapp (rødvin/hvitvin)
        komp_type.setOnCheckedChangeListener(this);

        // oppdatere poeng når 'slider' til høyre/venstre
        komp_poengSlider.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
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
        // forskjellige menyer hvis bruker skal endre dataene
        if(!editable)
            getMenuInflater().inflate(R.menu.menu_vin_detaljer, menu);
        else
            getMenuInflater().inflate(R.menu.menu_endre_vin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.menu_endre:
                invalidateOptionsMenu();
                Log.d("DETALJER", "er i onOptionsItemSelected: " + editable);
                editable = true;
                felterKanEndres(editable);
                return true;

            case R.id.menu_slett:
                new AlertDialog.Builder(this)
                        .setTitle("Slett vin")
                        .setMessage("Vil du slette denne vin?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(db.slettVin(valgtVin)) {
                                    MainActivity.adapter.slettFraListe(valgtVin);
                                    finish();
                                }
                                else
                                    Toast.makeText(getApplicationContext(),
                                            "Feil ved sletting av " + valgtVin.getNavn(),
                                            Toast.LENGTH_SHORT).show();
                            }

                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // gjør ingenting
                            }
                        })
                        .setIcon(R.drawable.ikon_alert)
                        .show();
                return true;

            case R.id.menu_lagre:

                if(sjekkFelter()){
                    valgtVin.setFigur(figur);
                    valgtVin.setNavn(komp_navn.getText().toString());
                    valgtVin.setPoeng(poeng);
                    valgtVin.setType(erRodvin ? "rodvin" : "hvitvin");
                    valgtVin.setPris(komp_pris.getText().toString().matches("") ?
                            0 : Double.parseDouble(komp_pris.getText().toString()));
                    valgtVin.setArgang(komp_argang.getText().toString().matches("") ?
                            0 : Integer.parseInt(komp_argang.getText().toString()));
                    valgtVin.setAlkohol(komp_alkohol.getText().toString().matches("") ?
                            0 : Double.parseDouble(komp_alkohol.getText().toString()));
                    valgtVin.setLand(komp_land.getText().toString().matches("") ?
                            "" : komp_land.getText().toString());
                    valgtVin.setNotater(komp_notater.getText().toString().matches("") ?
                            "" : komp_notater.getText().toString());

                    if(db.endreVin(valgtVin) != -1){
                        // fjerne fra view, hvis bruker har endret type
                        if(MainActivity.rodvinEllerHvitvin.equals(valgtVin.getType()))
                            MainActivity.adapter.endreIListe(pos, valgtVin);
                        else
                            MainActivity.adapter.slettFraListe(pos);
                        finish();
                        return true;
                    }
                    else {
                        Toast.makeText(getApplicationContext(),
                                "Feil ved oppdatering av " + valgtVin.getNavn(),
                                Toast.LENGTH_SHORT).show();
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
                Toast.makeText(VinDetaljer.this, "Feil i bilde",Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(VinDetaljer.this, "Kunne ikke lagre bilde",Toast.LENGTH_LONG).show();
        }
    }

    // brukt til å få verdi fra Switch knapp
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        erRodvin = !isChecked;
    }

    // kalles hvis bruker skal endre dataene
    public void felterKanEndres(boolean kanEndres) {

        if(kanEndres) {
            komp_bilde.setFocusable(kanEndres); komp_bilde.setClickable(kanEndres);
            komp_navn.setKeyListener((KeyListener) komp_navn.getTag());
            komp_poengSlider.setEnabled(kanEndres);
            komp_type.setClickable(kanEndres); komp_type.setFocusable(kanEndres);
            komp_pris.setKeyListener((KeyListener) komp_pris.getTag());
            komp_argang.setKeyListener((KeyListener) komp_argang.getTag());
            komp_alkohol.setKeyListener((KeyListener) komp_alkohol.getTag());
            komp_land.setKeyListener((KeyListener) komp_land.getTag());
            komp_notater.setKeyListener((KeyListener) komp_notater.getTag());

            // sett alertListener til kamera-knapp
            komp_bilde.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    taBilde(v);
                }
            });

            // vis linje undir EditText-feltene
            komp_navn.setBackground(bakgrunn_navn);
            komp_pris.setBackground(bakgrunn_pris);
            komp_argang.setBackground(bakgrunn_argang);
            komp_alkohol.setBackground(bakgrunn_alkohol);
            komp_land.setBackground(bakgrunn_land);
            komp_notater.setBackground(bakgrunn_notater);
        }
        else {
            komp_bilde.setFocusable(kanEndres); komp_bilde.setClickable(kanEndres);
            komp_navn.setKeyListener(null);
            komp_poengSlider.setEnabled(kanEndres);
            komp_type.setClickable(kanEndres); komp_type.setFocusable(kanEndres);
            komp_pris.setKeyListener(null);
            komp_argang.setKeyListener(null);
            komp_alkohol.setKeyListener(null);
            komp_land.setKeyListener(null);
            komp_notater.setKeyListener(null);

            // fjerne alertListener til kamera-knapp
            komp_bilde.setOnClickListener(null);

            // fjerne linje undir EditText-feltene
            komp_navn.setBackground(null);
            komp_pris.setBackground(null);
            komp_argang.setBackground(null);
            komp_alkohol.setBackground(null);
            komp_land.setBackground(null);
            komp_notater.setBackground(null);
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
}
