package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class FileManager {
	private static final String COMMA = "\\s*,\\s*";
	boolean validFile;

	/**
	 * Used to load a file dragged and dropped the the appropriate location
	 * 
	 * @param inputFilePath
	 * @return
	 */
	List<FarmData> loadFile(String inputFilePath) {
		List<FarmData> inputList = new ArrayList<>();
		try {
			File inputF = new File(inputFilePath);
			InputStream inputFS = new FileInputStream(inputF);
			BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));

			// skip the header of the csv
			inputList = br.lines().skip(1).map(mapToItem).collect(Collectors.toList());
			br.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());

		} catch (Exception e) {
			Alert invalidFile = new Alert(Alert.AlertType.WARNING,
					"One or more of the provided files had invalid csv format");
			invalidFile.showAndWait().filter(alert -> alert == ButtonType.OK);
			this.validFile = false;
		}
		return inputList;
	}

	private Function<String, FarmData> mapToItem = (line) -> {

		String[] curFarm = line.split(COMMA);// a CSV has comma separated lines
		String[] dateParts = curFarm[0].split("-"); // use to get month which will be at index 1

		return new FarmData(curFarm[0], getMonth(Integer.parseInt(dateParts[1])), curFarm[1],
				Integer.parseInt(curFarm[2]));
	};

	private String getMonth(int month) {
		return new DateFormatSymbols().getMonths()[month - 1];
	}

	void outputToFile(TableView<FarmData> table, String printStatus) {
		if (printStatus.equals("farmReport")) {
			printFarmReport(table);
		} else if (printStatus.equals("annualReport")) {
			printAnnualReport(table);
		} else if (printStatus.equals("monthlyReport")) {
			printMonthlyReport(table);
		}
	}

	void printFarmReport(TableView<FarmData> table) {
		try {
			File output = new File("output.txt");
			FileWriter outputFile = new FileWriter(output);
			ObservableList<FarmData> data = table.getItems();
//			outputFile.write("Farm_id: " + data.get(1).getFarmID() + "\n");
			outputFile.write("Month, Weight\n");

			for (FarmData farm : data) {
				outputFile.write(farm.getMonth() + ", " + farm.getWeight() + "\n");
			}

			outputFile.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	void printAnnualReport(TableView<FarmData> table) {
		try {
			File output = new File("output.txt");
			FileWriter outputFile = new FileWriter(output);
			ObservableList<FarmData> data = table.getItems();
			outputFile.write("Farm ID, Total Weight, % Total Weight\n");

			for (FarmData farm : data) {
				outputFile.write(farm.getFarmID() + ", " + farm.getWeight() + ", " + farm.getPercent() + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	void printMonthlyReport(TableView<FarmData> table) {
		try {
			File output = new File("output.txt");
			FileWriter outputFile = new FileWriter(output);
			ObservableList<FarmData> data = table.getItems();
			outputFile.write("Farm ID, Total Weight, % Total Weight\n");

			for (FarmData farm : data) {
				outputFile.write(farm.getFarmID() + ", " + farm.getWeight() + ", " + farm.getPercent() + "\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
