package neilbantoc.riseandsmile.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import neilbantoc.riseandsmile.service.AlarmService;

/**
 * Created by neilbantoc on 17/11/2016.
 */

public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlarmService.start(this);
    }
}
