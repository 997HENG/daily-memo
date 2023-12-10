package com.example.daily_memo;

public class Notes {
    public String type;
    public String toDo;
    public double latitude;
    public double longitude;
    public double docId;

    Notes(String type,String toDo,String latitude,String longitude,String docId){
        this.type = type;
        this.toDo = toDo;
        this.latitude = Double.valueOf(latitude);
        this.longitude = Double.valueOf(longitude);
        this.docId = Double.valueOf(docId);
    }

}
