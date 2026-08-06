package DesignPattern.Mediator;

interface ChatMediator {
    void sendMessage(String message, User user);
    void addUser(User user);
}
