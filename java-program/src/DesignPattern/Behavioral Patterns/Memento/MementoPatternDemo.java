package DesignPattern.Memento;

public class MementoPatternDemo {
    public static void main(String[] args) {
        Editor editor = new Editor();
        History history = new History();

        editor.write("First sentence. ");
        history.save(editor.save());

        editor.write("Second sentence. ");
        history.save(editor.save());

        editor.write("Third sentence. ");
        System.out.println("Current content: " + editor.getContent());

        editor.restore(history.undo());
        System.out.println("After undo: " + editor.getContent());

        editor.restore(history.undo());
        System.out.println("After undo: " + editor.getContent());
    }
}
