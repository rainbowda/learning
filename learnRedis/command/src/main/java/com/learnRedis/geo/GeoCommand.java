package com.learnRedis.geo;

import com.learnRedis.base.RedisBaseConnection;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;

import java.util.HashMap;
import java.util.Map;

public class GeoCommand extends RedisBaseConnection {


    /**
     * 将指定的地理空间位置（纬度、经度、名称）添加到指定的key中。
     * https://redis.io/commands/geoadd
     * GEOADD key longitude latitude member [longitude latitude member ...]
     * 返回值：添加到sorted set元素的数目，但不包括已更新score的元素。
     * 命令：
     * GEOADD geoKey 116.4072154982 39.9047253699 BeiJing 121.4737919321 31.2304324029 ShangHai 113.2643446427 23.1290765766 GuangZhou
     */
    @Before
    public void geoAdd() {
        Map<String, Point> localtions = new HashMap();
        localtions.put("BeiJing", new Point(116.4072154982,39.9047253699));
        localtions.put("ShangHai", new Point(121.4737919321,31.2304324029));
        localtions.put("GuangZhou", new Point(113.2643446427,23.1290765766));

        geoOperations.add("geoKey", localtions);
    }

    /**
     * 返回两个给定位置之间的距离。
     * 指定单位的参数 unit 必须是以下单位的其中一个：
     * m 表示单位为米(默认)。
     * km 表示单位为千米。
     * mi 表示单位为英里。
     * ft 表示单位为英尺。
     * GEODIST key member1 member2 [unit]
     * 返回值：两个给定位置之间的距离
     * 命令：
     * GEOADD geoKey 116.4072154982 39.9047253699 BeiJing 121.4737919321 31.2304324029 ShangHai 113.2643446427 23.1290765766 GuangZhou
     * GEODIST geoKey BeiJing ShangHai
     * GEODIST geoKey BeiJing GuangZhou
     * GEODIST geoKey ShangHai GuangZhou
     */
    @Test
    public void geoDist() {
        System.out.println("BeiJing到ShangHai的距离：" + jedis.geodist("geoKey", "BeiJing", "ShangHai"));

        System.out.println("BeiJing到GuangZhou的距离：" + geoOperations.distance("geoKey", "BeiJing", "GuangZhou"));

        System.out.println("ShangHai到GuangZhou的距离：" + geoOperations.distance("geoKey", "ShangHai", "GuangZhou"));
    }

    /**
     * 返回一个或多个位置元素的 Geohash 表示。https://redis.io/commands/geohash
     * GEOHASH key member [member ...]
     * 返回值：一个数组， 数组的每个项都是一个 geohash 。 命令返回的 geohash 的位置与用户给定的位置元素的位置一一对应。
     * 命令：
     * GEOADD geoKey 116.4072154982 39.9047253699 BeiJing 121.4737919321 31.2304324029 ShangHai 113.2643446427 23.1290765766 GuangZhou
     * GEOHASH geoKey BeiJing ShangHai GuangZhou
     */
    @Test
    public void geoHash() {
        System.out.println(geoOperations.hash("geoKey", "BeiJing", "ShangHai", "GuangZhou"));
    }

    /**
     * 所有对应key的成员的位置（经度和纬度）。
     * GEOPOS key member [member ...]
     * 返回值：经度和纬度
     * 命令：
     * GEOADD geoKey 116.4072154982 39.9047253699 BeiJing 121.4737919321 31.2304324029 ShangHai 113.2643446427 23.1290765766 GuangZhou
     * GEOPOS geoKey BeiJing ShangHai GuangZhou
     */
    @Test
    public void geoPos() {
        System.out.println(geoOperations.position("geoKey", "BeiJing", "ShangHai", "GuangZhou"));
    }

    /**
     * 以给定的经纬度为中心， 返回键包含的位置元素当中， 与中心的距离不超过给定最大距离的所有位置元素。
     * 范围可以使用以下其中一个单位：
     *
     * m 表示单位为米。
     * km 表示单位为千米。
     * mi 表示单位为英里。
     * ft 表示单位为英尺。
     * 在给定以下可选项时， 命令会返回额外的信息：
     *
     * WITHDIST: 在返回位置元素的同时， 将位置元素与中心之间的距离也一并返回。 距离的单位和用户给定的范围单位保持一致。
     * WITHCOORD: 将位置元素的经度和维度也一并返回。
     * WITHHASH: 以 52 位有符号整数的形式， 返回位置元素经过原始 geohash 编码的有序集合分值。 这个选项主要用于底层应用或者调试， 实际中的作用并不大。
     * 命令默认返回未排序的位置元素。 通过以下两个参数， 用户可以指定被返回位置元素的排序方式：
     *
     * ASC: 根据中心的位置， 按照从近到远的方式返回位置元素。
     * DESC: 根据中心的位置， 按照从远到近的方式返回位置元素。
     * 可以使用 COUNT <count> 选项去获取前 N 个匹配元素
     * https://redis.io/commands/georadius
     * GEORADIUS key longitude latitude radius m|km|ft|mi [WITHCOORD] [WITHDIST] [WITHHASH] [COUNT count]
     * 返回值：
     * 命令：
     * GEOADD geoKey 116.4072154982 39.9047253699 BeiJing 121.4737919321 31.2304324029 ShangHai 113.2643446427 23.1290765766 GuangZhou
     * GEORADIUS geoKey 121.4737919321 31.2304324029 1067672 m
     */
    @Test
    public void geoRadius() {
        Circle circle = new Circle(new Point(121.4737919321,31.2304324029),new Distance(1067672));

        System.out.println(geoOperations.radius("geoKey", circle));
    }

    /**
     * 这个命令和 GEORADIUS 命令一样， 都可以找出位于指定范围内的元素， 但是 GEORADIUSBYMEMBER 的中心点是由给定的位置元素决定的， 而不是像 GEORADIUS 那样， 使用输入的经度和纬度来决定中心点
     * 指定成员的位置被用作查询的中心。
     * GEORADIUSBYMEMBER key member radius m|km|ft|mi [WITHCOORD] [WITHDIST] [WITHHASH] [COUNT count]
     * 返回值：
     * 命令：
     * GEOADD geoKey 116.4072154982 39.9047253699 BeiJing 121.4737919321 31.2304324029 ShangHai 113.2643446427 23.1290765766 GuangZhou
     * GEORADIUSBYMEMBER geoKey ShangHai 1067672 m
     */
    @Test
    public void geoRadiusByMember() {
        System.out.println(geoOperations.radius("geoKey", "ShangHai", 1067672));
    }


}
