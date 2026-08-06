package DesignPattern.Memento;

import java.util.ArrayList;
import java.util.List;

class History {
    private List<EditorMemento> mementos = new ArrayList<>();

    public void save(EditorMemento memento) {
        mementos.add(memento);
    }

    public EditorMemento undo() {
        if (!mementos.isEmpty()) {
            int lastIndex = mementos.size() - 1;
            EditorMemento memento = mementos.get(lastIndex);
            mementos.remove(lastIndex);
            return memento;
        }
        return null;
    }
}
