package DesignPattern.TemplateMethod;

class JSONDataProcessor extends DataProcessor {
    @Override
    void readData() {
        System.out.println("Reading data from JSON file");
    }

    @Override
    void processData() {
        System.out.println("Processing JSON data");
    }
}
