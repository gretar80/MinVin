/**
 * Gretar Ævarsson
 * gretar80@gmail.com
 * © 2016
 */
package com.example.s198586.minvin;

public class Vin {
    //variabler
    private int _ID;
    private String navn;
    private double poeng;
    private String type;
    private String land;
    private double alkohol;
    private int argang;
    private double pris;
    private String notater;
    private byte[] figur;

    // konstruktører
    public Vin(){

    }

    public Vin(int id, String n, double p, String t, String l, double a, int ar, double pr, String no, byte[] f){
        _ID = id;
        navn = n;
        poeng = p;
        type = t;
        land = l;
        alkohol = a;
        argang = ar;
        pris = pr;
        notater = no;
        figur = f;
    }

    // getters og setters
    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public double getAlkohol() {
        return alkohol;
    }

    public void setAlkohol(double alkohol) {
        this.alkohol = alkohol;
    }

    public int getArgang() {
        return argang;
    }

    public void setArgang(int argang) {
        this.argang = argang;
    }

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public String getNotater() {
        return notater;
    }

    public void setNotater(String notater) {
        this.notater = notater;
    }

    public double getPoeng() {
        return poeng;
    }

    public void setPoeng(double poeng) {
        this.poeng = poeng;
    }

    public double getPris() {
        return pris;
    }

    public void setPris(double pris) {
        this.pris = pris;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getFigur() {
        return figur;
    }

    public void setFigur(byte[] figur) {
        this.figur = figur;
    }
}
