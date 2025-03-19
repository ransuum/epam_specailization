package org.epam.utils.menurender.choosermanagement;

import org.epam.models.SecurityContextHolder;

import java.util.Scanner;

public interface Chooser {
    void show(Scanner scanner, SecurityContextHolder securityContextHolder);
    Integer getChoice();

    default byte getMenuChoice(Scanner scanner) {
        return scanner.nextByte();
    }
}
