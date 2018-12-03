import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class graph {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String dirPATH="/Users/miloni_134/Downloads/nypost";
		HashMap<String,String> fileUrlMap=new HashMap();
		HashMap<String,String> urlFileMap=new HashMap();
		
	 File f1 = new File("/Users/miloni_134/Downloads/URLtoHTML_nypost.csv");

File dir=new File(dirPATH);
String delimeter = ",";
BufferedReader fileReader = null;
String line;
try (BufferedReader br = new BufferedReader(new FileReader(f1))) {

    while ((line = br.readLine()) != null) {

        // use comma as separator
        String[] data = line.split(delimeter);
        fileUrlMap.put(data[0], data[1]);
        urlFileMap.put(data[1], data[0]);
    }

} catch (IOException e) {
    e.printStackTrace();
}

//PrintWriter writer=new PrintWriter(System.out);
FileWriter writer = new FileWriter("/Users/miloni_134/Downloads/edges.txt");

Set<String> edges =new HashSet<String>();
for(File file: dir.listFiles()) {
	//System.out.println("filename: "+file.getName());
	Document doc=Jsoup.parse(file,"UTF-8",fileUrlMap.get(file.getName()));
	Elements links=doc.select("a[href]");
	Elements pngs=doc.select("[src]");
	for(Element link: links) {
		String url =link.attr("href").trim();
		if(urlFileMap.containsKey(url)) {
			edges.add(file.getName()+" "+urlFileMap.get(url));
			
		}
	}
}


System.out.println("Started to make edgeList.txt !");

try (PrintWriter out = new PrintWriter("edgeList.txt") ) {
	for (String s: edges)
		out.println(s);
}

System.out.println("Done Creating edgeList.txt !");

System.out.println("done");
//writer.flush();
//writer.close();
	}

}
