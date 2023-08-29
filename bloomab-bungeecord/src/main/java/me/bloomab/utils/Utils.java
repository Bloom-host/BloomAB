package me.bloomab.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {

    public boolean checkIfInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

}
