//package com.itmuch.cloud.study;
//
//import redis.clients.jedis.Jedis;
//
///**
// * Created by haibozhang on 2019/9/13.
// */
//public class RedisTest {
//
//    private static final String redisIp = "192.168.106.243";
//    private static final int redisPort = 6399;
//
//    public static Jedis getRedisInstance() {
//        return new Jedis(redisIp, redisPort);
//    }
//
//    public static void main(String[] args) {
//        Jedis jedis = getRedisInstance();
//        long count = 0;
//        StringBuilder longValue = new StringBuilder();
//        for(int i = 0 ; i < 5000; i++){
//            longValue.append("value-");
//        }
//        while (count < 1000000000) {
//            count++;
//            String key = "key" + count;
//            String field = "field" + count;
//            String value = longValue.toString() + count;
//            jedis.hset(key, field, value);
//            try {
//                Thread.sleep(300);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//}
