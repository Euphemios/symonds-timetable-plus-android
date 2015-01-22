package com.psyngo.michael.symondstimetableplus;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LoginScreen extends ActionBarActivity {

    static LinearLayout newAc;
    static LinearLayout existingAc;
    static RelativeLayout loading;
    DataHandler db;
    static public int viewstate = 0;
    static String username;
    static String date;
    static boolean offlinemode;
    static View rootView;
    static int  uptodate;

    static String asyncHtml;
    static View asyncView;

    public LoginScreen(){};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_screen);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }
        Cursor C = getContentResolver().query(DbContentProvider.ATABLE_URI, null, null, null, null);
        if (C.moveToFirst()) {
            viewstate = 1;
            do {
                String formatDate = C.getString(3).split("to ")[1];
                Log.e("myapp", formatDate);
                Calendar endDate = Calendar.getInstance();
                Calendar todaysDate = Calendar.getInstance();
                try{
                    endDate.setTime(new SimpleDateFormat("d MMMMM yyyy").parse(formatDate));
                } catch (ParseException e){
                    e.printStackTrace();
                }
                endDate.set(Calendar.HOUR_OF_DAY, 0);
                endDate.add(Calendar.DAY_OF_WEEK, -1);
                Log.e("myapp", new SimpleDateFormat("HH:mm dd MMMM yyyy").format(endDate.getTime()));
                Log.e("myapp", new SimpleDateFormat("HH:mm dd MMMM yyyy").format(todaysDate.getTime()));
                ContentValues content = new ContentValues();
                if (todaysDate.after(endDate)){
                    Log.e("myapp", "after");
                    content.put("uptodate", 0);
                    getContentResolver().update(DbContentProvider.ATABLE_URI, content, "username='"+C.getString(0)+"'", null);
                }
                else{
                    Log.e("myapp", "not after");
                }
            }
            while (C.moveToNext());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_screen, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_report_bug) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"michaeljbrown.6@gmail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Bug report");
            i.putExtra(Intent.EXTRA_TEXT, "Android version:\nThe Problem:");
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(LoginScreen.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class LoginFragment extends Fragment {

        public LoginFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_login_screen, container, false);
            LoginScreen.rootView = rootView;
            Typeface robotoLight = Typeface.createFromAsset(rootView.getContext().getAssets(), "fonts/Roboto-Light.ttf");
            EditText usernameEdit = (EditText) rootView.findViewById(R.id.username);
            EditText passwordEdit = (EditText) rootView.findViewById(R.id.password);
            TextView subtitle = (TextView) rootView.findViewById(R.id.login_prompt);
            ListView accountsList = (ListView) rootView.findViewById(R.id.accountslistView);
            subtitle.setTypeface(robotoLight);
            usernameEdit.setTypeface(robotoLight);
            passwordEdit.setTypeface(robotoLight);

            newAc = (LinearLayout) rootView.findViewById(R.id.loginLinearLayout);
            existingAc = (LinearLayout) rootView.findViewById(R.id.existingAccountLinLayout);
            loading = (RelativeLayout) rootView.findViewById(R.id.LoadingRelLayout);
            if (viewstate == 0) {
                newAc.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);
                existingAc.setVisibility(View.INVISIBLE);
            } else if (viewstate == 1) {
                newAc.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.INVISIBLE);
                existingAc.setVisibility(View.VISIBLE);
            }

            List<String> usernames = new ArrayList<String>();
            List<String> dates = new ArrayList<String>();
            Cursor C = rootView.getContext().getContentResolver().query(DbContentProvider.ATABLE_URI, null, null, null, null);
            if(C.moveToFirst()){
                do{
                    Log.e(C.getString(0), C.getString(1));
                    usernames.add(C.getString(0));
                    dates.add(C.getString(3));
                } while(C.moveToNext());
            }


            accountListAdapter adapter = new accountListAdapter(rootView.getContext(), R.layout.simple, usernames, dates);
            accountsList.setAdapter(adapter);

            accountsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Timetable.started = false;
                    TextView user = (TextView) view.findViewById(R.id.nametextView);
                    username = user.getText().toString();
                    Cursor C = view.getContext().getContentResolver().query(DbContentProvider.ATABLE_URI, null, "username='"+username+"'", null, null);
                    if(C.moveToFirst()){
                        String password = C.getString(1);
                        date = C.getString(3);
                        int uptodate = C.getInt(4);
                        String html = C.getString(2);


                        if (uptodate==1 || uptodate==2) {
                            getFriendsList l = new getFriendsList(rootView.getContext(), view, html);
                            l.execute();
                        } else {
                            GetSymondsTimetable x = new GetSymondsTimetable(rootView.getContext(), LoginScreen.rootView);
                            x.execute(username, password);
                        }
                    }



                }
            });
            return rootView;
        }
    }

    public void onSubmit(View view) {
        EditText usernameEdit = (EditText) findViewById(R.id.username);
        EditText passwordEdit = (EditText) findViewById(R.id.password);
        View root = view.getRootView();

        String username = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        this.username = username;

        GetSymondsTimetable post = new GetSymondsTimetable(this, root);
        post.execute(username, password);
    }

    public void onNew(View view) {
        loading.setVisibility(View.INVISIBLE);
        newAc.setVisibility(View.VISIBLE);
        existingAc.setVisibility(View.INVISIBLE);
    }

    static public void openAlert(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rootView.getContext());

        alertDialogBuilder.setTitle("No Internet connection.");
        alertDialogBuilder.setMessage("You must be online to see your friends' frees.");
        // set positive button: Yes message
        alertDialogBuilder.setNegativeButton("Login anyway",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                Intent intent = new Intent(rootView.getContext(), Timetable.class);
                intent.putExtra("timetableHTML", LoginScreen.asyncHtml);
                rootView.getContext().startActivity(intent);
                dialog.cancel();
            }
        });
        // set negative button: No message
        alertDialogBuilder.setPositiveButton("Retry",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                getFriendsList l = new getFriendsList(rootView.getContext(), LoginScreen.asyncView, asyncHtml);
                l.execute();
                dialog.cancel();

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }


}

