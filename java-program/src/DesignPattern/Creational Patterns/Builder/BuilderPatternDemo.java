package DesignPattern.Builder;

public class BuilderPatternDemo {
    public static void main(String[] args) {
        Computer computer = new Computer.ComputerBuilder("Intel i7", "16GB")
                .setStorage("512GB SSD")
                .setGPU("NVIDIA RTX 3060")
                .setWiFiEnabled(true)
                .setBluetoothEnabled(true)
                .build();

        System.out.println(computer);
    }
}
