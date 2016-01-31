/**
 * Gretar Ævarsson
 * gretar80@gmail.com
 * © 2016
 */

package com.example.s198586.minvin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class VinListAdapter extends BaseAdapter{

    private LayoutInflater layoutInflater;
    private ArrayList<Vin> vinListe;

    // konstruktør
    public VinListAdapter(Context context, ArrayList<Vin> liste){
        layoutInflater = LayoutInflater.from(context);
        vinListe = liste;
    }

    @Override
    public int getCount() {
        return vinListe.size();
    }

    @Override
    public Object getItem(int position) {
        return vinListe.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        // initialisere
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.en_vin_layout, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.ikon = (ImageView)convertView.findViewById(R.id.listVinFigur);
            viewHolder.ikon.setFocusable(false);
            viewHolder.navn = (TextView)convertView.findViewById(R.id.listVinNavn);
            viewHolder.poeng = (TextView)convertView.findViewById(R.id.listVinPoeng);
            viewHolder.argang = (TextView)convertView.findViewById(R.id.listVinArgang);
            viewHolder.land = (TextView)convertView.findViewById(R.id.listVinLand);
            viewHolder.alkohol = (TextView)convertView.findViewById(R.id.listVinAlkohol);
            viewHolder.pris = (TextView)convertView.findViewById(R.id.listVinPris);

            convertView.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) convertView.getTag();

        // fill viewene med data
        if(vinListe.get(position).getFigur() == null || vinListe.get(position).getFigur().length == 0) {
            if (MainActivity.rodvinEllerHvitvin.equals("rodvin"))
                viewHolder.ikon.setImageResource(R.drawable.ikon_rodvinflaske);
            else
                viewHolder.ikon.setImageResource(R.drawable.ikon_hvitvinflaske);
        }
        else{
            viewHolder.ikon.setImageBitmap(KonvertBitmap.getImage(vinListe.get(position).getFigur()));
        }

        // vis verdier i liste
        viewHolder.navn.setText(vinListe.get(position).getNavn());

        viewHolder.poeng.setText(Double.toString(vinListe.get(position).getPoeng()));

        if(vinListe.get(position).getArgang() != 0)
            viewHolder.argang.setText(Integer.toString(vinListe.get(position).getArgang()));
        else
            viewHolder.argang.setText("");

        if(!vinListe.get(position).getLand().equals(""))
            viewHolder.land.setText(vinListe.get(position).getLand());
        else
            viewHolder.land.setText("");

        if(vinListe.get(position).getAlkohol() != 0)
            viewHolder.alkohol.setText(Double.toString(vinListe.get(position).getAlkohol()) + "%");
        else
            viewHolder.alkohol.setText("");

        if(vinListe.get(position).getPris() != 0)
            viewHolder.pris.setText(Double.toString(vinListe.get(position).getPris()) + " kr");
        else
            viewHolder.pris.setText("");

        return convertView;
    }

    // sortere liste etter navn A-Å
    public void sorterEtterNavnAscending(){
        Comparator<Vin> comparator = new Comparator<Vin>() {
            @Override
            public int compare(Vin vin1, Vin vin2) {
                return vin1.getNavn().compareToIgnoreCase(vin2.getNavn());
            }
        };

        Collections.sort(vinListe,comparator);
        notifyDataSetChanged();
    }

    // sortere liste etter navn Å-A
    public void sorterEtterNavnDescending(){
        Comparator<Vin> comparator = new Comparator<Vin>() {
            @Override
            public int compare(Vin vin1, Vin vin2) {
                return vin2.getNavn().compareToIgnoreCase(vin1.getNavn());
            }
        };

        Collections.sort(vinListe,comparator);
        notifyDataSetChanged();
    }

    // sortere liste etter poeng ascending (lavest først)
    public void sorterEtterPoengAscending(){
        Comparator<Vin> comparator = new Comparator<Vin>() {
            @Override
            public int compare(Vin vin1, Vin vin2) {
                return Double.compare(vin1.getPoeng(), vin2.getPoeng());
            }
        };

        Collections.sort(vinListe,comparator);
        notifyDataSetChanged();
    }

    // sortere liste etter poeng descending (høyest først)
    public void sorterEtterPoengDescending(){
        Comparator<Vin> comparator = new Comparator<Vin>() {
            @Override
            public int compare(Vin vin1, Vin vin2) {
                return Double.compare(vin2.getPoeng(), vin1.getPoeng());
            }
        };

        Collections.sort(vinListe,comparator);
        notifyDataSetChanged();
    }

    // sortere liste etter pris ascending (billigst først)
    public void sorterEtterPrisAscending(){
        Comparator<Vin> comparator = new Comparator<Vin>() {
            @Override
            public int compare(Vin vin1, Vin vin2) {
                return Double.compare(vin1.getPris(), vin2.getPris());
            }
        };

        Collections.sort(vinListe,comparator);
        notifyDataSetChanged();
    }

    // sortere liste etter pris descending (dyrest først)
    public void sorterEtterPrisDescending(){
        Comparator<Vin> comparator = new Comparator<Vin>() {
            @Override
            public int compare(Vin vin1, Vin vin2) {
                return Double.compare(vin2.getPris(), vin1.getPris());
            }
        };

        Collections.sort(vinListe,comparator);
        notifyDataSetChanged();
    }

    // sortere liste etter årgang ascending (gammel først)
    public void sorterEtterArgangAscending(){
        Comparator<Vin> comparator = new Comparator<Vin>() {
            @Override
            public int compare(Vin vin1, Vin vin2) {
                return Integer.compare(vin1.getArgang(), vin2.getArgang());
            }
        };

        Collections.sort(vinListe,comparator);
        notifyDataSetChanged();
    }

    // sortere liste etter årgang descending (ny først)
    public void sorterEtterArgangDescending(){
        Comparator<Vin> comparator = new Comparator<Vin>() {
            @Override
            public int compare(Vin vin1, Vin vin2) {
                return Integer.compare(vin2.getArgang(), vin1.getArgang());
            }
        };

        Collections.sort(vinListe,comparator);
        notifyDataSetChanged();
    }

    // sortere liste etter alkohol ascending (minst først)
    public void sorterEtterAlkoholAscending(){
        Comparator<Vin> comparator = new Comparator<Vin>() {
            @Override
            public int compare(Vin vin1, Vin vin2) {
                return Double.compare(vin1.getAlkohol(), vin2.getAlkohol());
            }
        };

        Collections.sort(vinListe,comparator);
        notifyDataSetChanged();
    }

    // sortere liste etter alkohol descending (mest først)
    public void sorterEtterAlkoholDescending(){
        Comparator<Vin> comparator = new Comparator<Vin>() {
            @Override
            public int compare(Vin vin1, Vin vin2) {
                return Double.compare(vin1.getAlkohol(), vin2.getAlkohol());
            }
        };

        Collections.sort(vinListe,comparator);
        notifyDataSetChanged();
    }

    // sortere liste etter land A-Å
    public void sorterEtterLandAscending(){
        Comparator<Vin> comparator = new Comparator<Vin>() {
            @Override
            public int compare(Vin vin1, Vin vin2) {
                return vin1.getLand().compareToIgnoreCase(vin2.getLand());
            }
        };

        Collections.sort(vinListe,comparator);
        notifyDataSetChanged();
    }

    // sortere liste etter land Å-A
    public void sorterEtterLandDescending(){
        Comparator<Vin> comparator = new Comparator<Vin>() {
            @Override
            public int compare(Vin vin1, Vin vin2) {
                return vin2.getLand().compareToIgnoreCase(vin1.getLand());
            }
        };

        Collections.sort(vinListe,comparator);
        notifyDataSetChanged();
    }

    // legg vin objekt i arraylista
    public void leggTilListe(Vin v){
        vinListe.add(v);
        notifyDataSetChanged();
    }

    // slett vin objekt fra arraylista
    public void slettFraListe(Vin v){
        vinListe.remove(v);
        notifyDataSetChanged();
    }

    // legg vin objekt med 'nummer' i arraylista
    public void slettFraListe(int nummer){
        vinListe.remove(nummer);
        notifyDataSetChanged();
    }

    // erstatte vin objekt 'nr i' i arraylista med nytt vin objekt 'v'
    public void endreIListe(int i, Vin v){
        Log.d("ADAPTER","endreListe id: " + i);
        vinListe.set(i, v);
        notifyDataSetChanged();
    }

    // denne klassen holder elementene som ligger i hver rad
    public class ViewHolder{
        public ImageView ikon;
        public TextView navn;
        public TextView poeng;
        public TextView argang;
        public TextView land;
        public TextView alkohol;
        public TextView pris;
    }

    // erstatte lista med ny liste
    public void oppdaterListe(ArrayList<Vin> vin){
        this.vinListe = vin;
        notifyDataSetChanged();
    }

}
