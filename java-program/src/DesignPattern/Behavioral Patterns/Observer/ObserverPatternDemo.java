package DesignPattern.Observer;

public class ObserverPatternDemo {
    public static void main(String[] args) {
        NewsAgency agency = new NewsAgency();

        Observer channel1 = new NewsChannel("CNN");
        Observer channel2 = new NewsChannel("BBC");
        Observer app = new MobileApp("NewsApp");

        agency.attach(channel1);
        agency.attach(channel2);
        agency.attach(app);

        agency.setNews("Breaking: New design pattern discovered!");
        System.out.println();

        agency.detach(channel2);
        agency.setNews("Update: Pattern proves revolutionary!");
    }
}
