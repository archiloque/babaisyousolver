package net.archiloque.babaisyousolver;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class FiFoQueue<E> {

  private @Nullable FiFoQueue.QueueElement<E> currentElement;

  FiFoQueue() {
  }

  void add(@NotNull E newElement) {
    currentElement =
        new QueueElement<>(newElement, currentElement);
  }

  @Nullable E pop() {
    if (currentElement != null) {
      E element = currentElement.element;
      currentElement = currentElement.next;
      return element;
    } else {
      return null;
    }
  }

  boolean isEmpty() {
    return currentElement == null;
  }

  private final static class QueueElement<E> {

    private final @NotNull E element;

    private final @Nullable FiFoQueue.QueueElement<E> next;

    QueueElement(
        @NotNull E element,
        @Nullable FiFoQueue.QueueElement<E> next) {
      this.element = element;
      this.next = next;
    }
  }

}
