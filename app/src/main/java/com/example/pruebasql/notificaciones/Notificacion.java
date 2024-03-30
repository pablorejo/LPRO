package com.example.pruebasql.notificaciones;


import org.threeten.bp.LocalDate;

import java.util.Calendar;
import java.util.Date;

public class Notificacion {
    public Date date;
    public String titulo;
    public String texto;
    public int idNotificacion;

    public Notificacion(Date date, String titulo, String texto, int idNotificacion) {
        this.date = date;
        this.titulo = titulo;
        this.texto = texto;
        this.idNotificacion = idNotificacion;
    }

    public Notificacion(LocalDate localDate, String titulo, String texto, int idNotificacion) {


        Calendar calendar = Calendar.getInstance();
        calendar.set(localDate.getYear(),localDate.getMonthValue(),localDate.getDayOfMonth(),3,30,0);

        this.date = calendar.getTime();
        this.titulo = titulo;
        this.texto = texto;
        this.idNotificacion = idNotificacion;
    }
}
