package com.chinamobile.iot.monitor;

import com.chinamobile.iot.monitor.dao.NodeDao;
import com.chinamobile.iot.monitor.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by szl on 2016/2/4.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(App.class)
public class NodeDaoImplTest {

    @Autowired
    private NodeDao dao;

    @Before
    public void setUp() {

    }

    @Test
    public void Test() {
        List<Node> nodes = dao.selectNodes();

        Node node = dao.selectNode("768679");

        Calendar cal = Calendar.getInstance();
        cal.set(2016, 2, 1, 0, 0, 0);
        Date date = cal.getTime();
        dao.updateLastReqTime("768679", date);
        List<MiddleTarget> middleTargets = dao.selectMidTargets();
        MiddleTarget middleTarget = dao.selectMidTarget("cpu_total");
        List<Target> targets = dao.selectTargets();
        Target target = dao.selectTarget("cpu_user_percent");
        List<String> strings = dao.selectNodeTarget("768679");

        TargetRecord record = new TargetRecord();
        record.setNodeId("768679");
        record.setResult("100");
        record.setTargetName("cpu_user_percent");
        record.setTime(date);
        dao.insertTargetRecord(record);
        List<NodeTarget> nodeTargets = dao.selectNodeTargets();

        Date date11 = dao.selectMaxDate("768679", "cpu_user_percent");
    }
}
