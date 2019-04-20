package com.xcleans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mengliwei on 2019/3/17.
 *
 * @function:
 * @since 1.0.0
 */
public class MyAidlBean implements Parcelable {

    private int a;

    protected MyAidlBean() {
    }

    protected MyAidlBean(Parcel in) {
        a = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(a);
    }


    public void readFromParcel(Parcel in) {
        a = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MyAidlBean> CREATOR = new Creator<MyAidlBean>() {
        @Override
        public MyAidlBean createFromParcel(Parcel in) {
            return new MyAidlBean(in);
        }

        @Override
        public MyAidlBean[] newArray(int size) {
            return new MyAidlBean[size];
        }
    };
}
