package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class DataManager {
	private List<FarmData> formatedData = new ArrayList<>();
	private Label minMaxOrAve = new Label();
	private boolean validFile = true;
	private Set<FarmData> data;
	private TableView<FarmData> table;

	public DataManager(TableView<FarmData> table, Set<FarmData> data) {
		this.table = table;
		this.data = data;
	}
	
	void determineMax() {
		try {
			FarmData max = formatedData.stream().max(Comparator.comparing(FarmData::getWeight)).get();
			minMaxOrAve.setText(max.toString());
		} catch (NoSuchElementException ignored) {

		}
	}

	void determineMin() {
		try {
			FarmData min = formatedData.stream().min(Comparator.comparing(FarmData::getWeight)).get();
			System.out.println(min);
			minMaxOrAve.setText(min.toString());
		} catch (NoSuchElementException ignored) {

		}
	}

	void determineAverage() {
		if (data.size() == 0)
			return;

		double sum = 0;

		for (FarmData farm : formatedData) {
			sum += farm.getWeight();
		}
		minMaxOrAve.setText("Average weight : " + sum / data.size());
	}

	void setPercent(List<FarmData> farmData) {

		double sum = 0;

		for (FarmData farm : formatedData) {
			sum += farm.getWeight();
		}

		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.DOWN);
		for (FarmData farm : farmData) {
			farm.setPercent(df.format((farm.getWeight() / sum) * 100) + "%"); // format decimal for %
		}

	}
	

	void farmReport(String farmIdInput, String yearInput) throws IOException {
		table.getColumns().get(0).setVisible(false); // make date not visible
		table.getColumns().get(1).setVisible(true); // make month visible
		table.getColumns().get(2).setVisible(false); // make farmID not visible
	
		formatedData = data.stream().filter(farmData -> farmData.getDate().substring(0, 4).equalsIgnoreCase(yearInput)
				&& farmData.getFarmID().equalsIgnoreCase(farmIdInput)).collect(Collectors.toList());
		
		data.stream().filter(farmData -> farmData.getDate().substring(0, 4).equalsIgnoreCase(yearInput)
				&& farmData.getFarmID().equals(farmIdInput)).collect(Collectors.toList()).forEach(System.out::println);

		List<FarmData> weightByMonths = makeListForFarmReport();

		String[] dateParts;

		System.out.println("farmReport size: " + formatedData.size());
		for (FarmData farm : formatedData) {
			dateParts = farm.getDate().split("-");
			switch (Integer.parseInt(dateParts[1])) {
			case 1:
				weightByMonths.get(0).addWeight(farm.getWeight());
				break;
			case 2:
				weightByMonths.get(1).addWeight(farm.getWeight());
				break;
			case 3:
				weightByMonths.get(2).addWeight(farm.getWeight());
				break;
			case 4:
				weightByMonths.get(3).addWeight(farm.getWeight());
				break;
			case 5:
				weightByMonths.get(4).addWeight(farm.getWeight());
				break;
			case 6:
				weightByMonths.get(5).addWeight(farm.getWeight());
				break;
			case 7:
				weightByMonths.get(6).addWeight(farm.getWeight());
				break;
			case 8:
				weightByMonths.get(7).addWeight(farm.getWeight());
				break;
			case 9:
				weightByMonths.get(8).addWeight(farm.getWeight());
				break;
			case 10:
				weightByMonths.get(9).addWeight(farm.getWeight());
				break;
			case 11:
				weightByMonths.get(10).addWeight(farm.getWeight());
				break;
			case 12:
				weightByMonths.get(11).addWeight(farm.getWeight());
				break;

			}
		}

		// boolean containes = false;
		// for (FarmData farm : farmReport) {
		// for (FarmData farmInTemp : weightByMonths) {
		// if(farm.getFarmID().equals(farmInTemp.getFarmID())){
		// containes = true;
		// farmInTemp.addWeight(farm.getWeight());
		// }
		// }
		// if(!containes){
		// weightByMonths.add(new FarmData(farm.getDate(), farm.getFarmID(),
		// farm.getWeight()));
		// }
		// containes = false;
		// }
		setPercent(weightByMonths);

		table.setItems(FXCollections.observableArrayList(weightByMonths));
		printReport();
		String s = "2019-5-1,Farm 106,860";
//		System.out.println(s.substring(5, 6));
	}

	List<FarmData> makeListForFarmReport() {
		List<FarmData> weightByMonths = new ArrayList<>();
		weightByMonths.add(new FarmData("Jan"));
		weightByMonths.add(new FarmData("Feb"));
		weightByMonths.add(new FarmData("March"));
		weightByMonths.add(new FarmData("April"));
		weightByMonths.add(new FarmData("May"));
		weightByMonths.add(new FarmData("June"));
		weightByMonths.add(new FarmData("July"));
		weightByMonths.add(new FarmData("Aug"));
		weightByMonths.add(new FarmData("Sep"));
		weightByMonths.add(new FarmData("Oct"));
		weightByMonths.add(new FarmData("Nov"));
		weightByMonths.add(new FarmData("Dec"));

		return weightByMonths;
	}

	void annualReport(String yearInput) throws IOException {

		table.setVisible(true);
		table.getColumns().get(0).setVisible(false);
		table.getColumns().get(1).setVisible(false);
		// data.add(new FarmData("Sion's farm", 4558858));

		List<FarmData> annualFarmWeight = new ArrayList<>();

		System.out.println("ghj");
		System.out.println(data.size());
		System.out.println("ghj");

		formatedData = data.stream().filter(farmData -> farmData.getDate().substring(0, 4).equalsIgnoreCase(yearInput))
				.collect(Collectors.toList());

		////////////////
		HashSet<String> IDs = new HashSet<String>();
		for (FarmData e : formatedData) {
			IDs.add(e.getFarmID());
		}

		Hashtable<String, Integer> hashtable = new Hashtable<String, Integer>();
		for (String e : IDs) {
			hashtable.put(e, 0);
		}

		for (FarmData entry : formatedData) {
			String id = entry.getFarmID();
			Integer oldweight = hashtable.get(id);
			Integer newWeight = oldweight + entry.getWeight();
			hashtable.replace(id, newWeight);
		}

		System.out.println(hashtable);
		System.out.println(hashtable.size());

		for (String e : IDs) {
			annualFarmWeight.add(new FarmData(e, hashtable.get(e)));
		}

		setPercent(annualFarmWeight);

		table.setItems(FXCollections.observableArrayList(annualFarmWeight));
		printReport();
	}

	void monthlyReport(String yearInput, String monthInput) throws IOException {
		// table.getColumns().get(0).setVisible(false);
		// table.getColumns().get(1).setVisible(true);
		//
		// System.out.println("hello");
		//
		// formatedData = data.stream().filter(farmData ->
		// farmData.getDate().substring(0,4).equalsIgnoreCase(yearInput) &&
		// farmData.getMonth().equals(monthInput)
		// ).collect(Collectors.toList()); // filtered out all entries without the
		// desired year and
		// month
		//
		// List<FarmData> monthFormat =
		// formatedData.stream().filter(distinctByFarmId(FarmData::getFarmID)).collect(Collectors.toList());
		//
		// for (FarmData farm : monthFormat) {
		// farm.setWeight(0);
		// System.out.println(farm.getFarmID());
		// }
		//
		// for(FarmData farm : formatedData){
		//
		// }

		table.setVisible(true);
		table.getColumns().get(0).setVisible(false);
		table.getColumns().get(1).setVisible(false);
		// data.add(new FarmData("Sion's farm", 4558858));

		List<FarmData> annualFarmWeight = new ArrayList<>();

		System.out.println("ghj");
		System.out.println(data.size());
		System.out.println("ghj");

		formatedData = data.stream()
				.filter(farmData -> farmData.getDate().substring(0, 4).equalsIgnoreCase(yearInput)
						&& farmData.getDate().substring(5, 6).equalsIgnoreCase(monthInput))
				.collect(Collectors.toList());

		///////
		// formatedData = data.stream().filter(farmData ->
		// farmData.getDate().substring(0,4).equalsIgnoreCase(yearInput) &&
		// farmData.getFarmID().equals(farmIdInput)
		// ).collect(Collectors.toList());
		////////////////
		HashSet<String> IDs = new HashSet<String>();
		for (FarmData e : formatedData) {
			IDs.add(e.getFarmID());
		}

		Hashtable<String, Integer> hashtable = new Hashtable<String, Integer>();
		for (String e : IDs) {
			hashtable.put(e, 0);
		}

		for (FarmData entry : formatedData) {
			String id = entry.getFarmID();
			Integer oldweight = hashtable.get(id);
			Integer newWeight = oldweight + entry.getWeight();
			hashtable.replace(id, newWeight);
		}

		System.out.println(hashtable);
		System.out.println(hashtable.size());

		for (String e : IDs) {
			annualFarmWeight.add(new FarmData(e, hashtable.get(e)));
		}

		setPercent(annualFarmWeight);

		table.setItems(FXCollections.observableArrayList(annualFarmWeight));
		printReport();
	}

	static <T> Predicate<T> distinctByFarmId(Function<? super T, ?> keyExtractor) {

		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	// List<Person> personListFiltered = personList.stream()
	// .filter(distinctByKey(p -> p.getName()))
	// .collect(Collectors.toList());

	void dateRangeReport(String startDateInput, String endDateInput) throws IOException {
		String startDate = startDateInput;
		String endDate = endDateInput;
		String d3String = "";

		Date d1 = stringToDate(startDate);
		Date d2 = stringToDate(endDate);
		// Date d3 = stringToDate(d3String);

		///////////////////////////////

		table.setVisible(true);
		table.getColumns().get(0).setVisible(false);
		table.getColumns().get(1).setVisible(false);
		// data.add(new FarmData("Sion's farm", 4558858));

		List<FarmData> annualFarmWeight = new ArrayList<>();

		System.out.println("ghj");
		System.out.println(data.size());
		System.out.println("ghj");

		formatedData = data.stream().filter(farmData -> dateInRange(d1, d2, stringToDate(farmData.getDate())))
				.collect(Collectors.toList());

		HashSet<String> IDs = new HashSet<String>();
		for (FarmData e : formatedData) {
			IDs.add(e.getFarmID());
		}

		Hashtable<String, Integer> hashtable = new Hashtable<String, Integer>();
		for (String e : IDs) {
			hashtable.put(e, 0);
		}

		for (FarmData entry : formatedData) {
			String id = entry.getFarmID();
			Integer oldweight = hashtable.get(id);
			Integer newWeight = oldweight + entry.getWeight();
			hashtable.replace(id, newWeight);
		}

		System.out.println(hashtable);
		System.out.println(hashtable.size());

		for (String e : IDs) {
			annualFarmWeight.add(new FarmData(e, hashtable.get(e)));
		}

		setPercent(annualFarmWeight);

		table.setItems(FXCollections.observableArrayList(annualFarmWeight));
		printReport();

		///////////////////////////////
		// boolean b = dateInRange(startDate,startDate,startDate);

	}

	boolean dateInRange(Date d1, Date d2, Date d3) {
		return d3.after(d1) && d3.before(d2);
	}

	Date stringToDate(String s) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date d = new Date();
		try {
			d = format.parse(s);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return d;
	}

	void printReport() throws IOException {
		File newFile = new File(System.getProperty("user.dir"));
		File newFile2 = new File("C:\\Users\\sionc\\Downloads\\Temp\\testing.csv");
		FileWriter writer = new FileWriter(newFile2);
		writer.write("date,farm_id,weight\n");

		// write each data element in formatted
		for (FarmData farm : formatedData) {
			writer.write(farm.printToCsvFile() + "\n");
		}
		writer.close();
	}

}
