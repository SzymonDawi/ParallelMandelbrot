import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class main {

    public static chunk[] createChunks(int numCores, int width, int height,int chunkingMethod){
        int i,j;
        int currentCore = 0;
        int xCounter;
        chunk chunkArray[] = new chunk[numCores];
        int totalPixels = width*height;
        for (i=0;i<numCores;i++){
            chunkArray[i] = new chunk();
        }

        for(i=0;i<height;i++){
            for(j=0;j<width;j++){
                chunkArray[currentCore].add(j,i);
            }
            currentCore++;
            if (currentCore>= numCores){currentCore =0;}
        }

    return chunkArray;
    }

    public static void main(String []args) throws Exception {

        int chunkingMethod = 1;
        chunk test[] = createChunks(10, 720, 1280, chunkingMethod);
        System.out.println(test[0].size());
        System.out.println(test[1].size());
        System.out.println(test[2].size());
        System.out.println(test[3].size());
        System.out.println(test[4].size());
        System.out.println(test[5].size());
        System.out.println(test[6].size());
        System.out.println(test[7].size());
        System.out.println(test[8].size());
        System.out.println(test[9].size());

    }

}