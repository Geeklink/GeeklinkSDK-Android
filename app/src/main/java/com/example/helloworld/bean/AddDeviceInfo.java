package com.example.helloworld.bean;

import com.example.helloworld.enumdata.AddDevType;

public class AddDeviceInfo {
    public String mDevName;
    public AddDevType mAddDevType;

    public AddDeviceInfo(String mDevName, AddDevType mAddDevType) {
        this.mDevName = mDevName;
        this.mAddDevType = mAddDevType;
    }
}
