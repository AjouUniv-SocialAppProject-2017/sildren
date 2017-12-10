package media.socialapp.sildren;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class InitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
    }

    public void onSignUpBtnClicked(View view) {

        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    public void onLoginBtnClicked(View view) {

        Intent intent = new Intent(this,SignupActivity.class);
        startActivity(intent);

    }
}
