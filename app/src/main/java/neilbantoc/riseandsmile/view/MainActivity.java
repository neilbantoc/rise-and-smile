package neilbantoc.riseandsmile.view;

import android.app.AlarmManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import neilbantoc.riseandsmile.R;
import neilbantoc.riseandsmile.contract.AlarmList;
import neilbantoc.riseandsmile.model.Alarm;
import neilbantoc.riseandsmile.model.AlarmRepository;
import neilbantoc.riseandsmile.presenter.AlarmListPresenter;
import neilbantoc.riseandsmile.service.AlarmService;

/**
 * Created by neilbantoc on 17/11/2016.
 */

public class MainActivity extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
    }
}
