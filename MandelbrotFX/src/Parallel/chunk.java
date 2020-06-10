package Parallel;

import java.util.ArrayList;

public class chunk {
    //A list of all the pixels in the chunk
    private final ArrayList<pixel> chunk_list;

    private ArrayList<pixel> getArray(){
        return chunk_list;

    }

    public chunk(){
        chunk_list = new ArrayList<pixel>();
    }

    public void add(int x, int y){
        pixel p = new pixel(x,y);
        p.setColour("0xFFFFFF");
        p.setVisualisationColour("0xFFFFFF");
        chunk_list.add(p);
    }

    public pixel getPixel(int i){
        return chunk_list.get(i);
    }

    public int getSize(){
        return chunk_list.size();
    }

    public void appendChunks(chunk newChunk){
        chunk_list.addAll(chunk_list.size(),newChunk.getArray());

    }
}
