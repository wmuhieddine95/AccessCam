package com.example.accesscam.Models;

import com.google.gson.annotations.SerializedName;

public class PostByte {
        private byte[] image;

        public byte[] getImage() {
            return image;
        }

        public PostByte(byte[] b){this.image = b;}

}
