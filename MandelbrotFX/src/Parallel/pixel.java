package Parallel;

public class pixel {
    private int x;
    private int y;
    private String colour;

    public pixel(int x, int y){
        this.x = x;
        this.y = y;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public String getColour(){
        return colour;
    }

    public void setColour(String colour){
        this.colour = colour;
    }
}
