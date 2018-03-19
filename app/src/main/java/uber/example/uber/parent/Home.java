package uber.example.uber.parent;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class Home extends AppCompatActivity
{

    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        id= intent.getStringExtra("id");

    }

    public void eval_act(View view)
    {
        Toast.makeText(this , " Will Open Student Evaluation",Toast.LENGTH_SHORT).show();
    }

    public void man_act(View view)
    {
        Toast.makeText(this , " Will Open Student Management ",Toast.LENGTH_SHORT).show();

    }

    public void chat_act(View view)
    {
        Toast.makeText(this , " Will Open chat ",Toast.LENGTH_SHORT).show();

    }


    public void bus_follow_act(View view)
    {
        startActivity( new Intent(this , ParentMap.class).putExtra("id",id));
    }
}
