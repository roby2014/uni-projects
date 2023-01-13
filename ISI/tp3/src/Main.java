/// Main class, runs the application.
public class Main {
	public static void main(String[] args) {
		try {
			System.out.print("ISI TP3 by Vasco (49412), Pedro (49506) & Roberto (49418)");
			App.getInstance().Run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
