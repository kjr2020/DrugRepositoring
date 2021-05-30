import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

public class MakeWorkload {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String pocketString;
		String ligandString;
		
		PrintWriter pw = new PrintWriter("DrugRepoWorkload");
		FileReader ligandFile = new FileReader(args[0]);
		FileReader pocketFile = new FileReader(args[1]);
		
		BufferedReader ligandBR = new BufferedReader(ligandFile);
		BufferedReader pocketBR = new BufferedReader(pocketFile);
		
		ligandString = ligandBR.readLine();
		pocketString = pocketBR.readLine();
		
		String[] ligandList = ligandString.split(" ");
		String[] pocketList = pocketString.split(" ");
		
		for(String ligandStr : ligandList) {
			for(String pocketStr : pocketList) {
				pw.println(ligandStr + " " + pocketStr);
			}
		}
		
		pw.close();
		ligandFile.close();
		pocketFile.close();
		ligandBR.close();
		pocketBR.close();
	}

}