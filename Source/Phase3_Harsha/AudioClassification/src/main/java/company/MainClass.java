package company;

import java.io.File;

public class MainClass {

    public static void main(String[] args) {
	// write your code here
        File video = new File("data/ProsantaChakrabarty.mp4");
        Conversion convert = new Conversion();
        convert.convertToAudio(video);
    }


}
