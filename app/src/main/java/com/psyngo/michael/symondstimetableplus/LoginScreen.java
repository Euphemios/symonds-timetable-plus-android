package com.psyngo.michael.symondstimetableplus;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class LoginScreen extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login_screen);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        Button submit = (Button) findViewById(R.id.submitButton);




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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login_screen, container, false);
            Typeface robotoThin = Typeface.createFromAsset(rootView.getContext().getAssets(), "fonts/Roboto-Light.ttf");
            EditText usernameEdit = (EditText) rootView.findViewById(R.id.username);
            EditText passwordEdit = (EditText) rootView.findViewById(R.id.password);
            TextView subtitle = (TextView) rootView.findViewById(R.id.login_prompt);
            subtitle.setTypeface(robotoThin);
            usernameEdit.setTypeface(robotoThin);
            passwordEdit.setTypeface(robotoThin);
            return rootView;
        }
    }

    public void onSubmit(View view) {
        EditText usernameEdit = (EditText) findViewById(R.id.username);
        EditText passwordEdit = (EditText) findViewById(R.id.password);
        View root = view.getRootView();


        String username = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        String ProcessLoginForm = "true";
        String signin = "Sign In";
        GetSymondsTimetable post = new GetSymondsTimetable(this, root);
        post.execute(ProcessLoginForm, username, password, signin);



    }





}





