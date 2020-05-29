public class pixel {
    private int x;
    private int y;
    private int colour;

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

    public int getColour(){
        return colour;
    }

    public void setColour(int colour){
        this.colour = colour;
    }
}
