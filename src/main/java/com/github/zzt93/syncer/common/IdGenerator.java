package com.github.zzt93.syncer.common;

import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventHeaderV4;
import com.github.zzt93.syncer.producer.input.connect.BinlogInfo;
import org.springframework.util.Assert;

/**
 * @author zzt
 */
public class IdGenerator {

  public static final String EID = "eid";

  public static String fromEvent(Event event, String binlogFileName) {
    EventHeaderV4 header = event.getHeader();
    return header.getServerId() + "." + binlogFileName + "." + header
        .getPosition() + "." + header.getEventType();
  }

  public static String fromEventId(String eventId, int ordinal) {
    return eventId + "." + ordinal;
  }

  public static BinlogInfo fromDataId(String dataId) {
    String[] split = dataId.split("\\.");
    Assert.isTrue(split.length == 5, "[Invalid data id]");
    return new BinlogInfo(split[1], Long.parseLong(split[2]));
  }

}