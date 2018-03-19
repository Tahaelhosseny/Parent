package uber.example.uber.parent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;


public class Login extends Activity
{

    EditText user ;
    EditText pass ;

    ProgressDialog mProgressDialog ;

    String userName = "" ;
    String password = "";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = (EditText) findViewById(R.id.userName);
        pass = (EditText) findViewById(R.id.password);
        mProgressDialog = new ProgressDialog(this) ;
        mProgressDialog.setTitle("Login In ");
        mProgressDialog.setMessage("we are happy that you are use Schoola\n your children will be safe");
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    public void login(View view)
    {
        mProgressDialog.show();
        userName = user.getText().toString();
        password = pass.getText().toString();

        if (userName.length()<1)
        {
            user.setError("هذا حقل فارغ");
        }
        if (password.length()<1)
        {
            pass.setError("هذا حقل فارغ");
        }

        else
            {
                makeRequest();
            }
    }


    private void makeRequest ()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        mProgressDialog.dismiss();
        final String link = "http://bus.smartapp-eg.com/api/bus/login?userName="+userName+"&password="+password;

         StringRequest stringRequest = new StringRequest(Request.Method.GET, link, new Response.Listener<String>() {
             @Override
             public void onResponse(String response)
             {
                 mProgressDialog.dismiss();

                 try {
                     JSONObject jsonObject = new JSONObject(response);
                     String id = jsonObject.getString("id").toString();
                     if(!id.equals("0"))
                        startActivity(new Intent(getApplicationContext() , Home.class).putExtra("id",id));
                     else
                     {
                         Toast.makeText(getApplicationContext() , "login error " , Toast.LENGTH_LONG).show();
                         user.setError("تسجيل دخول خاطئ");
                         pass.setError("تسجيل دخول خاطئ");

                     }

                 } catch (JSONException e)
                 {
                     mProgressDialog.dismiss();
                     Toast.makeText(getApplicationContext() , "login error " , Toast.LENGTH_LONG).show();
                     Toast.makeText(getApplicationContext() , e.getMessage(), Toast.LENGTH_LONG).show();

                     e.printStackTrace();
                 }
             }
         }, new Response.ErrorListener()
         {
             @Override
             public void onErrorResponse(VolleyError error)
             {
                 Toast.makeText(getApplicationContext() , error.toString() , Toast.LENGTH_SHORT).show();

             }
         });
         requestQueue.add(stringRequest);
    }


}