class accountListAdapter extends ArrayAdapter<String> {
    List<String> objects;
    List<String> dates;
    Context context;
    LayoutInflater mInflater;

    public accountListAdapter(Context context, int resource, List<String> objects, List<String> dates) {
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;
        this.dates = dates;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {

            itemView = mInflater.inflate(R.layout.simple, parent, false);
        }

        String name = objects.get(position);
        String date = dates.get(position);
        TextView tv = (TextView) itemView.findViewById(R.id.nametextView);
        TextView dtv = (TextView) itemView.findViewById(R.id.dateTextview);
        dtv.setVisibility(View.VISIBLE);
        dtv.setText(date);
        Typeface robotoLight = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        tv.setTypeface(robotoLight);
        tv.setText(name);
        ImageView i = (ImageView) itemView.findViewById(R.id.imageView);

        i.setBackgroundDrawable(itemView.getResources().getDrawable(R.drawable.ic_arrow));

        return itemView;
    }
}

class getFriendsList extends AsyncTask<Void, Void, Void> {

    Context ctx;
    View view;
    String html;
    ImageView i;
    ProgressBar p;
    boolean pinged = false;
    boolean success = false;

    public getFriendsList(Context ctx, View view, String html) {
        this.view = view;
        this.ctx = ctx;
        this.html = html;
        if (view != null) {
            i = (ImageView) view.findViewById(R.id.imageView);
            p = (ProgressBar) view.findViewById(R.id.LoginprogressBar);
        }
    }

    protected void onPreExecute() {
        if (view != null) {
            i.setVisibility(View.INVISIBLE);
            p.setVisibility(View.VISIBLE);
        }
    }

    protected Void doInBackground(Void... params) {

        HttpClient httpclient = new DefaultHttpClient();

        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            pinged = false;
            return null;
        } else {
            pinged = true;
        }

            try {
                HttpGet request = new HttpGet("http://mooshoon.pythonanywhere.com/users/" + LoginScreen.username + "/friends/");
                HttpResponse timetableResponse = httpclient.execute(request);
                int responseCode = timetableResponse.getStatusLine().getStatusCode();

                if (responseCode == 200) {

                    InputStream inputStream = timetableResponse.getEntity().getContent();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = "";
                    String result = "";
                    while ((line = bufferedReader.readLine()) != null)
                        result += line;

                    inputStream.close();

                    Log.e("Json", result);

                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();

                    OrchestrateResponseObject orchestrateResponse = gson.fromJson(result, OrchestrateResponseObject.class);

                    if (orchestrateResponse != null) {
                        for (OrchestrateResult friend : orchestrateResponse.results) {
                            FriendObjectConverter converter = new FriendObjectConverter();
                            FriendDatabaseObject v = converter.convertToFriendDatabaseObject(friend.value);

                            AddAFriend_Activity.friends.add(new FriendList(friend.path.key, v));
                        }
                    }
                }
                else{
                    Log.d("response", Integer.toString(responseCode));
                }
                success = true;
            } catch (IOException e) {
                Log.e("myapp", e.toString());
                success = false;
            }


        return null;
    }

    protected void onPostExecute(Void a) {


        if (view != null) {
            i.setVisibility(View.VISIBLE);
            p.setVisibility(View.INVISIBLE);
        }
        LoginScreen.asyncHtml = html;
        LoginScreen.asyncView = view;
        LoginScreen.newAc.setVisibility(View.INVISIBLE);
        LoginScreen.existingAc.setVisibility(View.VISIBLE);
        LoginScreen.loading.setVisibility(View.INVISIBLE);

        if (success) {
            Intent intent = new Intent(ctx, Timetable.class);
            intent.putExtra("timetableHTML", html);
            ctx.startActivity(intent);
        }
        else{
            LoginScreen.offlinemode = true;
            LoginScreen.openAlert(LoginScreen.rootView);

        }


    }
}





