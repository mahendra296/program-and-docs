package DesignPattern.Observer;

class MobileApp implements Observer {
    private String appName;

    public MobileApp(String appName) {
        this.appName = appName;
    }

    @Override
    public void update(String news) {
        System.out.println(appName + " notification: " + news);
    }
}
