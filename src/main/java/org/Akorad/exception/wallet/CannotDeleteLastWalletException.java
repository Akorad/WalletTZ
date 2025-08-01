package org.Akorad.exception.wallet;

public class CannotDeleteLastWalletException extends RuntimeException{
    public CannotDeleteLastWalletException() {
        super("Невозможно удалить последний кошелек. Создайте новый кошелек перед удалением текущего.");
    }
}
