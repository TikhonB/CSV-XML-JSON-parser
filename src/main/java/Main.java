import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;

import java.util.List;


public class Main {
    public static void main(String[] args) {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        String csvFileName = "data.csv";
        String xmlFileName = "data.xml";
        String jsonFileForCsv = "data.json";
        String jsonFileForXML = "data2.json";

        List<Employee> staff = new ArrayList<>();
        staff.add(new Employee(1, "John", "Smith", "USA", 25));
        staff.add(new Employee(2, "Ivan", "Petrov", "RU", 23));
        createCsvFile(staff, csvFileName, columnMapping);

        List<Employee> listCSV = parseCSV(columnMapping, csvFileName);
        String json = listToJson(listCSV);
        writeString(json, jsonFileForCsv);

        List<Employee> listXML = parseXML(xmlFileName);
        String json1 = listToJson(listXML);
        writeString(json1, jsonFileForXML);

    }

    public static List<Employee> parseCSV(String[] columnMap, String name) {
        List<Employee> list = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(name))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMap);
            CsvToBean<Employee> ctb = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            list = ctb.parse();
            list.forEach(System.out::println);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return list;
    }

    public static <T> String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<T>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        System.out.println(json);
        return json;
    }

    public static void writeString(String json, String name) {
        try (FileWriter writer = new FileWriter(name)) {
            writer.write(json.toString());
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public static void createCsvFile(List employee, String name, String[] mapping) {
        ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(Employee.class);
        strategy.setColumnMapping(mapping);
        try (Writer writer = new FileWriter(name)) {
            StatefulBeanToCsv<Employee> sbc = new StatefulBeanToCsvBuilder<Employee>(writer)
                    .withMappingStrategy(strategy)
                    .build();

            sbc.write(employee);
        } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public static List<Employee> parseXML(String fileName) {
        List<Employee> staff = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(fileName));
            Node root = document.getDocumentElement();
            System.out.println("Корневой элемент " + root.getNodeName());
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.ELEMENT_NODE != node.getNodeType()) {
                    continue;
                }
                System.out.println("Узел " + node.getNodeName());
                Employee employeeTemp = new Employee();
                NodeList nodeList1 = node.getChildNodes();
                for (int k = 0; k < nodeList1.getLength(); k++) {
                    Node child = nodeList1.item(k);
                    if (child.getNodeName().equals("id")) {
                        employeeTemp.id = Long.parseLong(child.getTextContent());
                    }
                    if (child.getNodeName().equals("firstName")) {
                        employeeTemp.firstName = child.getTextContent();
                    }
                    if (child.getNodeName().equals("lastName")) {
                        employeeTemp.lastName = child.getTextContent();
                    }
                    if (child.getNodeName().equals("country")) {
                        employeeTemp.country = child.getTextContent();
                    }
                    if (child.getNodeName().equals("age")) {
                        employeeTemp.age = Integer.parseInt(child.getTextContent());
                    }
                }
                staff.add(employeeTemp);
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return staff;
    }

}


