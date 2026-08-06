package DesignPattern.Iterator;

public class IteratorPatternDemo {
    public static void main(String[] args) {
        BookCollection collection = new BookCollection();
        collection.addBook(new Book("Design Patterns", "Gang of Four"));
        collection.addBook(new Book("Effective Java", "Joshua Bloch"));
        collection.addBook(new Book("Clean Code", "Robert Martin"));

        Iterator<Book> iterator = collection.createIterator();

        while (iterator.hasNext()) {
            Book book = iterator.next();
            System.out.println(book);
        }
    }
}
