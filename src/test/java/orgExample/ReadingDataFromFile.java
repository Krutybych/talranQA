package orgExample;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ReadingDataFromFile {

    public List<String> data(String pathToDatabase) {
        List<String> cities = new ArrayList<>();
        int num = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(pathToDatabase))) {
            String str = br.readLine();
            while (str != null) {
                cities.add(str);
                str = br.readLine();
                num++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cities;
    }
}
