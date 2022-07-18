package com.iot.listview;

import android.content.DialogInterface;
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
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class NewMemo extends AppCompatActivity {
    MemoDB helper;
    SQLiteDatabase db;
    AlertDialog DialogseekBar = null;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_memo);

        getSupportActionBar().setTitle("새 메모");
        //뒤로가기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Date now = new Date();
        SimpleDateFormat sdf_n = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        TextView textwdate1 = (TextView)findViewById(R.id.textWdate1);
        String strnew = sdf_n.format(now);
        textwdate1.setText(strnew);

        helper = new MemoDB(this);
        db=helper.getWritableDatabase();


    FloatingActionButton savememo = findViewById(R.id.saveMemo);
        savememo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newContent = findViewById(R.id.newContent);
                String strcontent = newContent.getText().toString();
                if (strcontent.length() == 0) {
                    Toast.makeText(NewMemo.this, "텍스트가 없습니다", Toast.LENGTH_SHORT).show();
                }
                if (strcontent.length() >= 1) {
                    AlertDialog.Builder box = new AlertDialog.Builder(NewMemo.this);
                    box.setMessage("저장하시겠습니까?");
                    box.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String strnow = sdf.format(now);
                            String sql = "insert into memo(content,wdate) values(";
                            sql += "'" + strcontent + "',";
                            sql += "'" + strnow + "')";

                            db.execSQL(sql);
                            Toast.makeText(NewMemo.this, "저장하였습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    box.setNegativeButton("취소", null);
                    box.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_memo_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.slider:
                AlertDialog.Builder builder = new AlertDialog.Builder(NewMemo.this);
                LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.activity_seekbar, (ViewGroup)findViewById(R.id.seekbarLayout));
                builder.setView(layout);
                DialogseekBar = builder.create();
                DialogseekBar.show();
                seekBar = DialogseekBar.findViewById(R.id.seekbar1);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                        TextView newcontent = findViewById(R.id.newContent);

                        Log.i("test","setsize_n");
                        newcontent.setTextSize(TypedValue.COMPLEX_UNIT_PX, i*2);

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