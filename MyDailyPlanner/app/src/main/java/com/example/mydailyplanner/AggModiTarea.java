package com.example.mydailyplanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AggModiTarea extends AppCompatActivity {

    Calendar calendar;
    BD mydb;

    Boolean isModify = false;
    String idTarea;
    TextView titulo_toolbar;
    EditText edit_text;
    TextView dateText;
    Button btn_guardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_agg_modi_tarea);

        mydb = new BD(getApplicationContext());
        calendar = new GregorianCalendar();
        titulo_toolbar = findViewById(R.id.toolbar_title);
        edit_text = findViewById(R.id.edit_text);
        dateText = findViewById(R.id.dateText);
        btn_guardar = findViewById(R.id.save_btn);


        dateText.setText(new SimpleDateFormat("E, dd MMMM yyyy").format(calendar.getTime()));


        Intent intent = getIntent();
        if (intent.hasExtra("isModify")) {
            isModify = intent.getBooleanExtra("isModify", false);
            idTarea = intent.getStringExtra("id");
            init_modify();
        }


    }

    public void init_modify() {
        titulo_toolbar.setText("Modificar Tarea");
        btn_guardar.setText("Actualizar");
        LinearLayout deleteTask = findViewById(R.id.deleteTask);
        deleteTask.setVisibility(View.VISIBLE);
        Cursor task = mydb.obtenerUnaTarea(idTarea);
        if (task != null) {
            task.moveToFirst();


            edit_text.setText(task.getString(1));

            SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                calendar.setTime(iso8601Format.parse(task.getString(2)));
            } catch (ParseException e) {
            }

            dateText.setText(new SimpleDateFormat("E, dd MMMM yyyy").format(calendar.getTime()));


        }

    }


    public void saveTask(View v) {

        /*Checking for Empty Task*/
        if (edit_text.getText().toString().trim().length() > 0) {

            if (isModify) {
                mydb.actualizarTarea(idTarea, edit_text.getText().toString(), new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
                Toast.makeText(getApplicationContext(), "Tarea actualizada.", Toast.LENGTH_SHORT).show();
            } else {
                mydb.agregarTarea(edit_text.getText().toString(), new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
                Toast.makeText(getApplicationContext(), "Tarea agregada.", Toast.LENGTH_SHORT).show();
            }
            finish();

        } else {
            Toast.makeText(getApplicationContext(), "Esta vacio!.", Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteTask(View v) {
        mydb.eliminarTarea(idTarea);
        Toast.makeText(getApplicationContext(), "Tarea eliminada", Toast.LENGTH_SHORT).show();
        finish();
    }


    public void chooseDate(View view) {
        final View dialogView = View.inflate(this, R.layout.date_picker, null);
        final DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle("Choose Date");
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


                calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                dateText.setText(new SimpleDateFormat("E, dd MMMM yyyy").format(calendar.getTime()));

            }
        });
        builder.show();
    }
}