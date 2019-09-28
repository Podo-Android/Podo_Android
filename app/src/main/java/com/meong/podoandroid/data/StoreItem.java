package com.meong.podoandroid.data;

import android.os.Parcel;
import android.os.Parcelable;

public class StoreItem implements Parcelable {

    String name;
    Float latitude;
    Float longtitude;
    String address;

    public StoreItem(String name, Float latitude, Float longtitude, String address) {
        this.name = name;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.address = address;
    }

    protected StoreItem(Parcel in) {
        name = in.readString();
        latitude = in.readFloat();
        longtitude = in.readFloat();
        address = in.readString();
    }

    public static final Creator<StoreItem> CREATOR = new Creator<StoreItem>() {
        @Override
        public StoreItem createFromParcel(Parcel in) {
            return new StoreItem(in);
        }

        @Override
        public StoreItem[] newArray(int size) {
            return new StoreItem[size];
        }
    };

    @Override
    public String toString() {
        return "StoreItem{" +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longtitude=" + longtitude +
                ", address='" + address + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(Float longtitude) {
        this.longtitude = longtitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(address);

        if (latitude == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeFloat(latitude);
        }

        if (longtitude == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeFloat(longtitude);
        }
    }


}
