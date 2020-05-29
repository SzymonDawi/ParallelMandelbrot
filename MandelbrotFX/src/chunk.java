import java.util.LinkedList;
import java.util.Queue;
public class chunk {
    private Queue<pixel> q;




    public chunk(){
         q =new LinkedList<>();

    }

    public void add(int x, int y){
        q.add(new pixel(x,y));
    }

    public pixel remove(){
        return q.remove();
    }

    public int size(){
        return q.size();
    }
}
