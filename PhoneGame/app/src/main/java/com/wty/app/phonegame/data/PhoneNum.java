package com.wty.app.phonegame.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PhoneNum{

    private static String[] telFirst="134,135,136,137,138,139,150,151,152,157,158,159,130,131,132,155,156,133,153".split(",");
    private static String getTel() {
        int index=getNum(0,telFirst.length-1);
        String first=telFirst[index];
        String second=String.valueOf(getNum(1,888)+10000).substring(1);
        String thrid=String.valueOf(getNum(1,9100)+10000).substring(1);
        return first+second+thrid;
     }
    public static int getNum(int start,int end) {
        return (int)(Math.random()*(end-start+1)+start);
    }

    private static PhoneNum instance = new PhoneNum();
    private Map<String,Boolean> phoneMap = new LinkedHashMap<>();
    private List<String> phonelist = new ArrayList<>();
    private List<String> normalNotice = new ArrayList<>();
    private List<String> badNotice = new ArrayList<>();
    private PhoneNum(){

        //100个白名单
        for(int i = 0;i<100;i++){
            String mobile = getTel();
            phonelist.add(mobile);
            phoneMap.put(mobile,true);
        }

        normalNotice.add("快递送餐");
        normalNotice.add("出租车");
        normalNotice.add("专车");

        //100个白名单
        for(int i = 0;i<100;i++){
            String mobile = getTel();
            phonelist.add(mobile);
            phoneMap.put(mobile,false);
        }

        badNotice.add("诈骗电话");
        badNotice.add("广告推销");
        badNotice.add("骚扰电话");
    }
    public static PhoneNum getInstance(){
        return instance;
    }

    public String getRandomMoblie(){
        Random random = new Random();
        int randomint = random.nextInt(phonelist.size());
        return phonelist.get(randomint);
    }


    public String getNormalNotice(){
        Random random = new Random();
        int randomint = random.nextInt(normalNotice.size());
        return normalNotice.get(randomint);
    }

    public String getBadNotice(){
        Random random = new Random();
        int randomint = random.nextInt(badNotice.size());
        return badNotice.get(randomint);
    }

    /**
     * @Decription 是否是骚扰电话
     **/
    public boolean isNeedtoRefuse(String mobile){
        return phoneMap.get(mobile);
    }

}
