import java.util.HashMap;
import java.util.Scanner;
import java.time.LocalDate;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.regex.Pattern;

/// Application class, handles user input and execute expected job.
class App {
	private static App _instance = null;
	private HashMap<Option, DbWorker> _dbMethods;

	// Possible menu options
	private enum Option {
		Unknown,
		Exit,
		InsertDriver,
		PutVehicleOutOfService,
		CalculateTotalXOfVehicle,
		PrintClientListMostTrips,
		PrintClientListNoTrips,
		PrintTripsByNifGivenYear,
		PrintDriverAccumulatedCost
	}

	// Model function to be run
	interface DbWorker {
		void doWork();
	}

	// Private constructor, links each Option to its Model function inside a HashMap
	private App() {
		_dbMethods = new HashMap<Option, DbWorker>();

		// 2. InsertDriver
		_dbMethods.put(Option.InsertDriver, new DbWorker() {
			public void doWork() {
				System.out.println("Insert driver");
				Scanner scanner = new Scanner(System.in);

				System.out.print("idpessoa: ");
				int idpessoa = scanner.nextInt();

				String ncconducao = getNnconducao(scanner);

				LocalDate birthday = getBdayDate(scanner);

				Model.getInstance().insertDriver(idpessoa, ncconducao, birthday);
			}
		});

		// 3. PutVehicleOutOfService
		_dbMethods.put(Option.PutVehicleOutOfService, new DbWorker() {
			public void doWork() {
				Scanner scanner = new Scanner(System.in);

				String matricula = getMatricula(scanner);

				// scanner.close();

				Model.getInstance().insertOldVehicle(matricula);
			}
		});

		// 4. CalculateTotalXOfVehicle
		_dbMethods.put(Option.CalculateTotalXOfVehicle, new DbWorker() {
			public void doWork() {
				ResultSet vehicles = Model.getInstance().getVehicles();
				printResults(vehicles);

				Scanner scanner = new Scanner(System.in);

				String matricula = getMatricula(scanner);

				// scanner.close();

				Model.getInstance().displayVehicleInfo(matricula);
			}
		});

		// 5. PrintClientListMostTrips
		_dbMethods.put(Option.PrintClientListMostTrips, new DbWorker() {
			public void doWork() {
				Scanner scanner = new Scanner(System.in);

				System.out.print("year: ");
				Integer year = scanner.nextInt();

				// scanner.close();

				Model.getInstance().displayDriversMostTrips(year);
			}
		});

		// 6. PrintClientListNoTrips
		_dbMethods.put(Option.PrintClientListNoTrips, new DbWorker() {
			public void doWork() {
				Model.getInstance().displayDriversNoTrips();
			}
		});

		// 7. PrintTripsByNifGivenYear
		_dbMethods.put(Option.PrintTripsByNifGivenYear, new DbWorker() {
			public void doWork() {
				Scanner scanner = new Scanner(System.in);

				System.out.print("nif: ");
				String nif = scanner.next();

				System.out.print("year: ");
				Integer year = scanner.nextInt();

				// scanner.close();

				Model.getInstance().displayNrOfTrips(nif, year);
			}
		});

		// 8. PrintDriverAccumulatedCost
		_dbMethods.put(Option.PrintDriverAccumulatedCost, new DbWorker() {
			public void doWork() {
				Scanner scanner = new Scanner(System.in);

				System.out.print("year: ");
				Integer year = scanner.nextInt();

				// scanner.close();

				Model.getInstance().displayInfoOfDriver(year);
			}
		});
	}

	// Singleton
	public static App getInstance() {
		if (_instance == null) {
			_instance = new App();
		}
		return _instance;
	}

