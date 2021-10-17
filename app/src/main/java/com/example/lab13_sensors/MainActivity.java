package com.example.lab13_sensors;

import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

    // Описание тэга для логов debug
    private static final String TAG = "myLogs";

    // Лимит скорости тряски
    private static final int SHAKE_THRESHOLD = 600;

    // Объявим переменные для работы с акселерометром
    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;

    // Объявим переменные компонентов
    private TextView mAzimuthTextView, mPitchTextView, mRollTextView;
    private TabHost tabHost;
    private LinearLayout tabPage1;
    private LinearLayout tabPage2;
    private LinearLayout tabPage3;
    private LinearLayout tabPage4;

    // Текущая отображаемая вкладка
    private int currentTab = 0;

    // Последнее время во время проверки на тряску
    private long mLastUpdate = 0;

    // Последние координаты по акселерометру во время проверки на тряску
    private float mLastX, mLastY, mLastZ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Получаем доступ к датчику - акселерометр
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Найдем компоненты в XML разметке
        mAzimuthTextView = (TextView) findViewById(R.id.textViewAzimuth);
        mPitchTextView = (TextView) findViewById(R.id.textViewPitch);
        mRollTextView = (TextView) findViewById(R.id.textViewRoll);

        // Настройка элемента управления TabHost
        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        // Настройка первой вкладки
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator("Первое");
        tabHost.addTab(tabSpec);

        // Настройка второй вкладки
        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator("Второе");
        tabHost.addTab(tabSpec);

        // Настройка третьей вкладки
        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setContent(R.id.tab3);
        tabSpec.setIndicator("Третье");
        tabHost.addTab(tabSpec);

        // Настройка четвертой вкладки
        tabSpec = tabHost.newTabSpec("tag4");
        tabSpec.setContent(R.id.tab4);
        tabSpec.setIndicator("Четвертое");
        tabHost.addTab(tabSpec);

        // Устанавливаем первую вкладку текущей
        tabHost.setCurrentTab(currentTab);

        // Найдем компоненты вкладок в XML разметке
        tabPage1 = (LinearLayout) findViewById(R.id.tab1);
        tabPage2 = (LinearLayout) findViewById(R.id.tab2);
        tabPage3 = (LinearLayout) findViewById(R.id.tab3);
        tabPage4 = (LinearLayout) findViewById(R.id.tab4);

        // Установим значение вкладок прозрачным
        tabPage1.setVisibility(TextView.INVISIBLE);
        tabPage2.setVisibility(TextView.INVISIBLE);
        tabPage3.setVisibility(TextView.INVISIBLE);
        tabPage4.setVisibility(TextView.INVISIBLE);

        // Обработка нажатия на вкладки
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tag) {
                switch (tag) {
                    case "tag1":
                        tabPage1.setVisibility(TextView.INVISIBLE);
                        currentTab = 0;
                        break;
                    case "tag2":
                        tabPage2.setVisibility(TextView.INVISIBLE);
                        currentTab = 1;
                        break;
                    case "tag3":
                        tabPage3.setVisibility(TextView.INVISIBLE);
                        currentTab = 2;
                        break;
                    case "tag4":
                        tabPage4.setVisibility(TextView.INVISIBLE);
                        currentTab = 3;
                        break;
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        // Отменяем регистрацию прослушивания датчика
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Подписываемся на прослушивание датчика
        mSensorManager.registerListener(this, mAccelerometerSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    // Вызывается, когда меняется точность данных сенсора и в начале получения данных
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // Не используется
    }

    @Override
    // Получение данных от сенсора
    public void onSensorChanged(SensorEvent event) {

        // Получаем координаты x y z от акселерометра
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // Устанавливаем текст элементам управления
        mAzimuthTextView.setText("x: " + String.valueOf(x));
        mPitchTextView.setText("y: " + String.valueOf(y));
        mRollTextView.setText("z: " + String.valueOf(z));

        // Вертикальная ориентация
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            // Меняем текущую вкладку в зависимости от координаты X
            if (x <= -4.0f && currentTab != 3) currentTab++;
            else if (x >= 4.0f && currentTab != 0) currentTab--;
        }
        // Горизонтальная ориентация
        else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            // В зависиомсти в какую сторону телефон повернут
            if (x > 0) {
                // Меняем текущую вкладку в зависимости от координаты Y
                if (y <= -4.0f && currentTab != 0) currentTab--;
                else if (y >= 4.0f && currentTab != 3) currentTab++;
            }
            else {
                // Меняем текущую вкладку в зависимости от координаты Y
                if (y <= -4.0f && currentTab != 3) currentTab++;
                else if (y >= 4.0f && currentTab != 0) currentTab--;
            }
        }
        // Устанавливаем текушую вкладку по результатам работы акселерометра
        tabHost.setCurrentTab(currentTab);

        // Текущее время в мс
        long curTime = System.currentTimeMillis();

        // Отслеживаем каждые 100 мс
        if ((curTime - mLastUpdate) > 100)
        {
            long diffTime = (curTime - mLastUpdate);

            // Устанавливаем время системы во время "прошлой" проверки
            mLastUpdate = curTime;

            // Вычисляем скорость изменения показаний
            float speed = Math.abs(x + y + z - mLastX - mLastY - mLastZ) / diffTime * 10000;

            // Отправляем в LogCat скорсть
            Log.d(TAG, "Скорость изменения показаний - " + String.valueOf(speed));

            // Проверяем скорость изменения показаний
            if (speed > SHAKE_THRESHOLD) {

                // В зависимости от выбранной вкладки показываем содержимое
                switch (currentTab) {
                    case 0:
                        tabPage1.setVisibility(TextView.VISIBLE);
                        break;
                    case 1:
                        tabPage2.setVisibility(TextView.VISIBLE);
                        break;
                    case 2:
                        tabPage3.setVisibility(TextView.VISIBLE);
                        break;
                    case 3:
                        tabPage4.setVisibility(TextView.VISIBLE);
                        break;
                }

            }

            // Устанавливаем координаты во время "прошлой" проверки
            mLastX = x;
            mLastY = y;
            mLastZ = z;
        }

    }

}
