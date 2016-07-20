package com.chinamobile.iot.monitor;

import com.chinamobile.iot.monitor.dataload.OneNETDataLoader;
import com.chinamobile.iot.monitor.schedule.DeviceDataPoint;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by szl on 2016/3/24.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(MyBatisConfiguration.class)
public class DataLoaderTest {

    @Test
    public void testDataLoader() {

        OneNETDataLoader dataLoader = new OneNETDataLoader();
        dataLoader.init();

        Date start = null;
        Date end = null;

        Calendar cal = Calendar.getInstance();
        cal.set(2016, 2, 1);
        start = cal.getTime();
        cal.set(2016, 2, 2);
        end = cal.getTime();
        List<DeviceDataPoint> datas = dataLoader.loadData("768693", "oAyRbD5iUVLtafqIwy3B0uUjmJQA", "Picker", start, end);
        int i = 10;
    }

}
