package com.iot.listview;

import static java.lang.Math.abs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    MemoDB helper;
    SQLiteDatabase db;
    public ListView listView;
    public MyAdapter adapter;
    Cursor cursor;
    SeekBar seekBar;
    AlertDialog DialogseekBar = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("메모장");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        helper = new MemoDB(this);
        db = helper.getReadableDatabase();
        cursor = db.rawQuery("select * from memo order by wdate desc", null);
        listView = (ListView) findViewById(R.id.listView);

        adapter = new MyAdapter(this, cursor);
        listView.setAdapter(adapter);
        TextView empty = (TextView) findViewById(R.id.empty);
        empty.setHint("메모가 없습니다");
        listView.setEmptyView(empty);

        FloatingActionButton addmemo = findViewById(R.id.addmemo);
        addmemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewMemo.class);
                startActivity(intent);
            }
        });

        //TextView textcontent = (TextView)findViewById(R.id.textContent);


    }


    class MyAdapter extends CursorAdapter {
        public MyAdapter(MainActivity context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return getLayoutInflater().inflate(R.layout.activity_memo, parent, false);
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            TextView textcontent = view.findViewById(R.id.textContent);
            //String result = cursor.getString(1).substring(cursor.getString(1).lastIndexOf("\n")+1);

            if (cursor.getString(1).contains("\n")){
                String result = cursor.getString(1).substring(0,cursor.getString(1).indexOf("\n"));
                if (result.length() == 0){
                        textcontent.setText("제목없음");
                    }
                else{
                    textcontent.setText(result);}
                }
            else{
                textcontent.setText(cursor.getString(1));
            }
            TextView textwdate = view.findViewById(R.id.textWdate);
            textwdate.setText(cursor.getString(2));;

            FrameLayout updatememo = view.findViewById(R.id.frame);
            final int _id = cursor.getInt(0);
            updatememo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(MainActivity.this,Updater.class);
                    intent.putExtra("_id", _id);
                    startActivity(intent);
                }
            });
            updatememo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder box = new AlertDialog.Builder(MainActivity.this);
                    box.setMessage("메모를 삭제 하시겠습니까?");
                    box.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String sql = "delete from memo where _id=" + _id;
                            db.execSQL(sql);
                            onRestart();
                        }
                    });
                    box.setNegativeButton("취소", null);
                    box.show();
                    return false;
                }
            });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);

        MenuItem search=menu.findItem(R.id.search);
        SearchView view=(SearchView)search.getActionView();

        view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String sql="select * from memo where content like  '%" + newText + "%'";
                cursor=db.rawQuery(sql,null);
                adapter.changeCursor(cursor);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
    //옵션메뉴를 눌렀을때 발생하는 이벤트
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.slider:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.activity_seekbar, (ViewGroup)findViewById(R.id.seekbarLayout));
                builder.setView(layout);
                DialogseekBar = builder.create();
                DialogseekBar.show();
                seekBar = DialogseekBar.findViewById(R.id.seekbar1);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                        Log.i("test","setsize");
                        //TextView test12 = (TextView)findViewById(R.id.test1);
                        //test12.setTextSize(TypedValue.COMPLEX_UNIT_PX, i*2);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                break;
            case R.id.itemwdate:
                cursor=db.rawQuery("select * from memo order by wdate desc",null);
                break;
            case R.id.itemwdateR:
                cursor=db.rawQuery("select * from memo order by wdate asc",null);
                break;
        }
        //커서내용이 변경되었으므로 바뀐 커서값을 어덥터에서 바꿔줌
        adapter.changeCursor(cursor);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        cursor=db.rawQuery("select * from memo order by wdate desc",null);
        adapter.changeCursor(cursor);
        super.onRestart();
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



