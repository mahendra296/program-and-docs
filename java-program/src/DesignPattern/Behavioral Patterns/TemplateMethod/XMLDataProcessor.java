package DesignPattern.TemplateMethod;

class XMLDataProcessor extends DataProcessor {
    @Override
    void readData() {
        System.out.println("Reading data from XML file");
    }

    @Override
    void processData() {
        System.out.println("Processing XML data");
    }
}
