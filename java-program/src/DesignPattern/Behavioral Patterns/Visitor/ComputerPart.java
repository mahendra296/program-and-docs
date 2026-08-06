package DesignPattern.Visitor;

interface ComputerPart {
    void accept(ComputerPartVisitor visitor);
}
