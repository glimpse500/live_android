package com.oolive.live.model.custommsg;

public class ByteMsg {

    byte[] data;
    String text;
    public ByteMsg()
    {
    }
    public ByteMsg(byte[] data){
        this.data= data;
    }
    public byte[] getData() { return this.data == null ? "".getBytes() : this.data;
    }
    public void setText(String text)
    {
        this.text = text;
    }

    public void setData(byte[] var1) {
        this.data = var1;
    }


}
