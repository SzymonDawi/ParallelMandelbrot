package Parallel;

import java.util.ArrayList;

public class chunk {
    //A list of all the pixels in the chunk
    private ArrayList<pixel> chunk_list;

    public chunk(){
        chunk_list = new ArrayList<pixel>();
    }

    public void add(int x, int y){
        synchronized(chunk_list) {
            chunk_list.add(new pixel(x, y));
        }
    }

    public pixel getPixel(int i){
        synchronized(chunk_list) {
            return chunk_list.get(i);
        }
    }

    public int getsize(){
        synchronized(chunk_list) {
            return chunk_list.size();
        }
    }
}
