/**
 * Gretar Ævarsson
 * gretar80@gmail.com
 * © 2016
 */

package com.example.s198586.minvin;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import com.example.s198586.minvin.SorteringsDialog.AlertPositiveListener;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AlertPositiveListener {

    // komponenter
    ListView listView;
    ArrayList<Vin> vinListe;
    DB db;

    // adapter kalles fra andre klasser (NyVin og VinDetaljer) så den er 'public static'
    public static VinListAdapter adapter;



    // variabler
    public static String rodvinEllerHvitvin = "rodvin"; // hvilke type vin vises på main vinduet
    int itemNummer = 0; // brukt for alert-dialog
    int pos;            // brukt for å finne ut posisjon av objekt i arrayliste

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialisere
        listView = (ListView) findViewById(R.id.list);
        db = new DB(this);

        final Context c = this;

        // floating 'legg til vin objekt' knapp
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.leggTilKnapp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(c, NyVin.class);
                startActivity(intent1);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {

            case R.id.menu_rodvin:
                if (rodvinEllerHvitvin.equals("rodvin"))
                    return false;
                else {
                    rodvinEllerHvitvin = "rodvin";
                    vinListe = db.listAlleVin(rodvinEllerHvitvin);
                    adapter.oppdaterListe(vinListe);
                    onPositiveClick(itemNummer);
                    return true;
                }

            case R.id.menu_hvitvin:
                if (rodvinEllerHvitvin.equals("hvitvin"))
                    return false;
                else {
                    rodvinEllerHvitvin = "hvitvin";
                    vinListe = db.listAlleVin(rodvinEllerHvitvin);
                    adapter.oppdaterListe(vinListe);
                    onPositiveClick(itemNummer);
                    return true;
                }

            case R.id.menu_sort:
                FragmentManager manager = getFragmentManager();
                SorteringsDialog sorteringsDialog = new SorteringsDialog();

                // Bundle brukes til å lagre verdien fra alert-dialogen
                Bundle b  = new Bundle();
                b.putInt("position", itemNummer);
                sorteringsDialog.setArguments(b);
                sorteringsDialog.show(manager,"sorterings_dialog");

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // hente kun rødvin eller hvitvin fra DB og sende til adapter
        vinListe = db.listAlleVin(rodvinEllerHvitvin);

        // opprette adapteren og legge liste i den
        adapter = new VinListAdapter(this, vinListe);

        // koble sammen adapteren og listviewet
        listView.setAdapter(adapter);

        // sette lytter til hvert objekt i lista
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int vinID = ((Vin) adapter.getItem(position)).get_ID();
                pos = position;
                endreVin(vinID);
            }
        });
    }

    // hente valg fra "sorteringsliste" når brukeren trykker på OK i dialogvinduet
    @Override
    public void onPositiveClick(int position) {
        itemNummer = position;

        switch (itemNummer){
            case 0:
                adapter.sorterEtterNavnAscending();
                break;
            case 1:
                adapter.sorterEtterNavnDescending();
                break;
            case 2:
                adapter.sorterEtterPoengAscending();
                break;
            case 3:
                adapter.sorterEtterPoengDescending();
                break;
            case 4:
                adapter.sorterEtterPrisAscending();
                break;
            case 5:
                adapter.sorterEtterPrisDescending();
                break;
            case 6:
                adapter.sorterEtterArgangAscending();
                break;
            case 7:
                adapter.sorterEtterArgangDescending();
                break;
            case 8:
                adapter.sorterEtterAlkoholAscending();
                break;
            case 9:
                adapter.sorterEtterAlkoholDescending();
                break;
            case 10:
                adapter.sorterEtterLandAscending();
                break;
            case 11:
                adapter.sorterEtterLandDescending();
                break;
        }
    }

    // kalles når bruker trykker på et vin-objekt i lista
    public void endreVin(int vinID) {
        Intent intent = new Intent(this, VinDetaljer.class);
        intent.putExtra("vinID", vinID);
        intent.putExtra("pos", pos);
        startActivity(intent);
    }
}
