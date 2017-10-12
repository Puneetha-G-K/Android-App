package suryasoft.test.com.surya_soft;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    String email_aadr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText edit_email = (EditText) findViewById(R.id.edittext);
        Button login_button = (Button) findViewById(R.id.button);

        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                final SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                email_aadr = sharedPref.getString("email", "");
                if(!email_aadr.isEmpty()){
                    edit_email.setText(email_aadr);
                }
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String str = edit_email.getText().toString();

                if(str.isEmpty()) {
                    Toast msg = Toast.makeText(getBaseContext(), R.string.invalid_email, Toast.LENGTH_SHORT);
                    msg.show();
                    return;
                }

                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        final SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("email", str);
                        editor.commit(); // commit changes
                        Intent i = new Intent(MainActivity.this, JsonActivity.class);
                        i.putExtra("email",str);
                        startActivity(i);
                    }
                });
            }
        });
    }

}
