package com.example.mangacloud;

// Setters An Getters
public class Mangalist_item {

    String NOMBRE, DESCRIPCION, COVER, VOLUMENES, LINK;

    public Mangalist_item() {

    }

    public Mangalist_item(String NOMBRE, String DESCRIPCION, String COVER, String VOLUMENES, String LINK) {
        this.NOMBRE = NOMBRE;
        this.DESCRIPCION = DESCRIPCION;
        this.COVER = COVER;
        this.VOLUMENES = VOLUMENES;
        this.LINK = LINK;
    }

    public String getNOMBRE() {
        return NOMBRE;
    }

    public void setNOMBRE(String NOMBRE) {
        this.NOMBRE = NOMBRE;
    }

    public String getDESCRIPCION() {
        return DESCRIPCION;
    }

    public void setDESCRIPCION(String DESCRIPCION) {
        this.DESCRIPCION = DESCRIPCION;
    }

    public String getCOVER() {
        return COVER;
    }

    public void setCOVER(String COVER) {
        this.COVER = COVER;
    }

    public String getVOLUMENES() {return VOLUMENES;}

    public  void setVOLUMENES(String VOLUMENES) {this.VOLUMENES = VOLUMENES;}

    public void  setLINK(String LINK) {this.LINK = LINK;}

    public String getLINK() {return LINK;}
}