	// Displays menu and reads user input
	private Option DisplayMenu() {
		Option option = Option.Unknown;
		try {
			System.out.println("ISI TP3");
			System.out.println();
			System.out.println("1. Exit");
			System.out.println("2. Insert new driver");
			System.out.println("3. Put a vehicle out of service");
			System.out.println("4. Calculate total hours, kilometers and total cost of a vehicle");
			System.out.println("5. Print the list of client(s) (ID, name and NIF) with the most trips in a given year");
			System.out.println("6. Print the list of drivers with no trips done");
			System.out.println(
					"7. Prints the number of trips done by the cars of a given proprietary (NIF) in a given year");
			System.out.println(
					"8. Given a year, prints information about the driver which had the greatest accumulated cost");
			System.out.print(">");
			Scanner s = new Scanner(System.in);
			int result = s.nextInt();
			option = Option.values()[result];
		} catch (RuntimeException ex) {
			// nothing to do.
		}

		return option;
	}

	// Clears console
	private final static void clearConsole() {
		// console is 80 columns and 25 lines
		for (int y = 0; y < 25; y++) {
			System.out.println("\n");
		}
	}

	// Connects to database via connection url
	private void Login() {
		String url = "jdbc:postgresql://10.62.73.73:5432/?user=mp31&password=mp31&ssl=false";
		Model.getInstance().setConnectionString(url);
	}

	// Main loop, asking for user input and running model functions
	public void Run() throws Exception {
		Login();
		Option userInput = Option.Unknown;
		do {
			clearConsole();
			userInput = DisplayMenu();
			clearConsole();
			try {
				_dbMethods.get(userInput).doWork();
				System.out.println("Done, press enter to continue.");
				System.in.read();
			} catch (NullPointerException ex) {
			}

		} while (userInput != Option.Exit);
	}

	// Prints results from ResultSet
	private void printResults(ResultSet rs) {
		try {
			// Get ResultSet information
			ResultSetMetaData rsmd = rs.getMetaData();
			int colCount = rsmd.getColumnCount();

			// Prints columns name
			for (int i = 1; i <= colCount; i++) {
				System.out.print(rsmd.getColumnName(i));
				System.out.print("\t");
			}
			System.out.println();

			// Prints horizontal separator (----...)
			for (int i = 1; i <= colCount; i++) {
				int columnWidth = rsmd.getColumnDisplaySize(i);
				for (int j = 0; j < columnWidth; j++) {
					System.out.print("-");
				}
				System.out.print("\t");
			}
			System.out.println();

			// Iterates ResultSet and prints each column content
			while (rs.next()) {
				for (int i = 1; i <= colCount; i++) {
					String columnValue = rs.getString(i);
					System.out.print(columnValue + "\t");
				}
				System.out.println();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Asks for nnconducao while format is invalid
	private String getNnconducao(Scanner scanner) {
		String regex = "^[A-Z]{2}-\\d{7}$"; // CC-DDDDDDD
		while (true) {
			System.out.print(" ncconducao (CC-DDDDDDD): ");
			String ncconducao = scanner.next();
			if (ncconducao.matches(regex)) {
				return ncconducao;
			} else {
				System.out.println("Invalid format, try CC-DDDDDDD");
			}
		}
	}

	// Ask birthday date while invalid format
	private LocalDate getBdayDate(Scanner scanner) {
		while (true) {
			try {
				System.out.print("birthday (YYYY-MM-DD): ");
				String dateString = scanner.next();
				return LocalDate.parse(dateString); // throws DateTimeParseException if invalid format
			} catch (java.time.format.DateTimeParseException e) {
				System.out.println("Invalid format, try YYYY-MM-DD");
			}
		}
	}

	// Asks for matricula while format is invalid
	private String getMatricula(Scanner scanner) {
		String regex1 = "^[A-Za-z]{2}[0-9]{2}[A-Za-z]{2}$"; // CCDDCC
		String regex2 = "^[0-9]{2}[A-Za-z]{2}[0-9]{2}$"; // CCDDCC
		while (true) {
			System.out.print("matricula (CCDDCC or DDCCDD): ");
			String matricula = scanner.next();
			if (matricula.matches(regex1) || matricula.matches(regex2)) {
				return matricula;
			} else {
				System.out.println("Invalid format, try CCDDCC or DDCCDD");
			}
		}
	}

}