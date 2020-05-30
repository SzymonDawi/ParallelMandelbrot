package Parallel;

import java.util.ArrayList;

public class chunk {
    //A list of all the pixels in the chunk
    private ArrayList<pixel> chunk_list;

    public chunk(){
        chunk_list = new ArrayList<pixel>();
    }

    public void add(int x, int y){
        pixel p = new pixel(x,y);
        p.setColour("0xFFFFFF");
        chunk_list.add(p);
    }

    public pixel getPixel(int i){
        return chunk_list.get(i);
    }

    public int getsize(){
        return chunk_list.size();
    }
}
