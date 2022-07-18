package com.iot.listview;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Updater extends AppCompatActivity{
    int _id;
    MemoDB helper;
    SQLiteDatabase db;
    TextView textView;
    AlertDialog DialogseekBar = null;
    SeekBar seekBar_u;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updater);
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        getSupportActionBar().setTitle("메모");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        _id = intent.getIntExtra("_id", 0);

        helper = new MemoDB(this);
        db = helper.getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from memo where _id =" + _id, null);
        if(cursor.moveToNext()){
            TextView textWdate = findViewById(R.id.textWdate);
            textWdate.setText(cursor.getString(2));
            TextView editContent = findViewById(R.id.editContent);
            editContent.setText(cursor.getString(1));
        }
        FloatingActionButton savememo = findViewById(R.id.updateMemo);
        savememo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editContent = findViewById(R.id.editContent);
                String strContent = editContent.getText().toString();
                AlertDialog.Builder box=new AlertDialog.Builder(Updater.this);
                if(cursor.getString(1).equals(strContent)){
                    Toast.makeText(Updater.this, "변경사항이 없습니다", Toast.LENGTH_SHORT).show();
                }
                else{
                    box.setMessage("수정하시겠습니까?");
                    box.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String strnow = sdf.format(now);
                            String sql="update memo set content='" + strContent + "', wdate='" + strnow + "' ";
                            sql += " where _id=" +_id;
                            db.execSQL(sql);
                            finish();
                        }
                    });
                    box.setNegativeButton("취소",null);
                    box.show();
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_memo_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.slider:
                AlertDialog.Builder builder = new AlertDialog.Builder(Updater.this);
                LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.activity_seekbar, (ViewGroup)findViewById(R.id.seekbarLayout));
                builder.setView(layout);
                DialogseekBar = builder.create();
                DialogseekBar.show();
                seekBar_u = DialogseekBar.findViewById(R.id.seekbar1);
                seekBar_u.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                        //TextView textcontent = findViewById(R.id.textContent);
                        TextView editcontent = findViewById(R.id.editContent);
                        //Log.i("test","setsize_t");
                        //textcontent.setTextSize(TypedValue.COMPLEX_UNIT_PX, i);
                        Log.i("test","setsize_e");
                        editcontent.setTextSize(TypedValue.COMPLEX_UNIT_PX, i*2);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (DialogseekBar != null){
            Log.i("test","dismiss");
            DialogseekBar.dismiss();
        }
    }
}