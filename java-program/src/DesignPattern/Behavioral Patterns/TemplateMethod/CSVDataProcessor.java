package DesignPattern.TemplateMethod;

class CSVDataProcessor extends DataProcessor {
    @Override
    void readData() {
        System.out.println("Reading data from CSV file");
    }

    @Override
    void processData() {
        System.out.println("Processing CSV data");
    }
}
