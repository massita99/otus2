package atm;

public interface Statable {

    /**
     * Return object of current state of object
     * @return {@link Memento} that contains all valuable detail of current object state
     */

    Memento getState();

    /**
     * Set object staate from contained in {@link Memento}
     * @param memento
     * @throws RuntimeException if Memento doesn't fit for current object
     */

    void setState(Memento memento) throws RuntimeException;


}
